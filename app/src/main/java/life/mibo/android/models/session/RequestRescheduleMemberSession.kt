/*
 *  Created by Sumeet Kumar on 5/28/20 2:27 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/28/20 2:27 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.session


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class RequestRescheduleMemberSession(data: Data?, token: String?) :
    BasePost<RequestRescheduleMemberSession.Data?>(data, "RequestRescheduleMemberSession", token) {
    data class Data(
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("ScheduleID")
        var scheduleID: Int?,
        @SerializedName("StartDate")
        var startDate: String?,
        @SerializedName("StartTime")
        var startTime: String?,
        @SerializedName("TrainerID")
        var trainerID: Int?
    )
}