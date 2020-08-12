package wbq.frame.util.ob;

import androidx.annotation.NonNull;

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
public class EasyRegistry<ListenerType, Params> {

    private final boolean mSticky, mHasPriority;
    private final @NonNull Informer mInformer;
    private final Comparator<ListenerType> mComparator, mSeachComparator;
    private final List<ListenerType> mListeners = new ArrayList<>();
    private final ReadWriteLock mLock = new ReentrantReadWriteLock();
    private Params mCurParams;
    private boolean mParamModified = false;

    public EasyRegistry(@NonNull Informer<ListenerType, Params> informer) {
        this(false, informer);
    }

    public EasyRegistry(boolean sticky, @NonNull Informer<ListenerType, Params> informer) {
        this(sticky, informer, false, null);
    }

    public EasyRegistry(boolean sticky, @NonNull Informer<ListenerType, Params> informer, boolean hasPriority, Comparator<ListenerType> comparator) {
        mSticky = sticky;
        mHasPriority = hasPriority;
        mComparator = comparator;
        Preconditions.checkNotNull(informer, "informer can not be null");
        if (mHasPriority) {
            Preconditions.checkNotNull(mComparator, "comparator can not be null when hasPriority is true");
        }
        mSeachComparator = (o1, o2) -> -1 * mComparator.compare(o1, o2);
        mInformer = informer;
    }

    public void setCurParams(Params curParams) {
        this.mCurParams = curParams;
        if (!mParamModified) {
            mParamModified = true;
        }
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
            if (mSticky && mParamModified) {
                mInformer.inform(listener, mCurParams);
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

    public final void clear() {
        mLock.writeLock().lock();
        try {
            mListeners.clear();
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public final void notifyListeners(Params params) {
        mLock.readLock().lock();
        try {
            setCurParams(params);
            for (ListenerType l : mListeners) {
                mInformer.inform(l, params);
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

    /**
     * @author jerry
     * @created 2020/8/4 16:28
     */
    public interface Informer<ListenerType, Params> {
        void inform(ListenerType listener, Params params);
    }
}
