/*
 *  Created by Sumeet Kumar on 3/5/20 10:40 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/5/20 10:23 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl

import android.util.SparseArray
import android.util.SparseBooleanArray
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
import life.mibo.hexa.pods.rxl.parser.*
import life.mibo.hexa.pods.rxl.program.RxlPlayer
import life.mibo.hexa.pods.rxl.program.RxlProgram
import life.mibo.hexa.ui.main.MiboEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class RXLHelper private constructor() {

    init {
        life.mibo.hardware.core.Logger.e("RXLManager init for first time")
    }

    private var childListener = object :
        RxlParser.Listener {

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
            log("Parser.Listener : sendColorEvent player $playerId action $action")
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
        const val TAP_CODE = 200
        const val TAP_TIME = 10 * 1000
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

    private var parrentListener: RxlListener? = null

    private var publisher: PublishProcessor<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()
    private var observers: CompositeDisposable? = null
    private var delayObservers: DisposableArray? = null
    private var disposable: Disposable? = null
    // todo startObserver - start
    // private var publisher: PublishSubject<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()

    private var program: RxlProgram? = null
    private var programParser: RxlParser? = null

    // private var devicesUids = ArrayList<DeviceEvent>()
    //var events = ArrayList<Event>()
    //var wrongEvents = ArrayList<Event>()
    //private var isMultiPlayer = false
    // private var lastActivePod = -1

    private var type: PodType = PodType.UNKNOWN

    //private var colorDisposable: Disposable? = null
    private var colorDisposable = SparseArray<Disposable?>()

    //var isRandom = false
    private var lightLogic = 1 // 1= Sequence, 2 = Random, 3 = Focus, 4 = Focus at All

    //var lightLogic : RxlLight = RxlLight.SEQUENCE // todo enum may be costly for performance, using int
    private var unitTest = false
    private var isStarted = false
    private var isInternalStarted = false
    private var isRunning = false

    ///private var lastUid = ""
    //private var actionTime = 0

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


    fun withListener(listener: RxlListener): RXLHelper {
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
        programParser = when (program?.lightLogic()) {
            1 -> {
                SequenceParser(
                    program!!,
                    childListener
                )
            }
            2 -> {
                RandomParser(
                    program!!,
                    childListener
                )
            }
            3 -> {
                FocusParser(
                    program!!,
                    childListener
                )
            }
            4 -> {
                AllAtOnceParser(
                    program!!,
                    childListener
                )
            }
            else -> {
                SequenceParser(
                    program!!,
                    childListener
                )
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
        startNowInternal()
        //startInternal()
    }

    private fun createTapPublish() {
        publisher = PublishProcessor.create<RxlStatusEvent>()
        //publisher!!.toFlowable(BackpressureStrategy.BUFFER)
        //publisher!!.onBackpressureBuffer
        //publisher?.firstElement()
        publisher!!.onBackpressureBuffer().subscribeOn(Schedulers.io()).doOnNext {
            logi("publisherTap RxlStatusEvent doOnNext ${it.uid}  data ${it.data} ")
            if (it.data > TAP_CODE) {
                log("RxlStatusEvent2 it.data == TAP_CODE........... >> $isInternalStarted ")
                checkAndStartOnTap(it)
                return@doOnNext
            }
            if (!isStarted)
                return@doOnNext
            programParser!!.onEvent(it)
        }.doOnError {
            log("ERROR............ $it")
        }.subscribe()

        if (isInternalStarted)
            return
        programParser?.createPlayers();

        tapPlayerCount = programParser!!.players.size
        log("RxlStatusEvent2 isStarted >> $isStarted : $isInternalStarted $tapPlayerCount")
        programParser!!.players?.forEach { player ->
            log("RxlStatusEvent2 send tap to player $player")
            if (player.pods.size > 0) {
                val d: Device? = player.pods[0]
                log("RxlStatusEvent2 send tap to Device $d")
                d?.let {
                    it.colorPalet = player.color
                    player.lastUid = it.uid
                    EventBus.getDefault()
                        .postSticky(
                            ChangeColorEvent(it, it.uid,
                                TAP_TIME, TAP_CODE.plus(player.id))
                        )
                }
            }
            Thread.sleep(15)
        }
        log("RxlStatusEvent onTapColorSent ")
        parrentListener?.onTapColorSent(0)
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
            programParser!!.onEvent(it)
        }.doOnError {
            log("ERROR............ $it")
        }.subscribe()
    }

    // Private Functions
    var tapArray = SparseBooleanArray()
    var tapPlayerCount = 0
    var allPlayerTap = false


    private fun allPlayersTapped(): Boolean {
        return allPlayerTap
    }

    private fun checkAndStartOnTap(event: RxlStatusEvent) {
        if (event.time > 10) {
            program?.players?.forEach {
                if (event.data == TAP_CODE.plus(it.id))
                    startTapInternal(it)
            }
        }
    }

    private fun checkAndStartOnTap2(event: RxlStatusEvent) {
        if (event.time > 10) {
            when (event.data) {
                TAP_CODE.plus(1) -> {
                    program?.players?.let {
                        if (it.size > 0) {
                            //val p = it[0]
                            //if (p.lastUid == event.uid)
                            startTapInternal(it[0])
                        }
                    }
                }
                TAP_CODE.plus(2) -> {
                    program?.players?.let {
                        if (it.size > 1)
                            startTapInternal(it[1])
                    }
                }
                TAP_CODE.plus(3) -> {
                    program?.players?.let {
                        if (it.size > 2)
                            startTapInternal(it[2])
                    }
                }
                TAP_CODE.plus(4) -> {
                    program?.players?.let {
                        if (it.size > 3)
                            startTapInternal(it[3])
                    }
                }
            }
        }

    }


    private fun startTapInternal(player: RxlPlayer?) {
        log("startTapInternal.......... $player")
        if (player != null) {
            isInternalStarted = true
            //colorSent = false

            log(
                "startInternal.......... duration ${program?.getDuration()} cycle ${program?.getCyclesCount()} " +
                        "action ${program?.getAction()} pause ${program?.getPause()}"
            )

            if (observers == null || observers?.isDisposed == true)
                observers = CompositeDisposable()
            unitTest = false
            //isRandom = program!!.isRandom()
            //lightLogic = program!!.lightLogic()

            parrentListener?.onExerciseStart()
            logi(".......... startExercise for player ${player.id} .......... ")
            isRunning = true
            programParser?.startTapProgram(player);
            tapArray.put(player.id, true)
        }

    }


    private fun startNowInternal() {
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

//        if (lightLogic > 2) {
//            //isFocus = true
//            setColors()
//        }
        //events.clear()
        // baseParser?.startExercise(0, 0)
        //baseParser?.startObserver(1)
        startExercise(0, 0)
        //startObserver(1, 0)
        //log("startInternal >>> actionTime $actionTime : duration ${getDuration()} : cycles $cycles : pauseTime $pauseTime lightLogic $lightLogic")
    }

    // for focus and all at once only
//    val colors = ArrayList<RxlColor>()
//
//    private fun setColors() {
//        val list = Utils.getColors()
//        colors.clear()
//        list.forEach {
//            it.id?.let { c ->
//                colors.add(RxlColor(c))
//            }
//        }
//
////        devicesUids.clear()
////        devices.forEach {
////            devicesUids.add(DeviceEvent(it.uid, false))
////        }
//
//    }

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

        programParser?.onCycleStart()
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
        programParser?.completeCycle()
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
        programParser?.startProgram()
        //publisher.onNext("startExercise")
    }

    private fun completeExercise(cycle: Int, time: Int = 0) {
        logi(".......... completeExercise .......... ")
        isInternalStarted = false
        isRunning = false
        parrentListener?.onExerciseEnd()
        //EventBus.getDefault().post(NotifyEvent(REFLEX.plus(1), getTime() + " Completed...."))
        dispose()
        //programParser?.dispose()
        turnOffAndRelease(program?.players)
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


    fun sendColor(d: Device?, color: Int, action: Int, playerId: Int, observe: Boolean = false) {

        if (observe)
            delayObserver(
                Delay(
                    d?.uid,
                    action.plus(700).toLong(),
                    playerId
                )
            )
        d?.let {
            it.colorPalet = color
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, action, playerId))
        }
        //lastPod++
    }

    fun turnOffAndRelease(players: ArrayList<RxlPlayer>?) {
        try {
            Single.fromCallable {
                if (!players.isNullOrEmpty()) {
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
        programParser?.players?.forEach { it ->
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