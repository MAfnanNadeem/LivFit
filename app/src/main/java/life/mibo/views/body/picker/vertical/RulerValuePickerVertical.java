/*
 *  Created by Sumeet Kumar on 4/15/20 3:00 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 2:59 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.body.picker.vertical;

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
import life.mibo.views.body.picker.RulerValuePicker;
import life.mibo.views.body.picker.RulerValuePickerListener;
import life.mibo.views.body.picker.ScrollChangedListener;

//import life.mibo.views.body.picker.RulerView;

/**
 * Customized and added Vertical Ruler Value Picker
 * https://github.com/samigehi/android-ruler-picker/
 *
 * @author Sumeet Kumar (samigehi)
 */

public final class RulerValuePickerVertical extends FrameLayout implements ScrollChangedListener {

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
    private RulerViewVertical mRulerView;

    @SuppressWarnings("NullableProblems")
    @NonNull
    private ObservableVerticalScrollView mScrollView;

    @Nullable
    private RulerValuePickerListener mListener;

    @SuppressWarnings("NullableProblems")
    @NonNull
    private Paint mNotchPaint;

    @SuppressWarnings("NullableProblems")
    @NonNull
    private Path mNotchPath;

    private int mNotchColor = Color.RED;

    /**
     * Public constructor.
     */
    public RulerValuePickerVertical(@NonNull final Context context) {
        super(context);
        init(null);
    }

    /**
     * Public constructor.
     */
    public RulerValuePickerVertical(@NonNull final Context context,
                                    @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Public constructor.
     */
    public RulerValuePickerVertical(@NonNull final Context context,
                                    @Nullable final AttributeSet attrs,
                                    final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Public constructor.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerValuePickerVertical(@NonNull final Context context,
                                    @Nullable final AttributeSet attrs,
                                    final int defStyleAttr,
                                    final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(h, w, oldh, oldw);
//    }
//
//
//    @Override
//    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
//       // setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
//    }


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
                    setTextSize((int) a.getDimension(R.styleable.RulerValuePicker_rv_ruler_text_size, 10));
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
        prepareNotchPaint();

        mNotchPath = new Path();
    }

    /**
     * Create the paint for notch. This will
     */
    private void prepareNotchPaint() {
        mNotchPaint.setColor(mNotchColor);
        mNotchPaint.setStrokeWidth(5f);
        mNotchPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * Programmatically add the children to the view.
     * <p>
     * <li>The main view contains the {@link android.widget.HorizontalScrollView}. That allows
     * {@link RulerViewVertical} to scroll horizontally.</li>
     * <li>{@link #mScrollView} contains {@link LinearLayout} that will act as the container
     * to hold the children inside the horizontal view.</li>
     * <li>{@link LinearLayout} container will contain three children.
     * <ul><MyWebViewClient>Left spacer:</MyWebViewClient> Width of this view will be the half width of the view. This will add staring at the start of the ruler.</ul>
     * <ul><MyWebViewClient>Right spacer:</MyWebViewClient> Width of this view will be the half width of the view. This will add ending at the end of the ruler.</ul>
     * <ul><MyWebViewClient>{@link RulerViewVertical}:</MyWebViewClient> Ruler view will contain the ruler with indicator.</ul>
     * </li>
     */
    private void addChildViews() {
        mScrollView = new ObservableVerticalScrollView(getContext(), this);
        // mHorizontalScrollView.setHorizontalScrollBarEnabled(false); //Don't display the scrollbar
        mScrollView.setVerticalScrollBarEnabled(false);
        final LinearLayout rulerContainer = new LinearLayout(getContext());
        rulerContainer.setOrientation(LinearLayout.VERTICAL);

        //Add left spacing to the container
        mLeftSpacer = new View(getContext());
        rulerContainer.addView(mLeftSpacer);

        //Add ruler to the container
        mRulerView = new RulerViewVertical(getContext());
        rulerContainer.addView(mRulerView);

        //Add right spacing to the container
        mRightSpacer = new View(getContext());
        rulerContainer.addView(mRightSpacer);

        //Add this container to the scroll view.
        mScrollView.removeAllViews();
        mScrollView.addView(rulerContainer);
        //mScrollView.setBackgroundColor(Color.LTGRAY);
        // mLeftSpacer.setBackgroundColor(Color.RED);
        //mRulerView.setBackgroundColor(Color.GREEN);
        // mRightSpacer.setBackgroundColor(Color.BLUE);

        //Add scroll view to this view.
        removeAllViews();
        addView(mScrollView);
        RulerValuePicker.log("2: addChildViews width " + mScrollView.getWidth() + " -- " + mScrollView.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.rotate(-90);
        //canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);

        //Draw the top notch
        canvas.drawPath(mNotchPath, mNotchPaint);
    }

    @Override
    protected void onLayout(boolean isChanged, int left, int top, int right, int bottom) {
        super.onLayout(isChanged, left, top, right, bottom);

        if (isChanged) {
            final int width = getHeight();

            //Set width of the left spacer to the half of this view.
            final ViewGroup.LayoutParams leftParams = mLeftSpacer.getLayoutParams();
            leftParams.height = width / 2;
            mLeftSpacer.setLayoutParams(leftParams);

            //Set width of the right spacer to the half of this view.
            final ViewGroup.LayoutParams rightParams = mRightSpacer.getLayoutParams();
            rightParams.height = width / 2;
            mRightSpacer.setLayoutParams(rightParams);

            calculateNotchPath();
            RulerValuePicker.log("2: onLayout width " + width);
            RulerValuePicker.log("2: onLayout width --  " + leftParams.width + " :: rightParams.width " + rightParams.width);
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

        mNotchPath.moveTo(0, getHeight() / 2 - 30);
        mNotchPath.lineTo(40, getHeight() / 2);
        mNotchPath.lineTo(0, getHeight() / 2 + 30);

//        mNotchPath.moveTo(getHeight() / 2 - 20, getWidth());
//        mNotchPath.lineTo(getHeight() / 2 - 3, getWidth() - 30);
//        mNotchPath.lineTo(getHeight() / 2 - 3, 0);
//        mNotchPath.lineTo(getHeight() / 2 + 3, 0);
//        mNotchPath.lineTo(getHeight() / 2 + 3, getWidth() - 30);
//        mNotchPath.lineTo(getHeight() / 2 + 20, getWidth());

//        mNotchPath.moveTo(getWidth() / 2 - 20, getHeight());
//        mNotchPath.lineTo(getWidth() / 2 - 3, getHeight() - 30);
//        mNotchPath.lineTo(getWidth() / 2 - 3, 0);
//        mNotchPath.lineTo(getWidth() / 2 + 3, 0);
//        mNotchPath.lineTo(getWidth() / 2 + 3, getHeight() - 30);
//        mNotchPath.lineTo(getWidth() / 2 + 20, getHeight());
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
        mScrollView.postDelayed(new Runnable() {
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

                mScrollView.smoothScrollTo(
                        0, valuesToScroll * mRulerView.getIndicatorIntervalWidth());
            }
        }, 400);
    }

    /**
     * @return Get the current selected value.
     */
    public int getCurrentValue() {
        int absoluteValue = mScrollView.getScrollY() / mRulerView.getIndicatorIntervalWidth();
        int value = mRulerView.getMinValue() + absoluteValue;

        if (value > mRulerView.getMaxValue()) {
            return mRulerView.getMaxValue();
        } else if (value < mRulerView.getMinValue()) {
            return mRulerView.getMinValue();
        }
        return value;
    }

    public String getCurrentValueAsString() {
        int absoluteValue = mScrollView.getScrollY() / mRulerView.getIndicatorIntervalWidth();
        int value = mRulerView.getMinValue() + absoluteValue;

        if (value > mRulerView.getMaxValue()) {
            return mRulerView.getMaxValue() + absoluteValue + "'0";
        } else if (value < mRulerView.getMinValue()) {
            return mRulerView.getMaxValue() + "'0";
        } else {
            int divVal = value / 12;
            int feet = 2 + divVal;
            int inches = value - (divVal * 12);
            return feet + "'" + inches;
        }
    }

    @Override
    public void onScrollChanged() {
        RulerValuePicker.log("onScrollChanged----------");
        if (mListener != null)
            if (isFeetMode)
                mListener.onIntermediateValueChange(getCurrentValueAsString());
            else mListener.onIntermediateValueChange(getCurrentValue());
    }

    @Override
    public void onScrollStopped() {
        RulerValuePicker.log("onScrollStopped-------------------");
        makeOffsetCorrection(mRulerView.getIndicatorIntervalWidth());
        if (mListener != null) {
            if (isFeetMode)
                mListener.onValueChange(getCurrentValueAsString());
            else
                mListener.onValueChange(getCurrentValue());
        }
    }

    private void makeOffsetCorrection(final int indicatorInterval) {
        int offsetValue = mScrollView.getScrollY() % indicatorInterval;
        if (offsetValue < indicatorInterval / 2) {
            mScrollView.scrollBy(0, -offsetValue);
        } else {
            mScrollView.scrollBy(0, indicatorInterval - offsetValue);
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

    /**
     * @param notchColorRes Color resource of the notch to display. Default color os {@link Color#WHITE}.
     * @see #setNotchColor(int)
     * @see #getNotchColor()
     */
    public void setNotchColorRes(@ColorRes final int notchColorRes) {
        setNotchColor(ContextCompat.getColor(getContext(), notchColorRes));
    }

    /**
     * @return Integer color of the notch. Default color os {@link Color#WHITE}.
     * @see #setNotchColor(int)
     * @see #setNotchColorRes(int)
     */
    @ColorInt
    public int getNotchColor() {
        return mNotchColor;
    }

    /**
     * @param notchColor Integer color of the notch to display. Default color os {@link Color#WHITE}.
     * @see #prepareNotchPaint()
     * @see #getNotchColor()
     */
    public void setNotchColor(@ColorInt final int notchColor) {
        mNotchColor = notchColor;
        prepareNotchPaint();
        invalidate();
    }

    /**
     * @return Color integer value of the ruler text color.
     * @see #setTextColor(int)
     * @see #setTextColorRes(int)
     */
    @CheckResult
    @ColorInt
    public int getTextColor() {
        return mRulerView.getTextColor();
    }

    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color integer value.
     * @see #getTextColor()
     * @see RulerViewVertical#mTextColor
     */
    public void setTextColor(@ColorInt final int color) {
        mRulerView.setTextColor(color);
    }

    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color resource id.
     * @see RulerViewVertical#mTextColor
     */
    public void setTextColorRes(@ColorRes final int color) {
        setTextColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * @return Size of the text of ruler in dp.
     * @see #setTextSize(int)
     * @see #setTextSizeRes(int)
     * @see RulerViewVertical#mTextColor
     */
    @CheckResult
    public float getTextSize() {
        return mRulerView.getTextSize();
    }

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param dimensionDp Text size dimension in dp.
     * @see #getTextSize()
     * @see RulerViewVertical#mTextSize
     */
    public void setTextSize(final int dimensionDp) {
        mRulerView.setTextSize(dimensionDp);
    }

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param dimension Text size dimension resource.
     * @see #getTextSize()
     * @see RulerViewVertical#mTextSize
     */
    public void setTextSizeRes(@DimenRes final int dimension) {
        setTextSize((int) getContext().getResources().getDimension(dimension));
    }

    /**
     * @return Color integer value of the indicator color.
     * @see #setIndicatorColor(int)
     * @see #setIndicatorColorRes(int)
     * @see RulerViewVertical#mIndicatorColor
     */
    @CheckResult
    @ColorInt
    public int getIndicatorColor() {
        return mRulerView.getIndicatorColor();
    }

    /**
     * Set the indicator color.
     *
     * @param color Color integer value.
     * @see #getIndicatorColor()
     * @see RulerViewVertical#mIndicatorColor
     */
    public void setIndicatorColor(@ColorInt final int color) {
        mRulerView.setIndicatorColor(color);
    }

    public void setLongIndicatorColor(@ColorInt final int color) {
        mRulerView.setLongIndicatorColor(color);
    }

    /**
     * Set the indicator color.
     *
     * @param color Color resource id.
     * @see #getIndicatorColor()
     * @see RulerViewVertical#mIndicatorColor
     */
    public void setIndicatorColorRes(@ColorRes final int color) {
        setIndicatorColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * @return Width of the indicator in pixels.
     * @see #setIndicatorWidth(int)
     * @see #setIndicatorWidthRes(int)
     * @see RulerViewVertical#mIndicatorWidthPx
     */
    @CheckResult
    public float getIndicatorWidth() {
        return mRulerView.getIndicatorWidth();
    }

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param widthPx Width in pixels.
     * @see #getIndicatorWidth()
     * @see RulerViewVertical#mIndicatorWidthPx
     */
    public void setIndicatorWidth(final int widthPx) {
        mRulerView.setIndicatorWidth(widthPx);
    }

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param width Dimension resource for indicator width.
     * @see #getIndicatorWidth()
     * @see RulerViewVertical#mIndicatorWidthPx
     */
    public void setIndicatorWidthRes(@DimenRes final int width) {
        setIndicatorWidth(getContext().getResources().getDimensionPixelSize(width));
    }

    /**
     * @return Get the minimum value displayed on the ruler.
     * @see #setMinMaxValue(int, int)
     * @see RulerViewVertical#mMinValue
     */
    @CheckResult
    public int getMinValue() {
        return mRulerView.getMinValue();
    }

    /**
     * @return Get the maximum value displayed on the ruler.
     * @see #setMinMaxValue(int, int)
     * @see RulerViewVertical#mMaxValue
     */
    @CheckResult
    public int getMaxValue() {
        return mRulerView.getMaxValue();
    }

    /**
     * Set the maximum value to display on the ruler. This will decide the range of values and number
     * of indicators that ruler will draw.
     *
     * @param minValue Value to display at the left end of the ruler. This can be positive, negative
     *                 or zero. Default minimum value is 0.
     * @param maxValue Value to display at the right end of the ruler. This can be positive, negative
     *                 or zero.This value must be greater than min value. Default minimum value is 100.
     * @see #getMinValue()
     * @see #getMaxValue()
     */
    private void setMinMaxValue() {
        mRulerView.setValueRange(mRulerView.getMinValue(), mRulerView.getMaxValue());
        invalidate();
        selectValue(mRulerView.getMinValue());
    }

    public void setMinMaxValue(final int minValue, final int maxValue) {
        mRulerView.setValueRange(minValue, maxValue);
        //invalidate();
        requestLayout();
        selectValue(minValue);
    }

    // @Override
//    public void invalidate() {
//        refresh();
//        super.invalidate();
//
//    }

    public void refresh() {
        for (int i = 0; i < getChildCount(); i++) {
            try {
                getChildAt(i).forceLayout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //todo do it later
        //mScrollView.setVisibility(GONE);
       // mScrollView.setVisibility(VISIBLE);
        requestLayout();
        super.invalidate();
    }



    /**
     * @return Get distance between two indicator in pixels.
     * @see #setIndicatorIntervalDistance(int)
     * @see RulerViewVertical#mIndicatorInterval
     */
    @CheckResult
    public int getIndicatorIntervalWidth() {
        return mRulerView.getIndicatorIntervalWidth();
    }

    /**
     * Set the spacing between two vertical lines/indicators. Default value is 14 pixels.
     *
     * @param indicatorIntervalPx Distance in pixels. This cannot be negative number or zero.
     * @see RulerViewVertical#mIndicatorInterval
     */
    public void setIndicatorIntervalDistance(final int indicatorIntervalPx) {
        mRulerView.setIndicatorIntervalDistance(indicatorIntervalPx);
    }

    /**
     * @return Ratio of long indicator height to the ruler height.
     * @see #setIndicatorHeight(float, float)
     * @see RulerViewVertical#mLongIndicatorHeightRatio
     */
    @CheckResult
    public float getLongIndicatorHeightRatio() {
        return mRulerView.getLongIndicatorHeightRatio();
    }

    /**
     * @return Ratio of short indicator height to the ruler height.
     * @see #setIndicatorHeight(float, float)
     * @see RulerViewVertical#mShortIndicatorHeight
     */
    @CheckResult
    public float getShortIndicatorHeightRatio() {
        return mRulerView.getShortIndicatorHeightRatio();
    }

    /**
     * Set the height of the long and short indicators.
     *
     * @param longHeightRatio  Ratio of long indicator height to the ruler height. This value must
     *                         be between 0 to 1. The value should greater than {@link #getShortIndicatorHeightRatio()}.
     *                         Default value is 0.6 (i.e. 60%). If the value is 0, indicator won't
     *                         be displayed. If the value is 1, indicator height will be same as the
     *                         ruler height.
     * @param shortHeightRatio Ratio of short indicator height to the ruler height. This value must
     *                         be between 0 to 1. The value should less than {@link #getLongIndicatorHeightRatio()}.
     *                         Default value is 0.4 (i.e. 40%). If the value is 0, indicator won't
     *                         be displayed. If the value is 1, indicator height will be same as
     *                         the ruler height.
     * @see #getLongIndicatorHeightRatio()
     * @see #getShortIndicatorHeightRatio()
     */
    public void setIndicatorHeight(final float longHeightRatio,
                                   final float shortHeightRatio) {
        mRulerView.setIndicatorHeight(longHeightRatio, shortHeightRatio);
    }

    /**
     * Set the {@link RulerValuePickerListener} to get callbacks when the value changes.
     *
     * @param listener {@link RulerValuePickerListener}
     */
    public void setValuePickerListener(@Nullable final RulerValuePickerListener listener) {
        mListener = listener;
    }

    /**
     * User interface state that is stored by RulerView for implementing
     * {@link View#onSaveInstanceState}.
     */
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

    int type = 1;
    private boolean isFeetMode = false;

    public synchronized void refreshCmToInches() {
        mRulerView.setFeetMode(false);
        if (type == 1)
            setMinMaxValue((int) (mRulerView.getMinValue() * 0.3937), (int) (mRulerView.getMaxValue() * 0.3937));
        else
            setMinMaxValue();
        type = 2;
        invalidate();
    }

    public void setFeetMode(boolean feetMode) {
        this.isFeetMode = feetMode;
        mRulerView.setFeetMode(feetMode);
    }

    public synchronized void refreshInchesToCm() {
        mRulerView.setFeetMode(false);
        if (type == 2 || type == 3)
            setMinMaxValue((int) (mRulerView.getMinValue() * 2.54), (int) (mRulerView.getMaxValue() * 2.54));
        else
            setMinMaxValue();
        type = 1;
        invalidate();
    }

    public synchronized void refreshInchesToFeet() {
        mRulerView.setFeetMode(true);
        if (type == 1)
            setMinMaxValue((int) (mRulerView.getMinValue() * 0.3937), (int) (mRulerView.getMaxValue() * 0.3937));
        else
            setMinMaxValue();
        type = 3;
        invalidate();
    }
}
