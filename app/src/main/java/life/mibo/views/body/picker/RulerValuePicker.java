/*
 *  Created by Sumeet Kumar on 4/15/20 2:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 2:47 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.body.picker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import life.mibo.android.R;
import life.mibo.hardware.core.Logger;


/**
 * <p>
 * <li>Diagram:</li>
 * Observable ScrollView
 * |------------------|---------------------\--/----------------------|------------------|<br/>
 * |                  |                      \/                       |                  |<br/>
 * |                  |                                               |                  |<br/>
 * |  Left Spacer     |                 RulerView                     |  Right Spacer    |<br/>
 * |                  |                                               |                  |<br/>
 * |                  |                                               |                  |<br/>
 * |------------------|-----------------------------------------------|------------------|<br/>
 *
 * @Created by Kevalpatel2106 on 29-Mar-2018.
 * @Modified by Sumeet Kumar (sumeetgehi@gmail.com) 04-Apr-2020.
 */
public final class RulerValuePicker extends FrameLayout implements ScrollChangedListener {

    /**
     * Left side empty view to add padding to the ruler.
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    private View mLeftSpacer;

    /**
     * Right side empty view to add padding to the ruler.
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    private View mRightSpacer;


    @SuppressWarnings("NullableProblems")
    @NonNull
    private RulerView mRulerView;

    @SuppressWarnings("NullableProblems")
    @NonNull
    private ObservableHorizontalScrollView mHorizontalScrollView;

    @Nullable
    private RulerValuePickerListener mListener;

    @SuppressWarnings("NullableProblems")
    @NonNull
    private Paint mNotchPaint;

    @SuppressWarnings("NullableProblems")
    @NonNull
    private Path mNotchPath;

    private int mNotchColor = Color.WHITE;

    /**
     * Public constructor.
     */
    public RulerValuePicker(@NonNull final Context context) {
        super(context);
        init(null);
    }

    /**
     * Public constructor.
     */
    public RulerValuePicker(@NonNull final Context context,
                            @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Public constructor.
     */
    public RulerValuePicker(@NonNull final Context context,
                            @Nullable final AttributeSet attrs,
                            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Public constructor.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerValuePicker(@NonNull final Context context,
                            @Nullable final AttributeSet attrs,
                            final int defStyleAttr,
                            final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * Initialize the view and parse the {@link AttributeSet}.
     *
     * @param attributeSet {@link AttributeSet} to parse or null if no attribute parameters set.
     */
    private void init(@Nullable AttributeSet attributeSet) {

        //Add all the children
        addChildViews();

        if (attributeSet != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet,
                    R.styleable.RulerValuePicker,
                    0,
                    0);

            try { //Parse params
                if (a.hasValue(R.styleable.RulerValuePicker_rv_notch_color)) {
                    mNotchColor = a.getColor(R.styleable.RulerValuePicker_rv_notch_color, Color.WHITE);
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_ruler_text_color)) {
                    setTextColor(a.getColor(R.styleable.RulerValuePicker_rv_ruler_text_color, Color.WHITE));
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_ruler_text_size)) {
                    setTextSize((int) a.getDimension(R.styleable.RulerValuePicker_rv_ruler_text_size, 14));
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_indicator_color)) {
                    setIndicatorColor(a.getColor(R.styleable.RulerValuePicker_rv_indicator_color, Color.WHITE));
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_long_indicator_color)) {
                    setLongIndicatorColor(a.getColor(R.styleable.RulerValuePicker_rv_long_indicator_color, Color.WHITE));
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_indicator_width)) {
                    setIndicatorWidth(a.getDimensionPixelSize(R.styleable.RulerValuePicker_rv_indicator_width,
                            4));
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_indicator_interval)) {
                    setIndicatorIntervalDistance(a.getDimensionPixelSize(R.styleable.RulerValuePicker_rv_indicator_interval,
                            4));
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_long_height_height_ratio)
                        || a.hasValue(R.styleable.RulerValuePicker_rv_short_height_height_ratio)) {

                    setIndicatorHeight(a.getFraction(R.styleable.RulerValuePicker_rv_long_height_height_ratio,
                            1, 1, 0.6f),
                            a.getFraction(R.styleable.RulerValuePicker_rv_short_height_height_ratio,
                                    1, 1, 0.4f));
                }

                if (a.hasValue(R.styleable.RulerValuePicker_rv_min_value) ||
                        a.hasValue(R.styleable.RulerValuePicker_rv_max_value)) {
                    setMinMaxValue(a.getInteger(R.styleable.RulerValuePicker_rv_min_value, 0),
                            a.getInteger(R.styleable.RulerValuePicker_rv_max_value, 100));
                }
            } finally {
                a.recycle();
            }
        }

        //Prepare the notch color.
        mNotchPaint = new Paint();
        mNotchPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        prepareNotchPaint();

        mNotchPath = new Path();
    }

    /**
     * Create the paint for notch. This will
     */
    private void prepareNotchPaint() {
        mNotchPaint.setColor(mNotchColor);
        mNotchPaint.setStrokeWidth(10f);
        mNotchPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * Programmatically add the children to the view.
     * <p>
     * <li>The main view contains the {@link android.widget.HorizontalScrollView}. That allows
     * {@link RulerView} to scroll horizontally.</li>
     * <li>{@link #mHorizontalScrollView} contains {@link LinearLayout} that will act as the container
     * to hold the children inside the horizontal view.</li>
     * <li>{@link LinearLayout} container will contain three children.
     * <ul><MyWebViewClient>Left spacer:</MyWebViewClient> Width of this view will be the half width of the view. This will add staring at the start of the ruler.</ul>
     * <ul><MyWebViewClient>Right spacer:</MyWebViewClient> Width of this view will be the half width of the view. This will add ending at the end of the ruler.</ul>
     * <ul><MyWebViewClient>{@link RulerView}:</MyWebViewClient> Ruler view will contain the ruler with indicator.</ul>
     * </li>
     */
    private void addChildViews() {
        mHorizontalScrollView = new ObservableHorizontalScrollView(getContext(), this);
        mHorizontalScrollView.setHorizontalScrollBarEnabled(false); //Don't display the scrollbar

        final LinearLayout rulerContainer = new LinearLayout(getContext());

        //Add left spacing to the container
        mLeftSpacer = new View(getContext());
        rulerContainer.addView(mLeftSpacer);

        //Add ruler to the container
        mRulerView = new RulerView(getContext());
        rulerContainer.addView(mRulerView);

        //Add right spacing to the container
        mRightSpacer = new View(getContext());
        rulerContainer.addView(mRightSpacer);

        //Add this container to the scroll view.
        mHorizontalScrollView.removeAllViews();
        mHorizontalScrollView.addView(rulerContainer);

        //Add scroll view to this view.
        removeAllViews();
        addView(mHorizontalScrollView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw the top notch
        canvas.drawPath(mNotchPath, mNotchPaint);
    }

    @Override
    protected void onLayout(boolean isChanged, int left, int top, int right, int bottom) {
        super.onLayout(isChanged, left, top, right, bottom);
        log("onLayout: ----- " + isChanged);
        if (isChanged) {
            final int width = getWidth();
            log("onLayout ----- " + width);

            //Set width of the left spacer to the half of this view.
            final ViewGroup.LayoutParams leftParams = mLeftSpacer.getLayoutParams();
            leftParams.width = width / 2;
            mLeftSpacer.setLayoutParams(leftParams);

            //Set width of the right spacer to the half of this view.
            final ViewGroup.LayoutParams rightParams = mRightSpacer.getLayoutParams();
            rightParams.width = width / 2;
            mRightSpacer.setLayoutParams(rightParams);
            log("onLayout width ----- " + leftParams.width + " :: " + rightParams.width);
            mLeftSpacer.invalidate();
            mRightSpacer.invalidate();

            calculateNotchPath();

            invalidate();
        }
    }

    /**
     * Calculate notch path. Notch will be in the triangle shape at the top-center of this view.
     *
     * @see #mNotchPath
     */
    private void calculateNotchPath() {
        mNotchPath.reset();

        int width = getWidth();
//        mNotchPath.moveTo(getWidth() / 2 - 30, 0);
//        mNotchPath.lineTo(getWidth() / 2, getHeight());
//        mNotchPath.lineTo(getWidth() / 2 + 30, 0);

        mNotchPath.moveTo(width / 2, 0);
        mNotchPath.lineTo(width / 2, getHeight());
        mNotchPath.lineTo(width / 2, 0);

//        mNotchPath.moveTo(width / 2 - 20, getHeight());
//        mNotchPath.lineTo(width / 2 - 3, getHeight() - 30);
//        mNotchPath.lineTo(width / 2 - 3, 0);
//        mNotchPath.lineTo(width / 2 + 3, 0);
//        mNotchPath.lineTo(width / 2 + 3, getHeight() - 30);
//        mNotchPath.lineTo(width / 2 + 20, getHeight());
    }

    /**
     * Scroll the ruler to the given value.
     *
     * @param value Value to select. Value must be between {@link #getMinValue()} and {@link #getMaxValue()}.
     *              If the value is less than {@link #getMinValue()}, {@link #getMinValue()} will be
     *              selected.If the value is greater than {@link #getMaxValue()}, {@link #getMaxValue()}
     *              will be selected.
     */
    public void selectValue(final int value) {
        log("selectValue " + value);
        mHorizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int valuesToScroll;
                if (value < mRulerView.getMinValue()) {
                    valuesToScroll = 0;
                } else if (value > mRulerView.getMaxValue()) {
                    valuesToScroll = mRulerView.getMaxValue() - mRulerView.getMinValue();
                } else {
                    valuesToScroll = value - mRulerView.getMinValue();
                }

                mHorizontalScrollView.smoothScrollTo(
                        valuesToScroll * mRulerView.getIndicatorIntervalWidth(), 0);
            }
        }, 400);
    }

    /**
     * @return Get the current selected value.
     */
    public int getCurrentValue() {
        int absoluteValue = mHorizontalScrollView.getScrollX() / mRulerView.getIndicatorIntervalWidth();
        int value = mRulerView.getMinValue() + absoluteValue;
        log("getCurrentValue absolute " + absoluteValue + " -value- " + value);
        if (value > mRulerView.getMaxValue()) {
            return mRulerView.getMaxValue();
        } else if (value < mRulerView.getMinValue()) {
            return mRulerView.getMinValue();
        } else {
            return value;
        }

    }

    @Override
    public void onScrollChanged() {
        if (mListener != null) mListener.onIntermediateValueChange(getCurrentValue());
    }

    @Override
    public void onScrollStopped() {
        makeOffsetCorrection(mRulerView.getIndicatorIntervalWidth());
        if (mListener != null) {
            mListener.onValueChange(getCurrentValue());
        }
        log("onScrollStopped ");
    }

    private void makeOffsetCorrection(final int indicatorInterval) {
        log("makeOffsetCorrection " + indicatorInterval);

        int offsetValue = mHorizontalScrollView.getScrollX() % indicatorInterval;
        if (offsetValue < indicatorInterval / 2) {
            mHorizontalScrollView.scrollBy(-offsetValue, 0);
        } else {
            mHorizontalScrollView.scrollBy(indicatorInterval - offsetValue, 0);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.value = getCurrentValue();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        selectValue(ss.value);
    }

    //**********************************************************************************//
    //******************************** GETTERS/SETTERS *********************************//
    //**********************************************************************************//


    public void setNotchColorRes(@ColorRes final int notchColorRes) {
        setNotchColor(ContextCompat.getColor(getContext(), notchColorRes));
    }


    @ColorInt
    public int getNotchColor() {
        return mNotchColor;
    }


    public void setNotchColor(@ColorInt final int notchColor) {
        mNotchColor = notchColor;
        prepareNotchPaint();
        invalidate();
    }


    @CheckResult
    @ColorInt
    public int getTextColor() {
        return mRulerView.getTextColor();
    }


    public void setTextColor(@ColorInt final int color) {
        mRulerView.setTextColor(color);
    }


    public void setTextColorRes(@ColorRes final int color) {
        setTextColor(ContextCompat.getColor(getContext(), color));
    }


    @CheckResult
    public float getTextSize() {
        return mRulerView.getTextSize();
    }


    public void setTextSize(final int dimensionDp) {
        mRulerView.setTextSize(dimensionDp);
    }

    public void setTextSizeRes(@DimenRes final int dimension) {
        setTextSize((int) getContext().getResources().getDimension(dimension));
    }


    @CheckResult
    @ColorInt
    public int getIndicatorColor() {
        return mRulerView.getIndicatorColor();
    }

    public void setIndicatorColor(@ColorInt final int color) {
        mRulerView.setIndicatorColor(color);
    }

    public void setLongIndicatorColor(@ColorInt final int color) {
        mRulerView.setLongIndicatorColor(color);
    }


    public void setIndicatorColorRes(@ColorRes final int color) {
        setIndicatorColor(ContextCompat.getColor(getContext(), color));
    }


    @CheckResult
    public float getIndicatorWidth() {
        return mRulerView.getIndicatorWidth();
    }

    public void setIndicatorWidth(final int widthPx) {
        mRulerView.setIndicatorWidth(widthPx);
    }


    public void setIndicatorWidthRes(@DimenRes final int width) {
        setIndicatorWidth(getContext().getResources().getDimensionPixelSize(width));
    }

    @CheckResult
    public int getMinValue() {
        return mRulerView.getMinValue();
    }


    @CheckResult
    public int getMaxValue() {
        return mRulerView.getMaxValue();
    }


    public void setMinMaxValue(final int minValue, final int maxValue) {
        log("setMinMaxValue " + minValue + " -- " + maxValue);
        mRulerView.setValueRange(minValue, maxValue);
        mHorizontalScrollView.requestLayout();
        //refresh();
        requestLayout();
        selectValue(minValue);
    }

    public void refresh() {
        for (int i = 0; i < getChildCount(); i++) {
            try {
                getChildAt(i).forceLayout();
                getChildAt(i).requestLayout();
            } catch (Exception e) {

            }
        }
        requestLayout();
        super.invalidate();
    }

    int type = 0;

    public synchronized void refreshCmToInches() {
        setMinMaxValue((int) (mRulerView.getMinValue() * 0.3937), (int) (mRulerView.getMaxValue() * 0.3937));
        type = 2;
        invalidate();
    }

    public synchronized void refreshInchesToCm() {
        setMinMaxValue((int) (mRulerView.getMinValue() * 2.54), (int) (mRulerView.getMaxValue() * 2.54));
        type = 1;
        invalidate();
    }

    private int cmToInch(int value) {
        return (int) (value * 0.3937);
    }

    private int inchToCm(int value) {
        return (int) (value * 2.54);
    }

    @CheckResult
    public int getIndicatorIntervalWidth() {
        return mRulerView.getIndicatorIntervalWidth();
    }

    public void setIndicatorIntervalDistance(final int indicatorIntervalPx) {
        mRulerView.setIndicatorIntervalDistance(indicatorIntervalPx);
    }

    @CheckResult
    public float getLongIndicatorHeightRatio() {
        return mRulerView.getLongIndicatorHeightRatio();
    }

    @CheckResult
    public float getShortIndicatorHeightRatio() {
        return mRulerView.getShortIndicatorHeightRatio();
    }

    public void setIndicatorHeight(final float longHeightRatio,
                                   final float shortHeightRatio) {
        mRulerView.setIndicatorHeight(longHeightRatio, shortHeightRatio);
    }

    public void setValuePickerListener(@Nullable final RulerValuePickerListener listener) {
        mListener = listener;
    }

    public static class SavedState extends BaseSavedState {

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        private int value = 0;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
        }
    }

    public static void log(String msg) {
        Logger.e("RulerValuePicker: " + msg);
    }
}
