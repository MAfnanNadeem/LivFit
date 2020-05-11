/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * Created by KARL-dujinyang on 2017-04-13.
 */
public class SimpleDateUtils {

    public static String getNowTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
