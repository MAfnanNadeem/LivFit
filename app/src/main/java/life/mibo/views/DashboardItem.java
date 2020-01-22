/*
 *  Created by Sumeet Kumar on 1/9/20 8:38 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 12/26/19 12:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

import life.mibo.hardware.core.Logger;
import life.mibo.hexa.R;
import life.mibo.hexa.utils.Utils;


public class DashboardItem extends FrameLayout {

    private ImageView imageView;
    //private ImageView imageView, iconView;
    // private TextView textView, headerView;
    //private LinearLayout layout;
    //Constraints constraints;
    private ConstraintLayout constraintLayout;

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

    //private int imageRes, iconRes, textRes;
    //private int imageRes, iconRes, textRes;
    //private CharSequence text;
    private int gravity_ = -1, size_ = -1;

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DashboardItem);
        //imageRes = array.getResourceId(R.styleable.DashboardItem_item_image, 0);
        //iconRes = array.getResourceId(R.styleable.DashboardItem_item_icon, 0);
        //textRes = array.getResourceId(R.styleable.DashboardItem_item_text, 0);
        gravity_ = array.getInteger(R.styleable.DashboardItem_item_gravity, -1);
        // text = array.getText(R.styleable.DashboardItem_item_text);
        array.recycle();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dashboard_item, this, true);
        //layout = view.findViewById(R.id.linearLayout);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        // constraints = view.findViewById(R.id.constraints1);

        //ImageView test = view.findViewById(R.id.iv_dashboard_item_test);
        imageView = view.findViewById(R.id.iv_dashboard_item);
//        iconView = view.findViewById(R.id.iv_dashboard_item_icon);
//        textView = view.findViewById(R.id.iv_dashboard_item_text);
//        headerView = view.findViewById(R.id.iv_dashboard_item_icon_text);
//
//        if (imageRes != 0)
//            imageView.setImageResource(imageRes);
//        if (iconRes != 0)
//            iconView.setImageResource(iconRes);
//        if (text != null)
//            textView.setText(text);
//
        if (gravity_ == 0) {
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            //test.setScaleType(ImageView.ScaleType.FIT_START);
        } else if (gravity_ == 1) {
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
            //test.setScaleType(ImageView.ScaleType.FIT_END);
        }
        //size_ = getResources().getDisplayMetrics().widthPixels / 3;
    }

    public void set(int image, int icon, String title, String header) {
//        imageRes = image;
//        iconRes = icon;
//        this.text = title;
//        imageView.setImageDrawable(null);
//        iconView.setImageDrawable(null);
//        if (imageRes != 0)
//            imageView.setImageResource(imageRes);
//
//        textView.setText(text);
//
//        if (!TextUtils.isEmpty(header)) {
//            headerView.setText(header);
//            headerView.setVisibility(VISIBLE);
//            iconView.setVisibility(GONE);
//        } else if (iconRes != 0) {
//            iconView.setImageResource(iconRes);
//            iconView.setVisibility(VISIBLE);
//        }
//        if (iconRes == 0) {
//            iconView.setVisibility(GONE);
//        }
//        invalidate();
//
//        width2 = imageView.getDrawable().getIntrinsicWidth();
//        height2 = imageView.getDrawable().getIntrinsicHeight();
//        ConstraintSet set = new ConstraintSet();
//        set.clone(constraints);
//
//        if (width2 > 0 && height2 > 0) {
//            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.width = width2;
//            params.height = height2;
//            layout.setLayoutParams(params);
//            Log.e("DashboardItem", "set: w " + width + ", h " + height + ", iconW " + width2 + ", iconH " + height2);
//            invalidate();
//        }


        //Log.d("DashboardItem", "set: w " + width + ", h " + height + ", iconW " + imageView.getDrawable().getIntrinsicWidth() + ", iconH " + imageView.getDrawable().getIntrinsicHeight());
        // Log.d("DashboardItem", "set2: w " + width + ", h " + height + ", iconW " + imageView.getMeasuredWidth() + ", iconH " + imageView.getMeasuredHeight());
        //requestLayout();
        //invalidate();
        //addViews(icon, title, header);
    }

    public void addViews(int imageId, int iconId, String header, String title) {
        //Logger.e("DashboardItem addViews image="+imageId+" icon="+iconId+" header="+header+" text="+title);
        ConstraintSet set = new ConstraintSet();
        LinearLayout layout = new LinearLayout(this.getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);

        if (imageId != 0) {
            imageView.setImageResource(imageId);
        }

        if (iconId != 0) {
            ImageView iv = new ImageView(this.getContext());
            iv.setId(View.generateViewId());
            iv.setImageResource(iconId);
            iv.setLayoutParams(new ViewGroup.LayoutParams(getDp(40), getDp(40)));
            layout.addView(iv);
        }


        if (!Utils.isEmpty(header)) {
            TextView tv2 = new TextView(getContext());
            tv2.setText(header);
            tv2.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            tv2.setId(View.generateViewId());
            tv2.setTextColor(Color.WHITE);
            tv2.setTypeface(null, Typeface.BOLD);
            //tv2.setPadding(0,getDp(8),0,0);
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            layout.addView(tv2);
        }

        if (!Utils.isEmpty(title)) {
            TextView tv1 = new TextView(getContext());
            tv1.setText(title);
            tv1.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            tv1.setId(View.generateViewId());
            tv1.setPadding(0, getDp(8), 0, 0);
            tv1.setTextColor(Color.WHITE);
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            layout.addView(tv1);
        }

//        if (imageId != 0) {
//            width2 = imageView.getDrawable().getIntrinsicWidth();
//            height2 = imageView.getDrawable().getIntrinsicHeight();
//            Logger.e("DashboardItem addViews: w=" + width2 + " : h=" + height2);
//            Logger.e("DashboardItem addViews: imageView0 w=" + imageView.getLayoutParams().width + " : h=" + imageView.getLayoutParams().height);
//            Logger.e("DashboardItem addViews: imageView w=" + imageView.getWidth() + " : h=" + imageView.getHeight());
//            if (width2 > 0 && height2 > 0) {
//                //ViewGroup.LayoutParams params = imageView.getLayoutParams();
//                //params.width = getDp(width2);
//                //params.height = getDp(height2);
//                //imageView.getLayoutParams().width = width2;
//                //imageView.getLayoutParams().height = height2;
//                // imageView.setLayoutParams(params);
//                //imageView.setLayoutParams(new ViewGroup.LayoutParams(getDp(width2), getDp(height2)));
//            }
//            Logger.e("DashboardItem addViews: imageView2 w=" + imageView.getWidth() + " : h=" + imageView.getHeight());
//        }

//        if (gravity_ == 0) {
//            //imageView.setScaleType(ImageView.ScaleType.FIT_START);
//            //test.setScaleType(ImageView.ScaleType.FIT_START);
//            set.connect(imageView.getUid(), ConstraintSet.TOP, constraintLayout.getUid(), ConstraintSet.TOP, 0);
//            set.connect(imageView.getUid(), ConstraintSet.BOTTOM, constraintLayout.getUid(), ConstraintSet.BOTTOM, 0);
//            set.connect(imageView.getUid(), ConstraintSet.START, constraintLayout.getUid(), ConstraintSet.START, 0);
//            //set.connect(imageView.getUid(), ConstraintSet.END, constraintLayout.getUid(), ConstraintSet.END, 0);
//        } else if (gravity_ == 1) {
//            //imageView.setScaleType(ImageView.ScaleType.FIT_END);
//            //test.setScaleType(ImageView.ScaleType.FIT_END);
//            set.connect(imageView.getUid(), ConstraintSet.TOP, constraintLayout.getUid(), ConstraintSet.TOP, 0);
//            set.connect(imageView.getUid(), ConstraintSet.BOTTOM, constraintLayout.getUid(), ConstraintSet.BOTTOM, 0);
//            //set.connect(imageView.getUid(), ConstraintSet.START, constraintLayout.getUid(), ConstraintSet.START, 0);
//            set.connect(imageView.getUid(), ConstraintSet.END, constraintLayout.getUid(), ConstraintSet.END, 0);
//
//        }


        //layout.setBackgroundColor(Color.LTGRAY);
        layout.setId(View.generateViewId());
        layout.setHorizontalGravity(Gravity.CENTER);

        constraintLayout.addView(layout);
        set.clone(constraintLayout);
        set.connect(layout.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP, 0);
        set.connect(layout.getId(), ConstraintSet.BOTTOM, imageView.getId(), ConstraintSet.BOTTOM, 0);

        if (gravity_ == 0) {
            set.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, getDp(4));
            set.connect(layout.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.START, 0);
            set.connect(layout.getId(), ConstraintSet.END, imageView.getId(), ConstraintSet.END, getDp(12));
        } else if (gravity_ == 1) {
            set.connect(imageView.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, getDp(4));
            set.connect(layout.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.START, getDp(12));
            set.connect(layout.getId(), ConstraintSet.END, imageView.getId(), ConstraintSet.END, 0);
        } else {
            set.connect(layout.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.START, 0);
            set.connect(layout.getId(), ConstraintSet.END, imageView.getId(), ConstraintSet.END, 0);
        }
        Logger.e("DashboardItem addViews: gravity_ = " + gravity_);
        set.applyTo(constraintLayout);

        //headerView.setVisibility(GONE);
       // textView.setVisibility(GONE);
    }

    private float scale = 0f;

    int getDp(int dp) {
        if (scale == 0f) {
            scale = getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }
    //int width, height;
    //int width2 = 0, height2 = 0;

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
