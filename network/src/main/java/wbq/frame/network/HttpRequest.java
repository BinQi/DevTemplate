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
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import wbq.frame.network.rx.ObservableService;

/**
 * TODO log；缓存；签名；加解密；|| retrofit & Rxjava
 * Created by Jerry on 2020-03-26 11:48
 */
public class HttpRequest {
    static final String TAG = "HttpRequest";
    static final int DEFAULT_READ_TIMEOUT_SECS = 15;
    static final int DEFAULT_WRITE_TIMEOUT_SECS = 15;
    static final int DEFAULT_CONNECT_TIMEOUT_SECS = 60;

    public static void main(String... args) throws Exception {
        System.out.println("main start");
        new HttpRequest().okhttp();
    }

    protected OkHttpClient getClient() {
        return null;
    }

    public void okhttp() {
        OkHttpClient okHttpClient = createClient();
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

    private void retrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(createClient())
                .build();
        retrofit.create(CallService.class);
    }

    public void retrofitObservable() {
        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(boolean.class, new BooleanTypeAdapter())
//                .registerTypeAdapter(int.class, new IntegerTypeAdapter())
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(createClient())
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ObservableService.class);
    }

    private OkHttpClient createClient() {
        HttpLoggingInterceptor interceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        System.out.println(message);
                    }
                });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        File directory = new File("./network/cache");
        long maxSize = 1024*1024;
        return new OkHttpClient.Builder()
                .readTimeout(DEFAULT_READ_TIMEOUT_SECS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_SECS, TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .cache(new Cache(directory, maxSize))
                .build();
    }
}
