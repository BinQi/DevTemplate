package wbq.frame.network;

/**
 * Created by Jerry on 2020/5/21 15:44
 */
public class DemoRequest extends RetrofitHttpRequest<BaiduApi> {

    @Override
    public String getBaseUrl() {
        return "http://www.baidu.com";
    }
}
