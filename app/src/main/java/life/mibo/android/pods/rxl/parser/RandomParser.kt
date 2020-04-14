/*
 *  Created by Sumeet Kumar on 3/4/20 3:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/4/20 3:56 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.rxl.parser

import io.reactivex.Single
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.android.pods.rxl.program.RxlPlayer
import life.mibo.android.pods.rxl.program.RxlProgram
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.pods.rxl.Event
import java.util.concurrent.TimeUnit

class RandomParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "RandomParser") {

    override fun onCycleStart(player: RxlPlayer) {
        log("child STARTING PROGRAM")
        nextRandomLight(player)
    }

    override fun onCycleStart() {
        log("child STARTING PROGRAM")

        if (isTap) {
            for (p in players) {
                if (p.isTapReceived) {
                    if (!p.isStarted) {
                        log("child STARTING PROGRAM starting player $p")
                        p.isStarted = true
                       // p.lastPod = 250
                        nextRandomLight(p)
                    }
                    Thread.sleep(THREAD_SLEEP)
                }
            }
        } else {
            for (p in players) {
                //p.lastPod = 250
                nextRandomLight(p)
                Thread.sleep(THREAD_SLEEP)
            }
        }
    }

    override fun onCycleTapStart(playerId: Int) {
        log("child STARTING onCycleTapStart")
        for (p in players) {
            if (p.id == playerId) {
                nextRandomLight(p)
                break
            }
        }
    }

    @Synchronized
    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        log("child nextLightEvent called")
        if (player.lastUid == event.uid) {
            log("RxlStatusEvent UID Matched ${player.lastUid} == $event.uid ")
            player.events.add(
                Event(
                    player.events.size + 1,
                    actionTime,
                    event.time
                )
            )
            if (delayTime > MIN_DELAY) {
                Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                    nextRandomLight(player)
                }.doOnError {
                    nextRandomLight(player)
                    MiboEvent.log("RandomParser onNext error: $it")
                }.subscribe()
            } else {
                nextRandomLight(player)
            }
        } else {
            log("RxlStatusEvent UID NOT Matched >> ${player.lastUid} ")
            player.wrongEvents.add(
                Event(
                    player.wrongEvents.size + 1,
                    actionTime,
                    event.time
                )
            )
        }
    }


    private fun nextRandomLight(player: RxlPlayer) {
        log("child lightOnSequence $player")

        val id = player.nextRandom()
        val pod = player.pods[id]
        player.lastUid = pod.uid
        listener.sendColorEvent(pod, player.color, actionTime, player.id, true)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        //player.inc()
    }

    override fun completeCycle() {
        super.completeCycle()
    }
}