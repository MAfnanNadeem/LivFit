/*
 *  Created by Sumeet Kumar on 9/8/20 5:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/8/20 5:40 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.core

import kotlin.random.Random

class RxlPlayer(
    var id: Int,
    var program: RxlProgram,
    var pods: List<RxlPod>,
    var name: String = "",
    var playerName: String = "",
    var color: Int = 0
) {



    var isTapReceived: Boolean = false
    var isStarted: Boolean = false
    var events = ArrayList<RxlEvent>()
    var wrongEvents = ArrayList<RxlEvent>()


    fun color(): Int {
        if (color == 0)
            color = program.color
        return color
    }

    var currentCycle = 0
    fun getDuration(): Int {
        return program.duration(currentCycle)
    }

    fun getAction(): Int {
        return program.action(currentCycle)
    }

    fun getPause(): Int {
        return program.pause(currentCycle)
    }

    fun getCycles(): Int {
        return program.blocks.size
    }

    fun getTileCount(): Int {
        return pods.size
    }

    fun hasNextCycle(): Boolean {
        return currentCycle < program.blocks.size
    }

    fun first(): RxlBlock {
        return program.blocks.get(0)
    }

//    fun next(): RxtBlock {
//        currentCycle++
//        return program.blocks.get(currentCycle)
//    }

    fun nextBlock(): RxlBlock? {
        currentCycle++
        if (currentCycle < program.blocks.size)
            return program.blocks.get(currentCycle)
        return null
    }

    fun currentBlock(): RxlBlock {
        return program.blocks.get(currentCycle)
    }

    // for test
    private var currentTile = -1;
    private var tileCount = 0;

    fun nextTile(): RxlPod {
        currentTile++
        if (currentTile >= pods.size)
            currentTile = 0
        return pods[currentTile]
    }

    fun nextTile(id: Int): RxlPod {
        if (id < pods.size)
            return pods[id]
        return pods[0]
    }

    fun next(id: Int): RxlPod {
        return pods[id]
    }

    fun nextSeq(): RxlPod {
        currentTile++
        if (currentTile >= pods.size)
            currentTile = 0
        return pods[currentTile]
    }

    fun getLastTile() = currentTile

    fun initCount() {
        tileCount = pods.size
    }

    fun nextRandomTile(): RxlPod {
        var id = Random.nextInt(tileCount)
        if (id == currentTile)
            id = Random.nextInt(tileCount)
        currentTile = id
        if (currentTile >= tileCount)
            currentTile = 0
        return pods[currentTile]
    }
}