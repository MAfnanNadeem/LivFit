/*
 *  Created by Sumeet Kumar on 5/18/20 12:36 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/18/20 12:36 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.session


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class RescheduleMemberSession(data: Data?, token: String?) :
    BasePost<RescheduleMemberSession.Data?>(data, "RescheduleMemberSession", token) {
    data class Data(
        @SerializedName("ScheduleID")
        var scheduleID: Int?,
        @SerializedName("StartDate")
        var startDate: String?,
        @SerializedName("StartTime")
        var startTime: String?
    )
}