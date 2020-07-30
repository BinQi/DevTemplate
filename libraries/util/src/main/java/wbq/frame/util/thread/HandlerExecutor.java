package wbq.frame.util.thread;

import androidx.annotation.NonNull;

/**
 * Created by Jerry on 2020-03-25 18:09
 */
public interface HandlerExecutor extends CancelableExecutor {
    /**
     * post to queue even if the caller thread is the executor thread;
     * {@link #execute(Runnable)} if the caller thread is the executor thread, then execute the task; else post to queue;
     */
    void post(@NonNull Runnable r);

    void postDelayed(@NonNull Runnable r, long delayMillis);

    /**
     * @param r
     * @return whether successfully posted
     */
    boolean executeOnIdleTime(@NonNull Runnable r);
}
