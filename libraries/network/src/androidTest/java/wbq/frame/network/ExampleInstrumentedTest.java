package wbq.frame.network;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.RCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wbq.frame.network.cache.RequestCache;
import wbq.frame.util.log.LogUtils;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RCache.context = appContext;
        LogUtils.setShowLog(true);

        DemoRequest request = new DemoRequest();
        request.setRequestCache(
                RequestCache.forceValidCache("baidu", 1, TimeUnit.HOURS)
//                RequestCache.forceNetworkOnly("baidu")
        );
        final long tt = 1;
        try {
            final Response<String> response = request.create()
                    .homePage(tt).execute();
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            request.setRequestCache(
                    RequestCache.forceValidCacheOnly("baidu", 1, TimeUnit.HOURS)
//                RequestCache.forceNetworkOnly("baidu")
            );
            final Response<String> response = request.create()
                    .homePage(tt).execute();
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        assertEquals("wbq.frame.network.test", appContext.getPackageName());
    }
}
