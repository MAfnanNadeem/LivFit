/*
 *  Created by Sumeet Kumar on 1/15/20 3:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.program


import com.google.gson.annotations.SerializedName

data class Duration(
    @SerializedName("default")
    var default: String?,
    @SerializedName("format")
    var format: String?,
    @SerializedName("max")
    var max: String?,
    @SerializedName("min")
    var min: String?,
    @SerializedName("unit")
    var unit: String?,
    @SerializedName("value")
    var value: String?
) {

    fun valueInt() = value?.toInt() ?: 0
}