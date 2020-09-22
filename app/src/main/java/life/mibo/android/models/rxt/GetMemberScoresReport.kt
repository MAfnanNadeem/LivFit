/*
 *  Created by Sumeet Kumar on 9/2/20 3:25 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/2/20 3:25 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxt


import com.google.gson.annotations.SerializedName

data class GetMemberScoresReport(
    @SerializedName("data")
    var `data`: List<Data?>?,
    @SerializedName("status")
    var status: String?
) {
    data class Data(
        @SerializedName("ExerciseDate")
        var exerciseDate: String?,
        @SerializedName("ExerciseType")
        var exerciseType: String?,
        @SerializedName("Hits")
        var hits: Int?,
        @SerializedName("LocationID")
        var locationID: Int?,
        @SerializedName("MemberID")
        var memberID: Int?,
        @SerializedName("Missed")
        var missed: Int?,
        @SerializedName("PlayerName")
        var playerName: String?,
        @SerializedName("WorkoutDuration")
        var duration: String?,
        @SerializedName("Total")
        var total: Int?,
        @SerializedName("TrainerID")
        var trainerID: Int?,
        @SerializedName("VirtualMember")
        var virtualMember: Int?,
        @SerializedName("WorkoutID")
        var workoutID: Int?
    )
}