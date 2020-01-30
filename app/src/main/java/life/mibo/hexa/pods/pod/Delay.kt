/*
 *  Created by Sumeet Kumar on 1/27/20 2:07 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 2:05 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.pod

//import com.google.gson.annotations.SerializedName

class Delay(var value : Int) {

    var name: String? = null
    var valueString: String = "$value"
    var unit: String = "ms"
    var max: String? = null
    var min: String? = null
}