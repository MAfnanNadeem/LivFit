/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.presenter.impl;

import android.content.Context;

import java.util.List;

import life.mibo.imagepicker.bean.BucketBean;
import life.mibo.imagepicker.bean.MediaBean;
import life.mibo.imagepicker.interactor.MediaBucketFactoryInteractor;
import life.mibo.imagepicker.interactor.MediaSrcFactoryInteractor;
import life.mibo.imagepicker.interactor.impl.MediaBucketFactoryInteractorImpl;
import life.mibo.imagepicker.interactor.impl.MediaSrcFactoryInteractorImpl;
import life.mibo.imagepicker.presenter.MediaGridPresenter;
import life.mibo.imagepicker.view.MediaGridView;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/5/14 上午10:58
 */
public class MediaGridPresenterImpl implements MediaGridPresenter, MediaSrcFactoryInteractor.OnGenerateMediaListener,
        MediaBucketFactoryInteractor.OnGenerateBucketListener {

    private final MediaSrcFactoryInteractor mediaSrcFactoryInteractor;
    private final MediaBucketFactoryInteractor mediaBucketFactoryInteractor;

    private MediaGridView mediaGridView;

    public MediaGridPresenterImpl(Context context, boolean isImage) {
        this.mediaSrcFactoryInteractor = new MediaSrcFactoryInteractorImpl(context, isImage, this);
        this.mediaBucketFactoryInteractor = new MediaBucketFactoryInteractorImpl(context, isImage, this);
    }

    /**
     * 设置MVP view(操作UI接口)
     */
    @Override
    public void setMediaGridView(MediaGridView mediaGridView) {
        this.mediaGridView = mediaGridView;
    }

    /**
     * 分页获取media
     */
    @Override
    public void getMediaList(String bucketId, int pageSize, int currentOffset) {
        mediaSrcFactoryInteractor.generateMeidas(bucketId, pageSize, currentOffset);
    }

    @Override
    public void getBucketList() {
        mediaBucketFactoryInteractor.generateBuckets();
    }

    /**
     * Media获取事件回调
     */
    @Override
    public void onFinished(String bucketId, int pageSize, int currentOffset, List<MediaBean> list) {
        mediaGridView.onRequestMediaCallback(list);
    }

    /**
     * BUCKET获取事件回调
     */
    @Override
    public void onFinished(List<BucketBean> list) {
        mediaGridView.onRequestBucketCallback(list);
    }
}
