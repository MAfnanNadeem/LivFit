/*
 *  Created by Sumeet Kumar on 1/27/20 2:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 2:01 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

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
import life.mibo.hexa.pods.pod.PodType
import life.mibo.hexa.ui.main.MiboEvent
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

        fun getInstance(): RXLManager =
            INSTANCE ?: synchronized(this) {
                life.mibo.hardware.core.Logger.e("RXLManager INSTANCE init ")
                INSTANCE = RXLManager()
                INSTANCE!!
            }
    }


    var listener: Listener? = null
    var devices = ArrayList<Device>()
    var events = ArrayList<Event>()

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

    fun refresh() {
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
    var isRandom = false
    var exerciseType = 0
    private var test = false
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

    fun startNow() {
        //isRandom = random
        //register()
        isStarted = false
        startPublish()
        //startInternal()
    }


    fun startOnHit(random: Boolean) {

    }


    fun startTest(program: RxlProgram) {
        startPublish()
        log("startTest.......... duration ${program.getDuration()} cycle ${program.getCyclesCount()} action ${program.getAction()} pause ${program.getPause()}")
        test = true
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
        startObserver(currentCycle, program!!.getNext().cycleDuration)
    }

    private fun startPublish() {
        publisher = PublishSubject.create<RxlStatusEvent>()
        publisher!!.subscribeOn(Schedulers.io()).doOnNext {
            log("publisher RxlStatusEvent doOnNext ${it.uid}  == lastUid $lastUid")

            if (it.uid == lastUid) {
                if (!isInternalStarted) {
                    log("RxlStatusEvent2 starting........... >> $isInternalStarted ")
                    if (it.time > 10)
                        startInternal()
                    else
                        return@doOnNext
                }

                log("RxlStatusEvent UID Matched ${it.uid} == $lastUid ")
                events.add(Event(events.size + 1, getAction(), it.time))
                lightOnDynamic()
            } else {
                log("RxlStatusEvent UID NOT Matched >> ${it.uid} == $lastUid ")
            }
        }.subscribe()

        if (isInternalStarted)
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

    // Private Functions

    private fun testObserver(action: Int) {
        Single.just(action).delay(action.toLong(), TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .doOnSuccess {
                onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0), lastUid))
            }.subscribe()
    }

    private fun startInternal() {
        isInternalStarted = true
        log("startInternal..........")
        log(
            "startInternal.......... duration ${program?.getDuration()} cycle ${program?.getCyclesCount()} " +
                    "action ${program?.getAction()} pause ${program?.getPause()}"
        )
        if (isRunning) {
            //Toasty.info(MiboApplication.context, "")
            log("exercise is already running")
            disposable?.dispose()
            return
        }
        if (observers == null || observers?.isDisposed == true)
            observers = CompositeDisposable()
        test = false
        startExercise(0, 0)
        // isRandom = exercise?.logic == LightLogic.RANDOM
        cycles = program!!.getCyclesCount().toLong()
        actionTime = program?.getAction()?.times(1000) ?: 0
        pauseTime = program!!.getPause()
        currentCycle = 1
        events.clear()
        startObserver(currentCycle, program!!.getDuration())
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
        if (test) {
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
        // EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle started"))
        isStarted = true
        lastPod = 0
        lightOnDynamic()
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
        publisher?.onComplete()
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
    private fun lightOnDynamic() {
        log("lightOnDynamic sequence $lastPod $isStarted")
        if (devices.size > 0 && isStarted) {
            try {
                if (isRandom) {
                    lightOnRandom()
                    return
                } else
                    lightOnSequence()

                isException = false
                return
            } catch (e: Exception) {
                log("lightOnDynamic Error " + e.message)
                if (!isException)
                    lightOnDynamic()
                e.printStackTrace()
                isException = true
            }
        }
        log("lights signals (random=$isRandom) sent to $lastPod")
    }

    private var nextPod = 0
    private var lastPod = 0
    private var lastUid = ""
    @Synchronized
    private fun lightOnSequence() {
        if (lastPod >= devices.size)
            lastPod = 0
        sendColorEvent(devices[lastPod])

        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))

        lastPod++
    }

    var random: Random? = null
    private fun nextRandom(): Int {
        return Random.nextInt(devices.size)
    }

    @Synchronized
    private fun lightOnRandom() {
        val id = nextRandom()
        log("nextRandom  $lastPod  == $id")

        lastPod = if (id == lastPod)
            nextRandom()
        else
            id

        if (lastPod >= devices.size)
            lastPod = nextRandom()

        sendColorEvent(devices[lastPod])

        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        //log("lights random sent $lastPod  == $id")
        //lastPod++
    }

    private fun sendColorEvent(device: Device?) {
        //publisher.onNext("sendColorEvent ${device?.uid}")
        delayObserver()
        if (test) {
            log("sendColorEvent test >> sent $lastPod")
            lastUid = "000002"
            testObserver(getAction())
            return
        }
        device?.let {
            it.colorPalet = getColor()
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
                EventBus.getDefault()
                    .postSticky(
                        ChangeColorEvent(
                            it,
                            it.uid,
                            getAction().times(1000)
                        )
                    )
            }
            lastPod++
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

    @Subscribe
    fun onResponse(event: PodResponseEvent) {
        events.add(Event(devices.size.plus(1), getAction(), event.tapTime))
    }

    @Subscribe
    fun onEvent(event: RxlStatusEvent) {
        EventBus.getDefault().removeStickyEvent(event)
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
        //return String(b)
        return b.toString()
        // return ""
    }


    fun test(s: String) {
        log("test $s")
    }
}