/*
 *  Created by Sumeet Kumar on 1/9/20 2:12 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.weight


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.Error

data class WeightAllResponse(
    @SerializedName("data")
    var `data`: List<Data?>?,
    @SerializedName("error")
    var error: List<Error?>?,
    @SerializedName("status")
    var status: String?
)