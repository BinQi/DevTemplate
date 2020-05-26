package wbq.frame.arch.mvp;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Usage:
 * <pre>{@code
 * interface IDemoPresenter extends IPresenter {
 *     ...
 * }
 * interface IDemoView extends IView<DemoPresenter> {
 *     ...
 * }
 * class DemoPresenter extends BasePresenter<IDemoView> implements IDemoPresenter {
 *     ...
 * }</pre>
 * Created by Jerry on 2019/4/9 6:11 PM
 */
public abstract class BasePresenter<T extends IView> {
    private WeakReference<T> mViewRef;
    protected final T mViewImpl;

    public BasePresenter(@NonNull T view) {
        view.setPresenter(this);
        mViewRef = new WeakReference<>(view);
        mViewImpl = (T) Proxy.newProxyInstance(view.getClass().getClassLoader(), view.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                T obj = mViewRef.get();
                return obj != null ? method.invoke(obj, args) : null;
            }
        });
        ((IPresenter) this).initiate();
    }

    protected final T getView() {
        return mViewImpl;
    }
}
