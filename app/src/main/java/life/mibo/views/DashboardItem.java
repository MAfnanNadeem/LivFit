/*
 *  Created by Sumeet Kumar on 1/9/20 8:38 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 12/26/19 12:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;

import life.mibo.hexa.R;


public class DashboardItem extends FrameLayout {

    private ImageView imageView, iconView;
    private TextView textView, headerView;
    private LinearLayout layout;
    Constraints constraints;

    public DashboardItem(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public DashboardItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public DashboardItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private int imageRes, iconRes, textRes;
    private CharSequence text;
    private int gravity_ = -1, size_ = -1;

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DashboardItem);
        imageRes = array.getResourceId(R.styleable.DashboardItem_item_image, 0);
        iconRes = array.getResourceId(R.styleable.DashboardItem_item_icon, 0);
        textRes = array.getResourceId(R.styleable.DashboardItem_item_text, 0);
        gravity_ = array.getInteger(R.styleable.DashboardItem_item_gravity, -1);
        text = array.getText(R.styleable.DashboardItem_item_text);
        array.recycle();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dashboard_item, this, true);
        layout = view.findViewById(R.id.linearLayout);
        constraints = view.findViewById(R.id.constraints1);

        //ImageView test = view.findViewById(R.id.iv_dashboard_item_test);
        imageView = view.findViewById(R.id.iv_dashboard_item);
        iconView = view.findViewById(R.id.iv_dashboard_item_icon);
        textView = view.findViewById(R.id.iv_dashboard_item_text);
        headerView = view.findViewById(R.id.iv_dashboard_item_icon_text);

        if (imageRes != 0)
            imageView.setImageResource(imageRes);
        if (iconRes != 0)
            iconView.setImageResource(iconRes);
        if (text != null)
            textView.setText(text);

        if (gravity_ == 0) {
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            //test.setScaleType(ImageView.ScaleType.FIT_START);
        } else if (gravity_ == 1) {
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
            //test.setScaleType(ImageView.ScaleType.FIT_END);
        }
        size_ = getResources().getDisplayMetrics().widthPixels / 3;
    }

    public void set(int image, int icon, String title, String header) {
        imageRes = image;
        iconRes = icon;
        this.text = title;
        imageView.setImageDrawable(null);
        iconView.setImageDrawable(null);
        if (imageRes != 0)
            imageView.setImageResource(imageRes);

        textView.setText(text);

        if (!TextUtils.isEmpty(header)) {
            headerView.setText(header);
            headerView.setVisibility(VISIBLE);
            iconView.setVisibility(GONE);
        } else if (iconRes != 0) {
            iconView.setImageResource(iconRes);
            iconView.setVisibility(VISIBLE);
        }
        if (iconRes == 0) {
            iconView.setVisibility(GONE);
        }
        invalidate();

        width2 = imageView.getDrawable().getIntrinsicWidth();
        height2 = imageView.getDrawable().getIntrinsicHeight();
        ConstraintSet set = new ConstraintSet();
        set.clone(constraints);

        if (width2 > 0 && height2 > 0) {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = width2;
            params.height = height2;
            layout.setLayoutParams(params);
            Log.e("DashboardItem", "set: w " + width + ", h " + height + ", iconW " + width2 + ", iconH " + height2);
            invalidate();
        }
        //Log.d("DashboardItem", "set: w " + width + ", h " + height + ", iconW " + imageView.getDrawable().getIntrinsicWidth() + ", iconH " + imageView.getDrawable().getIntrinsicHeight());
        // Log.d("DashboardItem", "set2: w " + width + ", h " + height + ", iconW " + imageView.getMeasuredWidth() + ", iconH " + imageView.getMeasuredHeight());
        //requestLayout();
        //invalidate();
    }

    int width, height;
    int width2 = 0, height2 = 0;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // super.onSizeChanged(size_, size_, oldw, oldh);
//        width = w;
//        height = h;
//        if (width2 > 0 && height2 > 0)
//            super.onSizeChanged(width2, height2, oldw, oldh);
//        else super.onSizeChanged(w, h, oldw, oldh);
        super.onSizeChanged(w, h, oldw, oldh);
        //Log.d("DashboardItem", "onSizeChanged: w " + w + ", h " + h + ", oldW " + oldw + ", oldH " + oldh+ ", width2 " + width2 + ", height2 " + height2);
        //int actualWidth = w / 2;
        //int actualHeight = h / 2;
        //setImageView(actualWidth, actualHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setMeasuredDimension(width|MeasureSpec.EXACTLY, height|MeasureSpec.EXACTLY);
        //setMeasuredDimension(size_|MeasureSpec.EXACTLY, size_|MeasureSpec.EXACTLY);
        //super.onMeasure(size_, size_);
        Log.d("DashboardItem", "onMeasure: size " + size_);
        Log.d("DashboardItem", "onMeasure: widthMeasureSpec " + widthMeasureSpec + " , heightMeasureSpec " + heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
