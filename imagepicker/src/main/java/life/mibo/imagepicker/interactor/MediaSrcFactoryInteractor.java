/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.interactor;

import java.util.List;

import life.mibo.imagepicker.bean.MediaBean;

public interface MediaSrcFactoryInteractor {

    /**
     * 生产资源
     */
    void generateMeidas(String bucketId, int page, int limit);

    interface OnGenerateMediaListener {
        void onFinished(String bucketId, int pageSize, int currentOffset, List<MediaBean> list);
    }

}
