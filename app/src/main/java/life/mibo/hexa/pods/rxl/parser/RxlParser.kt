/*
 *  Created by Sumeet Kumar on 3/3/20 4:27 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/3/20 4:27 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hexa.pods.rxl.RxlPlayer
import life.mibo.hexa.pods.rxl.RxlProgram
import kotlin.random.Random

abstract class RxlParser(
    var program: RxlProgram,
    var listener: Listener,
    private val tagName: String
) {

    interface Listener {
        fun onDispose()
        //fun createCompositeDisposable()
        //fun startExercise(cycle: Int, duration: Int)
        fun startProgram(cycle: Int, duration: Int)

        fun nextCycle(cycle: Int, pause: Int, duration: Int)
        fun sendColorEvent(
            device: Device, color: Int,
            action: Int, playerId: Int, observe: Boolean
        )

        fun endProgram(cycle: Int, duration: Int)
    }

    var unitTest = false
    var cycles = 0L
    var currentCycle = 1
    var actionTime = 0
    var pauseTime = 0
    var delayTime = 0
    var duration = 0
    var activeColor = 0
    var colorPosition = 0
    var lightLogic = 0
    var isStarted = false
    var isInternalStarted = false
    var isRunning = false
    var isMulti = false
    var colorSent = false
    var players = ArrayList<RxlPlayer>()
    var devices = ArrayList<Device>()

    //var lastPod = 0
    var lastRandom = 0
    var lastUid = ""

    //private var random: Random? = null


    fun random(limit: Int): Int {
        return Random.nextInt(limit)
    }

    private fun assignPlayers(program: RxlProgram) {
        players.clear()
        program.players?.forEach {
            it.events.clear()
            it.wrongEvents.clear()
            players.add(it)
        }
    }

    fun log(msg: String) {
        Logger.e("RXLTest $tagName: $msg")
    }

    fun onTapEvent(event: RxlStatusEvent) {
        players.forEach {
            if (it.id == event.data) {
                onNext(it, event)
            }
        }
    }

    fun onEvent(event: RxlStatusEvent) {
        log("onEvent RxlStatusEvent ${event.data} size: ${players.size}")
        players.forEach {
            if (it.id == event.data) {
                onNext(it, event)
//                if (it.lastUid == event.uid) {
//                    log("RxlStatusEvent UID Matched ${it.lastUid} == $event.uid ")
//                    it.events.add(Event(it.events.size + 1, actionTime, event.time))
//                } else {
//                    log("RxlStatusEvent UID NOT Matched >> ${it.lastUid} == $lastUid ")
//                    it.wrongEvents.add(Event(it.events.size + 1, actionTime, event.time))
//                }
//
//                if (delayTime > 0) {
//                    Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess { _ ->
//                        onNext(it, event)
//                    }.doOnError { _ ->
//                        onNext(it, event)
//                    }.subscribe()
//                } else {
//                    onNext(it, event)
//                }
                return@forEach
            }
        }

        log("onEvent RxlStatusEvent end...............")

    }

    fun checkPod(id: Int, list: List<Device>): Boolean {
        if (id >= 0 && id < list.size)
            return true
        //lightOn(list[id])
        return false
    }


    fun startProgram() {
        startInternal()
    }

    private fun startInternal() {

        log("startInternal..........")
        log(
            "startInternal.......... duration ${program?.getDuration()} cycle ${program?.getCyclesCount()} " +
                    "action ${program?.getAction()} pause ${program?.getPause()}"
        )
        if (isRunning) {
            log("exercise is already running")
            listener?.onDispose()
            return
        }
        assignPlayers(program)
        //listener?.createCompositeDisposable()

        //isRandom = program!!.isRandom()
        lightLogic = program.lightLogic()
        activeColor = program.getActiveColor()
        colorPosition = program.getActivePosition()
        cycles = program.getCyclesCount().toLong()
        actionTime = program.getAction()
        pauseTime = program.getPause()
        duration = program.getDuration()
        delayTime = program.getDelay()
        currentCycle = 1
        if (players.size > 1)
            isMulti = true

        // listener?.startExercise(0, 0)
        listener?.startProgram(currentCycle, program!!.getDuration())
        log("startInternal >>> actionTime $actionTime : duration $duration : cycles $cycles : pauseTime $pauseTime lightLogic $lightLogic")
    }

    // abstract

    abstract fun onCycleStart()
    abstract fun onCycleTapStart(playerId: Int)
    abstract fun onNext(player: RxlPlayer, event: RxlStatusEvent)
    //abstract fun nextLightEvent()
    abstract fun completeCycle()
}