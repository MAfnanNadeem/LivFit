/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.ui.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import life.mibo.imagepicker.Configuration;
import life.mibo.imagepicker.R;
import life.mibo.imagepicker.bean.BucketBean;
import life.mibo.imagepicker.ui.widget.SquareImageView;
import life.mibo.imagepicker.utils.ThemeUtils;

public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.BucketViewHolder> {

    private final List<BucketBean> mBucketList;
    private final Drawable mDefaultImage;
    private final Configuration mConfiguration;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private BucketBean mSelectedBucket;

    public BucketAdapter(
            List<BucketBean> bucketList,
            Configuration configuration,
            @ColorInt int color) {
        this.mBucketList = bucketList;
        this.mConfiguration = configuration;
        this.mDefaultImage = new ColorDrawable(color);
    }

    @NonNull
    @Override
    public BucketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_adapter_bucket_item, parent, false);
        return new BucketViewHolder(parent, view);
    }

    @Override
    public void onBindViewHolder(BucketViewHolder holder, int position) {
        BucketBean bucketBean = mBucketList.get(position);
        String bucketName = bucketBean.getBucketName();
        if (position != 0) {
            SpannableString nameSpannable = new SpannableString(bucketName + "\n" + bucketBean.getImageCount() + " Images");
            nameSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), bucketName.length(), nameSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameSpannable.setSpan(new RelativeSizeSpan(0.8f), bucketName.length(), nameSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mTvBucketName.setText(nameSpannable);
        } else {
            holder.mTvBucketName.setText(bucketName);
        }
        if (mSelectedBucket != null && TextUtils.equals(mSelectedBucket.getBucketId(), bucketBean.getBucketId())) {
            holder.mRbSelected.setVisibility(View.VISIBLE);
            holder.mRbSelected.setChecked(true);
        } else {
            holder.mRbSelected.setVisibility(View.GONE);
        }

        String path = bucketBean.getCover();
        mConfiguration.getImageLoader()
                .displayImage(holder.itemView.getContext(), path, holder.mIvBucketCover, mDefaultImage, mConfiguration.getImageConfig(),
                        true, mConfiguration.isPlayGif(), 100, 100, bucketBean.getOrientation());
    }

    public void setSelectedBucket(BucketBean bucketBean) {
        this.mSelectedBucket = bucketBean;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBucketList.size();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnRecyclerViewItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    class BucketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mTvBucketName;
        final SquareImageView mIvBucketCover;
        final AppCompatRadioButton mRbSelected;

        private final ViewGroup mParentView;

        BucketViewHolder(ViewGroup parent, View itemView) {
            super(itemView);
            this.mParentView = parent;
            mTvBucketName = itemView.findViewById(R.id.tv_bucket_name);
            mIvBucketCover = itemView.findViewById(R.id.iv_bucket_cover);
            mRbSelected = itemView.findViewById(R.id.rb_selected);

            itemView.setOnClickListener(this);

            int checkTint = ThemeUtils.resolveColor(itemView.getContext(), R.attr.gallery_checkbox_button_tint_color, R.color.gallery_default_checkbox_button_tint_color);
            CompoundButtonCompat.setButtonTintList(mRbSelected, ColorStateList.valueOf(checkTint));
        }

        @Override
        public void onClick(View v) {
            if (mOnRecyclerViewItemClickListener != null) {
                mOnRecyclerViewItemClickListener.onItemClick(v, getLayoutPosition());
            }

            setRadioDisChecked(mParentView);
            mRbSelected.setVisibility(View.VISIBLE);
            mRbSelected.setChecked(true);
        }

        private void setRadioDisChecked(ViewGroup parentView) {
            if (parentView == null || parentView.getChildCount() < 1) {
                return;
            }

            for (int i = 0; i < parentView.getChildCount(); i++) {
                View itemView = parentView.getChildAt(i);
                RadioButton rbSelect = itemView.findViewById(R.id.rb_selected);
                if (rbSelect != null) {
                    rbSelect.setVisibility(View.GONE);
                    rbSelect.setChecked(false);
                }
            }
        }
    }
}
