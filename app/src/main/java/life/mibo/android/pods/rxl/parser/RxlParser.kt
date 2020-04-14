/*
 *  Created by Sumeet Kumar on 3/3/20 4:27 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/3/20 4:27 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.rxl.parser

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.android.pods.rxl.program.RxlPlayer
import life.mibo.android.pods.rxl.program.RxlProgram
import org.greenrobot.eventbus.EventBus
import kotlin.random.Random

abstract class RxlParser(
    var program: RxlProgram,
    var listener: Listener,
    private val tagName: String
) {

    companion object {
        fun getParser(type: Int, program: RxlProgram, listener: Listener): RxlParser {
            return when (type) {
                1 -> {
                    SequenceParser(
                        program,
                        listener
                    )
                }
                2 -> {
                    RandomParser(
                        program,
                        listener
                    )
                }
                3 -> {
                    FocusParser(
                        program,
                        listener
                    )
                }
                4 -> {
                    AllAtOnceParser(
                        program,
                        listener
                    )
                }
                5 -> {
                    TapAtAllParser(
                        program,
                        listener
                    )
                }
                else -> {
                    SequenceParser(
                        program,
                        listener
                    )
                }
            }
        }
    }


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

        fun sendDelayColorEvent(
            device: Device, color: Int,
            action: Int, playerId: Int, delay: Int, observe: Boolean
        )

        fun endProgram(cycle: Int, duration: Int)
    }

    var unitTest = false
    var MIN_DELAY = 100
    var THREAD_SLEEP: Long = 20L
    var MIN_TAP_DELAY = 100
    var cycles = 0L
    var currentCycle = 1
    var actionTime = 0
    var pauseTime = 0
    var delayTime = 0
    var duration = 0
    var lightLogic = 0
    var isStarted = false
    var isInternalStarted = false
    var isRunning = false
    var isMulti = false

    //var colorSent = false
    var players = ArrayList<RxlPlayer>()
    //var devices = ArrayList<Device>()

    //var lastPod = 0
    var lastRandom = 0
    //var lastUid = ""

    //private var random: Random? = null


    init {
        // assignPlayers(program)
    }

    constructor(program: RxlProgram, listener: Listener, logicType: Int) : this(
        program, listener, ""
    ) {
        isInternalStarted = false
    }

    fun random(limit: Int): Int {
        return Random.nextInt(limit)
    }

    private fun assignPlayers(program: RxlProgram) {
        if (players.size == 0) {
            //players.clear()
            program.players?.forEach {
                it.events.clear()
                it.wrongEvents.clear()
                players.add(it)
            }
        }
    }

    fun createPlayers() {
        if (players.size == 0) {
            //players.clear()
            program.players.forEach {
                it.events.clear()
                it.wrongEvents.clear()
                players.add(it)
            }
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
        if (isPaused)
            return
        log("onEvent RxlStatusEvent ${event.data} size: ${players.size} : lightLogic $lightLogic")
        if (lightLogic == 4) {
            onAllATOnce(event, event.data)
            return
        }
        log("onEvent RxlStatusEvent2 ${event.data} size: ${players.size}")
        players.forEach {
            if (it.id == event.data) {
                onNext(it, event)
                return
            }
        }

        log("onEvent RxlStatusEvent end...............${event.data}")

    }

    fun checkPod(id: Int, list: List<Device>): Boolean = id >= 0 && id < list.size

    var isTap = false
    var isPaused = false
    fun startTapProgram(player: RxlPlayer) {
        log("startTapProgram ")
        isTap = true
        startTapInternal(player)
    }

    fun startProgram() {
        isTap = false
        startInternal()
    }

    private fun startInternal() {
        onProgramStart()
        //log("startInternal..........")
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
        //activeColor = program.color()
        //colorPosition = program.colorPosition()
        cycles = program.cycles().toLong()
        duration = program.duration()
        actionTime = program.action().times(1000)
        pauseTime = program.pause().times(1000)
        delayTime = program.delay().times(1000)
        currentCycle = 1
        if (players.size > 1)
            isMulti = true

        // listener?.startExercise(0, 0)pass
        listener?.startProgram(currentCycle, program!!.getDuration())
        log("startInternal >>> actionTime $actionTime : duration $duration : cycles $cycles : pauseTime $pauseTime lightLogic $lightLogic")
    }

    private fun startTapInternal(player: RxlPlayer) {
        log("startTapInternal.......... ${player.id}")
        player.isTapReceived = true
        player.isStarted = false
        if (isInternalStarted) {
            onCycleStart(player)
            return
        }
        isPaused = false
        isInternalStarted = true
        onProgramStart()
        //log("startInternal..........")
        log(
            "startTapInternal.......... duration ${program?.getDuration()} cycle ${program?.getCyclesCount()} " +
                    "action ${program?.getAction()} pause ${program?.getPause()}"
        )
        if (isRunning) {
            log("exercise is already running")
            //listener?.onDispose()
            return
        }
        assignPlayers(program)
        //listener?.createCompositeDisposable()

        //isRandom = program!!.isRandom()
        lightLogic = program.lightLogic()
        //activeColor = program.color()
        //colorPosition = program.colorPosition()
        cycles = program.cycles().toLong()
        duration = program.duration()
        actionTime = program.action().times(1000)
        pauseTime = program.pause().times(1000)
        delayTime = program.delay().times(1000)
        currentCycle = 1
        if (players.size > 1)
            isMulti = true

        // listener?.startExercise(0, 0)
        listener?.startProgram(currentCycle, program!!.getDuration())
        log("startTapProgram >>>>>>>>>> actionTime $actionTime : duration $duration : cycles $cycles : pauseTime $pauseTime lightLogic $lightLogic")
    }

    // abstract

    abstract fun onCycleStart(player: RxlPlayer)
    abstract fun onCycleStart()

    //abstract fun onCycleTapStart()
    //abstract fun onCycleTapStart(playerId: Int)
    abstract fun onNext(player: RxlPlayer, event: RxlStatusEvent)

    //abstract fun nextLightEvent()
    //abstract fun completeCycle()
    open fun completeCycle() {
        log("completeCycle")
        if (cycles > currentCycle) {
            currentCycle++
            if (isTap) {
                for (p in players) {
                    p.isStarted = false
                }
            }
            //pauseCycle(0, getPause())
            log("completeCycle start new cycle")
            listener.nextCycle(currentCycle, pauseTime, duration)
            //resumeObserver(currentCycle, getPause(), duration)
        } else {
            log("completeCycle end program...")
            listener.endProgram(0, 0)
            isInternalStarted = false
        }
    }

    // for focus all, will handle players when program start
    open fun onProgramStart() {

    }

    open fun onAllATOnce(event: RxlStatusEvent, id: Int) {
        // child will handle all events
    }

    open fun onCycleTapStart(playerId: Int = 0) {
        // never called
    }


    //abstract fun dispose()
    fun dispose() {
        // turnOff(players)
    }

    fun turnOff(players: ArrayList<RxlPlayer>) {
        try {
            Single.fromCallable {
                if (players.isNotEmpty()) {
                    players.forEach { p ->
                        p.pods.forEach { d ->
                            EventBus.getDefault().postSticky(ChangeColorEvent(d, d.uid, 0, 0))
                            Thread.sleep(20)
                        }
                    }
                }
                ""
            }.subscribeOn(Schedulers.io()).doOnSuccess {

            }.doOnError {

            }.subscribe()
        } catch (e: java.lang.Exception) {

        }
    }

    fun stop() {
        isTap = false
        isInternalStarted = false
    }

    fun paused(pause: Boolean) {
        isPaused = pause
    }
}