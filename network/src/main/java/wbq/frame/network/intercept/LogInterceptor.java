package wbq.frame.network.intercept;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import wbq.frame.util.log.LogUtils;

import static wbq.frame.network.HttpRequest.TAG;

/**
 *
 * @author jerrywu
 * @created 2020-04-07 18:03
 */
public class LogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean isLog = LogUtils.isShowLog();
        if (!isLog) {
            return chain.proceed(request);
        }

        long t1 = System.nanoTime();//请求发起的时间

        LogUtils.i(TAG, String.format("--> %s [%d]发送请求: %s  %nCacheControl:%s %nBody: %s %non connection: %s %nHeaders:[%n%s]",
                request.method(),
                request.hashCode(),
                request.url(),
                request.cacheControl(),
                getRequestBodyString(request),
                chain.connection(),
                request.headers()));

        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            LogUtils.w(TAG, String.format("<-- %s [%d] network fail-->url:%s", request.method(), request.hashCode(), request.url().toString()), e);
            throw e;
        }

        long t2 = System.nanoTime();//收到响应的时间

        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);

        LogUtils.i(TAG, String.format("<-- END %s [%d]接收响应: %s %n响应码：%d %n耗时：%.1fms, from %s %nCacheControl:%s %nHeaders：[%n%s] %n返回数据:%s",
                request.method(),
                request.hashCode(),
                response.request().url(),
                response.code(),
                (t2 - t1) / 1e6d,
                response.cacheResponse() != null ? "cache" : "network",
                response.cacheControl(),
                response.headers(),
                responseBody.string()));

        return response;
    }

    public static String getRequestBodyString(Request request) {
        if (request == null || request.body() == null) {
            return null;
        }
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
