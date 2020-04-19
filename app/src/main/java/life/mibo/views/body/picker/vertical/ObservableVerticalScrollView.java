/*
 *  Created by Sumeet Kumar on 4/15/20 2:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 2:57 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.body.picker.vertical;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import life.mibo.hardware.core.Logger;
import life.mibo.views.body.picker.RulerValuePicker;
import life.mibo.views.body.picker.ScrollChangedListener;

/**
 * Customized and added Vertical Ruler Value Picker
 * https://github.com/samigehi/android-ruler-picker/
 * @author Sumeet Kumar (samigehi)
 */
@SuppressLint("ViewConstructor")
final class ObservableVerticalScrollView extends ScrollView {
    private static final long NEW_CHECK_DURATION = 100L;

    private long mLastScrollUpdateMills = -1;

    @Nullable
    private ScrollChangedListener mScrollChangedListener;

    private Runnable mScrollerTask = new Runnable() {

        public void run() {
            if (System.currentTimeMillis() - mLastScrollUpdateMills > NEW_CHECK_DURATION) {
                mLastScrollUpdateMills = -1;
                mScrollChangedListener.onScrollStopped();
            } else {
                //Post next delay
                postDelayed(this, NEW_CHECK_DURATION);
            }
        }
    };

    /**
     * Constructor.
     *
     * @param context  {@link Context} of caller.
     * @param listener {@link ScrollChangedListener} to get callbacks when scroll starts or stops.
     * @see ScrollChangedListener
     */
    public ObservableVerticalScrollView(@NonNull final Context context,
                                        @NonNull final ScrollChangedListener listener) {
        super(context);
        mScrollChangedListener = listener;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        boolean scroll = super.canScrollHorizontally(direction);
        Logger.e("canScrollHorizontally scroll " + scroll);
        return scroll;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        boolean scroll = super.canScrollVertically(direction);
        Logger.e("canScrollVertically scroll " + scroll);
        return scroll;
    }

    @Override
    protected void onScrollChanged(final int horizontalOrigin,
                                   final int verticalOrigin,
                                   final int oldHorizontalOrigin,
                                   final int oldVerticalOrigin) {
        super.onScrollChanged(horizontalOrigin, verticalOrigin, oldHorizontalOrigin, oldVerticalOrigin);
        RulerValuePicker.log(":onScrollChanged: " + horizontalOrigin + " : " + verticalOrigin + " -old- " + oldHorizontalOrigin + " : " + oldVerticalOrigin);
        if (mScrollChangedListener == null)
            return;
        mScrollChangedListener.onScrollChanged();

        if (mLastScrollUpdateMills == -1) postDelayed(mScrollerTask, NEW_CHECK_DURATION);
        mLastScrollUpdateMills = System.currentTimeMillis();
    }

}



