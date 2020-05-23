/*
 *  Created by Sumeet Kumar on 5/21/20 11:17 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/21/20 11:17 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SaveTrainerSessionReport(data: Data?, token: String?) :
    BasePost<SaveTrainerSessionReport.Data?>(data, "SaveSessionReport", token) {

    companion object {
        fun create(token: String? ){

        }
    }

    data class Data(
        @SerializedName("breaks")
        var breaks: Int?,
        @SerializedName("duration")
        var duration: Int?,
        @SerializedName("endDatetime")
        var endDatetime: String?,
        @SerializedName("LocationID")
        var locationID: String?,
        @SerializedName("members")
        var members: List<Member?>?,
        @SerializedName("programCircuitName")
        var programCircuitName: String?,
        @SerializedName("SessionID")
        var sessionID: String?,
        @SerializedName("startDatetime")
        var startDatetime: String?,
        @SerializedName("TrainerID")
        var trainerID: String?,
        @SerializedName("trainerIssuesLog")
        var trainerIssuesLog: String?
    )

    data class Member(
        @SerializedName("caloriesBurnt")
        var caloriesBurnt: Int?,
        @SerializedName("channelValues")
        var channelValues: List<Int?>?,
        @SerializedName("memberId")
        var memberId: Int?,
        @SerializedName("peakHR")
        var peakHR: Int?,
        @SerializedName("restingHR")
        var restingHR: Int?,
        @SerializedName("sessionCount")
        var sessionCount: Int?,
        @SerializedName("trainerFeedback")
        var trainerFeedback: String?,
        @SerializedName("userRating")
        var userRating: Int?,
        @SerializedName("variableHR")
        var variableHR: List<Int?>?
    )
}