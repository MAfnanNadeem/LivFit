/*
 *  Created by Sumeet Kumar on 9/20/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/20/20 5:16 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.circuits

import com.google.gson.annotations.SerializedName
import life.mibo.android.models.workout.Workout
import life.mibo.hardware.models.BaseModel
import java.io.Serializable

class Circuit(
    @SerializedName("AccessType")
    var accessType: String?,
    @SerializedName("CreatedBy")
    var createdBy: Int?,
    @SerializedName("Description")
    var description: String?,
    @SerializedName("DurationUnit")
    var durationUnit: String?,
    @SerializedName("DurationValue")
    var durationValue: Int?,
    @SerializedName("Id")
    var id: Int?,
    @SerializedName("MemberID")
    var memberID: Any?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("Type")
    var type: String?,
    @SerializedName("Workout")
    var workout: List<Workout?>?
) : BaseModel, Serializable {

    fun getDuration(): String {
        try {
            if (durationUnit?.contains("seconds")!!) {
                val d = durationValue!!.toInt()
                return String.format("%02d:%02d", d.div(60), d % 60)
            }

        } catch (e: Exception) {

        }
        return "" + durationValue
    }

    fun getDurationSec(): Int {
        return durationValue ?: 0
    }

    fun updateCheck() {
        isSelected = !isSelected
    }

    var rxtProgram: Any? = null

    var color: Int = 0

    var isSelected = false
}