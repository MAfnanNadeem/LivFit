/*
 *  Created by Sumeet Kumar on 4/16/20 9:04 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/16/20 9:04 AM
 *  Mibo Hexa - app
 */

package life.mibo.views.body.picker;

public interface ScrollChangedListener {

        /**
         * Called upon change in scroll position.
         */
        void onScrollChanged();

        /**
         * Called when the scrollview stops scrolling.
         */
        void onScrollStopped();
    }