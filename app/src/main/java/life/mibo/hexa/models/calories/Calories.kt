/*
 *  Created by Sumeet Kumar on 1/12/20 11:50 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 11:50 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.calories


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel
import life.mibo.hexa.models.base.Error

data class Calories  (
    @SerializedName("data")
    var `data`: List<CaloriesData?>?,
    @SerializedName("error")
    var error: List<Error?>?,
    @SerializedName("status")
    var status: String?
): BaseModel()