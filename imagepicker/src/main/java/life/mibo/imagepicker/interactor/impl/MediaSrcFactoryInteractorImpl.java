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
import life.mibo.imagepicker.bean.MediaBean;
import life.mibo.imagepicker.interactor.MediaSrcFactoryInteractor;
import life.mibo.imagepicker.utils.MediaUtils;

public class MediaSrcFactoryInteractorImpl implements MediaSrcFactoryInteractor {

    private final Context context;
    private final OnGenerateMediaListener onGenerateMediaListener;
    private final boolean isImage;

    public MediaSrcFactoryInteractorImpl(Context context, boolean isImage, OnGenerateMediaListener onGenerateMediaListener) {
        this.context = context;
        this.isImage = isImage;
        this.onGenerateMediaListener = onGenerateMediaListener;
    }

    @Override
    public void generateMeidas(final String bucketId, final int page, final int limit) {
        Observable.create((ObservableOnSubscribe<List<MediaBean>>) subscriber -> {
            List<MediaBean> mediaBeanList = null;
            if (isImage) {
                mediaBeanList = MediaUtils.getMediaWithImageList(context, bucketId, page, limit);
            } else {
                mediaBeanList = MediaUtils.getMediaWithVideoList(context, bucketId, page, limit);
            }
            subscriber.onNext(mediaBeanList);
            subscriber.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<MediaBean>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        onGenerateMediaListener.onFinished(bucketId, page, limit, null);
                    }

                    @Override
                    public void onNext(List<MediaBean> mediaBeenList) {
                        onGenerateMediaListener.onFinished(bucketId, page, limit, mediaBeenList);
                    }
                });
    }
}
