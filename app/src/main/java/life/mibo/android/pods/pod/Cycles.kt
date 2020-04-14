/*
 *  Created by Sumeet Kumar on 1/27/20 2:42 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 2:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.pod

//import com.google.gson.annotations.SerializedName

class Cycles(var value: Int = 1, val pause: Pause) {

    var name: String? = null
    var valueString: String = "1000"
    var unit: String = "ms"
    var max: String? = null
    var min: String? = null
}