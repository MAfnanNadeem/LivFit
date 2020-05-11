/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.rxbus.event;

import java.util.ArrayList;

import life.mibo.imagepicker.bean.MediaBean;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/7/27 下午11:14
 */
public class OpenMediaPageFragmentEvent {
    private final ArrayList<MediaBean> mediaBeanList;
    private final int position;

    public OpenMediaPageFragmentEvent(ArrayList<MediaBean> mediaBeanList, int position) {
        this.mediaBeanList = mediaBeanList;
        this.position = position;
    }

    public ArrayList<MediaBean> getMediaBeanList() {
        return mediaBeanList;
    }

    public int getPosition() {
        return position;
    }
}
