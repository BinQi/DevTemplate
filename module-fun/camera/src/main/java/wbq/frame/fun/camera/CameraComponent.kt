package wbq.frame.`fun`.camera

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import wbq.frame.base.router.Component
import wbq.frame.base.router.Module
import wbq.frame.base.router.PathType

/**
 *
 * @author jerry
 * @created 2020/11/5 11:10
 */
@Route(path = Module.fun_camera + PathType.component)
class CameraComponent : Component() {
    override fun onCreate(context: Context) {
        println("CameraComponent onCreate")
    }

    override fun onDestroy(context: Context) {
        println("CameraComponent onDestroy")
    }

    override fun process(): Array<String>? {
        return arrayOf("abcdefg")
    }
}