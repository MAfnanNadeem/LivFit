/*
 *  Created by Sumeet Kumar on 1/15/20 3:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.program


import com.google.gson.annotations.SerializedName
import life.mibo.android.core.toIntOrZero

data class Block(
    @SerializedName("Id")
    var id: Int?,
    @SerializedName("Parameter")
    var parameter: Parameter?,
    @SerializedName("ShortName")
    var shortName: String?
) {
    fun create(): life.mibo.hardware.models.program.Block {
        val block = life.mibo.hardware.models.program.Block()
        block.id = "$id"
        block.shortName = shortName

        block.setPauseDuration(
            getMillis(parameter?.pauseDuration?.value, parameter?.pauseDuration?.unit)
        )
        block.setActionDuration(
            getMillis(parameter?.actionDuration?.value, parameter?.actionDuration?.unit)
        )
        block.setUpRampDuration(
            getMillis(parameter?.upRampDuration?.value, parameter?.upRampDuration?.unit)
        )
        block.setDownRampDuration(
            getMillis(parameter?.downRampDuration?.value, parameter?.downRampDuration?.unit)
        )
        block.setPulseWidth(
            getMillis(parameter?.pulseWidth?.value, parameter?.pulseWidth?.unit)
        )
        block.setFrequency(getMillis(parameter?.frequency?.value, parameter?.frequency?.unit))
        block.setWaveform(parameter?.waveform?.value)

        if (parameter?.blockDuration?.value?.equals("Automatic", true)!!) {

            block.setBlockDuration(
                block.actionDuration.valueInteger.plus(block.pauseDuration.valueInteger)
                    .plus(block.upRampDuration.valueInteger).plus(block.downRampDuration.valueInteger)
            )
        } else {
            block.setBlockDuration(
                getMillis(parameter?.blockDuration?.value, parameter?.blockDuration?.unit)
            )
        }

        return block
    }

    private fun getMillis(value: String?, unit: String?): Int {
        if (value == null)
            return 0
        val d = value.toIntOrZero()
        if (unit == "s")
            return d.times(1000)
        if (unit == "seconds")
            return d.times(1000)
        //if (unit == "ms")
        //  return d
        return d
    }

    fun getTotalDuration(value: String, unit: life.mibo.hardware.models.program.Block): Int {
        if (value.equals("Automatic", true)) {


        }

        return 0
    }
}