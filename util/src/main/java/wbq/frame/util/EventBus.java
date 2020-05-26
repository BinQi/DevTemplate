package wbq.frame.util;

import android.util.Log;

import org.greenrobot.eventbus.Logger;

import java.util.logging.Level;

import wbq.frame.util.log.LogUtils;

/**
 * Created by Jerry on 2019/4/9 4:25 PM
 */
public class EventBus {

    private static org.greenrobot.eventbus.EventBus sEventBus;

    public static org.greenrobot.eventbus.EventBus get() {
        if (null == sEventBus) {
            synchronized (EventBus.class) {
                if (null == sEventBus) {
                    sEventBus = org.greenrobot.eventbus.EventBus.builder()
                            .logger(new LoggerImpl())
                            .installDefaultEventBus();
                }
            }
        }
        return sEventBus;
    }

    static class LoggerImpl implements Logger {
        final String TAG = "EventBus";

        @Override
        public void log(Level level, String msg) {
            if (LogUtils.isShowLog()) {
                Log.println(mapLevel(level), TAG, msg);
            }
        }

        @Override
        public void log(Level level, String msg, Throwable th) {
            if (LogUtils.isShowLog()) {
                Log.println(mapLevel(level), TAG, msg + "\n" + Log.getStackTraceString(th));
            }
        }

        public static int mapLevel(Level level) {
            int value = level.intValue();
            if (value < 800) { // below INFO
                if (value < 500) { // below FINE
                    return Log.VERBOSE;
                } else {
                    return Log.DEBUG;
                }
            } else if (value < 900) { // below WARNING
                return Log.INFO;
            } else if (value < 1000) { // below ERROR
                return Log.WARN;
            } else {
                return Log.ERROR;
            }
        }
    }
}
