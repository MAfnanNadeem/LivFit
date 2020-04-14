/*
 *  Created by Sumeet Kumar on 2/1/20 8:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:37 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.rxl.parser

import io.reactivex.Single
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.android.pods.rxl.Event
import life.mibo.android.pods.rxl.program.RxlColor
import life.mibo.android.pods.rxl.program.RxlPlayer
import life.mibo.android.pods.rxl.program.RxlProgram
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Utils
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FocusParser(program: RxlProgram, listener: Listener) :

    RxlParser(program, listener, "FocusParser") {

    override fun onProgramStart() {
        super.onProgramStart()
        setColors()
    }

    override fun onCycleStart(player: RxlPlayer) {
        log("child STARTING onCycleStart-player")
        nextFocusLight(player)
        player.isStarted = true
    }

    override fun onCycleStart() {
        log("child STARTING onCycleStart")
        //colorSent = false
        if (isTap) {
            for (p in players) {
                if (p.isTapReceived) {
                    if (!p.isStarted) {
                        p.isStarted = true
                        p.lastPod = 250
                        nextFocusLight(p)
                    }
                    Thread.sleep(THREAD_SLEEP)
                }
            }
        } else {
            for (p in players) {
                //p.lastPod = 250
                nextFocusLight(p)
                Thread.sleep(THREAD_SLEEP)
            }
        }
    }

    override fun onCycleTapStart(playerId: Int) {
        log("child STARTING onCycleTapStart")
        for (p in players) {
            if (p.id == playerId) {
                nextFocusLight(p)
                break
            }
        }
    }

    @Synchronized
    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        log("child nextLightEvent called")
        if (player.lastUid == event.uid) {
            colorSent = false
            log("RxlStatusEvent UID Matched ${player.lastUid} == $event.uid ")
            player.events.add(Event(player.events.size + 1, actionTime, event.time, player.isFocus))
            if (delayTime > MIN_DELAY) {
                Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                    nextFocusLight(player)
                }.doOnError {
                    MiboEvent.log("FocusParser onNext error: $it")
                    nextFocusLight(player)
                }.subscribe()
            } else {
                nextFocusLight(player)
            }
        } else {
            log("RxlStatusEvent UID NOT Matched >> ${player.lastUid} ")
            //player.wrongEvents.add(Event(player.wrongEvents.size + 1, actionTime, event.time))
        }
    }


    //private var focusValid = false
    //private var isFocus = false
    private fun isNextFocus(): Boolean {
        //return Random.nextInt(50) % 2 == 0
        return Random.nextInt(50) % 3 == 0
    }


    override fun completeCycle() {
        super.completeCycle()
    }

    var colors = ArrayList<RxlColor>()
    private fun setColors() {
        val list = Utils.getColors()
        colors.clear()
        list.forEach {
            it.id?.let { c ->
                colors.add(RxlColor(c))
            }
        }
    }

    private fun nextRandomColor(player: RxlPlayer): Int {
        var i = Random.nextInt(colors.size)
        //var c = colors[i].activeColor
        log("nextRandomColor i $i , colorPosition ${player.colorId}")
        if (i == player.colorId && i < colors.size - 2)
            i++
        return colors[i].activeColor
    }

    var colorSent = false
    private fun nextFocusLight(player: RxlPlayer) {
        //if (colorSent)
        //     return
        colorSent = true
        log("child nextFocusLight $player")

        val id = player.nextRandom()
        val pod = player.pods[id]
        log("lightOnFocus")
        player.isFocus = isNextFocus()
        if (player.isFocus) {
            // focusValid = true
            player.lastUid = pod.uid
            listener.sendColorEvent(pod, player.color, actionTime, player.id, true)
            //sendToFocusLight(player)
        } else {
            //focusValid = false
            var c = Random.nextInt(colors.size)
            //var c = colors[i].activeColor
            log("nextRandomColor i $c , colorPosition ${player.colorId}")
            if (c == player.colorId && c < colors.size - 2)
                c++
            player.lastUid = pod.uid
            listener.sendColorEvent(pod, colors[c].activeColor, actionTime, player.id, true)
            //sendToNonFocusLight(player)
        }
    }
}