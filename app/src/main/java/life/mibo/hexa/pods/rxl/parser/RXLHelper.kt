/*
 *  Created by Sumeet Kumar on 3/3/20 4:47 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/3/20 4:47 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import android.util.SparseArray
import androidx.core.util.forEach
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hexa.pods.pod.PodType
import life.mibo.hexa.pods.rxl.RXLManager
import life.mibo.hexa.pods.rxl.RxlColor
import life.mibo.hexa.pods.rxl.RxlProgram
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class RXLHelper private constructor() {

    init {
        life.mibo.hardware.core.Logger.e("RXLManager init for first time")
    }

    private var childListener = object : RxlParser.Listener {

        override fun onDispose() {
            log("BaseTestParser.Listener : onDispose")
            dispose()
        }

        fun createCompositeDisposable() {
            log("BaseTestParser.Listener : createCompositeDisposable")

        }

        fun startExercise(cycle: Int, duration: Int) {
            log("BaseTestParser.Listener : startExercise")

        }

        override fun startProgram(cycle: Int, duration: Int) {
            log("BaseTestParser.Listener : startObserver $duration")

            startObserver(cycle, duration)
        }

        override fun nextCycle(cycle: Int, pause: Int, duration: Int) {
            log("BaseTestParser.Listener : nextCycle $cycle $pause $duration")
            pauseCycle(cycle, pause)
            resumeObserver(cycle, pause, duration)
        }

        override fun sendColorEvent(
            device: Device,
            color: Int,
            action: Int,
            playerId: Int,
            observe: Boolean
        ) {
            // log("BaseTestParser.Listener : sendColorEvent player $playerId action $action")
            sendColor(device, color, action, playerId, observe)
        }

        override fun endProgram(cycle: Int, duration: Int) {
            log("BaseTestParser.Listener : endProgram $duration")
            completeExercise(cycle, duration)
        }
    }


    companion object {
        val REFLEX = 10
        @Volatile
        private var INSTANCE: RXLHelper? = null

        // @Volatile
        //private var receivedFocusAll = false

        fun getInstance(): RXLHelper =
            INSTANCE ?: synchronized(this) {
                life.mibo.hardware.core.Logger.e("RXLManager INSTANCE init ")
                INSTANCE =
                    RXLHelper()
                INSTANCE!!
            }
    }


    private var parrentListener: RXLManager.Listener? = null
    // private var devicesUids = ArrayList<DeviceEvent>()
    //var events = ArrayList<Event>()
    //var wrongEvents = ArrayList<Event>()
    private var isMultiPlayer = false
    private var lastActivePod = -1
    var program: RxlProgram? = null
    var type: PodType = PodType.UNKNOWN

    private fun refresh() {
        // devices.clear()
        //events.clear()
        // players.clear()
        //wrongEvents.clear()
    }


    fun with(exercise: RxlProgram): RXLHelper {
        this.program = exercise
        refresh()
        return this
    }


    fun withListener(listener: RXLManager.Listener): RXLHelper {
        this.parrentListener = listener
        return this
    }


    fun getTime(): String {
        try {
            return SimpleDateFormat("mm:ss").format(Date())
        } catch (e: Exception) {

        }
        return "00:00"
    }


    // todo startObserver - start
    // private var publisher: PublishSubject<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()
    private var publisher: PublishProcessor<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()
    private var observers: CompositeDisposable? = null
    private var delayObservers: DisposableArray? = null
    private var disposable: Disposable? = null
    //private var colorDisposable: Disposable? = null
    private var colorDisposable = SparseArray<Disposable?>()
    //var isRandom = false
    var lightLogic = 1 // 1= Sequence, 2 = Random, 3 = Focus, 4 = Focus at All
    //var lightLogic : RxlLight = RxlLight.SEQUENCE // todo enum may be costly for performance, using int
    private var unitTest = false
    private var isStarted = false
    private var isInternalStarted = false
    private var isRunning = false
    var baseParser: RxlParser? = null
    //private var actionTime = 0

    private fun reset() {
        isStarted = false
        isInternalStarted = false
        isRunning = false
        //colorSent = false
        //isFocus = false
        //receivedFocusAll = false
        //actionTime = 0
        //focusCount = 0
        publisher?.unsubscribeOn(Schedulers.io())
        publisher = null
    }

    private fun createProgram() {
        baseParser = when (program?.lightLogic()) {
            1 -> {
                SequenceParser(program!!, childListener)
            }
            else -> {
                SequenceParser(program!!, childListener)
            }
        }
    }

    fun start(tap: Boolean) {
        if (tap)
            startOnTap()
        else
            startNow()
    }

    private fun startOnTap() {
        //isRandom = random
        //register()
        reset()
        createProgram()
        createTapPublish()
        //startInternal()
    }

    private fun startNow() {
        //isRandom = random
        //register()
        reset()
        createProgram()
        createNowPublish()
        startInternal()
        //startInternal()
    }

    private fun createTapPublish() {
//        publisher = PublishSubject.create<RxlStatusEvent>()
//        publisher!!.subscribeOn(Schedulers.io()).doOnNext {
//            logi("publisherTap RxlStatusEvent doOnNext ${it.uid}  == lastUid $lastUid  receivedFocusAll $receivedFocusAll isFocus $isFocus")
//            if (isFocus) {
//                nextFocusEvent(it)
//                return@doOnNext
//            }
//
//            if (it.uid == lastUid) {
//                if (!isInternalStarted) {
//                    log("RxlStatusEvent2 starting........... >> $isInternalStarted ")
//                    if (it.time > 10) {
//                        startInternal()
//                        return@doOnNext
//                    } else
//                        return@doOnNext
//                }
//
//                log("RxlStatusEvent UID Matched ${it.uid} == $lastUid ")
//                nextEvent(it.time)
//            } else {
//                log("RxlStatusEvent UID NOT Matched >> ${it.uid} == $lastUid ")
//                return@doOnNext
//            }
//        }.subscribe()

        if (isInternalStarted)
            return

        log("RxlStatusEvent2 isStarted >> $isInternalStarted ")
//        if (devices.isNotEmpty()) {
//            val device = devices[0]
//            lastUid = device.uid
//            device.let {
//                it.colorPalet = getColor()
//                lastUid = it.uid
//                EventBus.getDefault()
//                    .postSticky(
//                        ChangeColorEvent(
//                            it,
//                            it.uid,
//                            10 * 10000
//                        )
//                    )
//            }
//            lastPod = 1
//            log("RxlStatusEvent2 ChangeColorEvent2 send To UID == $lastUid ")
//            listener?.onTapColorSent()
//        }
    }

    //Back Pressure handel
    //https://www.baeldung.com/rxjava-backpressure
    private fun createNowPublish() {
        //publisher = PublishSubject.create<RxlStatusEvent>()
        publisher = PublishProcessor.create<RxlStatusEvent>()
        //publisher!!.toFlowable(BackpressureStrategy.BUFFER)
        //publisher!!.onBackpressureBuffer
        //publisher?.firstElement()
        publisher!!.onBackpressureBuffer().subscribeOn(Schedulers.io()).doOnNext {
            logi("publisherNow RxlStatusEvent doOnNext ${it.uid}  data ${it.data} ")
            if (!isStarted)
                return@doOnNext
            baseParser!!.onEvent(it)
        }.doOnError {
            log("ERROR............ $it")
        }.subscribe()
    }

    // Private Functions

    private fun startInternal() {
        //baseParser = TestParser(program!!, testListener)
        isInternalStarted = true
        //colorSent = false
        log("startInternal..........")
        log(
            "startInternal.......... duration ${program?.getDuration()} cycle ${program?.getCyclesCount()} " +
                    "action ${program?.getAction()} pause ${program?.getPause()}"
        )
        if (isRunning) {
            log("exercise is already running")
            disposable?.dispose()
            return
        }
        if (observers == null || observers?.isDisposed == true)
            observers = CompositeDisposable()
        unitTest = false
        //isRandom = program!!.isRandom()
        lightLogic = program!!.lightLogic()

        if (lightLogic > 2) {
            //isFocus = true
            setColors()
        }
        //events.clear()
        // baseParser?.startExercise(0, 0)
        //baseParser?.startObserver(1)
        startExercise(0, 0)
        //startObserver(1, 0)
        //log("startInternal >>> actionTime $actionTime : duration ${getDuration()} : cycles $cycles : pauseTime $pauseTime lightLogic $lightLogic")
    }

    // for focus and all at once only
    val colors = ArrayList<RxlColor>()

    private fun setColors() {
        val list = Utils.getColors()
        colors.clear()
        list.forEach {
            it.id?.let { c ->
                colors.add(RxlColor(c))
            }
        }

//        devicesUids.clear()
//        devices.forEach {
//            devicesUids.add(DeviceEvent(it.uid, false))
//        }

    }

    private fun startObserver(cycle: Int, duration: Int) {
        log("startObserver >> cycle $cycle - duration $duration")
        disposable?.dispose()
        observers?.add(
            Observable.timer(duration.toLong(), TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).doOnComplete {
                    log("startObserver >> doOnComplete cycle $cycle - duration $duration")
                    completeCycle(cycle, duration)
                }.doOnSubscribe {
                    log("startObserver >> doOnSubscribe cycle $cycle - duration $duration")
                    startCycle(cycle, duration)
                }.doOnError {
                    log("startObserver >> doOnError cycle $cycle ${it.message}")
                    it.printStackTrace()
                }.subscribe()
        )
    }

    private fun resumeObserver(cycle: Int, delay: Int, duration: Int) {
        log("resumeObserver >> cycle $cycle - duration $duration")
        disposable =
            Single.timer(delay.toLong(), TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .doOnSuccess {
                    resumeCycle(cycle)
                    startObserver(cycle, duration)
                    //resumeCycle(cycles, 0)
                }.subscribe()
        disposable?.let {
            observers?.add(it)
        }
//        disposable = Observable.just(cycle).delay(delay.toLong(), TimeUnit.SECONDS).doOnComplete {
//            resumeCycle(currentCycle)
//            startObserver(cycle, duration)
//        }.doOnSubscribe {
//            //startCycle(currentCycle)
//            pauseCycle(currentCycle)
//        }.subscribe()
    }

    private fun pauseObserver(pause: Int, cycles: Int) {
        log("pauseObserver >> cycle $cycles - pause $pause")
        Single.just(pause).delay(pause.toLong(), TimeUnit.SECONDS).doOnSuccess {
            resumeCycle(cycles, 0)
        }.subscribe()
    }


    private fun delayObserver(delay: Delay) {
        log("delayObserver $delay")
        //colorDisposable?.dispose()
        //colorDisposable = null
        colorDisposable.get(delay.playerId)?.dispose()
        //colorDisposable.get(playerId) = null


        colorDisposable.put(
            delay.playerId,
            Single.just(delay).delay(delay.action, TimeUnit.MILLISECONDS).doOnSuccess {
                onEvent(
                    RxlStatusEvent(byteArrayOf(0, 3, 0, 0, it.playerId.toByte(), 0, 0, 0), it.uid)
                )
            }.subscribe()
        )

//        colorDisposable.put(playerId, Single.timer(action.toLong().plus(700), TimeUnit.MILLISECONDS)
//            .subscribeOn(Schedulers.io()).doOnSuccess {
//                //receivedFocusAll = false
//                //[-64, 3, 0, 0, 30]
//                onEvent(
//                    RxlStatusEvent(
//                        byteArrayOf(0, 3, 0, 0, playerId.toByte(), 0, 0, 0),
//                        uid
//                    )
//                )
//                log("RxlStatusEvent delayObserver doOnComplete")
//            }.doOnDispose {
//                log("RxlStatusEvent delayObserver doOnDispose")
//            }.subscribe()
//        )
    }

    private fun delayObserver2(uid: String?, action: Int, playerId: Int) {
        delayObservers?.remove(playerId)


        delayObservers?.add(Single.timer(action.toLong().plus(1), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io()).doOnSuccess {
                //receivedFocusAll = false
                //[-64, 3, 0, 0, 30]
                onEvent(
                    RxlStatusEvent(
                        byteArrayOf(0, 3, 0, 0, playerId.toByte(), 0, 0, 0),
                        uid
                    )
                )
                log("RxlStatusEvent delayObserver doOnComplete")
            }.doOnDispose {
                log("RxlStatusEvent delayObserver doOnDispose")
            }.subscribe()
        )
    }


    private fun startCycle(cycle: Int, time: Int = 0) {
        logi(".......... startCycle .......... " + getTime())
        parrentListener?.onCycleStart(cycle, time)
        //colorSent = false
        // EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle started"))
        isStarted = true

        baseParser?.onCycleStart()
        //publisher.onNext("startCycle")
    }

    private fun pauseCycle(cycle: Int, time: Int = 0) {
        logi(".......... pauseCycle .......... " + getTime())
        parrentListener?.onCyclePaused(cycle, time)
        //EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle paused"))
        isStarted = false
        //publisher.onNext("pauseCycle")
    }

    private fun resumeCycle(cycle: Int, time: Int = 0) {
        logi(".......... resumeCycle .......... ")
        parrentListener?.onCycleResumed(cycle)
        //EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle resumed"))
        //startCycle(cycle)
        //isStarted = true
        //publisher.onNext("resumeCycle")
    }

    private fun completeCycle(cycle: Int, duration: Int = 0) {
        logi(".......... completeCycle .......... " + getTime())
        isStarted = false
        parrentListener?.onCycleEnd(cycle)
        //colorDisposable?.dispose()
        colorDisposable?.forEach { key, value ->
            value?.dispose()
        }
        disposable?.dispose()
        baseParser?.completeCycle()
//        if (cycles > currentCycle) {
//            currentCycle++
//            pauseCycle(0, getPause())
//            resumeObserver(currentCycle, getPause(), duration)
//        } else {
//            completeExercise(0, 0)
//        }
        isStarted = false

        //publisher.onNext("completeCycle")

    }

    private fun startExercise(cycle: Int, time: Int = 0) {
        parrentListener?.onExerciseStart()
        logi(".......... startExercise .......... ")
        isRunning = true
        baseParser?.startProgram()
        //publisher.onNext("startExercise")
    }

    private fun completeExercise(cycle: Int, time: Int = 0) {
        logi(".......... completeExercise .......... ")
        isInternalStarted = false
        isRunning = false
        parrentListener?.onExerciseEnd()
        //EventBus.getDefault().post(NotifyEvent(REFLEX.plus(1), getTime() + " Completed...."))
        dispose()

        try {
            publisher?.onComplete()
            publisher?.unsubscribeOn(Schedulers.io())
            publisher = null
        } catch (e: Exception) {
        }
        //publisher.onNext("completeExercise")
    }

    fun stopProgram() {
        log(".......... stopProgram .......... ")
        isStarted = false
        //colorDisposable?.dispose()
        colorDisposable?.forEach { key, value ->
            value?.dispose()
        }
        disposable?.dispose()
        completeExercise(0, 0)
        isStarted = false
        log(".......... stopProgram .......... stopped")
    }


    //private var nextPod = 0
    // private var lastPod = 0
    // private var lastUid = ""
    //private var random: Random? = null


//    @Synchronized
//    private fun lightOnSequence() {
//        if (lastPod >= devices.size)
//            lastPod = 0
//        sendColorEvent(devices[lastPod], activeColor)
//        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
//        lastPod++
//    }
//
//    @Synchronized
//    private fun lightOnRandom() {
//        val id = nextRandom()
//
//        lastPod = if (id == lastPod)
//            nextRandom()
//        else
//            id
//
//        if (lastPod >= devices.size)
//            lastPod = nextRandom()
//
//        sendColorEvent(devices[lastPod], activeColor)
//    }


    //private var allFocus = 0
    //private var focusCount = 0


    fun sendColor(d: Device?, color: Int, action: Int, playerId: Int, observe: Boolean = false) {
        if (observe)
            delayObserver(Delay(d?.uid, action.plus(700).toLong(), playerId))
        d?.let {
            it.colorPalet = color
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, action, playerId))
        }
        //lastPod++
    }


    private fun dispose() {
        observers?.add(Single.fromCallable {
            log("disposing observers")
            observers?.dispose()
            disposable?.dispose()
            colorDisposable?.forEach { key, value ->
                value?.dispose()
            }
            //colorDisposable?.dispose()
        }.subscribeOn(Schedulers.computation()).subscribe())
    }

    fun register() {
        // if (!EventBus.getDefault().isRegistered(this))
        log("register")
        try {
            EventBus.getDefault().register(this)
        } catch (e: java.lang.Exception) {
            MiboEvent.log(e)
        }
    }

    fun unregister() {
        EventBus.getDefault().unregister(this)
        isStarted = false
        isRunning = false
        dispose()
        observers?.dispose()
        //if(::disposable.isInitialized)
        disposable?.dispose()
        //colorDisposable?.dispose()
        colorDisposable?.forEach { key, value ->
            value?.dispose()
        }
        colorDisposable?.clear()
        disposable = null
        parrentListener = null
        log("unregister")
    }


    private fun log(msg: String?) {
        life.mibo.hardware.core.Logger.e("RXLTest - $msg")
    }

    private fun logi(msg: String?) {
        life.mibo.hardware.core.Logger.i("RXLTest - $msg")
    }


    @Subscribe(sticky = true)
    fun onEvent(event: RxlStatusEvent) {
        //log("onEvent RxlStatusEvent")
        EventBus.getDefault().removeStickyEvent(event)
        publisher?.onNext(event)
    }

    fun getHits(): String {
        val b = StringBuilder()
        b.append("\n")
        baseParser?.players?.forEach { it ->
            b.append(it.name)
            b.append("\n")
            b.append("--------------")
            b.append("\n")
            b.append("Total: " + it.events.size)
            var hits = 0
            var missed = 0
            it.events?.forEach { ev ->
                if (ev.tapTime > 1)
                    hits++
                else
                    missed++
            }
            b.append(", Hits: $hits")
            b.append(", Missed: $missed")
            b.append("\n")
            b.append("\n")
            b.append("\n")
        }

        return b.toString()
        // return ""
    }

    data class Delay(var uid: String?, var action: Long, var playerId: Int)

}