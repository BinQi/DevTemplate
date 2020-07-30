package wbq.frame.base

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import wbq.frame.base.app.AppFactory
import wbq.frame.base.app.CommonApp
import wbq.frame.util.ProcessUtil
import wbq.frame.util.thread.AppExecutors
import wbq.frame.util.time.TimingLogger

/**
 * Created by Jerry on 2020/5/19 17:10
 */
open abstract class GlobalApplication : Application() {

    private lateinit var mApp: CommonApp
    private var mTimingLogger: TimingLogger? = null

    companion object {
        private lateinit var globalApplication: GlobalApplication

        fun getApplication(): GlobalApplication {
            return globalApplication
        }

        fun getContext(): Context {
            return getApplication()
        }
    }

    abstract fun getAppFactory(): AppFactory

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        globalApplication = this
    }

    override fun onCreate() {
        super.onCreate()
        mTimingLogger = TimingLogger(BuildConfig.DEBUG, this::class.java.name, "GlobalApplication-onCreate")
        mApp = getAppFactory()?.create(ProcessUtil.getCurrentProcessName(this))
        mTimingLogger?.addSplit("createApp")

        mApp?.setTimingLogger(mTimingLogger)
        AppExecutors.threadPool.execute(Runnable { mApp?.asyncOnCreate() })
        mApp?.onCreate()

        mTimingLogger?.dumpToLog()
        mTimingLogger = null
        mApp?.setTimingLogger(null)
    }

    override fun onTerminate() {
        super.onTerminate()
        mApp?.onTerminate()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mApp?.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mApp?.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        mApp?.onTrimMemory(level)
    }
}