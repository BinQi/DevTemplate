package wbq.frame.demo.kt

import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.zip.ZipFile

/**
 *
 * @author jerry
 * @created 2020/8/25 11:47
 */
object ChannelUtil {
    private const val PREFIX = "META-INF/channel"

    fun getChannel(context: Context) : String? {
        val sourceDir = context.applicationInfo.sourceDir
        val zipFile: ZipFile
        try {
            zipFile = ZipFile(sourceDir)
            val entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                val name = entries.nextElement().name
                Log.i("wbq", "Zip file entry - $name")
                if (name.startsWith(PREFIX)) {
                    return name.replace(PREFIX, "")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}