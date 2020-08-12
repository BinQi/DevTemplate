package wbq.frame.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import wbq.frame.base.activity.BaseActivity
import wbq.frame.util.GlideApp
import java.io.File

/**
 *
 * @author jerry
 * @created 2020/8/12 15:52
 */
class ResultActivity : BaseActivity() {
    val imageView: ImageView by lazy {
        ImageView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(imageView)
        GlideApp.with(this)
                .load(File(intent.getStringExtra(PATH)))
                .into(imageView)
    }

    companion object {
        const val PATH = "path"

        fun start(context: Context, imgFilePath: String) {
            val intent = Intent(context, ResultActivity::class.java).apply {
                if (context !is Activity)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra(PATH, imgFilePath)
            context.startActivity(intent)
        }
    }
}