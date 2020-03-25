package wbq.frame.util.thread.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;

import androidx.annotation.NonNull;

import wbq.frame.util.reflect.ReflectUtil;
import wbq.frame.util.thread.HandlerExecutor;

/**
 * Created by Jerry on 2020-03-25 18:11
 */
public abstract class HandlerThreadExecutor implements HandlerExecutor {

    protected final @NonNull Handler mHandler;
    protected @NonNull MessageQueue mMessageQueue;

    public HandlerThreadExecutor() {
        mHandler = createHandler();
        final Looper targetLooper = mHandler.getLooper();
        if (targetLooper == Looper.myLooper()) {
            mMessageQueue = Looper.myQueue();
        } else {
            Object queue = null;
            try {
                queue = ReflectUtil.getValue(targetLooper, "mQueue");
            } catch (Throwable var3) {
                var3.printStackTrace();
            }
            if (queue instanceof MessageQueue) {
                mMessageQueue = (MessageQueue)queue;
            } else {
                post(() -> mMessageQueue = Looper.myQueue());
            }
        }
    }

    @Override
    public void execute(Runnable command) {
        if (isTargetThread()) {
            command.run();
        } else {
            mHandler.post(command);
        }
    }

    @Override
    public void post(@NonNull Runnable r) {
        mHandler.post(r);
    }

    @Override
    public void postDelayed(@NonNull Runnable r, long delayMillis) {
        mHandler.postDelayed(r, delayMillis);
    }

    @Override
    public boolean executeOnIdleTime(@NonNull final Runnable r) {
        if (null == mMessageQueue) {
            return false;
        }
        MessageQueue.IdleHandler handler = () -> {
            r.run();
            return false;
        };
        mMessageQueue.addIdleHandler(handler);
        return true;
    }

    @Override
    public void cancel(Runnable r) {
        mHandler.removeCallbacksAndMessages(r);
    }

    protected final boolean isTargetThread() {
        return Looper.myLooper() == mHandler.getLooper();
    }

    protected abstract Handler createHandler();
}
