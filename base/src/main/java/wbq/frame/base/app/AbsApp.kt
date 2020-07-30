package wbq.frame.base.app

import android.app.Application
import android.content.res.Configuration

/**
 * Created by Jerry on 2020/5/20 18:36
 */
abstract class AbsApp {
    /**
     * [Application.onCreate]
     */
    abstract fun onCreate()

    /**
     * executed in another thread started when [Application.onCreate] called
     */
    abstract fun asyncOnCreate()

    /**
     * [Application.onTerminate]
     */
    abstract fun onTerminate()

    /**
     * [Application.onConfigurationChanged]
     */
    abstract fun onConfigurationChanged(newConfig: Configuration?)

    /**
     * [Application.onLowMemory]
     */
    abstract fun onLowMemory()

    /**
     * [Application.onTrimMemory]
     */
    abstract fun onTrimMemory(level: Int)
}