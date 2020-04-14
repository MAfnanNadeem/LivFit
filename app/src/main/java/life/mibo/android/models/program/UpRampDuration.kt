/*
 *  Created by Sumeet Kumar on 1/15/20 3:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.program


import com.google.gson.annotations.SerializedName

data class UpRampDuration(
    @SerializedName("default")
    var default: String?,
    @SerializedName("desc")
    var desc: String?,
    @SerializedName("max")
    var max: String?,
    @SerializedName("min")
    var min: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("unit")
    var unit: String?,
    @SerializedName("value")
    var value: String?
)