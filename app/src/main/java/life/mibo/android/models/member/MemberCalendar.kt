/*
 *  Created by Sumeet Kumar on 5/17/20 11:51 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/17/20 11:51 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class MemberCalendar(data: List<Data?>?) : BaseResponse<List<MemberCalendar.Data?>>(data) {
    data class Data(
        @SerializedName("Completed")
        var completed: Int?,
        @SerializedName("EndDateTime")
        var endDateTime: String?,
        @SerializedName("ServiceName")
        var serviceName: String?,
        @SerializedName("SessionID")
        var sessionID: Int?,
        @SerializedName("StartDateTime")
        var startDateTime: String?,
        @SerializedName("Started")
        var started: Int?,
        @SerializedName("TrainerFullName")
        var trainerFullName: String?,
        @SerializedName("TrainerID")
        var trainerID: Int?
    ) {

        fun getDate() {

        }
    }
}