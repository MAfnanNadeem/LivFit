/*
 *  Created by Sumeet Kumar on 4/5/20 11:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 11:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.muscle

import com.google.gson.annotations.SerializedName
import life.mibo.hardware.core.Logger
import java.io.Serializable

data class Muscle(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("muscle_name")
    var muscleName: String?,
    @SerializedName("position")
    var position: Int?,
    @SerializedName("governor")
    var govern: Int?
) : Serializable {

    var isSelected: Boolean = false

    // var value: Int = 25
    var channelId: Int = position ?: 1
    var channelValue: Int = 0
    var mainValue: Int = 0
    var isPlay = false

    fun getChanneld(): Int {
        position?.let {
            return it
        }
        return channelId
    }

    fun getGovern(): Int {
        govern?.let {
            return it
        }
        return 75
    }

    fun incValue() {
        channelValue += 1
    }

    fun decValue() {
        channelValue -= 1
    }

    fun from(chPerc: Int): Muscle {
        val ch6 = Muscle(id, image, muscleName, position, govern)
        Logger.e("Channel6Model Copy $ch6")
        ch6.channelValue = chPerc
        ch6.mainValue = mainValue
        if (chPerc == 0)
            ch6.isPlay = true
        return ch6

    }

    fun incChannelPercent() {
        if (channelValue < 100)
            channelValue++
    }

    fun decChannelPercent() {
        if (channelValue > 1)
            channelValue--
    }

    fun incMainPercent() {
        if (mainValue < 100)
            mainValue++
    }

    fun decMainPercent() {
        if (mainValue > 1)
            mainValue--
    }

    override fun toString(): String {
        return "Muscle(id=$id, image=$image, muscleName=$muscleName, position=$position, isSelected=$isSelected, channelId=$channelId, channelValue=$channelValue, mainValue=$mainValue, isPlay=$isPlay)"
    }

//    fun print(): String {
//        return "Muscle(uid='$uid', id=$id, image=$image, percentChannel=$percentChannel, percentMain=$percentMain, title='$title', isPlay=$isPlay)"
//    }

//    fun from2(chPerc: Int): Channel6Model {
//        Logger.e("Channel6Model "+toString())
//        val ch6 = Channel6Model(id, image, chPerc, percentMain, title)
//        Logger.e("Channel6Model Copy $ch6")
//        if(chPerc == 0)
//            ch6.isPlay = true
//        return ch6
//    }

    companion object {
        fun from(position: Int, imageRes: Int, isSelected: Boolean = false): Muscle {
            val m = Muscle(
                position,
                "" + imageRes,
                "",
                position, 100
            )
                m.isSelected = isSelected
            return m
        }
    }
}