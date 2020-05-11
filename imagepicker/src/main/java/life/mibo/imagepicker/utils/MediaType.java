/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.utils;

import java.io.Serializable;

/**
 * Desction:支持的Media类型
 * Author:pengjianbo  Dujinyang
 * Date:16/5/5 下午5:03
 */
public enum MediaType implements Serializable {
    JPG, PNG, WEBP, GIF, MP4;

    public boolean hasVideo() {
        return this == MP4;
    }
}
