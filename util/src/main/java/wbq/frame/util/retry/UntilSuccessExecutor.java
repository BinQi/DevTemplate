package wbq.frame.util.retry;

import java.util.concurrent.atomic.AtomicBoolean;

import wbq.frame.util.thread.AppThreadExecutors;

/**
 * Created by Jerry on 2020-03-31 19:25
 */
public class UntilSuccessExecutor<Result> {

    protected final InformerImpl mInformer;
    private final AtomicBoolean mIsRunning;
    private Task<Result> mTask;
    private Callback<Result> mCallback;
    private int mRetryNum;
    private int mMaxRetryNum = Integer.MAX_VALUE;
    private long mRetryDelay = Integer.MAX_VALUE;

    public UntilSuccessExecutor() {
        mInformer = new InformerImpl();
        mIsRunning = new AtomicBoolean(false);
    }

    /**
     * @param maxRetryNum 最大重试次数；
     */
    public void setMaxRetryNum(int maxRetryNum) {
        this.mMaxRetryNum = Math.max(maxRetryNum, 0);
    }

    /**
     * @param delayMills 失败时重试的延迟时间；Integer.MAX_VALUE means not set retry delay
     */
    public void setRetryDealy(long delayMills) {
        this.mRetryDelay = delayMills;
    }

    /**
     * @param task
     * @return 是否成功提交任务
     */
    public boolean execute(Task<Result> task) {
        if (mIsRunning.compareAndSet(false, true)) {
            mTask = task;
            mRetryNum = -1;
            initTask();
            return true;
        }
        return false;
    }

    protected void initTask() {
        mInformer.runTask();
    }

    protected boolean isOk2Retry() {
        return true;
    }

    /**
     * 提交的任务执行完成（已成功或已达到最大重试次数）
     */
    protected void onTaskFinish(Task<Result> task) {

    }

    public void setCallback(Callback<Result> callback) {
        mCallback = callback;
    }

    class InformerImpl implements Informer<Result>, Runnable {

        AtomicBoolean mTaskRunning = new AtomicBoolean(false);

        @Override
        public void inform(boolean success, Result result) {
            mTaskRunning.compareAndSet(true, false);
            final boolean isFinished = !canRetry();
            if (!isFinished && !success) {
                final long delayMills = mRetryDelay;
                if (canRetry() && delayMills != Integer.MAX_VALUE && delayMills > -1) {
                    AppThreadExecutors.asyncThread.cancel(this);
                    AppThreadExecutors.asyncThread.postDelayed(this, delayMills);
                }
                return;
            }
            if (mIsRunning.compareAndSet(true, false)) {
                onTaskFinish(mTask);
                if (mCallback != null) {
                    mCallback.onFinish(result);
                }
            }
        }

        @Override
        public void run() {
            // retry time reached
            if (isOk2Retry()) {
                runTask();
            }
        }

        private boolean canRetry() {
            return mRetryNum < mMaxRetryNum;
        }

        void runTask() {
            if (!mIsRunning.get() || !canRetry()) {
                return;
            }
            if (mTaskRunning.compareAndSet(false, true)) {
                mRetryNum++;
                mTask.run(mInformer);
            }
        }
    }

    public interface Callback<Result> {
        void onFinish(Result result);
    }

    public interface Informer<Result> {
        void inform(boolean success, Result result);
    }

    public interface Task<Result> {
        void run(Informer<Result> informer);
    }
}
