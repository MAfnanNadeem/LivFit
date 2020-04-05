/*
 *  Created by Sumeet Kumar on 2/11/20 5:53 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 5:53 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.rxl2


import com.google.gson.annotations.SerializedName

data class RXLPrograms(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("error")
    var error: List<Any?>?,
    @SerializedName("status")
    var status: String?
) {
    data class Data(
        @SerializedName("Programs")
        var programs: List<Program?>?
    ) {
        data class Program(
            @SerializedName("accessories")
            var accessories: String?,
            @SerializedName("action_duration")
            var actionDuration: Int?,
            @SerializedName("created_at")
            var createdAt: String?,
            @SerializedName("cycles")
            var cycles: Int?,
            @SerializedName("delay_pause")
            var delayPause: Int?,
            @SerializedName("description")
            var description: String?,
            @SerializedName("id")
            var id: Int?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("lights_logic")
            var lightsLogic: String?,
            @SerializedName("member_id")
            var memberId: Int?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("number_of_players")
            var numberOfPlayers: Int?,
            @SerializedName("number_of_rxl")
            var numberOfRxl: Int?,
            @SerializedName("program_type")
            var programType: String?,
            @SerializedName("proximity")
            var proximity: Int?,
            @SerializedName("start_type")
            var startType: String?,
            @SerializedName("structure")
            var structure: String?,
            @SerializedName("total_durations")
            var totalDurations: Int?,
            @SerializedName("type")
            var type: String?,
            @SerializedName("updated_at")
            var updatedAt: String?,
            @SerializedName("working_stations")
            var workingStations: Int?
        )
    }
}