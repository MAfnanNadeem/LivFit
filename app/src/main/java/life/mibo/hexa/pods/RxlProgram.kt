/*
 *  Created by Sumeet Kumar on 2/1/20 11:20 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 11:20 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

import android.graphics.Color
import life.mibo.hexa.pods.pod.LightLogic
import life.mibo.hexa.pods.pod.Players

class RxlProgram(var cycle: RxlCycle?) {

    private var cycles: ArrayList<RxlCycle>? = null
    //var cycle: PodCycle? = null
    //var repeat = 0
    var player: Players = Players.SINGLE
    var lightLogic: LightLogic = LightLogic.SEQUENCE
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

        return RxlCycle(0, 0, 0, 0)
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

    fun getActiveColor(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].activeColor
            }
        } else {
            return cycle?.activeColor ?: Color.WHITE
        }

        return 0
    }

    fun getDestractiveColor(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].distractiveColor
            }
        } else {
            return cycle?.distractiveColor ?: Color.WHITE
        }

        return 0
    }

    companion object {
        fun getExercise(
            duration: Int, action: Int, pause: Int, cycle: Int, color: Int, random: Boolean = false
        ): RxlProgram {

            return RxlProgram(RxlCycle(duration, action, pause, color, random)).repeat(cycle)
        }
    }

}