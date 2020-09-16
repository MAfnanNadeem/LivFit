/*
 *  Created by Sumeet Kumar on 9/2/20 12:12 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/2/20 12:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.workout


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SaveMemberScores(data: Data?, token: String?) :
    BasePost<SaveMemberScores.Data?>(data, "SaveMemberScores", token) {

    constructor(scores: List<Score?>?, token: String?) : this(SaveMemberScores.Data(scores), token)


    data class Data(
        @SerializedName("Scores")
        var scores: List<Score?>?
    )

    data class Score(
        @SerializedName("ExerciseDate")
        var exerciseDate: String?,
        @SerializedName("ExerciseType")
        var exerciseType: String?,
        @SerializedName("Hits")
        var hits: String?,
        @SerializedName("LocationID")
        var locationID: String?,
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("Missed")
        var missed: String?,
        @SerializedName("Total")
        var total: String?,
        @SerializedName("TrainerID")
        var trainerID: String?,
        @SerializedName("VirtualMember")
        var virtualMember: String?,
        @SerializedName("PlayerName")
        var playerName: String?,
        @SerializedName("WorkoutID")
        var workoutID: String?,
        @SerializedName("WorkoutDuration")
        var duration: String?
    )
}