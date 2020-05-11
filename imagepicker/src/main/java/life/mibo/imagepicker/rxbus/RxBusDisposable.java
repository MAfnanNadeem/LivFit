/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.rxbus;

import io.reactivex.observers.DisposableObserver;
import life.mibo.imagepicker.utils.Logger;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/7/22 下午2:40
 */
public abstract class RxBusDisposable<T> extends DisposableObserver<T> {

    @Override
    public void onNext(T t) {
        try {
            onEvent(t);
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }


    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        Logger.e(e.getMessage());
    }

    protected abstract void onEvent(T t) throws Exception;

}