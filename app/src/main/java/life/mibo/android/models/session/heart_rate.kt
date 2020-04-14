/*
 *  Created by Sumeet Kumar on 1/8/20 3:18 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 3:18 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.session


import com.google.gson.annotations.SerializedName

data class heart_rate(
    @SerializedName("heart_rate")
    var heartRate: List<String?>?
)