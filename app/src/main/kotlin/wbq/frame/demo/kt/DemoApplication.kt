package wbq.frame.demo.kt

import android.content.Intent
import com.wbq.frame.yw.main.MainActivity
import wbq.frame.base.GlobalApplication
import wbq.frame.base.app.AppFactory
import wbq.frame.util.thread.AppExecutors

/**
 *
 * @author jerry
 * @created 2020/11/3 14:14
 */
class DemoApplication : GlobalApplication() {
    override fun getAppFactory(): AppFactory? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        AppExecutors.mainThread.postDelayed(Runnable {
            Runtime.getRuntime().exec("input tap " + 500 + " " + 600 + " \n");
            System.out.println("ActivityTaskManager:DemoApplicationDemoApplicationDemoApplicationDemoApplicationDemoApplicationDemoApplication")
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }, 15000)

    }
}