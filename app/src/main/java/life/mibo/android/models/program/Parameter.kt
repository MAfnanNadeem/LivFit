/*
 *  Created by Sumeet Kumar on 1/15/20 3:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.program


import com.google.gson.annotations.SerializedName

data class Parameter(
    @SerializedName("ActionDuration")
    var actionDuration: ActionDuration?,
    @SerializedName("BlockDuration")
    var blockDuration: BlockDuration?,
    @SerializedName("DownRampDuration")
    var downRampDuration: DownRampDuration?,
    @SerializedName("Frequency")
    var frequency: Frequency?,
    @SerializedName("PauseDuration")
    var pauseDuration: PauseDuration?,
    @SerializedName("PulseWidth")
    var pulseWidth: PulseWidth?,
    @SerializedName("UpRampDuration")
    var upRampDuration: UpRampDuration?,
    @SerializedName("Waveform")
    var waveform: Waveform?
)