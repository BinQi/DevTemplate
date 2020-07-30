package wbq.frame.util.thread;

import wbq.frame.util.thread.handler.AsyncThreadExecutor;
import wbq.frame.util.thread.handler.MainThreadExecutor;

/**
 * Created by Jerry on 2020-03-25 17:58
 */
public final class AppExecutors {
    private AppExecutors() {}

    private static final DefaultPoolExecutor defaultPoolExecutor = new DefaultPoolExecutor();
    /**
     * Android main thread executor
     */
    public static final HandlerExecutor mainThread = new MainThreadExecutor();
    /**
     * Android async thread executor
     */
    public static final HandlerExecutor asyncThread = new AsyncThreadExecutor();
    /**
     * Thread pool
     */
    public static final CancelableExecutor threadPool = defaultPoolExecutor;
    /**
     * FutureExecutor
     */
    public static final FutureExecutor futureExecutor = defaultPoolExecutor;

//  scheduledThreadPool; TODO
}
