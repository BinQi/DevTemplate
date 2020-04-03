package wbq.frame.network.rx;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
 * @created 2020-03-26 11:41
 */
public interface ObservableService {

    @GET
    Observable<ResponseBody> get(@Url String url,
                                 @HeaderMap Map<String, String> headerMap,
                                 @QueryMap Map<String, String> queryMap);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url String url,
                                  @HeaderMap Map<String, String> headerMap,
                                  @FieldMap Map<String, String> fieldMap
    );

    @POST
    Observable<ResponseBody> post(@Url String url,
                                  @HeaderMap Map<String, String> headerMap,
                                  @Body RequestBody body
    );

    @PUT
    @FormUrlEncoded
    Observable<ResponseBody> put(@Url String url,
                                 @HeaderMap Map<String, String> headerMap,
                                 @FieldMap Map<String, String> fieldMap
    );

    @PUT
    Observable<ResponseBody> put(@Url String url,
                                 @HeaderMap Map<String, String> headerMap,
                                 @Body RequestBody body
    );

    @DELETE
    @FormUrlEncoded
    Observable<ResponseBody> delete(@Url String url,
                                    @HeaderMap Map<String, String> headerMap,
                                    @FieldMap Map<String, String> fieldMap
    );

    @DELETE
    Observable<ResponseBody> delete(@Url String url,
                                    @HeaderMap Map<String, String> headerMap,
                                    @Body RequestBody body
    );
}
