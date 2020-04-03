package wbq.frame.network;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 *
 * @author jerrywu
 * @created 2020-03-26 11:40
 */
public interface CallService {

    @GET
    Call<ResponseBody> get(@Url String url,
                           @HeaderMap Map<String, String> headerMap,
                           @QueryMap Map<String, String> queryMap);

    @POST
    @FormUrlEncoded
    Call<ResponseBody> post(@Url String url,
                            @HeaderMap Map<String, String> headerMap,
                            @FieldMap Map<String, String> fieldMap
    );


    @POST
    Call<ResponseBody> post(@Url String url,
                            @HeaderMap Map<String, String> headerMap,
                            @Body RequestBody body
    );

    @PUT
    @FormUrlEncoded
    Call<ResponseBody> put(@Url String url,
                           @HeaderMap Map<String, String> headerMap,
                           @FieldMap Map<String, String> fieldMap
    );

    @PUT
    Call<ResponseBody> put(@Url String url,
                           @HeaderMap Map<String, String> headerMap,
                           @Body RequestBody body
    );

    @DELETE
    @FormUrlEncoded
    Call<ResponseBody> delete(@Url String url,
                              @HeaderMap Map<String, String> headerMap,
                              @FieldMap Map<String, String> fieldMap
    );

    @DELETE
    Call<ResponseBody> delete(@Url String url,
                              @HeaderMap Map<String, String> headerMap,
                              @Body RequestBody body
    );
}
