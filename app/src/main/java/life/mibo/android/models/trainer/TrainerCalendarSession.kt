/*
 *  Created by Sumeet Kumar on 5/11/20 3:30 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/11/20 3:30 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class TrainerCalendarSession(data: Data?, token: String?) :
    BasePost<TrainerCalendarSession.Data?>(data, "TrainerCalendarSession", token) {
    data class Data(
        @SerializedName("TrainerID")
        var trainerID: String?,
        @SerializedName("FromDatetime")
        var fromDatetime: String?,
        @SerializedName("ToDatetime")
        var toDatetime: String?
    )
}