/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.ui.base;

/**
 * 复选
 * Created by KARL on 2017-03-17 04-22-30.
 */
public interface IMultiImageCheckedListener {
    void selectedImg(Object t, boolean isChecked);

    void selectedImgMax(Object t, boolean isChecked, int maxSize);
}
