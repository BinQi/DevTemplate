package wbq.frame.arch.mvvm;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import wbq.frame.util.log.LogUtils;
import wbq.frame.util.thread.AppThreadExecutors;

/**
 * Created by Jerry on 2019-09-11 16:09
 */
public class ViewController {

    private static final String TAG = "ViewController";
    private final FragmentActivity mFragmentActivity;
    private final Fragment mFragment;
    private final ViewController mParent;
    private final LifecycleDispatcher mLifecycleDispatcher;

    public ViewController(@NonNull FragmentActivity fragmentActivity) {
        this(fragmentActivity, null, null);
    }

    public ViewController(@NonNull Fragment fragment) {
        this(null, fragment, null);
    }

    public ViewController(@NonNull ViewController parent) {
        this(null, null, parent);
    }

    private ViewController(FragmentActivity fragmentActivity, Fragment fragment, ViewController parent) {
        mFragmentActivity = fragmentActivity;
        mFragment = fragment;
        mParent = parent;
        mLifecycleDispatcher = new LifecycleDispatcher(this);
    }

    public final <T extends ViewModel> T getViewModel(@Nullable Class<T> clazz) {
        return getViewModel(clazz, null);
    }

    public final <T extends ViewModel> T getViewModel(@Nullable Class<T> clazz, @Nullable ViewModelProvider.Factory factory) {
        return getViewModelProvider(factory).get(clazz);
    }

    public final LifecycleOwner getLifecycleOwner() {
        if (mFragmentActivity != null) {
            return mFragmentActivity;
        } else if (mFragment != null) {
            return mFragment;
        } else {
            return mParent.getLifecycleOwner();
        }
    }

    protected Fragment getFragment() {
        final LifecycleOwner lifecycleOwner = getLifecycleOwner();
        return lifecycleOwner instanceof Fragment ? (Fragment) lifecycleOwner : null;
    }

    protected Activity getActivity() {
        final LifecycleOwner lifecycleOwner = getLifecycleOwner();
        if (lifecycleOwner instanceof Activity) {
            return (Activity) lifecycleOwner;
        } else if (lifecycleOwner instanceof Fragment) {
            return ((Fragment) lifecycleOwner).getActivity();
        } else {
            return null;
        }
    }

    protected final ViewModelProvider getViewModelProvider(@Nullable ViewModelProvider.Factory factory) {
        LifecycleOwner lifecycleOwner = getLifecycleOwner();
        if (lifecycleOwner instanceof FragmentActivity) {
            return new ViewModelProvider((FragmentActivity) lifecycleOwner, factory);
        } else {
            return new ViewModelProvider((Fragment) lifecycleOwner, factory);
        }
    }

    public ViewController getParent() {
        return mParent;
    }

    protected void onCreate() {}

    protected void onStart() {}

    protected void onResume() {}

    protected void onPause() {}

    protected void onStop() {}

    protected void onDestroy() {
    }

    /**
     *
     * @author jerrywu
     * @created 2019-09-11 17:15
     */
    private final static class LifecycleDispatcher implements LifecycleObserver {

        private final ViewController mVC;
        private void log(String callback) {
            if (LogUtils.isShowLog()) {
                LogUtils.i(TAG, mVC.getClass().getSimpleName() + "-" + callback);
            }
        }

        LifecycleDispatcher(@NonNull ViewController vc) {
            mVC = vc;
            AppThreadExecutors.mainThread.execute(() -> registerLifecycle());
        }

        private void registerLifecycle() {
            final LifecycleOwner lifecycleOwner = mVC.getLifecycleOwner();
            if (lifecycleOwner != null) {
                final Lifecycle lifecycle = lifecycleOwner.getLifecycle();
                lifecycle.addObserver(this);
            }
        }

        private void unregisterLifecycle() {
            final LifecycleOwner lifecycleOwner = mVC.getLifecycleOwner();
            if (lifecycleOwner != null) {
                final Lifecycle lifecycle = lifecycleOwner.getLifecycle();
                lifecycle.removeObserver(this);
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate() {
            mVC.onCreate();
            log("onCreate");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onStart() {
            mVC.onStart();
            log("onStart");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            mVC.onResume();
            log("onResume");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
            mVC.onPause();
            log("onPause");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onStop() {
            mVC.onStop();
            log("onStop");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            mVC.onDestroy();
            log("onDestroy");
            unregisterLifecycle();
        }
    }
}
