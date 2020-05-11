/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.ui;


import life.mibo.imagepicker.ui.adapter.MediaGridAdapter;
import life.mibo.imagepicker.ui.base.IMultiImageCheckedListener;
import life.mibo.imagepicker.ui.base.IRadioImageCheckedListener;
import life.mibo.imagepicker.ui.fragment.MediaGridFragment;

public class RxGalleryListener {

    public static RxGalleryListener getInstance() {
        return RxGalleryListenerHolder.RX_GALLERY_LISTENER;
    }

    /**
     * 图片多选的事件
     */
    public void setMultiImageCheckedListener(IMultiImageCheckedListener checkedImageListener) {
        MediaGridAdapter.setCheckedListener(checkedImageListener);
    }

    /**
     * 图片单选的事件
     */
    public void setRadioImageCheckedListener(IRadioImageCheckedListener checkedImageListener) {
        MediaGridFragment.setRadioListener(checkedImageListener);
    }

    private static final class RxGalleryListenerHolder {
        private static final RxGalleryListener RX_GALLERY_LISTENER = new RxGalleryListener();
    }
}
