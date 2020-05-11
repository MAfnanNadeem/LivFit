/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.ui.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import life.mibo.imagepicker.Configuration;
import life.mibo.imagepicker.R;
import life.mibo.imagepicker.bean.MediaBean;
import life.mibo.imagepicker.rxbus.RxBus;
import life.mibo.imagepicker.rxbus.event.CloseMediaViewPageFragmentEvent;
import life.mibo.imagepicker.rxbus.event.MediaCheckChangeEvent;
import life.mibo.imagepicker.rxbus.event.MediaViewPagerChangedEvent;
import life.mibo.imagepicker.ui.activity.MediaActivity;
import life.mibo.imagepicker.ui.adapter.MediaPreviewAdapter;
import life.mibo.imagepicker.utils.DeviceUtils;
import life.mibo.imagepicker.utils.ThemeUtils;

public class MediaPreviewFragment extends BaseFragment implements ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private static final String EXTRA_PAGE_INDEX = EXTRA_PREFIX + ".PageIndex";

    DisplayMetrics mScreenSize;

    private AppCompatCheckBox mCbCheck;
    private ViewPager mViewPager;
    private List<MediaBean> mMediaBeanList;
    private RelativeLayout mRlRootView;

    private MediaActivity mMediaActivity;
    private int mPagerPosition;

    public static MediaPreviewFragment newInstance(Configuration configuration, int position) {
        MediaPreviewFragment fragment = new MediaPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_CONFIGURATION, configuration);
        bundle.putInt(EXTRA_PAGE_INDEX, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MediaActivity) {
            mMediaActivity = (MediaActivity) context;
        }
    }

    @Override
    public int getContentView() {
        return R.layout.gallery_fragment_media_preview;
    }


    @Override
    public void onViewCreatedOk(View view, @Nullable Bundle savedInstanceState) {
        mCbCheck = view.findViewById(R.id.cb_check);
        mViewPager = view.findViewById(R.id.view_pager);
        mRlRootView = view.findViewById(R.id.rl_root_view);
        mScreenSize = DeviceUtils.getScreenSize(getContext());
        mMediaBeanList = new ArrayList<>();
        if (mMediaActivity.getCheckedList() != null) {
            mMediaBeanList.addAll(mMediaActivity.getCheckedList());
        }
        MediaPreviewAdapter mMediaPreviewAdapter = new MediaPreviewAdapter(mMediaBeanList,
                mScreenSize.widthPixels, mScreenSize.heightPixels, mConfiguration,
                ThemeUtils.resolveColor(getActivity(), R.attr.gallery_page_bg, R.color.gallery_default_page_bg),
                ContextCompat.getDrawable(getActivity(), ThemeUtils.resolveDrawableRes(getActivity(), R.attr.gallery_default_image, R.drawable.gallery_default_image)));
        mViewPager.setAdapter(mMediaPreviewAdapter);
        mCbCheck.setOnClickListener(this);

        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewPager.setCurrentItem(mPagerPosition, false);
        mViewPager.addOnPageChangeListener(this);
        //#ADD UI预览数量的BUG
        RxBus.getDefault().post(new MediaViewPagerChangedEvent(mPagerPosition, mMediaBeanList.size(), true));
    }

    @Override
    public void setTheme() {
        super.setTheme();
        int checkTint = ThemeUtils.resolveColor(getContext(), R.attr.gallery_checkbox_button_tint_color, R.color.gallery_default_checkbox_button_tint_color);
        CompoundButtonCompat.setButtonTintList(mCbCheck, ColorStateList.valueOf(checkTint));
        int cbTextColor = ThemeUtils.resolveColor(getContext(), R.attr.gallery_checkbox_text_color, R.color.gallery_default_checkbox_text_color);
        mCbCheck.setTextColor(cbTextColor);

        int pageColor = ThemeUtils.resolveColor(getContext(), R.attr.gallery_page_bg, R.color.gallery_default_page_bg);
        mRlRootView.setBackgroundColor(pageColor);
    }

    @Override
    protected void onFirstTimeLaunched() {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX);
        }
    }

    @Override
    protected void onSaveState(Bundle outState) {
        if (outState != null) {
            outState.putInt(EXTRA_PAGE_INDEX, mPagerPosition);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPagerPosition = position;
        MediaBean mediaBean = mMediaBeanList.get(position);
        mCbCheck.setChecked(false);
        //判断是否选择
        if (mMediaActivity != null && mMediaActivity.getCheckedList() != null) {
            mCbCheck.setChecked(mMediaActivity.getCheckedList().contains(mediaBean));
        }

        RxBus.getDefault().post(new MediaViewPagerChangedEvent(position, mMediaBeanList.size(), true));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 改变选择
     */
    @Override
    public void onClick(View view) {
        int position = mViewPager.getCurrentItem();
        MediaBean mediaBean = mMediaBeanList.get(position);
        if (mConfiguration.getMaxSize() == mMediaActivity.getCheckedList().size()
                && !mMediaActivity.getCheckedList().contains(mediaBean)) {
            Toast.makeText(getContext(), getResources()
                    .getString(R.string.gallery_image_max_size_tip, mConfiguration.getMaxSize()), Toast.LENGTH_SHORT).show();
            mCbCheck.setChecked(false);
        } else {
            RxBus.getDefault().post(new MediaCheckChangeEvent(mediaBean));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPagerPosition = 0;
        RxBus.getDefault().post(new CloseMediaViewPageFragmentEvent());
    }
}
