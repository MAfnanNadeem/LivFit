/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.view;

import java.util.ArrayList;

import life.mibo.imagepicker.bean.MediaBean;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/5/14 下午9:56
 */
public interface ActivityFragmentView {

    void showMediaGridFragment();

    void showMediaPageFragment(ArrayList<MediaBean> list, int position);

    void showMediaPreviewFragment();
}
