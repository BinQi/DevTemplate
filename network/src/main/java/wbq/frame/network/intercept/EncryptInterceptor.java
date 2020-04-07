package wbq.frame.network.intercept;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 加解密拦截器
 */
public class EncryptInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//
//        String requestBodyStr = RequestBodyBuilder.toString(request.body());
//        String encryptedrequestBodyStr = null;
//        try {
//            encryptedrequestBodyStr = encrypt(DES_KEY, requestBodyStr);
//        } catch (Exception e) {
//            LogUtils.e(TAG, "[EncryptInterceptor#intercept]", e);
//        }
//        if (LogUtils.isShowLog()) {
//            LogUtils.i(TAG, String.format("[EncryptInterceptor#intercept] url: %s %nrequestEncrypted: %s",
//                    request.url().toString(), encryptedrequestBodyStr));
//        }
//        Request newRequest = request.newBuilder()
//                .post(RequestBodyBuilder.fromJsonObject(encryptedrequestBodyStr))
//                .build();
//
//        Response response = null;
//        try {
//            response = chain.proceed(newRequest);
//        } catch (IOException e) {
//            throw e;
//        }
//
//        ResponseBody responseBody = response.body();
//        String responseBodyStr = responseBody.string();
//        String decryptedResponseBodyStr = null;
//        try {
//            decryptedResponseBodyStr = decrypt(responseBodyStr, DES_KEY);
//        } catch (Exception e) {
//            LogUtils.e(TAG, "[EncryptInterceptor#intercept]", e);
//        }
//        if (LogUtils.isShowLog()) {
//            LogUtils.i(TAG, String.format("[EncryptInterceptor#intercept] url: %s 响应码: %d %nencryptedResponse: %s %ndecryptedResponse: %s"
//                    , request.url().toString()
//                    , response != null ? response.code() : -1
//                    , responseBodyStr
//                    , decryptedResponseBodyStr));
//        }
//
//        ResponseBody newResponseBody = ResponseBody.create(responseBody.contentType(), decryptedResponseBodyStr);
//
//        return response.newBuilder().body(newResponseBody).build();
        return chain.proceed(chain.request());
    }

//    static String decrypt(String src, String key)
//            throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
//        DESKeySpec desKey = new DESKeySpec(key.getBytes());
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//        SecretKey securekey = keyFactory.generateSecret(desKey);
//        Cipher cipher = Cipher.getInstance("DES");
//        cipher.init(Cipher.DECRYPT_MODE, securekey);
//        return new String(cipher.doFinal(Base64.decodeBase64(src)), "utf-8");
//    }
//
//    static String encrypt(String key, String data)
//            throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
//        DESKeySpec desKey = new DESKeySpec(key.getBytes());
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//        SecretKey securekey = keyFactory.generateSecret(desKey);
//        Cipher cipher = Cipher.getInstance("DES");
//        cipher.init(Cipher.ENCRYPT_MODE, securekey);
//        return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes("utf-8")));
//    }
}
