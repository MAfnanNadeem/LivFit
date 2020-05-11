/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/6/3 下午8:28
 */
public class CameraUtils {

    /**
     * 判断设备是否有摄像头
     */
    public static boolean hasCamera(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

    }
}
