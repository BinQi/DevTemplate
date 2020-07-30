package wbq.frame.network.cache;

import android.os.Build;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.RCache;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Internal;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import wbq.frame.util.log.LogUtils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static wbq.frame.network.HttpRequest.TAG;
import static wbq.frame.network.cache.CacheInterceptor.CacheType.both;
import static wbq.frame.network.cache.CacheInterceptor.CacheType.cache_after_net_fail;
import static wbq.frame.network.cache.CacheInterceptor.CacheType.cache_only;
import static wbq.frame.network.cache.CacheInterceptor.CacheType.net_only;
import static wbq.frame.network.cache.CacheInterceptor.CacheType.valid_cache_net;

/**
 * 自定义缓存拦截器
 * <br/>通过 {@link Request#headers()} 里的 {@link CacheControl} 控制，需要在 request里配置，不支持服务器配置。
 * <br/>调用 OkHttpClient#addInterceptor 来使用此拦截器
 * <br/>使用Retrofit通常这样配置即可缓存1小时 @Headers("Cache-Control: public, max-age=3600")
 * <br/>支持 {@link CacheControl#FORCE_CACHE} 、 {@link CacheControl#FORCE_NETWORK}
 * <br/>支持 {@link CacheControl#maxAgeSeconds()} 、 {@link CacheControl#maxStaleSeconds()}
 * <br/>不支持304，304使用okhttp默认机制，不要使用这个拦截器
 * <br/>与其它拦截器一起使用时，必须注意Cache-Control会被修改的问题
 * <p>
 */
public class CacheInterceptor implements Interceptor {
    /**
     * 缓存类型
     */
    public enum CacheType {
        /**
         * 优先使用有效期内的缓存，没缓存或缓存失效再走网络
         * <br/> 若使用了{@link CacheControl#FORCE_CACHE}，则不会再走网络
         */
        valid_cache_net,
        /**
         * 网络失败后再取缓存
         */
        cache_after_net_fail,
        /**
         * 优先使用有效期内的缓存，再考虑network，若network失败且有缓存（已过期），则仍然用回缓存
         */
        both,
        /**
         * 取数据时不走缓存只走网络，返回的数据会保存到缓存里
         */
        net_only,
        /**
         * 取数据时只走缓存，不走网络
         */
        cache_only,
    }

    final CacheType mCacheType;
    final String mCacheKey;
    final CacheControl mCacheControl;
    final InternalCache mInternalCache;

    CacheInterceptor(Builder builder) {
        mCacheType = builder.mCacheType;
        mCacheKey = builder.mCacheKey;
        mCacheControl = builder.mCacheControl;
        mInternalCache = RCache.getInternalCache();
    }

    /**
     * 返回一个内容一样的builder对象
     * @return
     */
    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * 强制走缓存，不考虑有效期。没有缓存再走网络.
     * @param cacheKey
     * @return
     */
    public static CacheInterceptor forceCache(String cacheKey) {
        return new Builder(valid_cache_net)
                .setCacheKey(cacheKey)
                .setCacheControl(new CacheControl.Builder().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build())
                .build();
    }

    /**
     * 强制只走缓存，不考虑有效期。没有缓存也不走网络.
     * @param cacheKey
     * @return
     */
    public static CacheInterceptor forceCacheOnly(String cacheKey) {
        return new Builder(valid_cache_net)
                .setCacheKey(cacheKey)
                .setCacheControl(CacheControl.FORCE_CACHE)
                .build();
    }

    /**
     * 取数据时不走缓存只走网络，返回的数据会保存到缓存里
     * @param cacheKey
     * @return
     */
    public static CacheInterceptor forceNetworkOnly(String cacheKey) {
        return new Builder(net_only)
                .setCacheKey(cacheKey)
                .build();
    }

    public static final class Builder {
        CacheType mCacheType;
        String mCacheKey;
        CacheControl mCacheControl;

        public Builder(CacheType type) {
            mCacheType = type;
        }

        public Builder(CacheInterceptor interceptor) {
            mCacheType = interceptor.mCacheType;
            mCacheKey = interceptor.mCacheKey;
            mCacheControl = interceptor.mCacheControl;
        }

        /**
         * 设置缓存的key，默认不设置的话使用url的md5
         * <br/>若url里带有易变参数如时间，则必须自行设置key，通常可以是baseurl+suffix
         *
         * @param cacheKey
         * @return
         */
        public Builder setCacheKey(String cacheKey) {
            mCacheKey = cacheKey;
            return this;
        }

        /**
         * 可以在Retrofit里设置 @Headers("Cache-Control: public, max-age=3600")
         * <br/>也可以在这里设置CacheControl。这里的优先级更高。
         *
         * @param cacheControl
         * @return
         */
        public Builder setCacheControl(CacheControl cacheControl) {
            mCacheControl = cacheControl;
            return this;
        }

        public CacheInterceptor build() {
            return new CacheInterceptor(this);
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (mCacheControl != null) {
            request = request.newBuilder().cacheControl(mCacheControl).build();
        }

        if (mCacheType == valid_cache_net) {
            Response cacheResponse = getValidateCacheResponse(request);
            if (cacheResponse != null) {
                return cacheResponse;
            }
            return getNetworkResponse(chain, request);
        } else if (mCacheType == cache_after_net_fail) {
            return getResponseAtCacheAfterNetFail(chain, request);
        } else if (mCacheType == both) {
            Response cacheResponse = getValidateCacheResponse(request);
            if (cacheResponse != null) {
                return cacheResponse;
            }
            return getResponseAtCacheAfterNetFail(chain, request);
        } else if (mCacheType == net_only) {
            return getNetworkResponse(chain, request);
        } else if (mCacheType == cache_only) {
            return getValidateCacheResponse(request);
        } else {
            throw new IOException("cache type error!");
        }
    }

    Response getResponseAtCacheAfterNetFail(Chain chain, Request request) throws IOException {
        Response networkResponse = null;
        IOException networkException = null;
        try {
            networkResponse = getNetworkResponse(chain, request);
        } catch (IOException e) {
            networkException = e;
        }

        if (networkResponse != null && networkResponse.isSuccessful()) {
            return networkResponse;
        } else {
            Response cacheResponse = mInternalCache.get(request, key(request));
            if (cacheResponse != null) {
                //这里返回缓存
                return cacheResponse.newBuilder()
                        .cacheResponse(stripBody(cacheResponse))
                        .build();
            } else {
                if (networkException != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        //添加被抑制的异常信息，方便收集后定位问题
                        networkException.addSuppressed(new IOException("network fail, and cache fail!"));
                    }
                    throw networkException;
                } else {
                    return networkResponse;
//                    throw new IOException("network fail, and cache fail!");
                }
            }
        }
    }

    Response getValidateCacheResponse(Request request) throws IOException {
        Response cacheCandidate = null;
        try {
            cacheCandidate = mInternalCache.get(request, key(request));
        } catch (IOException e) {
            LogUtils.w(TAG, "[CacheInterceptor#getValidateCacheResponse] -->", e);
        }

        long now = System.currentTimeMillis();

        CacheStrategy strategy = new CacheStrategy.Factory(now, request, cacheCandidate).get();
        Request networkRequest = strategy.networkRequest;
        Response cacheResponse = strategy.cacheResponse;

        //缓存命中等记录
        mInternalCache.trackResponse(strategy);

        if (cacheCandidate != null && cacheResponse == null) {
            closeQuietly(cacheCandidate.body()); // The cache candidate wasn't applicable. Close it.
        }

        // If we're forbidden from using the network and the cache is insufficient, fail.
        if (networkRequest == null && cacheResponse == null) {
            throw new IOException("Unsatisfiable Request (only-if-cached), but there is no cache!");
        }

        // If we don't need the network, we're done.
        if (networkRequest == null) { //and cacheResponse is not null
            //这里返回缓存
            return cacheResponse.newBuilder()
                    .cacheResponse(stripBody(cacheResponse))
                    .build();
        }

        //以下返回网络请求数据
        // If we have a cache response too, close it.
        if (cacheResponse != null) {
            closeQuietly(cacheResponse.body());
            cacheResponse = null;
        }

        return null;
    }

    /**
     * 获取网络返回的响应数据，并缓存
     * @param chain
     * @param request
     * @return
     * @throws IOException
     */
    Response getNetworkResponse(Chain chain, Request request) throws IOException {
        Response networkResponse = chain.proceed(request);

        Response response = networkResponse.newBuilder()
//                .header("MCache-Control", "max-age=" + request.cacheControl().maxAgeSeconds())
                // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .removeHeader("Pragma")
                .build();

        if (HttpHeaders.hasBody(response) && CacheStrategy.isCacheable(response, request)) {
            // Offer this request to the cache.
            CacheRequest cacheRequest = mInternalCache.put(response, key(request));
            return cacheWritingResponse(cacheRequest, response);
        }

        return response;
    }

    String key(Request request) {
        if (mCacheKey != null) {
            return ByteString.encodeUtf8(mCacheKey).md5().hex();
        } else {
            return ByteString.encodeUtf8(request.url().toString()).md5().hex();
        }
    }

    /**
     * Returns a new source that writes bytes to {@code cacheRequest} as they are read by the source
     * consumer. This is careful to discard bytes left over when the stream is closed; otherwise we
     * may never exhaust the source stream and therefore not complete the cached response.
     */
    private Response cacheWritingResponse(final CacheRequest cacheRequest, Response response)
            throws IOException {
        // Some apps return a null body; for compatibility we treat that like a null cache request.
        if (cacheRequest == null) return response;
        Sink cacheBodyUnbuffered = cacheRequest.body();
        if (cacheBodyUnbuffered == null) return response;

        final BufferedSource source = response.body().source();
        final BufferedSink cacheBody = Okio.buffer(cacheBodyUnbuffered);

        Source cacheWritingSource = new Source() {
            boolean cacheRequestClosed;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead;
                try {
                    bytesRead = source.read(sink, byteCount);
                } catch (IOException e) {
                    if (!cacheRequestClosed) {
                        cacheRequestClosed = true;
                        cacheRequest.abort(); // Failed to write a complete cache response.
                    }
                    throw e;
                }

                if (bytesRead == -1) {
                    if (!cacheRequestClosed) {
                        cacheRequestClosed = true;
                        cacheBody.close(); // The cache response is complete!
                    }
                    return -1;
                }

                sink.copyTo(cacheBody.buffer(), sink.size() - bytesRead, bytesRead);
                cacheBody.emitCompleteSegments();
                return bytesRead;
            }

            @Override
            public Timeout timeout() {
                return source.timeout();
            }

            @Override
            public void close() throws IOException {
                if (!cacheRequestClosed
                        && !discard(this, HttpCodec.DISCARD_STREAM_TIMEOUT_MILLIS, MILLISECONDS)) {
                    cacheRequestClosed = true;
                    cacheRequest.abort();
                }
                source.close();
            }
        };

        String contentType = response.header("Content-Type");
        long contentLength = response.body().contentLength();
        return response.newBuilder()
                .body(new RealResponseBody(contentType, contentLength, Okio.buffer(cacheWritingSource)))
                .build();
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing if {@code closeable} is
     * null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Attempts to exhaust {@code source}, returning true if successful. This is useful when reading a
     * complete source is helpful, such as when doing so completes a cache body or frees a socket
     * connection for reuse.
     */
    public static boolean discard(Source source, int timeout, TimeUnit timeUnit) {
        try {
            return skipAll(source, timeout, timeUnit);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads until {@code in} is exhausted or the deadline has been reached. This is careful to not
     * extend the deadline if one exists already.
     */
    public static boolean skipAll(Source source, int duration, TimeUnit timeUnit) throws IOException {
        long now = System.nanoTime();
        long originalDuration = source.timeout().hasDeadline()
                ? source.timeout().deadlineNanoTime() - now
                : Long.MAX_VALUE;
        source.timeout().deadlineNanoTime(now + Math.min(originalDuration, timeUnit.toNanos(duration)));
        try {
            Buffer skipBuffer = new Buffer();
            while (source.read(skipBuffer, 8192) != -1) {
                skipBuffer.clear();
            }
            return true; // Success! The source has been exhausted.
        } catch (InterruptedIOException e) {
            return false; // We ran out of time before exhausting the source.
        } finally {
            if (originalDuration == Long.MAX_VALUE) {
                source.timeout().clearDeadline();
            } else {
                source.timeout().deadlineNanoTime(now + originalDuration);
            }
        }
    }

    static Response stripBody(Response response) {
        return response != null && response.body() != null
                ? response.newBuilder().body(null).build()
                : response;
    }

    /**
     * Combines cached headers with a network headers as defined by RFC 7234, 4.3.4.
     */
    static Headers combine(Headers cachedHeaders, Headers networkHeaders) {
        Headers.Builder result = new Headers.Builder();

        for (int i = 0, size = cachedHeaders.size(); i < size; i++) {
            String fieldName = cachedHeaders.name(i);
            String value = cachedHeaders.value(i);
            if ("Warning".equalsIgnoreCase(fieldName) && value.startsWith("1")) {
                continue; // Drop 100-level freshness warnings.
            }
            if (!isEndToEnd(fieldName) || networkHeaders.get(fieldName) == null) {
                Internal.instance.addLenient(result, fieldName, value);
            }
        }

        for (int i = 0, size = networkHeaders.size(); i < size; i++) {
            String fieldName = networkHeaders.name(i);
            if ("Content-Length".equalsIgnoreCase(fieldName)) {
                continue; // Ignore content-length headers of validating responses.
            }
            if (isEndToEnd(fieldName)) {
                Internal.instance.addLenient(result, fieldName, networkHeaders.value(i));
            }
        }

        return result.build();
    }

    /**
     * Returns true if {@code fieldName} is an end-to-end HTTP header, as defined by RFC 2616,
     * 13.5.1.
     */
    static boolean isEndToEnd(String fieldName) {
        return !"Connection".equalsIgnoreCase(fieldName)
                && !"Keep-Alive".equalsIgnoreCase(fieldName)
                && !"Proxy-Authenticate".equalsIgnoreCase(fieldName)
                && !"Proxy-Authorization".equalsIgnoreCase(fieldName)
                && !"TE".equalsIgnoreCase(fieldName)
                && !"Trailers".equalsIgnoreCase(fieldName)
                && !"Transfer-Encoding".equalsIgnoreCase(fieldName)
                && !"Upgrade".equalsIgnoreCase(fieldName);
    }
}
