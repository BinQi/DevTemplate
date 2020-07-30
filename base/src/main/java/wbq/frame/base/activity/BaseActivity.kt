package wbq.frame.base.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import wbq.frame.util.StatusBarUtil

/**
 *
 * @author jerry
 * @created 2020/7/23 18:04
 */
open class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initScreen()
        initStatusBar()
        initBottomNavigationBar()
    }

     private fun initScreen() {
        if (!isFullScreen()) {
            return
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //        // 这个方法有弊端：目前测试5.1和4.4会在Activity进场时，有一个明显的状态栏消失过程
//        if (Build.VERSION.SDK_INT >= 19) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        } else {
//            getWindow().getDecorView().setSystemUiVisibility(View.GONE);
//        }
    }

    /**
     * 是否全屏模式
     * 该模式下会隐藏状态栏和导航栏
     *
     * @return
     */
    open fun isFullScreen(): Boolean {
        return false
    }

    /**
     * 初始化状态栏
     */
    private fun initStatusBar() {
        var color = -1
        if (isFullScreen() || getStatusBarColor().also { color = it } == -1) {
            return
        }
        StatusBarUtil.setColor(this, color, isContentFillStatusBar())
    }

    /**
     * Activity的内容是否延伸到状态栏
     * 默认为不延伸
     *
     * @return
     */
    open fun isContentFillStatusBar(): Boolean {
        return false
    }

    /**
     * 获取状态栏背景色
     *
     * @return
     */
    @ColorInt
    open fun getStatusBarColor(): Int {
        return -1
    }

    /**
     * 初始化虚拟键
     */
    protected open fun initBottomNavigationBar() {
        if (isHideBottomNavigationBar() || isFullScreen()) {
            if (Build.VERSION.SDK_INT >= 19) { // Android 4.4及以上的处理方式
                val decorView = window.decorView
                decorView.systemUiVisibility = (decorView.systemUiVisibility
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
            //            else {
//                // Android 4.4以下的处理方式
//                // 亲测，该方法无效
//                // 使用Android 4.4及以上的处理方式是可以隐藏，但是点击屏幕，虚拟键又重新出现，而且不会自动消失，且这次点击不会传递到下面的View
//                getWindow().getDecorView().setSystemUiVisibility(View.GONE);
//            }
        }
    }

    /**
     * 是否隐藏底部的控制栏
     *
     * @return
     */
    open fun isHideBottomNavigationBar(): Boolean {
        return false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // getWindow().getDecorView().setSystemUiVisibility()会在失去焦点后失效，所以获得焦点后需要再重新设置一下
        if (hasFocus) {
            initBottomNavigationBar()
        }
    }
}