/*
 *  Created by Sumeet Kumar on 3/3/20 4:27 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/3/20 4:27 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.parser

//import org.greenrobot.eventbus.EventBus
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hardware.rxl.program.RxlPlayer
import life.mibo.hardware.rxl.program.RxlProgram
import java.util.concurrent.CopyOnWriteArrayList
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
        fun onBlockStart(block: Int, cycle: Int)
        fun onBlockEnd(block: Int, cycle: Int)

    }

    //var unitTest = false
    var MIN_DELAY = 100
    var THREAD_SLEEP: Long = 20L
    //var MIN_TAP_DELAY = 100

    // var cycles = 0L
    var duration = 0

    //var pauseTime = 0
    var delayTime = 0
    var actionTime = 0
    var currentCycle = 1
    var lightLogic = 0
    var isStarted = false
    var isInternalStarted = false
    var isRunning = false
    var isMulti = false

    //var colorSent = false
    //var players = ArrayList<RxlPlayer>()
    var players = CopyOnWriteArrayList<RxlPlayer>()
    //var mapPlayers = SparseArray<RxlPlayer>(4)
    //var devices = ArrayList<Device>()

    //var lastPod = 0
    // var lastRandom = 0
    //var lastUid = ""
    fun getPlayers(): Iterator<RxlPlayer> = object : Iterator<RxlPlayer> {
        var index = 0
        override fun hasNext() = index < players.size
        override fun next() = players[index++]
    }

    //private var random: Random? = null



    constructor(program: RxlProgram, listener: Listener, logicType: Int) : this(
        program, listener, ""
    ) {
        isInternalStarted = false
    }

    fun random(limit: Int): Int {
        return Random.nextInt(limit)
    }

    private fun assignPlayers(program: RxlProgram) {
        log("assignPlayers")
        if (players.size == 0) {
            //players.clear()
            program.players?.forEach {
                it.events.clear()
                it.wrongEvents.clear()
                players.add(it)
            }
        }
    }

    private fun updateTapPlayer(player: RxlPlayer) {
        if (players.isNotEmpty()) {
            for (i in players) {
                if (i.id == player.id) {
                    i.isStarted = player.isStarted
                    i.isTapReceived = player.isTapReceived
                }
            }
        }
    }

    fun createPlayers() {
        log("assignPlayers")
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


    fun onEvent(event: RxlStatusEvent) {
        if (isPaused)
            return
        log("onEvent RxlStatusEvent ${event.data} size: ${players.size}")
        if (lightLogic == 4) {
            onAllATOnce(event, event.data)
            return
        }
        //log("onEvent RxlStatusEvent2 ${event.data} size: ${players.size}")
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
            "startInternal.......... duration ${program} "
        )
        if (isRunning) {
            log("exercise is already running")
            listener?.onDispose()
            return
        }
        assignPlayers(program)
        //listener?.createCompositeDisposable()

        //isRandom = program!!.isRandom()
        //lightLogic = program.lightLogic()
        //activeColor = program.color()
        //colorPosition = program.colorPosition()
        //cycles = program.cycles().toLong()
        duration = program.getDuration()
//        actionTime = program.action().times(1000)
//        //actionTime = 300
//        pauseTime = program.pause().times(1000)
//        delayTime = program.delay().times(1000)
        currentCycle = 1
        if (players.size > 1)
            isMulti = true

        // listener?.startExercise(0, 0)pass
        listener?.startProgram(currentCycle, duration)
        //("startInternal >>> actionTime  lightLogic $lightLogic")
    }

    private fun startTapInternal(player: RxlPlayer) {
        log("startTapInternal.......... ${player}")
        player.isTapReceived = true
        player.isStarted = false
        if (isInternalStarted) {
            onCycleStart(player)
            log("startTapInternal.......... isInternalStarted return")
            return
        }
        isPaused = false
        isInternalStarted = true
        assignPlayers(program)
        onProgramStart()
        //log("startInternal..........")
        //updateTapPlayer(player)

        if (isRunning) {
            log("exercise is already running")
            //listener?.onDispose()
            return
        }

        //listener?.createCompositeDisposable()

        //isRandom = program!!.isRandom()
        //lightLogic = program.lightLogic()
        //activeColor = program.color()
        //colorPosition = program.colorPosition()
        //cycles = program.cycles().toLong()
        duration = program.getDuration()


//        actionTime = program.action().times(1000)
//        pauseTime = program.pause().times(1000)
//        delayTime = program.delay().times(1000)
        currentCycle = 1
        if (players.size > 1)
            isMulti = true

        // listener?.startExercise(0, 0)
        listener?.startProgram(currentCycle, duration)
        log("startTapProgram >>>>>>>>>> actionTime $lightLogic")
    }

    // abstract

    abstract fun onCycleStart(player: RxlPlayer)
    abstract fun onCycleStart()

    open fun onResumeCycle() {

    }

    //abstract fun onCycleTapStart()
    //abstract fun onCycleTapStart(playerId: Int)
    abstract fun onNext(player: RxlPlayer, event: RxlStatusEvent)

    //abstract fun nextLightEvent()
    //abstract fun completeCycle()
    open fun completeCycle() {
//        log("completeCycle")
//        if (cycles > currentCycle) {
//            currentCycle++
//            if (isTap) {
//                for (p in players) {
//                    p.isStarted = false
//                }
//            }
//            //pauseCycle(0, getPause())
//            log("completeCycle start new cycle")
//            listener.nextCycle(currentCycle, pauseTime, duration)
//            //resumeObserver(currentCycle, getPause(), duration)
//        } else {
//            log("completeCycle end program...")
//            listener.endProgram(0, 0)
//            isInternalStarted = false
//        }

        listener.endProgram(0, 0)
        isInternalStarted = false
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

    open fun stop() {
        isTap = false
        isInternalStarted = false
        isStarted = false
    }

    open fun paused(pause: Boolean) {
        isPaused = pause

    }
}