/*
 *  Created by Sumeet Kumar on 2/16/20 8:59 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 4:27 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl

import android.graphics.Color
import life.mibo.hexa.pods.pod.Players

class RxlProgram(var cycle: RxlCycle) {

    private var cycles: ArrayList<RxlCycle>? = null
    //var cycle: PodCycle? = null
    //var repeat = 0
    var player: Players = Players.SINGLE
    //var lightLogic: LightLogic = LightLogic.SEQUENCE
    var stations = 1
    //private var rounds = 0
    var devices = 0
    private var isDiffCycle = false
    private var lastCycle = -1
    private var repeat: Int = 0
    private var count: Int = 0
    private var sequence: IntArray? = null

    private fun reset() {
        repeat = 0
        lastCycle = -1

    }

    fun addCycles(collection: Collection<RxlCycle>): RxlProgram {
        if (cycles == null)
            cycles = ArrayList()
        cycles!!.addAll(collection)
        isDiffCycle = true
        count = cycles!!.size
        reset()
        return this
    }

    fun repeat(repeat: Int): RxlProgram {
        this.repeat = repeat
        if (repeat > 0)
            isDiffCycle = false
        count = repeat
        return this
    }

    fun hasNext(): Boolean {
        if (isDiffCycle) {
            cycles?.let {
                return lastCycle < it.size
            }
        } else {
            return lastCycle < repeat
        }

        return false
    }

    fun getNext(): RxlCycle {
        lastCycle++
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle < it.size)
                    return it[lastCycle]
            }
        } else {
            if (lastCycle < repeat)
                return cycle!!
        }

        return RxlCycle(0, 0, 0, RxlStation().add(Color.WHITE, 0), RxlLight.SEQUENCE)
    }

    fun getCyclesCount(): Int = count

    fun getCycle(pos: Int): RxlCycle? {
        if (isDiffCycle) {
            cycles?.let {
                if (pos < it.size)
                    return it[pos]
            }
        } else {
            if (pos < repeat)
                return cycle
        }
        return null
    }

    fun getPause(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].cyclePause
            }
        } else {
            return cycle?.cyclePause ?: 0
        }

        return 0
    }

    fun getAction(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].cycleAction
            }
        } else {
            return cycle?.cycleAction ?: 0
        }

        return 0
    }

    fun getDuration(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].cycleDuration
            }
        } else {
            return cycle?.cycleDuration ?: 0
        }

        return 0
    }

    fun isRandom(): Boolean {
        return cycle.lights == RxlLight.RANDOM
    }

    fun type(): RxlLight {
        return cycle.lights
    }

    fun lightLogic(): Int {
        return when (cycle.lights) {
            RxlLight.SEQUENCE ->
                1
            RxlLight.RANDOM ->
                2
            RxlLight.FOCUS ->
                3
            RxlLight.ALL_AT_ONCE ->
                4
            RxlLight.HOME_BASED ->
                5
            else ->
                0
        }

    }

    fun getActiveColor(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].getColor()
            }
        } else {
            return cycle?.getColor() ?: Color.WHITE
        }

        return 0
    }

    fun getActivePosition(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].station.getActivePosition()
            }
        } else {
            return cycle.getPosition()
        }

        return 0
    }

    fun getDestractiveColor(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].getAlternativeColor()
            }
        } else {
            return cycle?.getAlternativeColor() ?: Color.WHITE
        }

        return 0
    }

    fun getParser(): Any? {

        return null
    }

    companion object {
        fun getExercise(
            duration: Int,
            action: Int,
            pause: Int,
            cycle: Int,
            color: Int,
            colorId: Int,
            type: RxlLight
        ): RxlProgram {

            // val station = RxlStation().add()
            return RxlProgram(
                RxlCycle(
                    duration,
                    action,
                    pause,
                    RxlStation().addColor(color, 0, colorId),
                    type
                )
            ).repeat(cycle)
        }
    }

}