/*
 *  Created by Sumeet Kumar on 3/5/20 11:26 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/5/20 11:26 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hexa.pods.rxl.Event
import life.mibo.hexa.pods.rxl.program.RxlPlayer
import life.mibo.hexa.pods.rxl.program.RxlProgram
import java.util.concurrent.TimeUnit

// implement later
class TapAtAllParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "AllAtOnceParser") {

    override fun onCycleStart(player: RxlPlayer) {
        log("child STARTING PROGRAM")
        turnOnAll(player)
    }

    override fun onCycleStart() {
        log("child STARTING PROGRAM")
        if (isTap) {
            for (p in players) {
                if (p.isTapReceived) {
                    if (!p.isStarted) {
                        log("child STARTING PROGRAM starting player $p")
                        p.isStarted = true
                        //p.lastPod = 250
                        turnOnAll(p)
                    }
                    Thread.sleep(THREAD_SLEEP)
                }
            }
        } else {
            for (p in players) {
                //p.lastPod = 250
                turnOnAll(p)
                Thread.sleep(THREAD_SLEEP)
            }
        }
    }


    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        log("child nextLightEvent called")
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
                turnOnAll(player)
            }.doOnError {
                turnOnAll(player)
            }.subscribe()
        } else {
            turnOnAll(player)
        }
    }


    private fun turnOnAll(player: RxlPlayer) {
        log("child lightOnSequence $player")

        //  val uid = player.randomPod()?.uid ?: ""
        // val id = getPlayerId(player)
        val id = player.pods.size
        //log("lightOnAllAtOnce player ${player.id} : $uid :: lastPod $id")
        var count = 1;
        Observable.fromIterable(player.pods).subscribeOn(Schedulers.io()).doOnNext { device ->
            // player.lastUid = uid
            listener.sendColorEvent(device, player.color, actionTime.times(count), player.id, true)
            count++
            //Thread.sleep(10)
            log("nextFocusEvent onChangeColorEvent Observable ON = ${device.uid}")
        }.doOnError {

        }.doOnComplete {
        }.subscribe()
    }

    override fun completeCycle() {
        super.completeCycle()
    }
}