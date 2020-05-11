/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.interactor;

import java.util.List;

import life.mibo.imagepicker.bean.BucketBean;


public interface MediaBucketFactoryInteractor {

    void generateBuckets();

    interface OnGenerateBucketListener {
        void onFinished(List<BucketBean> list);
    }
}
