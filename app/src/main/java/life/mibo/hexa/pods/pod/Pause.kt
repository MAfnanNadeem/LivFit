/*
 *  Created by Sumeet Kumar on 1/27/20 4:17 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 2:59 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.pod

//import com.google.gson.annotations.SerializedName

class Pause(var value: Int = 0) {

    var name: String? = null
    var valueString: String = "$value"
    var unit: String = "ms"
    var max: String? = null
    var min: String? = null
}