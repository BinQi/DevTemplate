package wbq.frame.util.retry;

import android.content.Context;

import wbq.frame.util.device.NetUtil;
import wbq.frame.util.ob.NetStateObserver;

/**
 * Created by Jerry on 2020-04-03 18:33
 */
public class NetStatExecutor extends UntilSuccessExecutor {

    private final Context mContext;
    private final NetStateObserver.OnNetStateChangeListener mListener;

    public NetStatExecutor(Context context) {
        super();
        mContext = context.getApplicationContext();
        mListener = isOk -> {
            if (isOk) {
                mInformer.runTask();
            }
        };
    }

    @Override
    protected void initTask() {
        NetStateObserver.getInstance(mContext).register(mListener);
        if (NetUtil.isNetWorkAvailable(mContext)) {
            mInformer.runTask();
        }
    }

    @Override
    protected boolean isOk2Retry() {
        return NetUtil.isNetWorkAvailable(mContext);
    }

    @Override
    protected void onTaskFinish(Task task) {
        super.onTaskFinish(task);
        NetStateObserver.getInstance(mContext).unregister(mListener);
    }
}
