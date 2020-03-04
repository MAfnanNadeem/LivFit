/*
 *  Created by Sumeet Kumar on 3/2/20 11:59 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/25/20 8:45 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import io.reactivex.Single
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hexa.pods.Event
import life.mibo.hexa.pods.rxl.RxlPlayer
import life.mibo.hexa.pods.rxl.RxlProgram
import java.util.concurrent.TimeUnit

class SequenceParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "TestParser") {


    fun create() {

    }

    fun nextEvent(event: RxlStatusEvent) {

        players.forEach {
            if (it.id == event.data) {
                if (delayTime > 0) {
                    Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess { _ ->
                        lightOnSequence(it)
                    }.subscribe()
                } else {
                    lightOnSequence(it)
                }
                return@forEach
            }
        }

    }

    fun hasNextCycle(): Boolean {
        if (cycles > currentCycle) {
            currentCycle++
            return true
        }
        return false
    }

//    override fun startExercise() {
//        startInternal()
//    }

    @Synchronized
    override fun onCycleTapStart(playerId: Int) {
        log("child STARTING PROGRAM")
        for (p in players) {
            if (p.id == playerId) {
                lightOnSequence(p)
                break
            }
        }
    }

    @Synchronized
    override fun onCycleStart() {
        log("child STARTING PROGRAM")
        for (p in players) {
            lightOnSequence(p)
            Thread.sleep(50)
        }
    }


    @Synchronized
    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        log("child nextLightEvent called")
        if (player.lastUid == event.uid) {
            log("RxlStatusEvent UID Matched ${player.lastUid} == $event.uid ")
            player.events.add(Event(player.events.size + 1, actionTime, event.time))
            if (delayTime > 0) {
                Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                    lightOnSequence(player)
                }.doOnError {
                    lightOnSequence(player)
                }.subscribe()
            } else {
                lightOnSequence(player)
            }
        } else {
            log("RxlStatusEvent UID NOT Matched >> ${player.lastUid} == $lastUid ")
            player.wrongEvents.add(Event(player.wrongEvents.size + 1, actionTime, event.time))
        }


//        for (p in players) {
//            if (p.id == player.id) {
//                lightOnSequence(player)
//                break
//            }
//        }

    }


    private fun lightOnSequence(player: RxlPlayer) {
        log("child lightOnSequence $player")
        log("lightOnSequence lastPod ${player.lastPod}, size ${player.pods.size}")
        if (player.lastPod >= player.pods.size)
            player.lastPod = 0
        val pod = player.pods[player.lastPod]
        player.lastUid = pod.uid
        listener.sendColorEvent(pod, player.color, actionTime, player.id, true)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        player.inc()
    }


    override fun completeCycle() {
        log("completeCycle")
        if (cycles > currentCycle) {
            currentCycle++
            //pauseCycle(0, getPause())
            log("completeCycle start new cycle")
            listener.nextCycle(currentCycle, pauseTime, duration)
            //resumeObserver(currentCycle, getPause(), duration)
        } else {
            log("completeCycle end program...")
            listener.endProgram(0, 0)
        }
    }
}