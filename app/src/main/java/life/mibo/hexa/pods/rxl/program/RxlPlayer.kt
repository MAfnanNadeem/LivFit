/*
 *  Created by Sumeet Kumar on 2/16/20 9:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/16/20 9:31 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.program

import life.mibo.hardware.models.Device
import life.mibo.hexa.pods.rxl.Event
import kotlin.random.Random

data class RxlPlayer(
    var id: Int,
    var name: String,
    var color: Int,
    var colorId: Int,
    var noOfPods: Int,
    var pods: ArrayList<Device>
) {


    var lastPod = 0
    var lastUid = ""
    var isFocus = false
    var isTapReceived = false
    var isStarted = false
    //var station = RxlStation().addColor(color, 0, colorId)
    var events = ArrayList<Event>()
    var wrongEvents = ArrayList<Event>()

    fun next(): Int {
        lastPod++
        if (lastPod >= pods.size)
            lastPod = 0
        return lastPod
    }

    fun nextRandom(): Int {
        val i = Random.nextInt(pods.size)
        lastPod = if (lastPod == i) lastPod.plus(1) else i
        if (lastPod >= pods.size)
            lastPod = 0
        return lastPod
    }

    fun generateRandom(): Int {
        val i = Random.nextInt(pods.size)
        if (i >= pods.size)
            return 0
        return i
    }

    fun randomPod(): Device? {
        if (pods.size > 0) {
            var i = Random.nextInt(pods.size)
            if (i >= pods.size)
                i = 0
            return pods[i]
        }
        return null
    }

    fun inc() {
        lastPod += 1
    }

    private fun isNextFocus(): Boolean {
        isFocus = Random.nextInt(50) % 3 == 0
        return isFocus
    }

    private fun podsUids(): String {
        var uid = ""
        pods?.forEach {
            uid += ", ${it.uid}"
        }
        return uid
    }

    override fun toString(): String {
        return "RxlPlayer(id=$id, name='$name', colorId=$colorId, noOfPods=$noOfPods, pods=${podsUids()}, lastPod=$lastPod, lastUid='$lastUid', events=${events.size}, wrongEvents=${wrongEvents.size})"
    }

    enum class Player {
        SINGLE, TWO_PLAYER, MULTI_PLAYERS;
    }

}