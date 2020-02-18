/*
 *  Created by Sumeet Kumar on 2/16/20 8:59 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/13/20 5:21 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl

import android.graphics.Color
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.PodEvent
import life.mibo.hardware.events.PodResponseEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceTypes
import life.mibo.hexa.events.NotifyEvent
import life.mibo.hexa.pods.Event
import life.mibo.hexa.pods.pod.PodType
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.random.Random


class RXLManager private constructor() {

    init {
        life.mibo.hardware.core.Logger.e("RXLManager init for first time")
    }

    interface Listener {
        fun onExerciseStart()
        fun onExerciseEnd()
        fun onCycleStart(cycle: Int, duration: Int)
        fun onCycleEnd(cycle: Int)
        fun onCyclePaused(cycle: Int, time: Int)
        fun onCycleResumed(cycle: Int)
        fun onPod(podId: Int, time: Int)
    }

    companion object {
        val REFLEX = 10
        @Volatile
        private var INSTANCE: RXLManager? = null

        @Volatile
        private var receivedFocusAll = false

        fun getInstance(): RXLManager =
            INSTANCE ?: synchronized(this) {
                life.mibo.hardware.core.Logger.e("RXLManager INSTANCE init ")
                INSTANCE =
                    RXLManager()
                INSTANCE!!
            }
    }


    var listener: Listener? = null
    var devices = ArrayList<Device>()
    private var devicesUids = ArrayList<DeviceEvent>()
    var events = ArrayList<Event>()
    var wrongEvents = ArrayList<Event>()
    private var colors = ArrayList<RxlColor>()

    private var lastActivePod = -1
    var program: RxlProgram? = null
    var type: PodType = PodType.UNKNOWN


    fun addDevices(pod: List<Device>): RXLManager {
        devices.clear()
        devices.addAll(pod)
        return this
    }

    fun with(exercise: RxlProgram): RXLManager {
        this.program = exercise
        refresh()
        return this
    }

    fun withListener(listener: Listener): RXLManager {
        this.listener = listener
        return this
    }

    private fun refresh() {
        devices.clear()
        events.clear()
    }

    fun getTime(): String {
        try {
            return SimpleDateFormat("mm:ss").format(Date())
        } catch (e: Exception) {

        }
        return "00:00"
    }


    // todo startObserver - start
    private var publisher: PublishSubject<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()
    private var observers: CompositeDisposable? = null
    private var disposable: Disposable? = null
    private var delayDisposable: Disposable? = null
    //var isRandom = false
    var lightLogic = 1 // 1= Sequence, 2 = Random, 3 = Focus, 4 = Focus at All
    //var lightLogic : RxlLight = RxlLight.SEQUENCE // todo enum may be costly for performance, using int
    var activeColor: Int = 0
    var colorPosition: Int = 0
    //var exerciseType = 0
    private var unitTest = false
    private var cycles = 0L
    private var currentCycle = 1
    private var actionTime = 0
    private var pauseTime = 0
    private var isStarted = false
    private var isInternalStarted = false
    private var isRunning = false
    //private var actionTime = 0

    private fun getDuration(): Int = program?.getDuration() ?: 0

    private fun getAction(): Int = program?.getAction() ?: 0
    private fun getCycles(): Int = program?.getCyclesCount() ?: 0

    private fun getPause(): Int = program?.getPause() ?: 0

    private fun getColor(): Int = program?.getActiveColor() ?: Color.RED

    private fun reset() {
        isStarted = false
        isInternalStarted = false
        isRunning = false
        colorSent = false
        isFocus = false
        receivedFocusAll = false
        publisher?.unsubscribeOn(Schedulers.io())
        publisher = null
    }

    fun startTest(program: RxlProgram) {
        createTapPublish()
        log("startTest.......... duration ${program.getDuration()} cycle ${program.getCyclesCount()} action ${program.getAction()} pause ${program.getPause()}")
        unitTest = true
        devices.clear()
        for (i in 1..6) {
            devices.add(Device("Name $i", "00000$i", "${i.times(123)}", DeviceTypes.RXL_WIFI))
        }

        if (observers == null || observers?.isDisposed == true) {
            log("------------------observer was disposed ----------------------sss")
            observers = CompositeDisposable()
        }

        startExercise(0, 0)
        this.program = program
        program.repeat(getCycles())

        cycles = getCycles().toLong()
        currentCycle = 1
        startObserver(currentCycle, program.getNext().cycleDuration)
    }

    fun startOnTap() {
        //isRandom = random
        //register()
        reset()
        createTapPublish()
        //startInternal()
    }

    fun startNow() {
        //isRandom = random
        //register()
        reset()
        createNowPublish()
        startInternal()
        //startInternal()
    }

    private fun createTapPublish() {
        publisher = PublishSubject.create<RxlStatusEvent>()
        publisher!!.subscribeOn(Schedulers.io()).doOnNext {
            logi("publisherTap RxlStatusEvent doOnNext ${it.uid}  == lastUid $lastUid  receivedFocusAll $receivedFocusAll isFocus $isFocus")
            if (isFocus) {
                nextFocusEvent(it)
                return@doOnNext
            }

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
                nextEvent(it.time)
            } else {
                log("RxlStatusEvent UID NOT Matched >> ${it.uid} == $lastUid ")
                return@doOnNext
            }
        }.subscribe()

        if (isInternalStarted)
            return
        if (isFocus)
            return
        log("RxlStatusEvent2 isStarted >> $isInternalStarted ")
        if (devices.isNotEmpty()) {
            val device = devices[0]
            lastUid = device.uid
            device.let {
                it.colorPalet = getColor()
                lastUid = it.uid
                EventBus.getDefault()
                    .postSticky(
                        ChangeColorEvent(
                            it,
                            it.uid,
                            10 * 10000
                        )
                    )
            }
            lastPod = 1
            log("RxlStatusEvent2 ChangeColorEvent2 send To UID == $lastUid ")
        }
    }

    private fun createNowPublish() {
        publisher = PublishSubject.create<RxlStatusEvent>()
        publisher!!.subscribeOn(Schedulers.io()).doOnNext {
            logi("publisherNow RxlStatusEvent doOnNext ${it.uid}  == lastUid $lastUid  receivedFocusAll $receivedFocusAll isFocus $isFocus")
            if (isFocus) {
                nextFocusEvent(it)
                return@doOnNext
            }

            if (it.uid == lastUid) {
                log("RxlStatusEvent UID Matched ${it.uid} == $lastUid ")
                nextEvent(it.time)
            } else {
                log("RxlStatusEvent UID NOT Matched >> ${it.uid} == $lastUid ")
            }
        }.subscribe()
    }

    private fun nextEvent(time: Int) {
        colorSent = false
        events.add(Event(events.size + 1, actionTime, time))
        nextLightEvent()
    }


    private fun nextFocusEvent(it: RxlStatusEvent) {
        logi("nextFocusEvent receivedFocusAll.............. $receivedFocusAll ${it.uid}")
        if (receivedFocusAll) {
            return
        }
        log("nextFocusEvent RxlStatusEvent >> ${it.uid} == $lastUid valid=$focusValid light=$lightLogic receivedFocusAll $receivedFocusAll")
        if (lightLogic == 4) {
            receivedFocusAll = true
            Observable.fromIterable(devices).subscribeOn(Schedulers.io()).doOnNext {
                //it.colorPalet = 0
                EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, 0))
                log("nextFocusEvent2 onChangeColorEvent Observable OFF = ${it.uid}")
            }.doOnComplete {
                //Thread.sleep(50)
                if (it.uid == lastUid) {
                    log("nextFocusEvent2 doOnComplete ID MATCHED  >> ${it.uid} == $lastUid ")
                    events.add(Event(events.size + 1, actionTime, it.time))
                } else {
                    wrongEvents.add(Event(wrongEvents.size + 1, actionTime, it.time))
                    log("nextFocusEvent2 doOnComplete NOT MATCH >> ${it.uid} == $lastUid ")
                }
                colorSent = false
                nextFocusLightEvent()
                //receivedFocusAll = false
            }.subscribe()

        } else {
            if (it.uid == lastUid) {
                colorSent = false
                if (focusValid)
                    events.add(Event(events.size + 1, actionTime, it.time))
                else {
                    wrongEvents.add(Event(wrongEvents.size + 1, actionTime, it.time))
                    log("nextFocusEvent wrong focus device tapped >> ${it.uid} == $lastUid ")
                }
                nextFocusLightEvent()
            } else {
                //wrongEvents.add(Event(events.size + 1, getAction(), it.time))
                log("nextFocusEvent UID NOT Matched >> ${it.uid} == $lastUid ")
            }
        }
    }

    private fun focusAllResponse(it: RxlStatusEvent) {
        receivedFocusAll = true
        log("set receivedFocusAll $receivedFocusAll")
        Observable.fromArray(devices).flatMapIterable { x -> x }.doOnNext {
            it.colorPalet = 0x00000000.toInt()
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, 0))
            log("Observable.fromArray OFF = ${it.uid} receivedFocusAll $receivedFocusAll")
        }.doOnComplete {
            Thread.sleep(100)
            if (it.uid == lastUid) {
                events.add(Event(events.size + 1, actionTime, it.time))
                log("nextFocusEvent2 match >> ${it.uid} == $lastUid ")
            } else {
                wrongEvents.add(Event(wrongEvents.size + 1, actionTime, it.time))
                log("nextFocusEvent2 no match >> ${it.uid} == $lastUid ")
            }
            nextFocusLightEvent()
        }.subscribe()
    }

    // Private Functions

    private fun testObserver(action: Int) {
        Single.just(action).delay(action.toLong(), TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .doOnSuccess {
                onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0), lastUid))
            }.subscribe()
    }

    private fun startInternal() {
        isInternalStarted = true
        colorSent = false
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
        activeColor = program!!.getActiveColor()
        colorPosition = program!!.getActivePosition()
        cycles = program!!.getCyclesCount().toLong()
        actionTime = program?.getAction()?.times(1000) ?: 0
        pauseTime = program!!.getPause()
        currentCycle = 1
        if (lightLogic == 3 || lightLogic == 4)
            setColors()
        events.clear()
        startExercise(0, 0)
        startObserver(currentCycle, program!!.getDuration())
    }

    private fun setColors() {
        val list = Utils.getColors()
        colors.clear()
        list.forEach {
            it.id?.let { c ->
                colors.add(RxlColor(c))
            }
        }
        isFocus = true

        devicesUids.clear()
        devices.forEach {
            devicesUids.add(DeviceEvent(it.uid, false))
        }

    }


    private fun startObserver(cycle: Int, duration: Int) {
        log("startObserver >> cycle $cycle - duration $duration")
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


    private fun delayObserver() {
        log("delayObserver >> ")
        delayDisposable?.dispose()
        delayDisposable = null
//        delayDisposable =
//            Observable.timer(exercise?.duration!!.actionTime.plus(1).toLong(), TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .doAfterNext {
//                    log("RxlStatusEvent delayObserver doAfterNext $it")
//                }.doOnComplete {
//                    onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0), lastUid))
//                    log("RxlStatusEvent delayObserver doOnComplete")
//                }.doOnDispose {
//                    log("RxlStatusEvent delayObserver doOnDispose")
//                }
//                .subscribe {
//                    log("RxlStatusEvent delayObserver subscribe")
//                }

        delayDisposable = Single.timer(getAction().toLong().plus(1), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io()).doOnSuccess {
                receivedFocusAll = false
                onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0), lastUid))
                log("RxlStatusEvent delayObserver doOnComplete")
            }.doOnDispose {
                log("RxlStatusEvent delayObserver doOnDispose")
            }.subscribe()
//        delayDisposable = Single.just("i")
//            .timeout(exercise?.duration!!.actionTime.toLong().plus(1), TimeUnit.SECONDS)
//            .delay(exercise?.duration!!.actionTime.toLong().plus(1), TimeUnit.SECONDS).doOnSuccess {
//                onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0), "000000"))
//                log("RxlStatusEvent delayObserver observed $it")
//            }.doOnError {
//                log("RxlStatusEvent delayObserver error")
//            }.doOnDispose {
//                log("RxlStatusEvent delayObserver disposed")
//            }.subscribe()
    }

    private fun startExercise() {
        log("startExercise exercise $program")
        if (unitTest) {
            startExercise(0, 0)
            currentCycle = 1
            startObserver(currentCycle, getDuration())
            return
        }
        //RXLManager.getInstance().with(PodExercise.getExercise1()).addPods(list).startExercise()
        disposable?.dispose()
        if (getDuration() > 0) {
            cycles = getCycles().toLong()
            disposable =
                Observable.interval(getDuration().toLong(), TimeUnit.SECONDS)
                    .concatMap { s ->
                        log("Observable concatMap $s  -- " + getPause())
                        pauseCycle(currentCycle)
                        Observable.just(s).delay(getPause().toLong(), TimeUnit.SECONDS)
                    }
                    .doOnNext {
                        log("Observable doOnNext $it")
                        completeCycle(currentCycle)
                        currentCycle++
                        //lightOff(lastActivePod)
                        // lightOn(next())
                    }.takeUntil { i ->
                        log("Observable takeUntil $i  == $cycles")
                        i == cycles
                    }.doOnComplete {
                        completeExercise(currentCycle)
                        disposable?.dispose()
                        log("Observable doOnComplete {cycle complete}")
                    }.doOnSubscribe {
                        log("Observable doOnSubscribe {cycle complete}")
                        startExercise(0, 0)
                    }.subscribe {

                        if (cycles > 1 && cycles < currentCycle) {
                            startCycle(currentCycle)
                            //pauseObserver(exercise?.duration!!.cycles.pause.value, currentCycle)
                        }
                        log("startExercise subscribe $it")
                        log("startExercise cycles $cycles")
                    }

            // test1()
        }
    }


    private fun startCycle(cycle: Int, time: Int = 0) {
        log(".......... startCycle .......... " + getTime())
        listener?.onCycleStart(cycle, time)
        colorSent = false
        // EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle started"))
        isStarted = true
        lastPod = 0
        nextLightEvent()
        //publisher.onNext("startCycle")
    }

    private fun pauseCycle(cycle: Int, time: Int = 0) {
        log(".......... pauseCycle .......... " + getTime())
        listener?.onCyclePaused(cycle, time)
        //EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle paused"))
        isStarted = false
        //publisher.onNext("pauseCycle")
    }

    private fun resumeCycle(cycle: Int, time: Int = 0) {
        log(".......... resumeCycle .......... ")
        listener?.onCycleResumed(cycle)
        EventBus.getDefault()
            .post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle resumed"))
        //startCycle(cycle)
        //isStarted = true
        //publisher.onNext("resumeCycle")
    }

    private fun completeCycle(cycle: Int, duration: Int = 0) {
        log(".......... completeCycle .......... " + getTime())
        isStarted = false
        listener?.onCycleEnd(cycle)
        delayDisposable?.dispose()
        disposable?.dispose()
        if (cycles > currentCycle) {
            currentCycle++
            pauseCycle(0, getPause())
            resumeObserver(currentCycle, getPause(), duration)
        } else {
            completeExercise(0, 0)
        }
        isStarted = false

        //publisher.onNext("completeCycle")

    }

    private fun startExercise(cycle: Int, time: Int = 0) {
        listener?.onExerciseStart()
        log(".......... startExercise .......... ")
        isRunning = true
        //publisher.onNext("startExercise")
    }

    private fun completeExercise(cycle: Int, time: Int = 0) {
        log(".......... completeExercise .......... ")
        isInternalStarted = false
        isRunning = false
        listener?.onExerciseEnd()
        EventBus.getDefault()
            .post(NotifyEvent(REFLEX.plus(1), getTime() + " Completed...."))
        dispose()

        try {
            publisher?.onComplete()
            publisher?.unsubscribeOn(Schedulers.io())
            publisher = null
        } catch (e: Exception) {
        }
        //publisher.onNext("completeExercise")
    }

    // todo startObserver - end

    fun test1() {
        log("test1 subscribe...")
        val d = Observable.intervalRange(
            1L,
            cycles,
            getPause().toLong(),
            getDuration().toLong(),
            TimeUnit.SECONDS
        ).subscribeOn(Schedulers.newThread()).doOnNext {
            log("test1 Observable doOnNext $it")
        }.doOnComplete {
            log("test1 Observable doOnComplete ")
        }.doOnSubscribe {
            log("test1 Observable doOnSubscribe $it")
        }.subscribe {
            log("test1 Observable subscribe $it")
        }
    }


    private fun random(): Int {
        return when (type) {
            PodType.PODS_4 -> {
                Random(4).nextInt()
            }
            PodType.PODS_6 -> {
                Random(6).nextInt()
            }

            PodType.DYNAMIC -> {
                if (devices.size > 0)
                    Random(getCycles()).nextInt()
                return -1
            }
            else -> {
                return -1
            }
        }
    }

    private fun next(): Int {
        var id = random()
        if (lastActivePod == id)
            id = random();
        return id
    }

    private fun checkPod(id: Int): Boolean {
        if (id >= 0 && id < devices.size)
            return true
        //lightOn(list[id])
        return false
    }

    private var isException = false
    private fun nextLightEvent() {
        log("nextLightEvent lightLogic $lightLogic  lastPod $lastPod isStarted $isStarted ")
        if (devices.size > 0 && isStarted) {
            try {
                when (lightLogic) {
                    1 -> {
                        lightOnSequence()
                    }
                    2 -> {
                        lightOnRandom()
                    }
                    3 -> {
                        lightOnFocus()
                    }
                    4 -> {
                        lightOnAllAtOnce()
                    }

                }
//                if (isRandom) {
//                    lightOnRandom()
//                    return
//                } else
//                    lightOnSequence()
                isException = false
                return
            } catch (e: Exception) {
                log("lightOnDynamic Error " + e.message)
                if (!isException)
                    nextLightEvent()
                e.printStackTrace()
                isException = true
            }
        }
    }

    private fun nextFocusLightEvent() {
        log("nextFocusLightEvent lightLogic $lightLogic  lastPod $lastPod isStarted $isStarted ")
        if (devices.size > 0 && isStarted) {
            try {
                when (lightLogic) {
                    3 -> {
                        lightOnFocus()
                    }
                    4 -> {
                        lightOnAllAtOnce()
                    }

                }
                isException = false
                return
            } catch (e: Exception) {
                log("lightOnDynamic Error " + e.message)
                if (!isException)
                    nextLightEvent()
                e.printStackTrace()
                isException = true
            }
        }
    }

    //private var nextPod = 0
    private var lastPod = 0
    private var lastUid = ""
    //private var random: Random? = null
    private fun nextRandom(): Int {
        return Random.nextInt(devices.size)
    }

    @Synchronized
    private fun lightOnSequence() {
        if (lastPod >= devices.size)
            lastPod = 0
        sendColorEvent(devices[lastPod], activeColor)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        lastPod++
    }

    @Synchronized
    private fun lightOnRandom() {
        val id = nextRandom()

        lastPod = if (id == lastPod)
            nextRandom()
        else
            id

        if (lastPod >= devices.size)
            lastPod = nextRandom()

        sendColorEvent(devices[lastPod], activeColor)
    }

    private var focusCount = 0
    private var focusValid = false
    private var isFocus = false

    private fun isNextFocus(): Boolean {
        //val id = nextRandom()
        //val id = nextRandom()
        //return id == 1
        //return Random.nextInt(50) % 2 == 0
        return Random.nextInt(50) % 3 == 0
    }

    @Synchronized
    private fun lightOnFocus() {
        log("lightOnFocus")
        if (isNextFocus()) {
            focusValid = true
            sendToFocusLight()
        } else {
            focusValid = false
            sendToNonFocusLight()
        }
    }

    private fun nextRandomColor(): Int {
        var i = Random.nextInt(colors.size)
        //var c = colors[i].activeColor
        log("nextRandomColor i $i , colorPosition $colorPosition")
        if (i == colorPosition && i < colors.size - 1)
            i++

        return colors[i].activeColor
//        log("nextRandomColor activeColor $activeColor newColor $c , i $i")
//        if (c == activeColor) {
//            log("nextRandomColor color matched")
//            c = if (i > 0)
//                colors[i.minus(1)].activeColor
//            else colors[i.plus(1)].activeColor
//        }
//        return c
    }


    private fun sendToFocusLight() {
        val id = nextRandom()

        lastPod = if (id == lastPod)
            nextRandom()
        else
            id

        if (lastPod >= devices.size)
            lastPod = nextRandom()

        sendColorEvent(devices[lastPod], activeColor)
    }

    private fun sendToNonFocusLight() {
        val id = nextRandom()

        lastPod = if (id == lastPod)
            nextRandom()
        else
            id

        if (lastPod >= devices.size)
            lastPod = nextRandom()

        sendColorEvent(devices[lastPod], nextRandomColor())
    }

    @Synchronized
    private fun lightOnAllAtOnce() {
        log("lightOnAllAtOnce")
        if (colorSent) {
            log("lightOnAllAtOnce color is already sent.......")
            return
        }
        val id = nextRandom()

        lastPod = if (id == lastPod)
            nextRandom()
        else
            id

        if (lastPod >= devices.size)
            lastPod = nextRandom()

        val uid = devices[lastPod].uid
        //sendColorEvent(devices[lastPod], activeColor)
//        Observable.fromIterable(devices).zipWith(
//            Observable.interval(50, TimeUnit.MILLISECONDS),
//            BiFunction<Device, Long, Device> { item, t ->
//                log("Observable.fromIterable BiFunction ${item.uid} $t")
//                item
//
//            }).doOnNext {
//            log("Observable.fromIterable doOnNext ")
//            if (it.uid == uid)
//                sendColorEvent(it, activeColor)
//            else {
//                it.colorPalet = nextRandomColor()
//                EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, actionTime))
//            }
//        }.doOnComplete {
//
//        }.subscribe()
        receivedFocusAll = false
        Observable.fromIterable(devices).subscribeOn(Schedulers.io()).doOnNext {
            if (it.uid == uid) {
                delayObserver()
                it.colorPalet = activeColor
                lastUid = it.uid
                //sendColorEvent(it, activeColor)
            } else {
                it.colorPalet = nextRandomColor()
            }
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, actionTime))
            log("nextFocusEvent onChangeColorEvent Observable ON = ${it.uid}")
        }.doOnComplete {

        }.subscribe()

//        Observable.just("", "", "", "")
//            .zipWith(
//                Observable.interval(
//                    500,
//                    TimeUnit.MILLISECONDS
//                ),
//                BiFunction<String, Long, String> { item: String?, interval: Long? -> item }
//            )
//            .subscribe { x: String? -> println(x) }.dispose()
    }

//    fun <T> Observable<T>.delayEach(interval: Long, timeUnit: TimeUnit): Observable<T> =
//        Observable.zip(
//            this,
//            Observable.interval(interval, timeUnit),
//            BiFunction { item, _ -> item }
//        )

    private fun clearDevicesColor() {
        Observable.fromArray(devices).flatMapIterable { x -> x }.doOnNext {
            it.colorPalet = 0x000000
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, 0))
        }.doOnComplete {

        }.subscribe()
    }

    private var colorSent = false

    private fun sendColorEvent(device: Device?, color: Int) {
        if (colorSent) {
            log("sendColorEvent color is already sent.......")
            return
        }
        colorSent = true
        //publisher.onNext("sendColorEvent ${device?.uid}")
        delayObserver()
        if (unitTest) {
            log("sendColorEvent test >> sent $lastPod")
            lastUid = "000002"
            testObserver(getAction())
            return
        }
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

    fun sendColor(d: Device?, color: Int) {
        d?.let {
            it.colorPalet = color
            EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, actionTime))
        }
        //lastPod++
    }


    private fun lightOn(pod: Int) {
        if (checkPod(pod))
            lightOn(devices[pod])
        log("lightOn $pod")
    }

    private fun lightOn(pod: Device?) {
        if (pod == null)
            return
        EventBus.getDefault().postSticky(
            PodEvent(
                pod.uid, getColor(), getAction(), false
            )
        )
        log("lightOn EventBus $pod")
    }

    private fun lightOff(pod: Int) {
        if (checkPod(pod))
            lightOff(devices[pod])
        log("lightOff $pod")
    }

    private fun lightOff(pod: Device?) {
        if (pod == null)
            return
        EventBus.getDefault().postSticky(
            PodEvent(
                pod.uid, 0, 0, false
            )
        )
        log("lightOff EventBus $pod")
    }

    private fun dispose() {
        observers?.add(Single.fromCallable {
            log("disposing observers")
            observers?.dispose()
            disposable?.dispose()
            delayDisposable?.dispose()
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
        delayDisposable?.dispose()
        delayDisposable = null
        disposable = null
        listener = null
        log("unregister")
    }


    private fun log(msg: String?) {
        life.mibo.hardware.core.Logger.e("RXLManager $currentCycle - $msg")
    }

    private fun logi(msg: String?) {
        life.mibo.hardware.core.Logger.i("RXLManager $currentCycle - $msg")
    }

    @Subscribe
    fun onResponse(event: PodResponseEvent) {
        events.add(
            Event(
                devices.size.plus(1),
                getAction(),
                event.tapTime
            )
        )
    }

    @Subscribe
    fun onEvent(event: RxlStatusEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        if (receivedFocusAll) {
            log("nextFocusEvent receivedFocusAll.............. ")
            return
        }
        publisher?.onNext(event)
    }

    fun getHits(): String {
        val b = StringBuilder()
        b.append("\n")
        b.append("\n")
        b.append("Total events   ")
        b.append(events.size)
        b.append("\n")
        b.append("\n")
        b.append("Total time   ")
        b.append(getDuration().times(cycles))
        b.append(" sec")
        b.append("\n")
        b.append("\n")
        b.append("Your hits   ")
        var hits = 0
        var missed = 0
        events?.forEach {
            if (it.tapTime > 1)
                hits++
            else
                missed++
        }
        b.append(hits)
        b.append("\n")
        b.append("\n")
        b.append("You missed   ")
        b.append(missed)
        if (isFocus) {
            b.append("\n")
            b.append("\n")
            b.append("Total Attempts ")
            b.append(wrongEvents.size)
            b.append(" + ")
            b.append(events.size)
        }
        //return String(b)
        return b.toString()
        // return ""
    }


    fun test(s: String) {
        log("test $s")
    }

    private data class DeviceEvent(
        var uid: String,
        var recieved: Boolean = false,
        var data: Any? = null
    )
}