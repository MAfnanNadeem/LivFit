/*
 *  Created by Sumeet Kumar on 3/4/20 3:57 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/4/20 3:56 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.parser

import androidx.core.util.valueIterator
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.MIBO
import life.mibo.hardware.core.Utils
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.rxl.program.RxlColor
import life.mibo.hardware.rxl.program.RxlPlayer
import life.mibo.hardware.rxl.program.RxlProgram
import life.mibo.hardware.rxl.Event
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class AllAtOnceParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "AllAtOnceParser") {

    init {
        log("AllAtOnceParser init..........")
    }

    private val DELAY: Int = 100


    override fun onCycleStart(player: RxlPlayer) {
        log("child STARTING onCycleStart-player")
        lightOnAllAtOnce(player)
    }

    override fun onCycleStart() {
        log("child STARTING onCycleStart isTap $isTap, $players")
        //colorSent = false
        //var pos = 1
//        val seed = RXLHelper.TAP_CODE
//        players?.forEachIndexed { i, player ->
//            player.lastPod = seed.times(i)
//            lightOnAllAtOnce(player)
//            Thread.sleep(20)
//        }
        if (isTap) {
            for (p in getPlayers()) {
                if (p.isTapReceived) {
                    if (!p.isStarted) {
                        p.isStarted = true
                        p.lastPod = 250
                        lightOnAllAtOnce(p)
                    }
                    Thread.sleep(THREAD_SLEEP)
                }
            }
        } else {
            for (p in getPlayers()) {
                p.lastPod = 250
                lightOnAllAtOnce(p)
                Thread.sleep(THREAD_SLEEP)
            }
        }
    }

    override fun onCycleTapStart(playerId: Int) {
        log("child STARTING onCycleTapStart")
        for (p in getPlayers()) {
            if (p.id == playerId) {
                lightOnAllAtOnce(p)
                break
            }
        }
    }

    override fun onProgramStart() {
        //log("onProgramStart")
        super.onProgramStart()
        setColors()
        //log("onProgramStart end")
    }

    @Synchronized
    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        log("child nextLightEvent called $player ------------------------------------------------- SHOULD NEVER CALL")
//        if (player.lastUid == event.uid) {
//            colorSent = false
//            log("RxlStatusEvent UID Matched ${player.lastUid} == ${event.uid} ")
//            player.events.add(Event(player.events.size + 1, actionTime, event.time, player.isFocus))
//            if (delayTime > MIN_DELAY) {
//                Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
//                    lightOnAllAtOnce(player)
//                }.doOnError {
//                    MiboEvent.log("AllAtOnceParser onNext error: $it")
//                    lightOnAllAtOnce(player)
//                }.subscribe()
//            } else {
//                lightOnAllAtOnce(player)
//            }
//        } else {
//            log("RxlStatusEvent UID NOT Matched >> ${player.lastUid} ")
//            //player.wrongEvents.add(Event(player.wrongEvents.size + 1, actionTime, event.time))
//        }
    }

    @Synchronized
    override fun onAllATOnce(event: RxlStatusEvent, id: Int) {
        log("child: onAllATOnce >> $id : $event ")
        when (id) {
            in 1..50 -> {
                playerEvent(players[0], event)
            }
            in 51..100 -> {
                playerEvent(players[1], event)
            }
            in 101..150 -> {
                playerEvent(players[2], event)
            }
            in 151..200 -> {
                playerEvent(players[3], event)
            }
        }
        //super.onAllATOnce(event, id)
    }

    private fun playerEvent(player: RxlPlayer, event: RxlStatusEvent) {
        log("child playerEvent player ${player.id} :: ${player.lastPod}  = ${event.data}")
        if (player.lastPod == event.data) {
            if (player.lastUid == event.uid) {
                //colorSent = false
                log("playerEvent UID Matched  ")
                player.events.add(Event(player.events.size + 1, actionTime, event.time, true))
                next(player)
            } else {
                log("playerEvent UID NOT Matched >> ${player.lastUid}  == ${event.uid}")
                player.events.add(Event(player.events.size + 1, actionTime, event.time, false))
                next(player)
                //player.wrongEvents.add(Event(player.wrongEvents.size + 1, actionTime, event.time))
            }
        } else {
            log("child playerEvent player already received...........")
        }
    }

    private fun next(player: RxlPlayer) {
        log("next next next")
        if (delayTime > MIN_DELAY) {
            Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                lightOnAllAtOnce(player)
            }.doOnError {
                MIBO.log("AllAtOnceParser onNext error: $it")
                lightOnAllAtOnce(player)
            }.subscribe()
        } else {
            lightOnAllAtOnce(player)
        }
    }

    private fun getPlayerId(player: RxlPlayer): Int {
        player.inc()
        when (player.id) {
            1 -> {
                if (player.lastPod >= 50)
                    player.lastPod = 1
            }
            2 -> {
                if (player.lastPod >= 100)
                    player.lastPod = 51
            }
            3 -> {
                if (player.lastPod >= 150)
                    player.lastPod = 101
            }
            4 -> {
                if (player.lastPod >= 200)
                    player.lastPod = 151
            }
        }

        return player.lastPod

    }

    @Synchronized
    private fun lightOnAllAtOnce(player: RxlPlayer) {
        //val id = player.nextRandom()
        val uid = player.randomPod()?.uid
        val id = getPlayerId(player)
        log("lightOnAllAtOnce player ${player.id}, uid $uid, getPlayerId $id, action $actionTime")
        Observable.fromIterable(player.pods).subscribeOn(Schedulers.io()).doOnNext { device ->
            if (uid == device.uid) {
                player.lastUid = device.uid
                //player.isFocus = true
                listener.sendDelayColorEvent(device, player.color, actionTime, id, DELAY, true)
            } else {
                //player.isFocus = false
                listener.sendDelayColorEvent(
                    device,
                    randomColor(player),
                    actionTime,
                    id,
                    DELAY,
                    false
                )
            }
            log("lightOnAllAtOnce sendDelayColorEvent to player ${player.id} - ${device.uid}")
            Thread.sleep(10)
            //log("nextFocusEvent onChangeColorEvent Observable ON = ${device.uid}")
        }.doOnError {

        }.doOnComplete {
        }.subscribe()
    }


//    //private var focusValid = false
//    //private var isFocus = false
//    private fun isNextFocus(): Boolean {
//        //return Random.nextInt(50) % 2 == 0
//        return Random.nextInt(50) % 3 == 0
//    }


    override fun completeCycle() {
        super.completeCycle()
    }

    var colors = ArrayList<RxlColor>()
    private fun setColors() {
        val list = Utils.getColors()
        colors.clear()
        colors.addAll(list)
    }

    private fun randomColor(player: RxlPlayer): Int {
        var i = Random.nextInt(colors.size)
        //var c = colors[i].activeColor
        log("nextRandomColor i $i , colorPosition ${player.colorId}")
        if (i == player.colorId && i < colors.size - 2)
            i++
        return colors[i].activeColor
    }

//    fun onNextEvent(player: RxlPlayer, event: RxlStatusEvent) {
//        Observable.fromIterable(player.pods).subscribeOn(Schedulers.io()).doOnNext {
//            log("nextFocusEvent2 onChangeColorEvent Observable OFF = ${it.uid}")
//        }.doOnComplete {
//            log("nextFocusEvent2 doOnComplete NOT MATCH >> ${event.uid} ")
//        }.subscribe()
//
//        Thread.sleep(20)
//    }
    // private var focusCount = 0

//    var colorSent = false
//    private fun nextFocusLight(player: RxlPlayer) {
//        //if (colorSent)
//        //     return
//        colorSent = true
//        log("child nextFocusLight $player")
//
//        val id = player.generateRandom()
//        val pod = player.pods[id]
//        log("lightOnFocus")
//        player.isFocus = isNextFocus()
//        if (player.isFocus) {
//            // focusValid = true
//            player.lastUid = pod.uid
//            listener.sendColorEvent(pod, player.color, actionTime, player.id, true)
//            //sendToFocusLight(player)
//        } else {
//            //focusValid = false
//            player.lastUid = pod.uid
//            listener.sendDelayColorEvent(pod, randomColor(player), actionTime, player.id, 100, true)
//            //sendToNonFocusLight(player)
//        }
//    }
}