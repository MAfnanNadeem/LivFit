/*
 *  Created by Sumeet Kumar on 1/23/20 4:28 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 4:28 PM
 *  Mibo Hexa - app
 */

package life.mibo.views;

// CircularView written by sumeet
// Copied from Mobit Android App developed in 2016

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import life.mibo.android.R;
import life.mibo.android.utils.Constants;


public class CircleProgressView extends View {

    private float progress;
    private float width;
    private float strokeWidth;
    private int circleColor;
    private int backgroundColor;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint circlePaint;
    private float startAngle;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();

        setDefaultValues();

        // Init Background
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        // Init Circle
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(width);
    }


    private void setDefaultValues() {
        progress = 0;
        width = getResources().getDimension(R.dimen.default_circle_width);
        strokeWidth = getResources().getDimension(R.dimen.default_circle_background_width);
        circleColor = Constants.PRIMARY;
        backgroundColor = Color.LTGRAY;
        startAngle = -90;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw Background Circle
        canvas.drawOval(rectF, backgroundPaint);

        // Draw Circle
        float angle = 360 * progress / 100;
        canvas.drawArc(rectF, startAngle, angle, false, circlePaint);

//        mLayout.measure(canvas.getWidth(), canvas.getHeight());
//        mLayout.layout(0, 0, canvas.getWidth(), canvas.getHeight());
//        canvas.translate(canvas.getWidth() / 2 - mTextView.getWidth() / 2,
//                canvas.getHeight() / 2 - mTextView.getHeight() / 2);
//        mLayout.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float stroke = (this.width > strokeWidth) ? this.width : strokeWidth;
        rectF.set(0 + stroke / 2, 0 + stroke / 2, min - stroke / 2, min - stroke / 2);
    }


    public float getProgress() {
        return progress;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public void setProgress(float progress) {
        this.progress = (progress <= 100) ? progress : 100;
        //mTextView.setText(mTextPrefix + String.valueOf(Math.round(progress)) + mTextSuffix);
        invalidate();
    }
}