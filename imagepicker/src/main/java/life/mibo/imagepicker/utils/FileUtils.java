/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.utils;

/**
 * Desction:文件工具类
 * Author:dujinyang
 */
public class FileUtils {

    /**
     * 验证是否是图片路径
     */
    public static int existImageDir(String dir) {
        return dir.trim().lastIndexOf(".");
    }
}
