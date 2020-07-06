/*
 *  Created by Sumeet Kumar on 7/6/20 4:25 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/6/20 4:25 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit.fitbit

import com.google.gson.annotations.SerializedName

data class Step(
        @SerializedName("dateTime")
        var dateTime: String?,
        @SerializedName("value")
        var value: String?
    )