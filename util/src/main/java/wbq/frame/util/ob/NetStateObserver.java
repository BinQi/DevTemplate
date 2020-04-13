package wbq.frame.util.ob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import wbq.frame.util.device.NetUtil;

/**
 * Created by Jerry on 2020-04-03 18:26
 */
public class NetStateObserver extends EasyRegistry<NetStateObserver.OnNetStateChangeListener> {

    private static volatile NetStateObserver sInstance;
    private final NetStateReceiver mReceiver;

    private NetStateObserver(Context context) {
        super();

        mReceiver = new NetStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filter);
    }

    public static NetStateObserver getInstance(Context context) {
        if (null == sInstance) {
            synchronized (NetStateObserver.class) {
                if (null == sInstance) {
                    sInstance = new NetStateObserver(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    protected void inform(OnNetStateChangeListener listener, Object[] params) {
        listener.onNetStateChanged((Boolean) params[0]);
    }

    static class NetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == context || null == intent || null == sInstance) {
                return;
            }
            sInstance.notifyListeners(NetUtil.isNetWorkAvailable(context));
        }
    }

    public interface OnNetStateChangeListener {
        void onNetStateChanged(boolean isOk);
    }
}
