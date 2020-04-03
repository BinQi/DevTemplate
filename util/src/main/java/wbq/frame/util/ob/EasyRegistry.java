package wbq.frame.util.ob;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Jerry on 2020-04-01 18:30
 */
public abstract class EasyRegistry<ListenerType> {

    private static final Object[] UNMODIFIED = new Object[0];
    private final boolean mSticky;
    private final List<ListenerType> mListeners = new ArrayList<>();
    private final ReadWriteLock mLock = new ReentrantReadWriteLock();
    private Object[] mCurParams = UNMODIFIED;

    public EasyRegistry(boolean sticky) {
        mSticky = sticky;
    }

    public void setCurParams(Object[] curParams) {
        this.mCurParams = curParams;
    }

    public final void register(ListenerType listener) {
        if (null == listener) {
            return;
        }
        mLock.readLock().lock();
        try {
            if (mListeners.contains(listener)) {
                return;
            }
        } finally {
            mLock.readLock().unlock();
        }

        mLock.writeLock().lock();
        try {
            mListeners.add(listener);
            if (mSticky && mCurParams != UNMODIFIED) {
                inform(listener, mCurParams);
            }
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public final void unregister(ListenerType listener) {
        if (null == listener) {
            return;
        }
        mLock.writeLock().lock();
        try {
            mListeners.remove(listener);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public final void notifyListeners(Object... params) {
        mLock.readLock().lock();
        try {
            mCurParams = params;
            for (ListenerType l : mListeners) {
                inform(l, params);
            }
        } finally {
            mLock.readLock().unlock();
        }
    }

    protected abstract void inform(ListenerType listener, Object[] params);

}
