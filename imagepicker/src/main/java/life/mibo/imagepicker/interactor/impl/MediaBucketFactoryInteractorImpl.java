/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.interactor.impl;


import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import life.mibo.imagepicker.bean.BucketBean;
import life.mibo.imagepicker.interactor.MediaBucketFactoryInteractor;
import life.mibo.imagepicker.utils.MediaUtils;

public class MediaBucketFactoryInteractorImpl implements MediaBucketFactoryInteractor {

    private final Context context;
    private final boolean isImage;
    private final OnGenerateBucketListener onGenerateBucketListener;

    public MediaBucketFactoryInteractorImpl(Context context, boolean isImage, OnGenerateBucketListener onGenerateBucketListener) {
        this.context = context;
        this.isImage = isImage;
        this.onGenerateBucketListener = onGenerateBucketListener;
    }

    @Override
    public void generateBuckets() {
        Observable.create((ObservableOnSubscribe<List<BucketBean>>) subscriber -> {
            List<BucketBean> bucketBeanList = null;
            if (isImage) {
                bucketBeanList = MediaUtils.getAllBucketByImage(context);
            } else {
                bucketBeanList = MediaUtils.getAllBucketByVideo(context);
            }
            subscriber.onNext(bucketBeanList);
            subscriber.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<BucketBean>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        onGenerateBucketListener.onFinished(null);
                    }

                    @Override
                    public void onNext(List<BucketBean> bucketBeanList) {
                        onGenerateBucketListener.onFinished(bucketBeanList);
                    }
                });
    }
}
