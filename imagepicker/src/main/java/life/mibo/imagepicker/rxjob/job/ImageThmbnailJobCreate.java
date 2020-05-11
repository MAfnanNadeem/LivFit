/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.rxjob.job;

import android.content.Context;

import life.mibo.imagepicker.bean.MediaBean;
import life.mibo.imagepicker.rxjob.Job;
import life.mibo.imagepicker.rxjob.JobCreator;


/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/7/31 上午11:46
 */
public class ImageThmbnailJobCreate implements JobCreator {

    private final MediaBean mediaBean;
    private final Context context;

    public ImageThmbnailJobCreate(Context context, MediaBean mediaBean) {
        this.context = context;
        this.mediaBean = mediaBean;
    }

    @Override
    public Job create() {
        Job.Params params = new Job.Params(mediaBean.getOriginalPath(), mediaBean);
        return new ImageThmbnailJob(context, params);
    }


}
