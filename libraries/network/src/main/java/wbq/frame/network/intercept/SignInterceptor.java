package wbq.frame.network.intercept;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Jerry on 2020-03-30 16:58
 */
public class SignInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request()
//                .newBuilder()
//                .addHeader("Content-Type", "application/json")
//                .build();
//        final String signature = getSignature(request.url().toString(), request.method(), RequestBodyBuilder.toString(request.body()));
//        if (!TextUtils.isEmpty(signature)) {
//            request = request.newBuilder()
//                    .addHeader("X-Signature", signature)
//                    .build();
//        }
//        return chain.proceed(request);
        return chain.proceed(chain.request());
    }

//    private String getSignature(String url, String method, String requestBody) {
//        final Context context = CoreBuyTracker.getInstance().getContext();
//        final String signatureKey = DomainHelper.getInstance(context).getAccessKey();
//
//        final Uri uri = Uri.parse(url);
//        final String relativeUri = uri.getPath();
//        final String queryString = uri.getQuery();
//
//        if (METHOD_GET.equalsIgnoreCase(method)) {
//            return Signature.getSign(relativeUri, signatureKey, queryString, requestBody);
//        } else if (METHOD_POST.equalsIgnoreCase(method)) {
//            return Signature.postSign(relativeUri, signatureKey, queryString, requestBody);
//        } else {
//            return "";
//        }
//    }
}
