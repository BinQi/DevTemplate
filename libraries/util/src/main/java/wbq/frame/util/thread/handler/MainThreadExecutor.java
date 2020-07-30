package wbq.frame.util.thread.handler;

import android.os.Handler;
import android.os.Looper;

/**
 * Android Main Thread Executor
 * Created by Jerry on 2020-03-25 18:23
 */
public class MainThreadExecutor extends HandlerThreadExecutor {

    @Override
    protected Handler createHandler() {
        return new Handler(Looper.getMainLooper());
    }
}
