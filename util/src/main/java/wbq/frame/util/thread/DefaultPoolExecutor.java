package wbq.frame.util.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import wbq.frame.util.device.CpuManager;

/**
 * Created by Jerry on 2020-03-25 19:23
 */
public class DefaultPoolExecutor extends ThreadPoolExecutor implements CancelableExecutor {
    private final static int DEFAULT_CORE_POOL_SIZE = 1;
    private final static int DEFAULT_MAX_POOL_SIZE = 6;
    private final static int KEEP_ALIVE_TIME = 60;

    public DefaultPoolExecutor() {
        super(computeCorePoolSize(), DEFAULT_MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS
                , new LinkedBlockingQueue<>(), new DefaultThreadFactory());
    }

    private static int computeCorePoolSize() {
        int coreSize = CpuManager.getNumCores();
        return Math.min(Math.max(DEFAULT_CORE_POOL_SIZE, coreSize), DEFAULT_MAX_POOL_SIZE);
    }

    @Override
    public void cancel(Runnable r) {
        remove(r);
    }

    /**
     *
     * @author jerrywu
     * @created 2020-03-25 19:44
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "AppThreadExecutors-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
