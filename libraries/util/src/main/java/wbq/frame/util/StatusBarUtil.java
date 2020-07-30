package wbq.frame.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StatusBarUtil {

    // 状态栏高度缓存
    private static int sStatusBarHeight = -1;

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColor(AppCompatActivity activity, @ColorInt int color, boolean isContentFillStatusBar) {
        if (activity == null) {
            return;
        }

        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* Android5.0或以上处理方式 */
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (isContentFillStatusBar) {
                window.getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            if (isMeizuFlymeOS()) {
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                if (isOpaqueColor(color)) {
                    /* 不透明的颜色值，需要设置为fe透明度的颜色值才能设置状态栏背景色 */
                    color |= 0xfe000000;
                    color &= 0xfeffffff;
                }
                window.setStatusBarColor(color);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            /* Android4.4处理方式 */
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar == null) {
                // TODO
//                setRootView(activity);
//                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//                View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
//                if (fakeStatusBarView != null) {
//                    if (fakeStatusBarView.getVisibility() == View.GONE) {
//                        fakeStatusBarView.setVisibility(View.VISIBLE);
//                    }
//                    fakeStatusBarView.setBackgroundColor(color);
//                } else {
//                    decorView.addView(createStatusBarView(activity, color, 0));
//                }
            } else {
                if (isContentFillStatusBar) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                View customView = activity.getSupportActionBar().getCustomView();
                if (customView != null) {
                    ((View) customView.getParent()).setFitsSystemWindows(true);
                }
            }
        }
//        else {
//            /* Android4.4以下没有处理方法 */
//        }
    }

//    /**
//     * 生成一个和状态栏大小相同的半透明矩形条
//     *
//     * @param activity 需要设置的activity
//     * @param color    状态栏颜色值
//     * @param alpha    透明值
//     * @return 状态栏矩形条
//     */
//    private static View createStatusBarView(Activity activity, @ColorInt int color, int alpha) {
//        // 绘制一个和状态栏一样高的矩形
//        View statusBarView = new View(activity);
//        FrameLayout.LayoutParams params =
//                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity), Gravity.TOP);
//        statusBarView.setLayoutParams(params);
//        statusBarView.setBackgroundColor(color);
//        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
//        return statusBarView;
//    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static final int getStatusBarHeight(Context context) {
        if (sStatusBarHeight > 0) {
            return sStatusBarHeight;
        }

        getStatusBarHeightByReflection(context);
        return sStatusBarHeight;
    }

    /**
     * 获取状态栏高度
     * 通过反射
     *
     * @param context
     * @return
     */
    private static final int getStatusBarHeightByReflection(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            if (statusBarHeight > sStatusBarHeight) {
                sStatusBarHeight = statusBarHeight;
            }
            return sStatusBarHeight;
        }
        return -1;
    }

//    /**
//     * 设置根布局参数
//     */
//    private static void setRootView(Activity activity) {
//        ViewGroup parent = activity.findViewById(android.R.id.content);
//        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
//            View childView = parent.getChildAt(i);
//            if (childView instanceof ViewGroup) {
//                childView.setFitsSystemWindows(true);
//                ((ViewGroup) childView).setClipToPadding(true);
//            }
//        }
//    }

    /**
     * 判断是魅族操作系统
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/18,9:43
     * <h3>UpdateTime</h3> 2016/6/18,9:43
     * <h3>CreateAuthor</h3> vera
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @return true 为魅族系统 否则不是
     */
    public static boolean isMeizuFlymeOS() {
        /* 获取魅族系统操作版本标识*/
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id", "");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            return false;
        } else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取系统属性
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/18,9:35
     * <h3>UpdateTime</h3> 2016/6/18,9:35
     * <h3>CreateAuthor</h3> vera
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @param key          ro.build.display.id
     * @param defaultValue 默认值
     * @return 系统操作版本标识
     */
    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * 是否不透明的颜色值
     *
     * @param color
     * @return
     */
    private static boolean isOpaqueColor(@ColorInt int color) {
        return (color != 0 && (color & 0xff000000) == 0) || (color >>> 24) >= 255;
    }

}
