/*
 *  Created by Sumeet Kumar on 3/5/20 10:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/4/20 4:28 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.program

import android.util.SparseArray
import life.mibo.hardware.core.Logger
import life.mibo.hardware.models.Device

class RxlProgram() {

    private var cycles: ArrayList<RxlCycle> = ArrayList()
    var players: ArrayList<RxlPlayer> = ArrayList()
    //var cycle: PodCycle? = null
    //var repeat = 0
    //var player: Players = Players.SINGLE
    //val players = ArrayList<PlayerType>()
    //var lightLogic: LightLogic = LightLogic.SEQUENCE
    var stations = 1
    //private var rounds = 0
    var playerType: PlayerType = PlayerType.SINGLE
    var devices = 0
    private var isDiffCycle = false
    private var lastCycle = -1
    private var repeat: Int = 0
    private var count: Int = 0
    private var sequence: IntArray? = null

    // don't change / final
    private val currentCycle: Int = 0
    private var currentPlayer: Int = 0

    private fun reset(): RxlProgram {
        repeat = 0
        lastCycle = -1
        players.clear()
        cycles.clear()
        return this

    }

    fun addCycles(collection: Collection<RxlCycle>): RxlProgram {
        cycles.clear()
        cycles.addAll(collection)
        isDiffCycle = true
        count = cycles!!.size
        //reset()
        return this
    }

    fun addCycle(cycle: RxlCycle): RxlProgram {
        cycles.add(cycle)
        return this
    }

    fun addPlayer(player: RxlPlayer): RxlProgram {
        players.add(player)
        return this
    }

    fun addPlayers(player: ArrayList<RxlPlayer>): RxlProgram {
        players.clear()
        players.addAll(player)
        return this
    }

    fun addPlayers(list: SparseArray<RxlPlayer>): RxlProgram {
        Logger.e("RxlProgram: addPlayers size ${list.size()}")
        players.clear()
        for (i in 0 until list.size()) {
            val player: RxlPlayer? = list.valueAt(i)
            player?.let {
                players.add(it)
            }
        }
        Logger.e("RxlProgram: addPlayers added size ${players.size}")

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
            cycles.let {
                return lastCycle < it.size
            }
        } else {
            return lastCycle < repeat
        }

    }

    fun getCycle(): RxlCycle {
        if (currentCycle < cycles.size)
            return cycles[currentCycle]
        return RxlCycle.empty()
    }

    fun getNext(): RxlCycle {
        lastCycle++
        if (isDiffCycle) {
            cycles.let {
                if (lastCycle < it.size)
                    return it[lastCycle]
            }
        } else {
            if (lastCycle < repeat)
                return cycles[lastCycle]
        }

        return cycles[currentCycle]
        // return RxlCycle(0, 0, 0, RxlStation().add(Color.WHITE, 0), RxlLight.SEQUENCE)
    }

    fun getCyclesCount(): Int = count

    fun getCycle(pos: Int): RxlCycle? {
        if (isDiffCycle) {
            cycles.let {
                if (pos < it.size)
                    return it[pos]
            }
        } else {
            if (pos < repeat)
                return cycles[pos]
        }

        if (currentCycle < cycles.size)
            return cycles[currentCycle]
        return RxlCycle.empty()
    }

    fun isMultiPlayer(): Boolean {
        return players.size > 1
    }

    fun getPause(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].cyclePause
            }
        }

        if (currentCycle < cycles.size)
            return cycles[currentCycle].cyclePause

        return 0
    }

    fun getDelay(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].actionDelay
            }
        }

        if (currentCycle < cycles.size)
            return cycles[currentCycle].actionDelay

        return 0
    }

    fun getAction(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].cycleAction
            }
        }

        if (currentCycle < cycles.size)
            return cycles[currentCycle].cycleAction
        return 0
    }

    fun getDuration(): Int {
        if (isDiffCycle) {
            cycles?.let {
                if (lastCycle >= 0 && lastCycle < it.size)
                    return it[lastCycle].cycleDuration
            }
        }
        if (currentCycle < cycles.size)
            return cycles[currentCycle].cycleDuration

        return 0
    }

    // if cycle is one
    fun pause(): Int {
        return cycles[currentCycle].cyclePause
    }

    fun delay(): Int {
        return cycles[currentCycle].actionDelay
    }

    fun action(): Int {
        return cycles[currentCycle].cycleAction
    }

    fun duration(): Int {
        return cycles[currentCycle].cycleDuration
    }

    fun color(): Int {
        return players[currentPlayer].color
    }

    fun colorPosition(): Int {
        return players[currentPlayer].colorId
    }

    fun cycles(): Int = count

    fun totalDuration(): Int {
        return try {
            val t1 = cycles[currentCycle].cycleDuration.times(count)
            val t2 = cycles[currentCycle].cyclePause.times(count.minus(1))
            t1.plus(t2)
        } catch (e: Exception) {
            0
        }
    }

    fun isRandom(): Boolean {
        if (currentCycle < cycles.size)
            return cycles[currentCycle].lightType == RxlLight.RANDOM
        return false
    }

    fun type(): RxlLight {
        if (currentCycle < cycles.size)
            return cycles[currentCycle].lightType
        return RxlLight.UNKNOWN
    }

    fun lightLogic(): Int {
        if (currentCycle < cycles.size) {
            return when (cycles[currentCycle].lightType) {
                RxlLight.SEQUENCE ->
                    1
                RxlLight.RANDOM ->
                    2
                RxlLight.FOCUS ->
                    3
                RxlLight.ALL_AT_ONCE ->
                    4
                RxlLight.TAP_AT_ALL ->
                    5
                RxlLight.HOME_BASED ->
                    6
                else ->
                    0
            }
        }
        return 0

    }

    fun getActiveColor(): Int {
        if (players.size > currentPlayer) {
            return players[currentPlayer].color
        }
        return 0
    }

    fun getActiveColor(playerId: Int): Int {
        for (p in players)
            if (p.id == playerId)
                return p.color
        return 0

    }

    fun getPlayer(playerId: Int): RxlPlayer? {
        for (p in players)
            if (p.id == playerId)
                return p
        return null

    }

    fun getActivePosition(): Int {
        if (players.size > currentPlayer) {
            return players[currentPlayer].colorId
        }
        return 0
    }



    companion object {
        fun getExercise(
            duration: Int, action: Int, pause: Int, cycle: Int,
            color: Int, colorId: Int, pods: ArrayList<Device>, type: RxlLight
        ): RxlProgram {
            // val station = RxlStation().add()
            return RxlProgram().addCycle(
                    RxlCycle(
                        duration,
                        action,
                        pause,
                        0,
                        "",
                        type
                    )
                )
                .addPlayer(
                    RxlPlayer(
                        1,
                        "Player 1",
                        color,
                        colorId,
                        pods.size,
                        pods
                    )
                )
                .repeat(cycle)
        }

        fun getExercise(
            duration: Int,
            action: Int,
            pause: Int,
            cycle: Int,
            delay: Int = 0,
            players: SparseArray<RxlPlayer>,
            logic: RxlLight
        ): RxlProgram {
            // val station = RxlStation().add()
            return RxlProgram().addCycle(
                    RxlCycle(
                        duration,
                        action,
                        pause,
                        delay,
                        "",
                        logic
                    )
                )
                .addPlayers(players)
                .repeat(cycle)
        }

    }

    fun getColor(): Int = getActiveColor()
    fun getPosition(): Int = getActivePosition()

    fun pauseProgram() {

    }

    fun resumeProgram(): RxlProgram {

        return this
    }

}