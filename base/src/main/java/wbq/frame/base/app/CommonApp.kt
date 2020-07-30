package wbq.frame.base.app

import android.content.res.Configuration
import wbq.frame.util.time.TimingLogger

/**
 * Created by Jerry on 2020/5/20 18:38
 */
open class CommonApp : AbsApp() {
    private var mTimingLogger: TimingLogger? = null

    /**
     * [AbsApp.onCreate]
     */
    override fun onCreate() {}

    /**
     * [AbsApp.onCreate]
     */
    override fun asyncOnCreate() {}

    /**
     * [AbsApp.onCreate]
     */
    override fun onTerminate() {}

    /**
     * [AbsApp.onCreate]
     */
    override fun onConfigurationChanged(newConfig: Configuration?) {}

    /**
     * [AbsApp.onCreate]
     */
    override fun onLowMemory() {}

    /**
     * [AbsApp.onCreate]
     */
    override fun onTrimMemory(level: Int) {}

    /**
     * 添加计时时间点
     * @param splitLabel 时间点标签
     */
    protected fun addTimeSplit(splitLabel: String?) {
        if (mTimingLogger != null) {
            mTimingLogger!!.addSplit(splitLabel)
        }
    }

    internal fun setTimingLogger(timingLogger: TimingLogger?) {
        mTimingLogger = timingLogger
    }
}