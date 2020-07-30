package wbq.frame.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;

/**
 * Created by Jerry on 2020-01-06 15:20
 */
public class ToastUtil {
    private static volatile Toast sToast;

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    public @interface Duration {
    }

    @UiThread
    public static void show(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    @UiThread
    public static void show(Context context, String msg, @Duration int duration) {
        showToast(context, msg, duration);
    }

    @UiThread
    public static void show(Context context, @StringRes int resId) {
        show(context, resId, Toast.LENGTH_SHORT);
    }

    @UiThread
    public static void show(Context context, @StringRes int resId, @Duration int duration) {
        showToast(context, context.getResources().getString(resId), duration);
    }

    private static void showToast(Context context, final String msg, final @Duration int duration) {
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = Toast.makeText(context.getApplicationContext(), msg, duration);
        sToast.show();
    }
}
