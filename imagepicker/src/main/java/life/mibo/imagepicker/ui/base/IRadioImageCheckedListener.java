/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.ui.base;

import java.io.File;

public interface IRadioImageCheckedListener {
    void cropAfter(File path);

    boolean isActivityFinish();
}
