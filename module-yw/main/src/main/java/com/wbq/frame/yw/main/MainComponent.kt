package com.wbq.frame.yw.main

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import wbq.frame.base.GlobalApplication
import wbq.frame.base.router.Component
import wbq.frame.base.router.Module
import wbq.frame.base.router.PathType

/**
 *
 * @author jerry
 * @created 2020/11/4 14:59
 */
@Route(path = Module.yw_main + PathType.component)
class MainComponent : Component() {
    override fun process(): Array<String>? {
        return arrayOf(GlobalApplication.getContext().packageName)
    }

    override fun onCreate(context: Context) {
        println("MainComponent onCreate")
    }

    override fun onDestroy(context: Context) {
        println("MainComponent onDestroy")
    }
}