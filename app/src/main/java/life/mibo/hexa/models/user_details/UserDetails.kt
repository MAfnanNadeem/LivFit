/*
 *  Created by Sumeet Kumar on 1/9/20 2:17 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.user_details


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel
import life.mibo.hexa.models.base.Error

data class UserDetails(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("error")
    var error: List<Error?>?,
    @SerializedName("status")
    var status: String?
): BaseModel()