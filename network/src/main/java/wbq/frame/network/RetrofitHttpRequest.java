package wbq.frame.network;

import java.lang.reflect.ParameterizedType;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jerry on 2020-03-26 11:48
 */
public abstract class RetrofitHttpRequest<T> extends HttpRequest {

    public abstract String getBaseUrl();

    public final T create() {
        Class<T> service = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return retrofit().create(service);
    }

    protected Retrofit retrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }
}
