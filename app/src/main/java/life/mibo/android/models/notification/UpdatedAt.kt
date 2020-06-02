/*
 *  Created by Sumeet Kumar on 6/2/20 4:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/2/20 4:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.notification


import com.google.gson.annotations.SerializedName

data class DateAt(
    @SerializedName("date")
    var date: String?,
    @SerializedName("timezone")
    var timezone: String?,
    @SerializedName("timezone_type")
    var timezoneType: Int?
)