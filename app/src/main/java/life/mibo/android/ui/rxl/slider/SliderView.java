/*
 *  Created by Sumeet Kumar on 2/15/20 11:29 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/15/20 11:25 AM
 *  Mibo Hexa - app
 */
package life.mibo.android.ui.rxl.slider;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import life.mibo.android.R;

public class SliderView extends LinearLayout{
    private ArrayList<Integer> IMAGES;
    private ArrayList<String> URLs;
    TimerTask updatePage;
    SliderAdapter sliderAdapter;
    int currentPage=0;
    int type;
    ViewPager viewPager;
    TabLayout tabDots;
    public SliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.rxl_slider_view,this);
        viewPager=findViewById(R.id.viewPager);
        tabDots=findViewById(R.id.tabDots);
        sliderAdapter = new SliderAdapter(context,viewPager,tabDots);

        //viewPager.setAdapter(sliderAdapter);
        tabDots.setupWithViewPager(viewPager,true);
        //sliderAdapter.execute();
    }


    public void setImages(ArrayList<Integer> IMAGES){
        this.IMAGES=IMAGES;
        sliderAdapter.setImages(IMAGES);
        viewPager.setAdapter(sliderAdapter);
    }

    public void setUrls(List<String> images) {
        this.URLs = new ArrayList<>(images);
        sliderAdapter.setUrls(this.URLs);
        viewPager.setAdapter(sliderAdapter);
        type =1;
    }
    public TimerTask getTimerTask(){
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 3) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        updatePage=new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        };
        return updatePage;
    }
}
