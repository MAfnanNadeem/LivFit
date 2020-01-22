/*
 *  Created by Sumeet Kumar on 1/21/20 10:30 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/21/20 10:30 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

import android.graphics.Color
import androidx.annotation.ColorInt

data class PodExercise(
    val logic: LightLogic = LightLogic.SEQUENCE,
    val players: Players = Players.SINGLE
) {

    enum class LightLogic {
        RANDOM, SEQUENCE, ALL_AT_ONCE, FOCUS, HOME_BASED, UNKNOWN
    }

    enum class Players {
        SINGLE, TWO_PLAYER, MULTI_PLAYERS

    }

    enum class TYpe {
        AGILITY, BALANCED, CORE
    }

    enum class Accessories {
        NO, ROPE, LADDER, MIRROR, BALL
    }

    var podsType: Pods.PodType = Pods.PodType.DYNAMIC
    var id: Int = -1
    var type: Int = 0
    var timeDuration: Int = 0 // total time
    var podTime: Int = 0 // pod's light on time
    var cycle: Int = 1
    @ColorInt
    var color: Int = Color.BLUE

    var unit: String = "ms"
    var name: String = ""

    fun getTime(time: Int, unit: String): Int {
        if (unit == "s")
            time.times(1000)
        return time
    }

}