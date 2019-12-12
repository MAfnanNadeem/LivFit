package life.mibo.hardware.core;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import life.mibo.hardware.MIBO;

public class Logger {
    public static final boolean DEBUG = true;
    private static final String TAG = "MIBO-LIFE";


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

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.v(tag, msg);
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
            android.util.Log.e(TAG, "error in saveLogcatToFile ", e);

        }
    }

}
