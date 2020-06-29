/*
 *  Created by Sumeet Kumar on 6/25/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/25/20 3:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
//com.google.android.material.floatingactionbutton.FloatingActionButton
public class MyNewFab extends FloatingActionButton {

    private Matrix imageMatrix;

    public MyNewFab(@NonNull Context context) {
        super(context);
    }

    public MyNewFab(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNewFab(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageMatrix = getImageMatrix();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        //setImageMatrix(imageMatrix);
    }
}