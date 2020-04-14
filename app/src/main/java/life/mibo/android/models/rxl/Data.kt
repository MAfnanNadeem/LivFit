/*
 *  Created by Sumeet Kumar on 2/11/20 9:15 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 9:15 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxl


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("Accessories")
    var accessories: String?,
    @SerializedName("ActionDuration")
    var actionDuration: Int?,
    @SerializedName("Cycles")
    var cycles: Int?,
    @SerializedName("DelayPause")
    var delayPause: Int?,
    @SerializedName("Description")
    var description: String?,
    @SerializedName("Image")
    var image: String?,
    @SerializedName("LightsLogic")
    var lightsLogic: String?,
    @SerializedName("MemberID")
    var memberID: String?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("NumberOfPlayers")
    var numberOfPlayers: Int?,
    @SerializedName("NumberOfRXL")
    var numberOfRXL: Int?,
    @SerializedName("ProgramType")
    var programType: String?,
    @SerializedName("Proximity")
    var proximity: Int?,
    @SerializedName("StartType")
    var startType: String?,
    @SerializedName("Structure")
    var structure: String?,
    @SerializedName("TotalDurations")
    var totalDurations: Int?,
    @SerializedName("Type")
    var type: String?,
    @SerializedName("WorkingStations")
    var workingStations: Int?
)