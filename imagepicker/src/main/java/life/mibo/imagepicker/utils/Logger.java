/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.utils;

import android.util.Log;

import com.yalantis.ucrop.BuildConfig;


/**
 * Desction:日志工具
 * Author:pengjianbo  Dujinyang
 * Date:16/5/23 下午3:12
 */
public class Logger {

    public static final String TAG = "RxGalleryFinal";
    public static boolean DEBUG = BuildConfig.DEBUG;

    public static void d(String value) {
        if (DEBUG) {
            Log.d(TAG, value);
        }
    }

    public static void e(String value) {
        if (DEBUG) {
            Log.e(TAG, value);
        }
    }

    public static void e(Exception value) {
        if (DEBUG && value != null) {
            Log.e(TAG, value.getMessage());
        }
    }

    public static void i(String value) {
        if (DEBUG) {
            Log.i(TAG, value);
        }
    }

    public static void w(String value) {
        if (DEBUG) {
            Log.w(TAG, value);
        }
    }
}
