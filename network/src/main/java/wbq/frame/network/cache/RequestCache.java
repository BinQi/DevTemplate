package wbq.frame.network.cache;

import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;

public class RequestCache {
    public final CacheInterceptor.CacheType cacheType;
    public final CacheControl cacheControl;
    public final String cacheKey;

    public RequestCache(CacheInterceptor.CacheType cacheType, CacheControl cacheControl, String cacheKey) {
        this.cacheType = cacheType;
        this.cacheControl = cacheControl;
        this.cacheKey = cacheKey;
    }

    /**
     * 强制走缓存，不考虑有效期。没有缓存再走网络.
     *
     * @param cacheKey
     * @return
     */
    public static RequestCache forceCache(String cacheKey) {
        return new RequestCache(CacheInterceptor.CacheType.valid_cache_net,
                new CacheControl.Builder().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build()
                , cacheKey);
    }

    /**
     * 强制只走缓存，不考虑有效期。没有缓存也不走网络.
     *
     * @param cacheKey
     * @return
     */
    public static RequestCache forceCacheOnly(String cacheKey) {
        return new RequestCache(CacheInterceptor.CacheType.valid_cache_net, CacheControl.FORCE_CACHE, cacheKey);
    }

    /**
     * 先走有效期内的缓存，缓存无效则走网络.
     *
     * @param cacheKey
     * @param maxAge   有效期
     * @param timeUnit maxAge的单位
     * @return
     */
    public static RequestCache forceValidCache(String cacheKey, int maxAge, TimeUnit timeUnit) {
        return new RequestCache(CacheInterceptor.CacheType.valid_cache_net,
                new CacheControl.Builder().maxAge(maxAge, timeUnit).build(),
                cacheKey);
    }

    /**
     * 强制只走有效期内的缓存，不走网络.
     *
     * @param cacheKey
     * @param maxAge   有效期
     * @param timeUnit maxAge的单位
     * @return
     */
    public static RequestCache forceValidCacheOnly(String cacheKey, int maxAge, TimeUnit timeUnit) {
        return new RequestCache(CacheInterceptor.CacheType.cache_only,
                new CacheControl.Builder().maxAge(maxAge, timeUnit).build(),
                cacheKey);
    }

    /**
     * 强制只走有效期内的缓存，不走网络.
     *
     * @param cacheKey
     * @param cacheControl
     * @return
     */
    public static RequestCache forceValidCacheOnly(String cacheKey, CacheControl cacheControl) {
        return new RequestCache(CacheInterceptor.CacheType.cache_only,
                cacheControl,
                cacheKey);
    }

    /**
     * 取数据时不走缓存只走网络，返回的数据会保存到缓存里
     *
     * @return
     */
    public static RequestCache forceNetworkOnly(String cacheKey) {
        return new RequestCache(CacheInterceptor.CacheType.net_only, null, cacheKey);
    }
}