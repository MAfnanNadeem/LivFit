/*
 *  Created by Sumeet Kumar on 2/23/20 11:21 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/23/20 11:17 AM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class RxlPager extends ViewPager {
    public RxlPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof RecyclerView) {
            return false;
        }

        return super.canScroll(v, checkV, dx, x, y);
    }
}