/*
 *  Created by Sumeet Kumar on 2/11/20 9:43 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 9:43 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxl


import androidx.navigation.fragment.FragmentNavigator
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse
import java.io.Serializable

class RXLPrograms(data: Data?) : BaseResponse<RXLPrograms.Data?>(data) {

    data class Data(
        @SerializedName("Programs")
        var programs: ArrayList<Program?>?
    )

    @Entity(tableName = "rxl_program_old")
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
        @PrimaryKey()
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
        var workingStations: Int?,
        var isFavourite: Boolean = false
    ) : Serializable, androidx.core.util.Predicate<Program> {

        var urlIcon: String? = null

        @Ignore
        var extras : FragmentNavigator.Extras? = null

        fun getTransitionTitle(): String {
            return String.format("ttitle_%02d", id)
        }

        fun getTransitionIcon(): String {
            return String.format("iicon_%02d", id)
        }

        override fun test(t: Program?): Boolean {

            return false
        }


        fun match(
            type: String?,
            pods: Int,
            players: Int,
            logic: String?,
            accessories: String?
        ): Boolean {
            var matched = false

            type?.let {
                matched = this.numberOfRxl == pods
            }

            return matched
        }

        fun matchPod(pods: Int?): Boolean {
            var matched = false
            pods?.let {
                if (it > 0) {
                    matched = numberOfRxl == it
                }
            }
            return matched
        }

        fun matchPlayer(p: Int?): Boolean {
            var matched = false
            p?.let {
                if (it > 0) {
                    matched = numberOfPlayers == it
                }
            }
            return matched
        }

        fun matchLogic(logic: String?): Boolean {
            var matched = false
            logic?.let {
                if (it != "") {
                    matched = lightsLogic?.contains(it) ?: false
                }
            }
            return matched
        }

        fun matchProgram(prg: String?): Boolean {
            var matched = false
            prg?.let {
                if (it != "") {
                    matched = programType?.contains(it) ?: false
                }
            }
            return matched
        }
//        fun copy(){
//
//        }
    }
}