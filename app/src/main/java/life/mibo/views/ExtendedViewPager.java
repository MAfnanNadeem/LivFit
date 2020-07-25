/*
 *  Created by Sumeet Kumar on 7/12/20 4:37 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/12/20 4:37 PM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import androidx.viewpager.widget.ViewPager;

public class ExtendedViewPager extends ViewPager {

    public ExtendedViewPager(Context context) {
        super(context);
    }

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView) {
            return ((TouchImageView) v).canScrollHorizontally(-dx);
        } else if (v instanceof ScrollView) {
            return false;
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return false;
    }
}