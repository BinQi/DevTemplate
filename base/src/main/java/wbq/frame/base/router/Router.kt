package wbq.frame.base.router

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter

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

    /**
     * 路由跳转
     *
     * @param module 模块
     * @param path   路径
     */
    fun navigate(@Module module: String, @Path path: String) {
        build(module, path)
                .navigation()
    }

    /**
     * 带参数的路由跳转
     *
     * @param module 模块
     * @param path   路径
     * @param params 参数
     */
    fun navigate(@Module module: String, @Path path: String, params: Bundle?) {
        build(module, path)
                .with(params)
                .navigation()
    }

    /**
     * 带requestCode的路由跳转
     *
     * @param module      模块
     * @param path        路径
     * @param activity    Activity实例
     * @param requestCode 请求码
     */
    fun navigate(@Module module: String, @Path path: String,
                 activity: Activity?, requestCode: Int) {
        build(module, path)
                .navigation(activity, requestCode)
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
    fun navigate(@Module module: String, @Path path: String, params: Bundle?,
                 activity: Activity?, requestCode: Int) {
        build(module, path)
                .with(params)
                .navigation(activity, requestCode)
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