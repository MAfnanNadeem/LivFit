/*
 *  Created by Sumeet Kumar on 2/1/20 11:21 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 11:21 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

import life.mibo.hexa.pods.pod.LightLogic
import life.mibo.hexa.pods.pod.Players

data class RxlCycle(
    var cycleDuration: Int,
    var cycleAction: Int,
    var cyclePause: Int,
    var activeColor: Int, var isRandom: Boolean = false
) {

    // Optionals
    var id: Int = 0
    var name: String? = ""
    var lightLogic: LightLogic = LightLogic.SEQUENCE
    var player: Players = Players.SINGLE
    var distractiveColor: Int = 0
    var totalPods = 0
    var activePods = 0


    fun getDuration(): Int = cycleDuration

    fun getAction(): Int = cycleAction

    fun getPause(): Int = cyclePause

    fun getColor(): Int = activeColor

    fun getAlternativeColor(): Int {
        return distractiveColor
    }
}