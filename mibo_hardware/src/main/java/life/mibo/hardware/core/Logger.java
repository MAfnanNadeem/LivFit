package life.mibo.hardware.core;

import android.util.Log;

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
}
