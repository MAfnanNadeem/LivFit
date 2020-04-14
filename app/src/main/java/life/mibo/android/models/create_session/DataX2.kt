/*
 *  Created by Sumeet Kumar on 1/25/20 9:20 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/25/20 9:20 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.create_session


import com.google.gson.annotations.SerializedName

data class DataX2(
    @SerializedName("caloriesBurnt")
    var caloriesBurnt: Int?,
    @SerializedName("channelValues")
    var channelValues: List<Int?>?,
    @SerializedName("duration")
    var duration: Int?,
    @SerializedName("endDateTime")
    var endDateTime: String?,
    @SerializedName("MemberID")
    var memberID: Int?,
    @SerializedName("programName")
    var programName: String?,
    @SerializedName("sessionCompleted")
    var sessionCompleted: Int?,
    @SerializedName("sessionCount")
    var sessionCount: Int?,
    @SerializedName("SessionID")
    var sessionID: Int?,
    @SerializedName("startDateTime")
    var startDateTime: String?,
    @SerializedName("userRating")
    var userRating: Int?
)