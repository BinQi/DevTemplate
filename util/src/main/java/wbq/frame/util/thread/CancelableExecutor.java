package wbq.frame.util.thread;

import java.util.concurrent.Executor;

/**
 * Created by Jerry on 2020-03-25 19:47
 */
public interface CancelableExecutor extends Executor {
    void cancel(Runnable r);
}
