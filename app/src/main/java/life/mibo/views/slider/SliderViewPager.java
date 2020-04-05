/*
 *  Created by Sumeet Kumar on 4/5/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 3:09 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.slider;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

import life.mibo.views.slider.utils.CardsPagerTransformerBasic;
import life.mibo.views.slider.utils.FixedSpeedScroller;

public class SliderViewPager extends ViewPager {

    private int baseElevation = 0;
    private int raisingElevation = 1;
    private float smallerScale = 0.6f;

    public SliderViewPager(Context context) {
        super(context);
        postInitViewPager();
    }

    public SliderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }


    private void postInitViewPager() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field mScroller;
            mScroller = viewpager.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext());
            mScroller.set(this, scroller);

            transformViewPager();
        } catch (Exception e) {
        }
    }


    private void transformViewPager() {
        Resources r = getResources();
        int partialWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());

        int viewPagerPadding = partialWidth + pageMargin;
        setPageMargin(pageMargin);
        setPadding(viewPagerPadding, 0, viewPagerPadding, 0);

        setPageTransformer(false, new CardsPagerTransformerBasic(baseElevation, raisingElevation, smallerScale));
    }

}
