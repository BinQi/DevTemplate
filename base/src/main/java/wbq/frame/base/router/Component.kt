package wbq.frame.base.router

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 *
 * @author jerry
 * @created 2020/11/4 15:04
 */
abstract class Component : IProvider {
    final override fun init(context: Context?) {
        // do nothing, can not be override
    }
    abstract fun process(): Array<String>?
    abstract fun onCreate(context: Context)
    abstract fun onDestroy(context: Context)
}