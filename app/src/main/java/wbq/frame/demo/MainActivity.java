package wbq.frame.demo;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.meituan.android.walle.WalleChannelReader;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import wbq.frame.base.router.Module;
import wbq.frame.base.router.Path;
import wbq.frame.base.router.Router;
import wbq.frame.demo.kt.ChannelUtil;
import wbq.frame.demo.ui.main.SectionsPagerAdapter;
import wbq.frame.util.thread.AppExecutors;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "wbq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            ChannelUtil.INSTANCE.getChannel(MainActivity.this);
            final long startTime = SystemClock.elapsedRealtime();
            final String channel = readChannel(MainActivity.this);
            final long readDur = SystemClock.elapsedRealtime() - startTime;
            final String walleChannel = WalleChannelReader.getChannel(this.getApplicationContext());
            Snackbar.make(view, String.format("Channel read = %s consumeTimeInMills=%d\n WalleChannel = %s", channel, readDur, walleChannel), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

//            Router.INSTANCE.navigate(Module.yw_main, Path.activityMain);
//            final String marketPkg = "com.huawei.appmarket";
//            final String detailClazz = "com.huawei.appgallery.detail.detailbase.common.activity.AppDetailCommentActivity";
//            final String commentActivityClazz = "com.huawei.appgallery.appcomment.ui.CommentActivity";
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName(marketPkg, detailClazz));
//            startActivity(intent);
        });
//        final Context context = getApplicationContext();
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.i(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
//                mockTouch();
//                Intent i = new Intent(context, MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
//            }
//        }, intentFilter);
    }

    static final String PREFIX = "META-INF/channel";
    static String readChannel(Context context) {
        final String dir = context.getApplicationInfo().sourceDir;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(dir);
            final Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final String name = ((ZipEntry) entries.nextElement()).getName();
                if (name.startsWith(PREFIX)) {
                    return name.replace(PREFIX, "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    void mockTouch() {
//        AppExecutors.asyncThread.execute(() -> {
//            Log.i(TAG, "mockTouch() called");
//            Instrumentation inst = new Instrumentation();
//            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 240, 400, 0));
//            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 240, 400, 0));
//        });
    }
}