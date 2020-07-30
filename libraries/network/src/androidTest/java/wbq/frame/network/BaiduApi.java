package wbq.frame.network;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Jerry on 2020/5/21 15:45
 */
public interface BaiduApi {
    @GET(" ")
    Call<String> homePage(@Query("requesttime") long timeStamp);

    @GET(" ")
    Observable<ResponseBody> homePageRx(@Query("requesttime") long timeStamp);

    @POST("ISO1818002")
    Call<String> userInfo(@Query("requesttime") long timeStamp, @Body RequestBody body);

}
