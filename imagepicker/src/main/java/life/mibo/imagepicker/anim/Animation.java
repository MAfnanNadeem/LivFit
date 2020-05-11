/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.anim;

import android.view.View;


public abstract class Animation {

    public static final int DIRECTION_DOWN = 4;
    public static final int DURATION_DEFAULT = 300; // 300 ms
    // constants
    static final int DIRECTION_LEFT = 1;
    static final int DIRECTION_RIGHT = 2;
    static final int DIRECTION_UP = 3;
    static final int DURATION_LONG = 500;    // 500 ms

    View view;

    public abstract void animate();

}
