package wbq.frame.util.thread;

import wbq.frame.util.thread.handler.MainThreadExecutor;

/**
 * Created by Jerry on 2020-03-25 17:58
 */
public class AppThreadExecutors {
    /**
     * Android main thread executor
     */
    public static final HandlerExecutor mainThread = new MainThreadExecutor();
    /**
     * Android async thread executor
     */
    public static final HandlerExecutor asyncThread = new MainThreadExecutor();
    /**
     * Thread pool
     */
    public static final CancelableExecutor threadPool = new DefaultPoolExecutor();

//  scheduledThreadPool; TODO
}
