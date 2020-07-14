/*
 *  Created by Sumeet Kumar on 2/15/20 11:29 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/15/20 11:25 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl.slider;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import life.mibo.android.R;
import life.mibo.hardware.core.Logger;
import life.mibo.views.TouchImageView;


public class SliderAdapter extends PagerAdapter {
    private ArrayList<Integer> IMAGES;
    private ArrayList<String> URLs;
    private LayoutInflater inflater;
    //TimerTask updatePage;
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
        if(type ==0) {
            return IMAGES.size();
        }
        else{
            return URLs.size();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.rxl_slider_main, view, false);

        assert imageLayout != null;
        //TouchImageView
        final ImageView imageView = imageLayout
                .findViewById(R.id.image);

        Logger.e("instantiateItem " + type);
        if (type == 0) {
            imageView.setImageResource(IMAGES.get(position));
        }
        if (type == 1) {
            //Picasso.get().load(URLs.get(position)).into(imageView);
            Glide.with(imageView).load(URLs.get(position)).fitCenter().fallback(R.drawable.ic_broken_image_black_24dp).error(R.drawable.ic_broken_image_black_24dp).into(imageView);
        }


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
        type = 0;
        this.IMAGES=IMAGES;
    }

    public void setUrls(ArrayList<String> URLs){
        this.URLs=URLs;
        type =1;
    }



}
