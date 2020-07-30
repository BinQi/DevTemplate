package wbq.frame.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author jerry
 * @created 2020/7/23 17:09
 */
public class ProcessUtil {
    private static volatile String sCurrentProcessName;

    /**
     * 获取进当前进程名
     * @return 进程名
     */
    public static String getCurrentProcessName(@NonNull Context context) {
        if (null == sCurrentProcessName) {
            synchronized (ProcessUtil.class) {
                if (null == sCurrentProcessName) {
                    sCurrentProcessName = fetchProcessName(context
                            , android.os.Process.myPid());
                }
            }
        }
        return sCurrentProcessName;
    }

    /**
     * 获取pid对应的进程名称
     * @param context
     * @param pid
     * @return
     */
    public static String fetchProcessName(Context context, int pid) {
        String result = fetchProcessNameByApi(context, pid);
        if (TextUtils.isEmpty(result)) {
            result = fetchProcessNameByFile(pid);
        }
        if (TextUtils.isEmpty(result)) {
            result = fetchProcessNameByCmd(pid);
        }
        return result;
    }

    private static String fetchProcessNameByApi(Context context, int pid) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = activityManager.getRunningAppProcesses();
        if (appProcessList != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

    private static String fetchProcessNameByFile(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private static String fetchProcessNameByCmd(int pid) {
        Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("ps " + pid);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            String[] texts = null;
            if (line != null) { //第一行为标题
                if ((line = reader.readLine()) != null) { //第二行才是数据
                    texts = line.split("\\s+", Integer.MAX_VALUE);
                    String name = texts[texts.length - 1];
                    return name;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return null;
    }
}
