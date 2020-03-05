/*
 *  Created by Sumeet Kumar on 2/16/20 9:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/16/20 9:31 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.program

import life.mibo.hardware.models.Device
import life.mibo.hexa.pods.Event
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
        lastPod = if (lastPod == i) lastPod++ else i
        if (lastPod >= pods.size)
            lastPod = 0
        return lastPod
    }

    fun inc() {
        lastPod++
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