/*
 *  Created by Sumeet Kumar on 5/14/20 2:25 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/14/20 2:23 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Specializations(
    @SerializedName("Name")
    var name: String?,
    @SerializedName("Value")
    var value: String?
)