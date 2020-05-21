package wbq.frame.demo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jerry on 2020/5/14 20:01
 */
public class LogStatistics {
    private static final String TAG = "LogStatistics";
    private Looper mLooper;
    private Handler mHandler;
    private BufferedReader mBufferedReader;
    private StringBuffer mLogData;
    private AtomicBoolean mFlag = new AtomicBoolean(false);

    public synchronized void go() {
        if (mLooper != null) {
            return;
        }
        log("go...");
        Log.d("AppsFlyer", "aaaaaaaaaaaaaaaaaaaaa");
        HandlerThread thread = new HandlerThread("logStatistic");
        thread.start();
        mLooper = thread.getLooper();
        mHandler = new LHandler();
        try {
            final String command = "logcat AppsFlyer:D *:S";
            final String[] commands = {"sh", "-c", command};
            Process p = Runtime.getRuntime().exec(commands);
//            Process p = Runtime.getRuntime().exec("logcat -c");
            mBufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            mLogData = new StringBuffer();
            log("go start");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("AppsFlyer", "fffffffffffffffffllllllllll" + Log.getStackTraceString(new Throwable()));
                }
            }, 5000);
            mFlag.set(true);
            mHandler.sendEmptyMessage(0);
        } catch (IOException e) {
            e.printStackTrace();
            reset();
        }
    }

    public synchronized void stop2Upload() {
        mFlag.set(false);
        reset();
        final String logData = mLogData.toString();
        log("stop2Upload--" + logData);
    }

    private synchronized void reset() {
        if (mLooper != null) {
            mLooper.quitSafely();
            mLooper = null;
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mBufferedReader != null) {
            try {
                mBufferedReader.close(); // close时阻塞太长时间
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mBufferedReader = null;
            }
        }
    }

    private void readLog() {
        log("readLog");
        String str;
        final BufferedReader reader = mBufferedReader;
        try {
            while (mFlag.get() && !TextUtils.isEmpty(str = reader.readLine())) {
                mLogData.append(str).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String msg) {
        Log.i(TAG, msg);
    }

    private class LHandler extends Handler {
        LHandler() {
            super(mLooper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            readLog();
//            sendEmptyMessageDelayed(0, 1000);
        }
    }
}
