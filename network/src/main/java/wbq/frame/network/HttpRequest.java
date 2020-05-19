package wbq.frame.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wbq.frame.network.intercept.EncryptInterceptor;
import wbq.frame.network.intercept.LogInterceptor;
import wbq.frame.network.intercept.SignInterceptor;

/**
 * log；缓存；签名；加解密；retrofit & Rxjava
 * <br/>{@linkplain okhttp3.OkHttpClient.Builder#cache(Cache)}只能对Get请求才有缓存机制；
 * <br/>如需对其它请求也生效或其他缓存机制需求可使用{@linkplain wbq.frame.network.cache.CacheInterceptor}
 * Created by Jerry on 2020-03-26 11:48
 */
public class HttpRequest {
    public static final String TAG = "HttpRequest";
    private static final int DEFAULT_READ_TIMEOUT_SECS = 15;
    private static final int DEFAULT_WRITE_TIMEOUT_SECS = 15;
    private static final int DEFAULT_CONNECT_TIMEOUT_SECS = 15;
    private static final int MAX_CACHE_SIZE = 5 * 1024 * 1024;
    private static volatile OkHttpClient sClient;

    public static void main(String... args) throws Exception {
        System.out.println("main start");
    }

    /**
     * @return 返回用于解析数据的{@code Gson}对象
     */
    protected Gson getGson() {
        return new GsonBuilder().create();
    }

    private void okhttp() {
        OkHttpClient okHttpClient = getClient();
        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .get()
                .cacheControl(new CacheControl.Builder().onlyIfCached().build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    static OkHttpClient getClient() {
        if (null == sClient) {
            synchronized (RetrofitHttpRequest.class) {
                if (null == sClient) {
                    File directory = new File("./network/cache");
//                    File directory = new File(context.getCacheDir(), "buynet"); FIXME
                    sClient = new OkHttpClient.Builder()
                            .readTimeout(DEFAULT_READ_TIMEOUT_SECS, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_WRITE_TIMEOUT_SECS, TimeUnit.SECONDS)
                            .connectTimeout(DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS)
                            .addInterceptor(new SignInterceptor())
                            .addInterceptor(new LogInterceptor())
                            .addInterceptor(new EncryptInterceptor())
                            .cache(new Cache(directory, MAX_CACHE_SIZE))
                            .build();
                }
            }
        }
        return sClient;
    }
}
