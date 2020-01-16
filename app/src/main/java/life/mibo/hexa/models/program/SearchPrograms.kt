/*
 *  Created by Sumeet Kumar on 1/15/20 3:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.program


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.Error

data class SearchPrograms(
    @SerializedName("data")
    var `data`: ProgramData?,
    @SerializedName("errors")
    var errors: List<Error?>?,
    @SerializedName("status")
    var status: String?
)