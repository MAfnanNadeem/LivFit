/*
 *  Created by Sumeet Kumar on 1/15/20 1:34 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 1:31 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.wifi_switch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

class ThumbView extends View {

    private PointF center;
    private float radius;
    private Paint paint;

    public ThumbView(Context context) {
        super(context);
    }

    public ThumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ThumbView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GRAY);
        center = new PointF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        center.set(w * 0.5f, h * 0.5f);
        radius = Math.min(w, h) * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public int getColor() {
        return paint.getColor();
    }
}
