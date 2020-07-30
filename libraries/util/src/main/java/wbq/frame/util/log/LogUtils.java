package wbq.frame.util.log;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/**
 * Log打印工具类
 */
public class LogUtils {

    /** 是否开启log记录, 默认为false*/
//	public static boolean sIsLog = false;
    /**
     * 是否同时写log到sdcard文件, 只有在sIsLog为true时, 此值才有意义
     */
    public static boolean sIsWrite2File = false;

    public static String sTag = "ZH";
    /***
     * 开发助手控制开关的文件名字
     */
    public static final String sDEV_HELPER_SWITCH_NAME = "log";
    /**
     * log文件路径
     */
    private static String sLogFilePath = null;
    public static final String TAG_SHOW_LOG = "showlog";
    //默认LogTag
    public static final String LOG_TAG = "LogUtils";
    //开发助手的打印日志开关
    private static boolean sIS_SHOW_LOG_DEV_HELPER;

    /***
     * 是否显示Log(true:显示;false:不显示)，不允许直接对其赋值，请使用{@link LogUtils#setShowLog}
     */
    public static boolean sIsLog = sIS_SHOW_LOG_DEV_HELPER;

    /**
     * @param isPrint 是否打印log
     */
    public static void setShowLog(boolean isPrint) {
        //本来就一致
        if (isPrint == sIsLog
                //开->关（助手打开的状态，不能关）
                || (sIS_SHOW_LOG_DEV_HELPER && !isPrint)) {
            return;
        }

        sIsLog = isPrint;
    }

    static String getExternalPath() {
        String path = "";
        try {
            path = Environment.getExternalStorageDirectory().getPath();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return path;
    }
    /**
     * 能不显示日志，API控制+文件控制，只满足其一就可以显示
     * @return
     */
    public static boolean isShowLog() {
        return sIS_SHOW_LOG_DEV_HELPER || sIsLog;
    }

    /**
     * debug级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     */
    public static void d(String logTag, String messageStr) {
        if (isShowLog()) {
            Log.d(logTag, messageStr);
        }
    }

    /**
     * info级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     */
    public static void i(String logTag, String messageStr) {
        if (isShowLog()) {
            Log.i(logTag, messageStr);
        }
    }

    /**
     * @param tag
     * @param msg
     * @param tr
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (isShowLog()) {
            Log.i(tag, msg, tr);
            writelog(tag, msg, tr, INFO);
        }
    }

    /**
     * info级别Log信息
     *
     * @param tag
     * @param msg
     * @param classRef
     */
    public static final void i(String tag, String msg, Object classRef) {
        if (isShowLog()) {
            Log.i(createTag(tag, classRef), msg);
        }
    }

    /**
     * warn级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     */
    public static void w(String logTag, String messageStr) {
        if (isShowLog()) {
            Log.w(logTag, messageStr);
        }
    }

    /**
     * warn级别Log信息
     *
     * @param logTag    日志Tag
     * @param throwable 异常对象
     */
    public static void w(String logTag, Throwable throwable) {
        if (isShowLog()) {
            Log.w(logTag, throwable);
        }
    }

    /**
     * warn级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     * @param throwable  异常对象
     */
    public static void w(String logTag, String messageStr, Throwable throwable) {
        if (isShowLog()) {
            Log.w(logTag, messageStr, throwable);
        }
    }

    /**
     * error级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     */
    public static void e(String logTag, String messageStr) {
        if (isShowLog()) {
            Log.e(logTag, messageStr);
        }
    }

    /**
     * error级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     * @param throwable  异常对象
     */
    public static void e(String logTag, String messageStr, Throwable throwable) {
        if (isShowLog()) {
            Log.e(logTag, messageStr, throwable);
        }
    }

    /**
     * v级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     */
    public static void v(String logTag, String messageStr) {
        if (isShowLog()) {
            Log.v(logTag, messageStr);
        }
    }

    /**
     * v级别Log信息
     *
     * @param logTag     日志Tag
     * @param messageStr 要打印的日志信息
     * @param throwable  异常对象
     */
    public static void v(String logTag, String messageStr, Throwable throwable) {
        if (isShowLog()) {
            Log.v(logTag, messageStr, throwable);
        }
    }

    /**
     * info级别Log信息
     *
     * @param strings 要打印的日志信息，其中第一位为logTag，后面按顺序拼接作为打印信息，null自动忽略
     */
    public static void i(String... strings) {
        if (isShowLog()) {
            String[] logStr = markLogMsg(strings);
            Log.i(logStr[0], logStr[1]);
        }
    }

    /**
     * debug级别Log信息
     *
     * @param strings 要打印的日志信息，其中第一位为logTag，后面按顺序拼接作为打印信息，null自动忽略
     */
    public static void d(String... strings) {
        if (isShowLog()) {
            String[] logStr = markLogMsg(strings);
            Log.d(logStr[0], logStr[1]);
        }
    }

    /**
     * error级别Log信息
     *
     * @param strings 要打印的日志信息，其中第一位为logTag，后面按顺序拼接作为打印信息，null自动忽略
     */
    public static void w(String... strings) {
        if (isShowLog()) {
            String[] logStr = markLogMsg(strings);
            Log.w(logStr[0], logStr[1]);
        }
    }

    /**
     * info级别Log信息
     *
     * @param strings 要打印的日志信息，其中第一位为logTag，后面按顺序拼接作为打印信息，null自动忽略
     */
    public static void e(String... strings) {
        if (isShowLog()) {
            String[] logStr = markLogMsg(strings);
            Log.e(logStr[0], logStr[1]);
        }
    }


    /**
     * 从可变参数中获取logTag和打印的消息
     *
     * @param strings
     * @return String[], 数组中第一位为logTag，第二位为logMsg
     */
    private static String[] markLogMsg(String... strings) {
        String logTag = LOG_TAG;
        String logMsg = "";
        if (strings != null && strings.length > 0) {
            logTag = strings[0] == null ? logTag : strings[0];
            for (int i = 1; i < strings.length; i++) {
                if (strings[i] != null) {
                    logMsg += strings[i];
                }
            }
        }
        return new String[]{logTag, logMsg};
    }


    /**
     * @param tag
     * @param classRef
     * @return
     */
    private static final String createTag(String tag, Object classRef) {
        String tagRet = "com.cs@";
        try {
            if (classRef != null) {
                tagRet += classRef.getClass().getName();
                tagRet += "::";
                tagRet += new Exception().getStackTrace()[2].getMethodName();
                tagRet += "@";
            }
            tagRet += tag;
        } catch (Exception e) {

        }
        return tagRet;
    }

    public static void v(String msg) {
        v(sTag, msg);
    }

    public static void d(String msg) {
        d(sTag, msg);
    }

    public static void i(String msg) {
        i(sTag, msg);
    }

    public static void w(String msg) {
        w(sTag, msg);
    }

    public static void e(String msg) {
        e(sTag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isShowLog()) {
            Log.d(tag, msg, tr);
            writelog(tag, msg, tr, DEBUG);
        }
    }


    /**
     * LOG开关控制的显示Toast
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void showToast(Context context, CharSequence text, int duration) {
        if (isShowLog()) {
            Toast.makeText(context, text, duration).show();
        }
    }

    /**
     * LOG开关控制的显示Toast
     *
     * @param context
     * @param resId
     * @param duration
     */
    public static void showToast(Context context, int resId, int duration) {
        if (isShowLog()) {
            Toast.makeText(context, resId, duration).show();
        }
    }

    /**
     * 获取当前调用堆栈信息
     *
     * @return
     */
    public static String getCurrentStackTraceString() {
        return Log.getStackTraceString(new Throwable());
    }

    /**
     * 获取堆栈信息
     *
     * @param tr
     * @return
     */
    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    synchronized private static void writelog(String tag, String msg, Throwable tr, int level) {
        if (sIsWrite2File) {
//            FileUtil.createNewFile(getLogfilePath(), true);
//            FileUtil.append2File(getLogfilePath(), getLogString(tag, msg, tr, level));
        }
    }

    private static String getLogfilePath() {
        if (sLogFilePath == null) {
            sLogFilePath = getExternalPath() + "/matt/matt-log.txt";
        }
        return sLogFilePath;
    }

    private static final int VERBOSE = 0;
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARN = 3;
    private static final int ERROR = 4;
    private static final String[] LOGLEVELS = new String[]{
            "VERBOSE", //0com.vos.util.io
            "DEBUG  ",
            "INFO   ",
            "WARN   ",
            "ERROR  ",
    };

    private static String getLogString(String tag, String msg, Throwable tr, int level) {
        final char seprator = '\t';
        StringBuilder sb = new StringBuilder();
        sb.append(DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()).toString());
        sb.append(seprator);
        sb.append(LOGLEVELS[level]);
        sb.append(seprator);
        sb.append(tag);
        sb.append(seprator);
        sb.append(msg);
        if (tr != null) {
            sb.append("\r\n");
            sb.append(getStackTraceString(tr));
        }
        //换行
        sb.append("\r\n");

        return sb.toString();
    }

}
