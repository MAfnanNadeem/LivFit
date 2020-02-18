/*
 *  Created by Sumeet Kumar on 2/16/20 9:00 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 4:23 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl

data class RxlCycle(
    var cycleDuration: Int,
    var cycleAction: Int,
    var cyclePause: Int,
    var station: RxlStation,
    var lights: RxlLight = RxlLight.SEQUENCE,
    var player: RxlPlayer = RxlPlayer.SINGLE
) {

    // Optionals
    var id: Int = 0
    var name: String? = ""


    fun getDuration(): Int = cycleDuration

    fun getAction(): Int = cycleAction

    fun getPause(): Int = cyclePause

    fun getColor(): Int = station.getActiveColor()
    fun getPosition(): Int = station.getActivePosition()

    fun getAlternativeColor(): Int {
        return station.getDestructiveColor()
    }
}