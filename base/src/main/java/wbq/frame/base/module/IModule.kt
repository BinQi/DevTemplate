package wbq.frame.base.module

import android.content.Context

/**
 *
 * @author jerry
 * @created 2020/7/27 15:35
 */
interface IModule {
    fun onCreate(context: Context)
    fun onInitialize(context: Context)
    fun onDestroy()
}