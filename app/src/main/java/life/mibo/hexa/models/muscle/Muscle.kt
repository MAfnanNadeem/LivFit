/*
 *  Created by Sumeet Kumar on 4/5/20 11:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 11:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.muscle

import com.google.gson.annotations.SerializedName

data class Muscle(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("muscle_name")
    var muscleName: String?,
    @SerializedName("position")
    var position: Int?
) {

    var isSelected: Boolean = false
    var value: Int = 25

    fun incValue() {
        value += 1
    }

    fun decValue() {
        value -= 1
    }

    companion object {
        fun from(position: Int, imageRes: Int, isSelected: Boolean = false): Muscle {
            val m = Muscle(
                position,
                "" + imageRes,
                "",
                position
            )
                m.isSelected = isSelected
            return m
        }
    }
}