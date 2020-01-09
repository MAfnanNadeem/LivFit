/*
 *  Created by Sumeet Kumar on 1/9/20 2:12 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.weight


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("date")
    var date: String?,
    @SerializedName("weight")
    var weight: Int?
)