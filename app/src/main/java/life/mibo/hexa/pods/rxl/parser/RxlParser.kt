/*
 *  Created by Sumeet Kumar on 2/23/20 9:35 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/23/20 9:34 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hexa.pods.Event
import life.mibo.hexa.pods.rxl.RXLManager
import life.mibo.hexa.pods.rxl.RxlLight
import life.mibo.hexa.pods.rxl.RxlPlayer
import life.mibo.hexa.pods.rxl.RxlProgram
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

abstract class RxlParser {
    //abstract functions
    abstract fun type(): RxlLight

    abstract fun name(): String
    abstract fun player(): RxlPlayer.Player
    abstract fun program(): RxlProgram
    //abstract fun onEvent(event: RxlStatusEvent, lastUid: String)
    abstract fun onEvent(event: RxlStatusEvent, matched: Boolean)
    //abstract fun onReset()

    abstract fun exerciseStart()
    abstract fun exerciseEnd()
    abstract fun cycleStart()
    abstract fun cycleEnd()
    abstract fun cyclePause()
    abstract fun cycleResume()


    // private vars
    private var devices = ArrayList<Device>()
    private var events = ArrayList<Event>()
    private var wrongEvents = ArrayList<Event>()
    private var publisher: PublishSubject<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()
    private var observers: CompositeDisposable? = null
    private var disposable: Disposable? = null
    private var delayDisposable: Disposable? = null
    private var lastUid = ""
    // time in millis
    private var actionTime = 0
    private var pauseTime = 0
    private var delayTime = 0
    private var cycles = 0
    private var currentCycle = 0
    private var duration = 0
    private var color = 0
    private var randomColor = listOf(0)
    private var isStarted = false
    private var isRunning = false
    private var isInternalStarted = false
    var listener: RXLManager.Listener? = null


    fun devices() = devices
    fun events() = events

    open fun onReset() {
        log("SequenceParser onReset")
        isStarted = false
        isInternalStarted = false
        isRunning = false
        //colorSent = false
        //isFocus = false
        //receivedFocusAll = false
        publisher?.unsubscribeOn(Schedulers.io())
        publisher = null
    }

    @Synchronized
    open fun onStart(onTap: Boolean = false) {
        //startPublish()
        actionTime = program().getAction().times(1000)
        pauseTime = program().getPause()
        cycles = program().getCyclesCount()
        duration = program().getDuration()
        color = program().getActiveColor()
        register(this)
        logi("onStart action $actionTime , pause $pauseTime, cycle $cycles, delay $delayTime, dur $duration, color $color tap $onTap")
        onReset()
        if (onTap) {
            startTapPublish()
        } else {
            startNowPublish()
        }
    }

    open fun onStop() {
        dispose()
        //publisher?.unsubscribeOn(Schedulers.io())
    }


    private fun startTapPublish() {
        log("SequenceParser startTapPublish")
        publisher = PublishSubject.create<RxlStatusEvent>()
        publisher!!.subscribeOn(Schedulers.io()).doOnNext {
            logi("publisherTap RxlStatusEvent doOnNext ${it.uid}  == lastUid $lastUid  receivedFocusAll")

            if (it.uid == lastUid) {
                if (!isInternalStarted) {
                    log("RxlStatusEvent2 starting........... >> $isInternalStarted ")
                    if (it.time > 10) {
                        startInternal()
                        return@doOnNext
                    } else
                        return@doOnNext
                }

                log("RxlStatusEvent UID Matched ${it.uid} == $lastUid ")
                events.add(Event(events.size + 1, actionTime, it.time))
                onEvent(it, true)
            } else {
                wrongEvents.add(Event(events.size + 1, actionTime, it.time))
                onEvent(it, false)
            }
            return@doOnNext
        }.subscribe()

        if (isInternalStarted)
            return
        log("RxlStatusEvent2 isStarted >> $isInternalStarted ")
        if (devices.isNotEmpty()) {
            val device = devices[0]
            lastUid = device.uid
            device.let {
                it.colorPalet = color
                lastUid = it.uid
                EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, 10 * 10000))
            }
            log("RxlStatusEvent2 ChangeColorEvent2 send To UID == $lastUid ")
        }
    }


    private fun startNowPublish() {
        log("SequenceParser startNowPublish")
        publisher = PublishSubject.create<RxlStatusEvent>()
        publisher!!.subscribeOn(Schedulers.io()).doOnNext {
            //logi("publisherTap RxlStatusEvent doOnNext ${it.uid}  == lastUid $lastUid  receivedFocusAll $receivedFocusAll isFocus $isFocus")
            if (it.uid == lastUid) {
                events.add(Event(events.size + 1, actionTime, it.time))
                onEvent(it, true)
            } else {
                wrongEvents.add(Event(events.size + 1, actionTime, it.time))
                onEvent(it, false)
            }
        }.subscribe()

        if (isInternalStarted)
            return

        startInternal()
    }

    private fun startInternal() {
        if (observers == null || observers?.isDisposed == true)
            observers = CompositeDisposable()
        isInternalStarted = true
        startExercise(currentCycle, duration)
    }

    private fun dispose() {
        observers?.add(Single.fromCallable {
            log("disposing observers")
            observers?.dispose()
            disposable?.dispose()
            delayDisposable?.dispose()
        }.subscribeOn(Schedulers.computation()).subscribe())
    }

    private fun startObserver(cycle: Int, duration: Int) {
        log("startObserver...")
        disposable?.dispose()
        observers?.add(
            Observable.timer(duration.toLong(), TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).doOnComplete {
                    log("startObserver >> doOnComplete cycle $cycle - duration $duration")
                    completeCycle(currentCycle, duration)
                }.doOnSubscribe {
                    log("startObserver >> doOnSubscribe cycle $cycle - duration $duration")
                    startCycle(currentCycle, duration)
                }.doOnError {
                    log("startObserver >> doOnError cycle $cycle ${it.message}")
                    it.printStackTrace()
                }.subscribe()
        )
    }

    private fun resumeObserver(cycle: Int, delay: Int, duration: Int) {
        log("resumeObserver >> cycle $cycle - duration $duration")
        disposable = Single.timer(delay.toLong(), TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .doOnSuccess {
                resumeCycle(cycle)
                startObserver(cycle, duration)
                //resumeCycle(cycles, 0)
            }.subscribe()
        disposable?.let {
            observers?.add(it)
        }
    }

    private fun delayObserver() {
        log("delayObserver >> ")
        delayDisposable?.dispose()
        delayDisposable = null
        delayDisposable = Single.timer(actionTime.toLong().plus(1000), TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io()).doOnSuccess {
                onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0), lastUid), true)
                log("RxlStatusEvent delayObserver doOnComplete")
            }.doOnDispose {
                log("RxlStatusEvent delayObserver doOnDispose")
            }.subscribe()

    }

    private fun startCycle(cycle: Int, time: Int = 0) {
        log(".......... startCycle .......... " + getTime())
        cycleStart()
        listener?.onCycleStart(cycle, time)
        isStarted = true
        //lastPod = 0
        //nextLightEvent()
        //publisher.onNext("startCycle")

    }

    private fun pauseCycle(cycle: Int, time: Int = 0) {
        log(".......... pauseCycle .......... " + getTime())
        listener?.onCyclePaused(cycle, time)
        //EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle paused"))
        isStarted = false
        //publisher.onNext("pauseCycle")
        cyclePause()
    }

    private fun resumeCycle(cycle: Int, time: Int = 0) {
        log(".......... resumeCycle .......... ")
        listener?.onCycleResumed(cycle)
        //startCycle(cycle)
        //isStarted = true
        //publisher.onNext("resumeCycle")
        cycleResume()
    }

    private fun completeCycle(cycle: Int, duration: Int = 0) {
        log(".......... completeCycle .......... " + getTime())
        isStarted = false
        listener?.onCycleEnd(cycle)
        delayDisposable?.dispose()
        disposable?.dispose()
        if (cycles > currentCycle) {
            currentCycle++
            pauseCycle(0, pauseTime)
            resumeObserver(currentCycle, pauseTime, duration)
        } else {
            completeExercise(0, 0)
        }
        // isStarted = false
        cycleEnd()

    }

    private fun startExercise(cycle: Int, time: Int = 0) {
        log("startExercise........")
        isRunning = true
        listener?.onExerciseStart()
        //exerciseStart()
        startObserver(currentCycle, duration)
    }

    private fun completeExercise(cycle: Int, time: Int = 0) {
        log(".......... completeExercise .......... ")
        isRunning = false
        try {
            publisher?.onComplete()
            publisher?.unsubscribeOn(Schedulers.io())
            publisher = null
        } catch (e: Exception) {
        }
        listener?.onExerciseEnd()
        exerciseEnd()
    }

    fun sendColor(device: Device?, color: Int) {
        log("sendColor ${device?.uid}  $color")
        delayObserver()
//        if (unitTest) {
//            log("sendColorEvent test >> sent $lastPod")
//            lastUid = "000002"
//            testObserver(getAction())
//            return
//        }
        device?.let {
            it.colorPalet = color
            lastUid = it.uid
            EventBus.getDefault()
                .postSticky(
                    ChangeColorEvent(
                        it,
                        it.uid,
                        actionTime
                    )
                )
            log("RxlStatusEvent ChangeColorEvent send To UID == $lastUid ")
        }
    }

    fun turnOn(device: Device?, color: Int, time: Int) {
        device?.let {
            it.colorPalet = color
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, time))
        }
    }

    fun turnOff(device: Device?) {
        EventBus.getDefault().postSticky(ChangeColorEvent(device, device?.uid, 0))
    }

    fun turnOffAll(action: () -> Unit, delay: Int = 0) {
        Observable.fromIterable(devices).subscribeOn(Schedulers.io()).doOnNext {
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, 0))
            if (delay > 0)
                Thread.sleep(delay.toLong())
        }.doOnComplete {
            action.invoke()
        }.subscribe()
    }

    fun turnOnAll(action: () -> Unit, color: Int, time: Int, delay: Int = 0) {
        Observable.fromIterable(devices).subscribeOn(Schedulers.io()).doOnNext {
            it.colorPalet = color
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, time))
            if (delay > 0)
                Thread.sleep(delay.toLong())
        }.doOnComplete {
            action.invoke()
        }.subscribe()
    }


    @Subscribe
    fun onTap(event: RxlStatusEvent) {
        log("onTap $event")
        EventBus.getDefault().removeStickyEvent(event)
        publisher?.onNext(event)
    }

    fun log(msg: String) {
        life.mibo.hardware.core.Logger.e("RxlParser: ${name()}: " + msg)
    }

    fun logi(msg: String) {
        life.mibo.hardware.core.Logger.i("RxlParser: ${name()}: " + msg)
    }

    fun register(any: Any) {
        log("register")
        EventBus.getDefault().register(any)
    }

    fun unregister(any: Any) {
        EventBus.getDefault().unregister(any)
        log("unregister")
    }

    private fun getTime(): String {
        try {
            return SimpleDateFormat("mm:ss").format(Date())
        } catch (e: Exception) {

        }
        return "00:00"
    }

}