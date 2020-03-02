/*
 *  Created by Sumeet Kumar on 3/1/20 2:13 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/1/20 2:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.rxl

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import life.mibo.hardware.models.BaseModel
import life.mibo.hexa.pods.rxl.RxlLight
import life.mibo.hexa.room.Converters
import java.io.Serializable

@Entity(tableName = "rxl_programs")
data class RxlProgram(
    @SerializedName("accessories")
    var accessories: String?,
    @SerializedName("action")
    var action: Int?,
    @SerializedName("active")
    var active: Int?,
    @SerializedName("activity_on")
    var activityOn: String?,
    @SerializedName("assigned_colors")
    var assignedColors: String?,
    @SerializedName("avatar")
    var avatar: String?,
    @SerializedName("avatar_base64")
    var avatarBase64: String?,
    @SerializedName("category")
    var category: String?,
    @SerializedName("created_name")
    var createdName: String?,
    @SerializedName("cycle")
    var cycle: Int?,
    @SerializedName("delay")
    var delay: Int?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("distracting_colors")
    var distractingColors: String?,
    @SerializedName("id")
    @PrimaryKey
    var id: Int?,
    @SerializedName("member_id")
    var memberId: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("pattern")
    var pattern: String?,
    @SerializedName("pause")
    var pause: Int?,
    @SerializedName("players")
    var players: Int?,
    @SerializedName("pods")
    var pods: Int?,
    @SerializedName("proximity_value")
    var proximityValue: String?,
    @SerializedName("tap_proximity")
    var tapProximity: String?,
    @SerializedName("total_duration")
    var totalDuration: Int?,
    @TypeConverters(Converters::class)
    @SerializedName("tutorial")
    var tutorial: List<String>?,
    @SerializedName("type")
    var type: Int?,
    @SerializedName("work_station")
    var workStation: Int?
) : BaseModel, Serializable {

    var isFavourite: Boolean = false


    fun logicType(): String {
        when (type) {
            1 -> {
                return "Sequence"
            }
            2 -> {
                return "Random"
            }
            3 -> {
                return "Focus"
            }
            4 -> {
                return "All at once - Tap one"
            }
            5 -> {
                return "Tap at Once"
            }

        }

        return ""
    }

    fun lightLogic(): RxlLight {
        when (type) {
            1 -> {
                return RxlLight.SEQUENCE
            }
            2 -> {
                return RxlLight.RANDOM
            }
            3 -> {
                return RxlLight.FOCUS
            }
            4 -> {
                return RxlLight.ALL_AT_ONCE
            }
            5 -> {
                return RxlLight.TAP_AT_ONCE
            }
            6 -> {
                return RxlLight.ALL_AT_ALL
            }
        }

        return RxlLight.UNKNOWN
    }
}