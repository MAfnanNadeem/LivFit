/*
 *  Created by Sumeet Kumar on 5/19/20 9:35 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/19/20 9:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class TrainerCalendarResponse(data: Data?) : BaseResponse<TrainerCalendarResponse.Data?>(data) {
    data class Data(
        @SerializedName("Sessions")
        var sessions: List<Session?>?
    )

    data class Session(
        @SerializedName("completed")
        var completed: Int?,
        @SerializedName("endDatetime")
        var endDatetime: String?,
        @SerializedName("members")
        var members: List<Member?>?,
        @SerializedName("notes")
        var notes: String?,
        @SerializedName("session_group_name")
        var sessionGroupName: Any?,
        @SerializedName("sessionId")
        var sessionId: Int?,
        @SerializedName("session_type")
        var sessionType: String?,
        @SerializedName("startDatetime")
        var startDatetime: String?,
        @SerializedName("started")
        var started: Int?
    )

    data class Member(
        @SerializedName("age")
        var age: String?,
        @SerializedName("attendance")
        var attendance: Attendance?,
        @SerializedName("channelValues")
        var channelValues: List<Int?>?,
        @SerializedName("contact")
        var contact: String?,
        @SerializedName("firstName")
        var firstName: String?,
        @SerializedName("gender")
        var gender: String?,
        @SerializedName("height")
        var height: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("profileImg")
        var profileImg: String?,
        @SerializedName("imageThumbnail")
        var imageThumbnail: String?,
        @SerializedName("isPresent")
        var isPresent: String?,
        @SerializedName("lastName")
        var lastName: String?,
        @SerializedName("memberMuscleCaps")
        var memberMuscleCaps: List<Int?>?,
        @SerializedName("primaryContactEmail")
        var primaryContactEmail: String?,
        @SerializedName("suitDescription")
        var suitDescription: String?,
        @SerializedName("suitGovernors")
        var suitGovernors: List<Int?>?,
        @SerializedName("suitModel")
        var suitModel: Any?,
        @SerializedName("suitName")
        var suitName: String?,
        @SerializedName("weight")
        var weight: String?,
        @SerializedName("weight_unit")
        var weightUnit: String?
    )

    data class Attendance(
        @SerializedName("completed")
        var completed: Int?,
        @SerializedName("missed")
        var missed: Int?,
        @SerializedName("remaining")
        var remaining: Int?
    )
}