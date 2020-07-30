package wbq.frame.util.thread.handler;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Jerry on 2020-03-25 18:33
 */
public class AsyncThreadExecutor extends HandlerThreadExecutor {

    private HandlerThread mThread;

    @Override
    protected Handler createHandler() {
        return new Handler(getThread().getLooper());
    }

    private HandlerThread getThread() {
        if (null == mThread) {
            mThread = new HandlerThread("AppThreadExecutors:AsyncThreadExecutor");
            mThread.start();
        }
        return mThread;
    }
}
