/*
 *  Created by Sumeet Kumar on 2/23/20 10:11 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/23/20 9:32 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hexa.pods.rxl.RxlLight
import life.mibo.hexa.pods.rxl.RxlPlayer
import life.mibo.hexa.pods.rxl.RxlProgram

class SequenceParser(var program: RxlProgram) : RxlParser() {
    //private var player: life.mibo.hexa.pods.base.Players? = null

    override fun type(): RxlLight {
        return RxlLight.SEQUENCE
    }

    override fun name(): String {
        return "SequenceParser"
    }

    override fun player(): RxlPlayer.Player {
        return RxlPlayer.Player.SINGLE
    }

    override fun program(): RxlProgram {
        return program
    }

    override fun onEvent(event: RxlStatusEvent, matched: Boolean) {
        log("onEvent received.....")
        if (matched)
            lightOnSequence()
    }

    fun onEvent(event: RxlStatusEvent, lastUid: String) {
        //lightOnSequence()
    }

    override fun exerciseStart() {
        //register(this)
        color = program.getActiveColor()
        //lightOnSequence()
    }

    override fun exerciseEnd() {
        unregister(this)
    }

    override fun cycleStart() {
        log("startCycle.......2")
        log("cycleStart.......2")
        lightOnSequence()
    }

    override fun cycleEnd() {

    }

    override fun cyclePause() {

    }

    override fun cycleResume() {

    }

    override fun onReset() {

    }

    override fun onStart(onTap: Boolean) {
        super.onStart(onTap)
    }

    override fun onStop() {
        super.onStop()
    }

    private var lastPod = 0
    private var color = 0

    @Synchronized
    private fun lightOnSequence() {
        log("lightOnSequence........... $lastPod - " + devices().size)
        if (lastPod >= devices().size)
            lastPod = 0
        sendColor(devices()[lastPod], color)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        lastPod++
    }


}