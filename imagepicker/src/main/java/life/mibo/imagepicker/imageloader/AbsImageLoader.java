/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import life.mibo.imagepicker.ui.widget.FixImageView;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/6/17 下午1:05
 */
public interface AbsImageLoader {
    void displayImage(Context context,
                      String path,
                      FixImageView imageView,
                      Drawable defaultDrawable,
                      Bitmap.Config config,
                      boolean resize,
                      boolean isGif,
                      int width,
                      int height,
                      int rotate);
}
