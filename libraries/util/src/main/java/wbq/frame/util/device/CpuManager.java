package wbq.frame.util.device;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpuManager {
    public static final String CPU_FILE_DIR = "/sys/devices/system/cpu/";
    public static final String CAT_DIR = "/system/bin/cat";
    private static final int LENGTH = 24;
    private static String sMaxCpuFreq = null;
    private static String sMinCpuFreq = null;
    private static String sCurCpuFreq = null;
    private static int sCpuCount = -1;

    public static String getMaxCpuFreq() {
        if (sMaxCpuFreq != null) {
            return sMaxCpuFreq;
        } else {
            String result = "";
            InputStream in = null;

            try {
                String[] args = new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
                ProcessBuilder cmd = new ProcessBuilder(args);
                Process process = cmd.start();
                in = process.getInputStream();

                for(byte[] re = new byte[24]; in.read(re) != -1; result = result + new String(re)) {
                }

                in.close();
            } catch (IOException var14) {
                result = "N/A";
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException var13) {
                    }
                }

            }

            if (result.trim().equals("")) {
                return "0";
            } else {
                sMaxCpuFreq = result;
                return sMaxCpuFreq;
            }
        }
    }

    public static String getMinCpuFreq() {
        if (sMinCpuFreq != null) {
            return sMinCpuFreq;
        } else {
            String result = "";
            InputStream in = null;

            try {
                String[] args = new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
                ProcessBuilder cmd = new ProcessBuilder(args);
                Process process = cmd.start();
                in = process.getInputStream();

                for(byte[] re = new byte[24]; in.read(re) != -1; result = result + new String(re)) {
                }

                in.close();
            } catch (IOException var14) {
                result = "N/A";
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException var13) {
                    }
                }

            }

            sMinCpuFreq = result.trim();
            return sMinCpuFreq;
        }
    }

    public static String getCurCpuFreq() {
        if (sCurCpuFreq != null) {
            return sCurCpuFreq;
        } else {
            String result = "N/A";
            FileReader fr = null;
            BufferedReader br = null;

            try {
                File file = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
                if (file.exists()) {
                    fr = new FileReader(file);
                    br = new BufferedReader(fr);
                    String text = br.readLine();
                    if (text != null) {
                        result = text.trim();
                    }
                }
            } catch (IOException var13) {
            } finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }

                    if (br != null) {
                        br.close();
                    }
                } catch (IOException var12) {
                }

            }

            sCurCpuFreq = result;
            return sCurCpuFreq;
        }
    }

    public static String getCpuName() {
        FileReader fr = null;
        BufferedReader br = null;

        try {
            File file = new File("/proc/cpuinfo");
            if (!file.exists()) {
                return "unknown";
            } else {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String text = br.readLine();
                String[] array = text.split(":\\s+", 2);

                for(int i = 0; i < array.length; ++i) {
                }

                String var18 = array[1];
                return var18;
            }
        } catch (IOException var16) {
            return "unknown";
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }

                if (br != null) {
                    br.close();
                }
            } catch (IOException var15) {
            }

        }
    }

    public static long getAvailableInternalMemorySize(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    public static long getTotalInternalMemorySize() {
        String str1 = "/proc/meminfo";
        String str2 = "0";
        FileReader fr = null;
        BufferedReader localBufferedReader = null;

        long var6;
        try {
            fr = new FileReader(str1);
            localBufferedReader = new BufferedReader(fr, 8192);
            if ((str2 = localBufferedReader.readLine()) == null) {
                localBufferedReader.close();
                return 0L;
            }

            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(str2);
            if (m.find()) {
                str2 = m.group(1);
            }

            var6 = Long.valueOf(str2) * 1000L;
        } catch (IOException var18) {
            return 0L;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }

                if (localBufferedReader != null) {
                    localBufferedReader.close();
                }
            } catch (IOException var17) {
            }

        }

        return var6;
    }

    public static int getNumCores() {
        if (sCpuCount != -1) {
            return sCpuCount;
        } else {
            sCpuCount = Runtime.getRuntime().availableProcessors();
            if (sCpuCount > 0) {
                return sCpuCount;
            } else {
                try {
                    File dir = new File("/sys/devices/system/cpu/");

                    class CpuFilter implements FileFilter {
                        CpuFilter() {
                        }

                        public boolean accept(File pathname) {
                            return Pattern.matches("cpu[0-9]", pathname.getName());
                        }
                    }

                    File[] files = dir.listFiles(new CpuFilter());
                    sCpuCount = files.length;
                } catch (Exception var2) {
                    sCpuCount = 1;
                }

                return sCpuCount;
            }
        }
    }
}
