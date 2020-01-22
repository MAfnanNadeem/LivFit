package life.mibo.hardware.core;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import life.mibo.hardware.MIBO;

public class Logger {
    public static final boolean DEBUG = true;
    private static final String TAG = "MIBO-LIFE";
    //Timber Log =  Timber.d();


    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static void e(String msg, Throwable t) {
        if (DEBUG)
            Log.e(TAG, msg, t);
    }

    public static void e(String tag, String msg, Throwable t) {
        if (DEBUG)
            Log.e(tag, msg, t);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }
    public static void w(String tag, String msg) {
        if (DEBUG)
            Log.w(tag, msg);
    }
    public static void w(String msg) {
        if (DEBUG)
            Log.w(TAG, msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.v(tag, msg);
    }

    public static void save(Throwable throwable) {
        try {
            File logFile = new File(MIBO.getContext().getExternalFilesDir(null), "" + new SimpleDateFormat("yyyy-MM-dd").format(new Date() + ".txt"));
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);

                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append("MIBO "+ DateFormat.getDateTimeInstance().format(new Date()));
                buf.append(sw.getBuffer());
                buf.newLine();
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "error in save file ", e);
            saveLogcatToFile(MIBO.getContext());
        }
    }
    public static void save(String tag, String msg) {
        try {
            File logFile = new File(MIBO.getContext().getExternalFilesDir(null), "" + new SimpleDateFormat("yyyy-MM-dd").format(new Date() + ".txt"));
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(tag);
                buf.append(msg);
                buf.newLine();
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "error in save file ", e);
            saveLogcatToFile(MIBO.getContext());
        }


    }

    public static void saveLogcatToFile(Context context) {
        try {
            String fileName = "logcat_" + new Date().toString() + ".txt";
            File outputFile = new File(context.getExternalCacheDir(), fileName);
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec("logcat -df " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            //android.util.Log.e(TAG, "error in saveLogcatToFile ", e);

        }
    }


    public static class AndroidLogger {
        static final boolean ANDROID_LOG_AVAILABLE;

        static {
            boolean android = false;
            try {
                android = Class.forName("android.util.Log") != null;
            } catch (ClassNotFoundException e) {
                // OK
            }
            ANDROID_LOG_AVAILABLE = android;
        }

        public static boolean isAndroidLogAvailable() {
            return ANDROID_LOG_AVAILABLE;
        }


        private final String tag;

        public AndroidLogger(String tag) {
            this.tag = tag;
        }

        public void log(Level level, String msg) {
            if (level != Level.OFF) {
                Log.println(mapLevel(level), tag, msg);
            }
        }

        public void log(Level level, String msg, Throwable th) {
            if (level != Level.OFF) {
                // That's how Log does it internally
                Log.println(mapLevel(level), tag, msg + "\n" + Log.getStackTraceString(th));
            }
        }

        protected int mapLevel(Level level) {
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

    public static class JavaLogger {
        protected final java.util.logging.Logger logger;

        public JavaLogger(String tag) {
            logger = java.util.logging.Logger.getLogger(tag);
        }

        public void log(Level level, String msg) {
            // TODO Replace logged method with caller method
            logger.log(level, msg);
        }

        public void log(Level level, String msg, Throwable th) {
            // TODO Replace logged method with caller method
            logger.log(level, msg, th);
        }
    }

    public static class SystemLogger {

        public void log(Level level, String msg) {
            System.out.println("[" + level + "] " + msg);
        }

        public void log(Level level, String msg, Throwable th) {
            System.out.println("[" + level + "] " + msg);
            th.printStackTrace(System.out);
        }

    }

}
