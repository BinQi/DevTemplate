package wbq.frame.util.ob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import wbq.frame.util.Preconditions;

/**
 * Created by Jerry on 2020-04-01 18:30
 */
public abstract class EasyRegistry<ListenerType> {

    private static final Object[] UNMODIFIED = new Object[0];
    private final boolean mSticky, mHasPriority;
    private final Comparator<ListenerType> mComparator, mSeachComparator;
    private final List<ListenerType> mListeners = new ArrayList<>();
    private final ReadWriteLock mLock = new ReentrantReadWriteLock();
    private Object[] mCurParams = UNMODIFIED;

    public EasyRegistry() {
        this(false);
    }

    public EasyRegistry(boolean sticky) {
        this(sticky, false, null);
    }

    public EasyRegistry(boolean sticky, boolean hasPriority, Comparator<ListenerType> comparator) {
        mSticky = sticky;
        mHasPriority = hasPriority;
        mComparator = comparator;
        if (mHasPriority) {
            Preconditions.checkNotNull(mComparator, "comparator can not be null when hasPriority is true");
        }
        mSeachComparator = (o1, o2) -> -1 * mComparator.compare(o1, o2);
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
            if (contains(listener)) {
                return;
            }
        } finally {
            mLock.readLock().unlock();
        }

        mLock.writeLock().lock();
        try {
            add(listener);
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
            remove(listener);
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

    private boolean contains(ListenerType listener) {
        return mHasPriority ? Collections.binarySearch(mListeners, listener, mSeachComparator) != -1
                : mListeners.contains(listener);
    }

    private void add(ListenerType listener) {
        if (!mHasPriority) {
            mListeners.add(listener);
            return;
        }
        // from big to small according to priority value
        int index = 0;
        for (; index < mListeners.size(); index++) {
            if (mComparator.compare(listener, mListeners.get(index)) > 0) {
                break;
            }
        }
        mListeners.add(index, listener);
    }

    private void remove(ListenerType listener) {
        if (!mHasPriority) {
            mListeners.remove(listener);
            return;
        }
        final int index = Collections.binarySearch(mListeners, listener, mSeachComparator);
        if (index != -1) {
            mListeners.remove(index);
        }
    }

    protected abstract void inform(ListenerType listener, Object[] params);

}
