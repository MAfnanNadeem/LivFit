/*
 *  Created by Sumeet Kumar on 3/8/20 9:27 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/8/20 9:27 AM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import life.mibo.hexa.R;

public class TextThumbSeekBar extends AppCompatSeekBar {

    private int mThumbSize;
    private TextPaint mTextPaint;


    public TextThumbSeekBar(Context context) {
        this(context, null);
    }

    public TextThumbSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    View thumbView;

    public TextThumbSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        thumbView = LayoutInflater.from(context).inflate(R.layout.seekbar_thumb_layout, null, false);
        //float scale = context.getResources().getDisplayMetrics().density;
        //mThumbSize = dpToPx(24);
//        mThumbSize = getResources().getDimensionPixelSize(R.dimen.grid_4x);
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setMin(10);
        }
        setMax(100);

//        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

    }

    public Drawable getThumb(int progress) {
        ((TextView) thumbView.findViewById(R.id.tvProgress)).setText(progress + " cm");

        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);

        return new BitmapDrawable(getResources(), bitmap);
    }

    static float scale = 0;

    int dpToPx(int dp) {
        if (scale == 0) {
            scale = getContext().getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            setThumb(getThumb(getProgress()));
        } catch (Exception e) {

        }
        // onDrawText(canvas);
        // int thumb_x = (int) (((double) this.getProgress() / this.getMax()) * (double) this.getWidth());
        // int middle = getHeight() / 2;
        // canvas.drawText(String.valueOf(getProgress()), thumb_x, middle, mTextPaint);

    }

    private void init() {
        mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_small));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void onDrawText(Canvas canvas) {

        String progressText = String.valueOf(getProgress());
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;
        float thumbY = getHeight() / 2f + bounds.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
    }

}