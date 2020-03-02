/*
 *  Created by Sumeet Kumar on 2/29/20 10:10 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/29/20 10:10 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.rxl


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BasePost

class SaveRxlExercise(data: Program, token: String?) :
    BasePost<SaveRxlExercise.Program>(data, "SaveRXLExerciseProgram", token) {

    data class Program(
        @SerializedName("Accessories")
        var accessories: String?,
        @SerializedName("ActionDuration")
        var actionDuration: Int?,
        @SerializedName("AvatarBase64")
        var avatarBase64: String?,
        @SerializedName("Category")
        var category: String?,
        @SerializedName("CreatedBy")
        var createdBy: String?,
        @SerializedName("Cycle")
        var cycle: Int?,
        @SerializedName("Delay")
        var delay: Int?,
        @SerializedName("Description")
        var description: String?,
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("Name")
        var name: String?,
        @SerializedName("NumberOfAssignedColorsPerPlayer")
        var numberOfAssignedColorsPerPlayer: String?,
        @SerializedName("NumberOfDistractingColors")
        var numberOfDistractingColors: String?,
        @SerializedName("NumberOfPlayers")
        var numberOfPlayers: Int?,
        @SerializedName("NumberOfPods")
        var numberOfPods: Int?,
        @SerializedName("Pattern")
        var pattern: String?,
        @SerializedName("Pause")
        var pause: String?,
        @SerializedName("ProximityValue")
        var proximityValue: String?,
        @SerializedName("StartActivityOn")
        var startActivityOn: String?,
        @SerializedName("Tap/Proximity")
        var tapProximity: String?,
        @SerializedName("TotalDuration")
        var totalDuration: Int?,
        @SerializedName("Tutorial")
        var tutorial: String?,
        @SerializedName("Type")
        var type: Int?,
        @SerializedName("Work Stations")
        var workStations: Int?
    )
}