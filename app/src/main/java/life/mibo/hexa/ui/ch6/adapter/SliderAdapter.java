/*
 *  Created by Sumeet Kumar on 4/5/20 4:01 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 4:01 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.ch6.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import life.mibo.hexa.models.muscle.Suit;

public class SliderAdapter extends PagerAdapter {
    private Context mContext;
    private List<Suit> list;

    public SliderAdapter(List<Suit> suits, Context context) {
        this.mContext = context;
        this.list = suits;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final Suit suit = list.get(position);
        ImageView imageView = new ImageView(mContext);
        RequestOptions request = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext).load(suit.getImage()).apply(request).into(imageView);
        container.addView(imageView);
        return imageView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}