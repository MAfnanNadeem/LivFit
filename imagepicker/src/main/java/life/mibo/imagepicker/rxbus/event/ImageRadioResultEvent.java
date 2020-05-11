/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.rxbus.event;

import life.mibo.imagepicker.bean.ImageCropBean;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/8/1 下午10:49
 */
public class ImageRadioResultEvent implements BaseResultEvent {
    private final ImageCropBean resultBean;

    public ImageRadioResultEvent(ImageCropBean bean) {
        this.resultBean = bean;
    }

    public ImageCropBean getResult() {
        return resultBean;
    }

}
