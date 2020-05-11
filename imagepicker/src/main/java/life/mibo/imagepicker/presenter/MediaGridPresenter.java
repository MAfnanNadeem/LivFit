/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.presenter;

import life.mibo.imagepicker.view.MediaGridView;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/5/14 上午10:53
 */
public interface MediaGridPresenter {

    void setMediaGridView(MediaGridView mediaGridView);

    void getMediaList(String bucketId, int pageSize, int currentOffset);

    void getBucketList();
}
