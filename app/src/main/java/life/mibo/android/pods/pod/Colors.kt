/*
 *  Created by Sumeet Kumar on 1/27/20 2:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 2:44 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.pod

import android.graphics.Color
import androidx.annotation.ColorInt

//import com.google.gson.annotations.SerializedName

class Colors(
    @ColorInt
    var activeColor: Int = Color.BLUE, @ColorInt var distractiveColor: Int = Color.RED, var isDestractive: Boolean = false
) {

    @ColorInt
    var color: Int = Color.GREEN

}