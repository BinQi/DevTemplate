package wbq.frame.base.router

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.AnimRes
import com.alibaba.android.arouter.core.AccessMediator
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter
import wbq.frame.util.ProcessUtil

/**
 * 主要提供页面跳转等功能
 * 目前使用了ARouter来做模块间跳转，具体资料可参考：https://github.com/alibaba/ARouter
 */
object Router {
    private var sInited = false
    private val sLock = java.lang.Object()
    /**
     * 初始化
     * 首次初始化比较耗时，大概需要1秒左右，所以为了不影响启动速度，最好延迟初始化，或者用到的时候再初始化
     *
     * @param app
     * @param isDebug 是否debug(true：打开Log)
     */
    fun init(app: Application?, isDebug: Boolean) {
        if (sInited) {
            return
        }
        synchronized(sLock) {
            if (isDebug) {
                ARouter.openLog()
                ARouter.openDebug()
            }
            ARouter.init(app)
            sInited = true
            sLock.notifyAll()
        }
    }

    fun initComponent(context: Context) {
        val curProcessName = ProcessUtil.getCurrentProcessName(context)
        getGroups()?.forEach { group: String ->
            val component = ARouter.getInstance().build("/$group${PathType.component}").navigation() as Component?
            component?.apply {
                val process = process()
                if (null == process || process.contains(curProcessName)) {
                    onCreate(context)
                }
            }
        }
    }

    /**
     * 带参数和requestCode的路由跳转
     *
     * @param module      模块
     * @param path        路径
     * @param params      参数
     * @param activity    Activity实例
     * @param requestCode 请求码
     */
    fun navigate(@Module module: String, @Path path: String, params: Bundle? = null,
                 activity: Activity? = null, requestCode: Int = -1, @AnimRes enterAnim: Int = -1, @AnimRes exitAnim: Int = -1) {
        build(module, path).apply {
            with(params)
            withTransition(enterAnim, exitAnim)
            if (activity != null) {
                navigation(activity, requestCode)
            } else {
                navigation()
            }
        }
    }

    /**
     * 获取服务接口
     *
     * @param interfaceClass
     * @param <T>
     * @return
    </T> */
    fun <T> getInterface(interfaceClass: Class<out T>?): T {
        checkInitWait()
        return ARouter.getInstance().navigation(interfaceClass)
    }

    /**
     * 获取Postcard
     *
     * @param module 模块
     * @param path   路径
     * @return
     */
    fun build(@Module module: String, @Path path: String): Postcard {
        checkInitWait()
        return ARouter.getInstance().build(module + path)
    }

    private fun getGroups(): Set<String>? {
        checkInitWait()
        return AccessMediator.getGroups()
    }

    private fun checkInitWait(): Boolean {
        if (sInited) {
            return true
        }
        synchronized(sLock) {
            if (!sInited) {
                try {
                    sLock.wait()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        return sInited
    }
}