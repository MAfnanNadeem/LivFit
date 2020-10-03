/*
 *  Created by Sumeet Kumar on 2/16/20 9:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/16/20 9:31 AM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.program

import life.mibo.hardware.core.Logger
import life.mibo.hardware.models.Device
import life.mibo.hardware.rxl.Event
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
    var lastFocusUid = ""

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

    fun lastPod(): Device? {
        if (pods.size > 0) {
            return pods[pods.size - 1]
        }
        return null
    }

    fun inc() {
        lastPod += 1
    }

    // Sequence
    private var lastSeq = 0;

    private var sequence: IntArray = IntArray(1)
    fun defaultSeq() {
        sequence = IntArray(noOfPods)
        for (i in 0 until noOfPods) {
            sequence[i] = i
        }

        Logger.e("Default Sequence ${sequence.contentToString()}")
    }


    fun createSeq(s: List<String>) {
        sequence = IntArray(s.size)
        s.forEachIndexed { index, i ->
            sequence[index] = getSeq(i)
        }
        Logger.e("Create Sequence ${sequence.contentToString()}")
    }

    fun createSeq(seq: String?) {
        Logger.e("Create Sequence seq $seq ")
        if (seq == null || seq.isEmpty()) {
            defaultSeq()
            return
        }
        val s = seq.split(",")
        if (s.isNotEmpty()) {
            sequence = IntArray(s.size)
            s.forEachIndexed { index, i ->
                sequence[index] = getSeq(i)
            }
        } else {
            defaultSeq()
        }
        Logger.e("Create Sequence ${sequence.contentToString()}")
    }

    private fun getSeq(string: String): Int {
        try {
            return Integer.parseInt(string).minus(1)
        } catch (e: Exception) {

        }
        return 0
    }

    fun nextSeq(): Int {
        if (lastSeq >= sequence.size)
            lastSeq = 0
        lastPod = sequence[lastSeq]
        Logger.e("sequence nextSeq $lastSeq")
        if (lastPod >= pods.size)
            lastPod = 0

        return lastPod
    }

    fun incSeq() {
        lastSeq += 1
    }
    // end

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
        return "RxlPlayer(id=$id, name='$name', colorId=$colorId, noOfPods=$noOfPods, pods=${podsUids()}, lastPod=$lastPod, lastUid='$lastUid', events=${events.size}, wrongEvents=${wrongEvents.size}) isTapReceived $isTapReceived isStarted $isStarted"
    }

    fun reset() {
        lastPod = 0
        lastUid = ""
        isFocus = false
        isTapReceived = false
        isStarted = false
    }

    enum class Player {
        SINGLE, TWO_PLAYER, MULTI_PLAYERS;
    }

}