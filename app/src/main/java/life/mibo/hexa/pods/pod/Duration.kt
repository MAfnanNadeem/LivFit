/*
 *  Created by Sumeet Kumar on 1/27/20 2:04 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 2:03 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.pod

//import com.google.gson.annotations.SerializedName

class Duration(
    var duration: Int = 30,
    var actionTime: Int = 3,
    val cycles: Cycles = Cycles(1, Pause())
) {

    var name: String? = null
    var valueString: String = "1000"
    //var value: Int = 1_000
    var unit: String = "ms"
    var max: String? = null
    var min: String? = null
    //var i = timeUnit.convert(value.toLong(), timeUnit)

}