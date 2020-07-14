/*
 *  Created by Sumeet Kumar on 7/12/20 2:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/12/20 2:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.biometric

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreatedAt(
        @SerializedName("date")
        var date: String?,
        @SerializedName("timezone")
        var timezone: String?,
        @SerializedName("timezone_type")
        var timezoneType: String?
    ) : Serializable