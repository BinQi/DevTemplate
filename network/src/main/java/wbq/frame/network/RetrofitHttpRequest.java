package wbq.frame.network;

import java.lang.reflect.ParameterizedType;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jerry on 2020-03-26 11:48
 */
public abstract class RetrofitHttpRequest<T> extends HttpRequest {

    public abstract String getBaseUrl();

    public T create() {
        Class<T> service = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(service);
    }
}
