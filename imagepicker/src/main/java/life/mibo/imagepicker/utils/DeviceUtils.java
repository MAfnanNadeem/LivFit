/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/8/8 下午11:05
 */
public class DeviceUtils {

    public static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }
}
