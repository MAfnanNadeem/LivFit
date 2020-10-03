/*
 *  Created by Sumeet Kumar on 9/8/20 5:44 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/8/20 5:44 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.core

import android.graphics.Color
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.RxlStatusEvent
import java.util.concurrent.TimeUnit

public class RXLHelperNew {
    companion object {
        val REFLEX = 10

        @Volatile
        private var INSTANCE: RXLHelperNew? = null
        const val DEBUG = true

        // @Volatile
        //private var receivedFocusAll = false

        fun getInstance(): RXLHelperNew =
            INSTANCE
                ?: synchronized(this) {
                    Logger.e("RXTManager INSTANCE init ")
                    INSTANCE = RXLHelperNew()
                    INSTANCE!!
                }
    }

    private var islandsMap = HashMap<Int, RxlPlayer>()
    private var islandParsers = ArrayList<PodParser>()

    private var disposable: Disposable? = null
    // todo startObserver - start
    // private var publisher: PublishSubject<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()

    private var isStarted = false
    private var isRunning = false

    var isPaused = false

    // var isResumed = false
    //var pauseDuration = 0L
    //var remainDuration = 0
    private var durationSec: Long = 0
    private var durationConsumed: Long = 0

    private var listener: RxlListener? = null


    fun testProgram(): RxlProgram {
        val block1 = RxlBlock(500, 30, 1)
        block1.round = 2
        block1.delay = 5

        val block2 = RxlBlock(150, 20, 1)
        block2.round = 1
        block2.delay = 3

        val block3 = RxlBlock(2000, 15, 1)
        block3.round = 3
        block3.delay = 7

        return RxlProgram("Test Program", Color.BLUE, 0, arrayListOf(block1, block2, block3))
    }


    private fun reset() {
        isStarted = false
        isRunning = false
        isPaused = false
        //remainDuration = 0
        //colorSent = false
        //isFocus = false
        //receivedFocusAll = false
        //actionTime = 0
        //focusCount = 0
        //publisher?.unsubscribeOn(Schedulers.io())
        //publisher = null
    }

    fun with(list: List<RxlPlayer>): RXLHelperNew {
        islandParsers.clear()
        for (i in list) {
            islandParsers.add(PodParser(i))
        }
        return this
    }

    fun with(list: List<RxlPlayer>, listener: RxlListener): RXLHelperNew {
        islandParsers.clear()
        for (i in list) {
            islandParsers.add(PodParser(i))
        }
        this.listener = listener
        return this
    }

    fun startNow(duration: Long) {
        durationSec = duration
        if (durationSec > 0) {
            startProgram()
        }
    }

    private var speed: Float = 1.0f
    fun startNow(duration: Long, speed: Float) {
        durationSec = duration
        this.speed = speed
        if (durationSec > 0) {
            startProgram()
        }
    }

    private fun onNext(it: RxlStatusEvent?) {
        log("onEvent onNext : $it")
        for (i in islandParsers) {
            if (i.islandId == it?.data) {
                log("onNext ID matched ${i.islandId}")
                if (i.lastTile == it.data || i.secondTile == it.data)
                    i.onNext(it)
                return
            } else {
                log("onNext ID NOT matched ${i.islandId} == ${it?.data} ${i.lastTile} or ${i.secondTile} == ${it?.data}")
            }
        }
    }

    private fun startProgram() {
        log("startProgram >>> $isStarted")
        if (isStarted)
            return
        isStarted = true;
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS).take(durationSec)
            .subscribeOn(Schedulers.io()).doOnNext {
                onTick(it)
            }.doOnComplete {
                onExerciseComplete()
            }.doOnSubscribe {
                onExerciseStart()
            }.doOnError {
                onExerciseError(it)
            }.subscribe()

    }


    private fun onExerciseStart() {
        log("....................onExerciseStart.....................")
//        Observable.fromIterable(islandParsers).doOnNext {
//            log("fromIterable islandParsers start ${it.id}")
//            it.onProgramStart()
//            log("fromIterable islandParsers end ")
//        }.doOnComplete {
//            log("fromIterable islandParsers complete ")
//        }.subscribe()
        for (i in islandParsers) {
            log("fromIterable islandParsers start ${i.islandId}")
            i.onProgramStart(speed)
            log("fromIterable islandParsers end ")
        }
        listener?.startProgram(0, durationSec.toInt())
    }

    private fun onExerciseComplete() {
        log("....................onExerciseComplete.....................")
        for (i in islandParsers) {
            i.onProgramEnd()
        }
        if (isStarted)
            listener?.endProgram(0, 0)
        isStarted = false;
    }

    private fun onTick(time: Long) {
        log("onTick $time.....................")
        listener?.onTime(0, time)
        durationConsumed = time
    }

    private fun onExerciseError(e: Throwable?) {
        log("....................onExerciseError..................... $e")
        e?.printStackTrace()
    }


    // END

    private fun dispose() {
        disposable?.dispose()
    }

    fun register() {

    }

    fun unregister() {
        // EventBus.getDefault().unregister(this)
        dispose()
        onExerciseComplete()
        isRunning = false
        isStarted = false;

    }


    @Synchronized
    fun postDirect(event: RxlStatusEvent) {
        log("postDirect onNext $isStarted: $event")
        if (isStarted)
            onNext(event)
    }

    fun getScore(): ArrayList<ScoreItem> {
        val list = ArrayList<ScoreItem>()
        for (i in islandParsers) {
            list.add(i.getScore())
        }
        return list;
    }

    var lastPauseBlock = 0;
    fun resumeProgram() {
        log("resumeProgram $isPaused : $durationSec  : $durationConsumed")
        if (isPaused) {
            if (durationSec > 0L) {
                durationSec = durationSec.minus(durationConsumed)
                log("durationSec.minus(durationConsumed) $durationSec : $durationConsumed")
                if (durationSec > 0) {
                    startProgram()
                }
            }
        }
    }

    fun pauseProgram() {
        log("pauseProgram")
        if (isPaused)
            return
        isPaused = true
        for (i in islandParsers) {
            i.onProgramPause()
        }
        disposable?.dispose()
    }

    private fun log(msg: String?) {
        Logger.e("RXLTest - $msg")
    }
}