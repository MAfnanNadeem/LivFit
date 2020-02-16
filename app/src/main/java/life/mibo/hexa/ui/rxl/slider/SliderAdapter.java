/*
 *  Created by Sumeet Kumar on 2/15/20 11:29 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/15/20 11:25 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.slider;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//import com.squareup.picasso.Picasso;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import life.mibo.hexa.R;

public class SliderAdapter extends PagerAdapter {
    private ArrayList<Integer> IMAGES;
    private LayoutInflater inflater;
    int currentPage=0;
    int type =0;
    TabLayout tabDots;
    ViewPager viewPager;

    public SliderAdapter(Context context, ViewPager viewPager, TabLayout tabDots) {
        inflater = LayoutInflater.from(context);
        this.viewPager=viewPager;
        this.tabDots=tabDots;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.rxl_slider_main, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout
                .findViewById(R.id.image);

        imageView.setImageResource(IMAGES.get(position));

        view.addView(imageLayout, 0);


        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void setImages(ArrayList<Integer> IMAGES){
        this.IMAGES=IMAGES;
    }




}
