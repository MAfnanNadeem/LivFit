/*
 *  Created by Sumeet Kumar on 1/27/20 2:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 2:01 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

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
import life.mibo.hexa.pods.pod.Pod
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
    var list = ArrayList<Pod?>()
    var devices = ArrayList<Device>()
    var events = ArrayList<Event>()

    var is4 = false
    var is6 = false


    private var lastActivePod = -1
    var exercise: PodExercise? = null
    var type: PodType = PodType.UNKNOWN

    fun add(pod: Pod) {
        if (!list.contains(pod))
            list.add(pod)
    }

    fun addAll(pod: Array<Pod>) {
        list.clear()
        list.addAll(pod)
    }

    fun addAll(pod: List<Pod>) {
        list.clear()
        list.addAll(pod)
    }

    fun addPods(pod: List<Pod>): RXLManager {
        list.clear()
        list.addAll(pod)
        return this
    }


    fun addDevices(pod: List<Device>): RXLManager {
        devices.clear()
        devices.addAll(pod)

        if (pod.isNotEmpty()) {
            list.clear()

            pod.forEach {
                list.add(Pod.from(it))
            }
        }
        return this
    }

    fun with(exercise: PodExercise): RXLManager {
        this.exercise = exercise
        refresh()
        return this
    }

    fun withListener(listener: Listener): RXLManager {
        this.listener = listener
        return this
    }

    fun refresh() {
        list.clear()
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
    val publisher = PublishSubject.create<Int>()
    private var observers: CompositeDisposable? = null
    private var disposable: Disposable? = null
    private var delayDisposable: Disposable? = null
    var isRandom = false
    var exerciseType = 0
    private var test = false
    private var cycles = 0L
    private var currentCycle = 1
    private var isStarted = false
    private var isRunning = false

    fun start(random: Boolean) {
        isRandom = random
        //register()
        isStarted = false
        startInternal()
//        source.subscribeOn(Schedulers.newThread()).subscribe {
//
//        }
    }

    fun startTest(duration: Int, cycle: Int, action: Int, pause: Int, random: Boolean = false) {
        log("startTest.......... duration $duration cycle $cycle action $action pause $pause")
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
        isRandom = random
        if (exercise == null)
            exercise = PodExercise("$duration - $cycle")
        exercise?.duration!!.duration = duration
        exercise?.duration!!.actionTime = action
        exercise?.duration!!.cycles.value = cycle
        exercise?.duration!!.cycles.pause.value = pause

        cycles = cycle.toLong()
        currentCycle = 1
        startObserver2(currentCycle, exercise?.duration!!.duration)
    }

    private fun testObserver(action: Int) {
        Single.just(action).delay(action.toLong(), TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .doOnSuccess {
                onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0), lastUid))
            }.subscribe()
    }

    private fun startInternal() {
        log("startInternal..........")
        log(
            "startInternal.......... duration ${exercise?.duration?.duration} cycle ${exercise?.duration?.cycles?.value} " +
                    "action ${exercise?.duration?.actionTime} pause ${exercise?.duration?.cycles?.pause?.value}"
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
        cycles = exercise?.duration!!.cycles.value.toLong()
        currentCycle = 1
        startObserver2(currentCycle, exercise?.duration!!.duration)
    }

    private fun startObserver(cycle: Int, duration: Int) {
        log("startObserver >> cycle $cycle - duration $duration")
        disposable?.dispose()
        observers?.add(
            Observable.just(cycle).delay(duration.toLong(), TimeUnit.SECONDS).observeOn(
                Schedulers.computation()
            ).doOnComplete {
                log("startObserver >> doOnComplete cycle $cycle - duration $duration")
                completeCycle(currentCycle, duration)
            }.doOnSubscribe {
                log("startObserver >> doOnSubscribe cycle $cycle - duration $duration")
                startCycle(currentCycle)
            }.doOnError {
                log("startObserver >> doOnError cycle $cycle ${it.message}")
                it.printStackTrace()
            }.subscribe()
        )
//        disposable =
//            Observable.just(cycle).delay(duration.toLong(), TimeUnit.SECONDS).observeOn(Schedulers.computation()).doOnComplete {
//                log("startObserver >> doOnComplete cycle $cycle - duration $duration")
//                completeCycle(currentCycle, duration)
//            }.doOnSubscribe {
//                log("startObserver >> doOnSubscribe cycle $cycle - duration $duration")
//                startCycle(currentCycle)
//                observers?.add(it)
//            }.doOnError {
//                log("startObserver >> doOnError cycle $cycle ${it.message}")
//                it.printStackTrace()
//            }.subscribe()

//        disposable?.let {
//            observers?.add(it)
//        }
        //observers.add(disposable)
    }

    private fun startObserver2(cycle: Int, duration: Int) {
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
                startObserver2(cycle, duration)
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

        delayDisposable = Single.timer(exercise?.duration!!.actionTime.toLong().plus(1), TimeUnit.SECONDS)
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
        log("startExercise exercise $exercise")
        if (test) {
            startExercise(0, 0)
            currentCycle = 1
            startObserver2(currentCycle, exercise?.duration!!.duration)
            return
        }
        //RXLManager.getInstance().with(PodExercise.getExercise1()).addPods(list).startExercise()
        disposable?.dispose()
        if (exercise?.duration!!.duration > 0) {
            cycles = exercise?.duration!!.cycles.value.minus(1).toLong()
            disposable =
                Observable.interval(exercise?.duration!!.duration.toLong(), TimeUnit.SECONDS)
                    .concatMap { s ->
                        log("Observable concatMap $s  -- " + exercise?.duration!!.cycles.pause.value)
                        pauseCycle(currentCycle)
                        Observable.just(s)
                            .delay(
                                exercise?.duration!!.cycles.pause.value.toLong(),
                                TimeUnit.SECONDS
                            )
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


    fun startCycle(cycle: Int, time: Int = 0) {
        log(".......... startCycle .......... " + getTime())
        listener?.onCycleStart(cycle, time)
        // EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle started"))
        isStarted = true
        lastPod = 0
        lightOnDynamic()
    }

    fun pauseCycle(cycle: Int, time: Int = 0) {
        log(".......... pauseCycle .......... " + getTime())
        listener?.onCyclePaused(cycle, time)
        //EventBus.getDefault().post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle paused"))
        isStarted = false
    }

    fun resumeCycle(cycle: Int, time: Int = 0) {
        log(".......... resumeCycle .......... ")
        listener?.onCycleResumed(cycle)
        EventBus.getDefault()
            .post(NotifyEvent(REFLEX, getTime() + " Cycle $cycle resumed"))
        //startCycle(cycle)
        //isStarted = true
    }

    fun completeCycle(cycle: Int, duration: Int = 0) {
        log(".......... completeCycle .......... " + getTime())
        isStarted = false
        listener?.onCycleEnd(cycle)
        delayDisposable?.dispose()
        disposable?.dispose()
        if (cycles > currentCycle) {
            currentCycle++
            pauseCycle(0, exercise?.duration!!.cycles.pause.value)
            resumeObserver(currentCycle, exercise?.duration!!.cycles.pause.value, duration)
        } else {
            completeExercise(0, 0)
        }
        isStarted = false


    }

    fun startExercise(cycle: Int, time: Int = 0) {
        listener?.onExerciseStart()
        log(".......... startExercise .......... ")
        isRunning = true
    }

    fun completeExercise(cycle: Int, time: Int = 0) {
        log(".......... completeExercise .......... ")
        isRunning = false
        listener?.onExerciseEnd()
        EventBus.getDefault()
            .post(NotifyEvent(REFLEX.plus(1), getTime() + " Completed...."))
        dispose()
    }

    // todo startObserver - end

    fun test1() {
        log("test1 subscribe...")
        val d = Observable.intervalRange(
            1L,
            cycles,
            exercise?.duration!!.cycles.pause.value.toLong(),
            exercise?.duration!!.duration.toLong(),
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


//        Observable.zip<Int, Long, Int>(Observable.range(1, 5)
//            .groupBy { n -> n % 5 }
//            .flatMap { g -> g.toList() },
//            Observable.interval(50, TimeUnit.MILLISECONDS),
//            { obs, timer -> obs })
//            .doOnNext { item ->
//                System.out.println(System.currentTimeMillis() - timeNow)
//                println(item)
//                println(" ")
//            }.toList().toBlocking().first()
    }


    //var duration = 0

    fun startPods(interval: Long, count: Long) {
        log("startPods type:$type $interval $count ")
        when (type) {
            PodType.PODS_4 -> {
                disposable = Observable.interval(interval, TimeUnit.SECONDS)
                    .doOnNext {
                        lightOff(lastActivePod)
                        lightOn(next())
                    }.takeUntil { i ->
                        i == count
                    }.doOnComplete {
                        disposable?.dispose()
                    }.subscribe()
            }
            PodType.PODS_6 -> {

            }
            PodType.DYNAMIC -> {
                disposable = Observable.interval(interval, TimeUnit.SECONDS)
                    .doOnNext {
                        lightOff(lastActivePod)
                        lightOn(next())
                    }.takeUntil { i ->
                        i == count
                    }.doOnComplete {
                        disposable?.dispose()
                    }.subscribe()
            }
            else -> {

            }
        }
    }

    fun stopPods() {
        disposable?.dispose()
        disposable = null
        log("stopPods")
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
                if (list.size > 0)
                    Random(list.size).nextInt()
                return -1
            }
            else -> {
                return -1
            }
        }
    }

    fun next(): Int {
        var id = random()
        if (lastActivePod == id)
            id = random();
        return id
    }

    private fun checkPod(id: Int): Boolean {
        if (id >= 0 && id < list.size)
            return true
        //lightOn(list[id])
        return false
    }

    var isException = false
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
        log("lights sequence sent $lastPod")
        lastPod++
    }

    var random: Random? = null
    private fun nextRandom(): Int {
        // if (random == null)
        //    random = Random(devices.size)
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
            lastPod = Random(devices.size).nextInt()

        sendColorEvent(devices[lastPod])

        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        log("lights random sent $lastPod  == $id")
        //lastPod++
    }

    private fun sendColorEvent(device: Device?) {
        delayObserver()
        if (test) {
            log("sendColorEvent test >> sent $lastPod")
            lastUid = "000002"
            testObserver(exercise?.duration!!.actionTime)
            return
        }
        device?.let {
            it.colorPalet = exercise?.colors!!.activeColor
            lastUid = it.uid
            EventBus.getDefault()
                .postSticky(
                    ChangeColorEvent(
                        it,
                        it.uid,
                        exercise?.duration!!.actionTime.times(1000)
                    )
                )
        }
    }

    fun sendColor(d2: Device?) {
        if (devices.size > 0) {
            if (lastPod >= devices.size)
                lastPod = 0
            val d = devices[lastPod]

            d?.let {
                it.colorPalet = exercise?.colors!!.activeColor
                EventBus.getDefault()
                    .postSticky(
                        ChangeColorEvent(
                            it,
                            it.uid,
                            exercise?.duration!!.actionTime.times(1000)
                        )
                    )
            }
            lastPod++
        }
    }

    private fun lightOn(pod: Int) {
        if (checkPod(pod))
            lightOn(list[pod])
        log("lightOn $pod")
    }

    fun lightOn(pod: Pod?) {
        if (pod == null)
            return
        EventBus.getDefault().postSticky(
            PodEvent(
                pod.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false
            )
        )
        log("lightOn EventBus $pod")
    }

    private fun lightOff(pod: Int) {
        if (checkPod(pod))
            lightOff(list[pod])
        log("lightOff $pod")
    }

    fun lightOff(pod: Pod?) {
        if (pod == null)
            return
        EventBus.getDefault().postSticky(
            PodEvent(
                pod.uid, 0, 0, false
            )
        )
        log("lightOff EventBus $pod")
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

    private fun dispose() {
        observers?.add(Single.fromCallable {
            log("disposing observers")
            observers?.dispose()
            disposable?.dispose()
            delayDisposable?.dispose()
        }.subscribeOn(Schedulers.computation()).subscribe())
    }

    fun log(msg: String?) {
        life.mibo.hardware.core.Logger.e("RXLManager $currentCycle - $msg")
    }

    @Subscribe
    fun onResponse(event: PodResponseEvent) {
        events.add(Event(list.size.plus(1), exercise!!.duration.actionTime, event.tapTime))
    }

    @Subscribe
    fun onEvent(event: RxlStatusEvent) {
        log("RxlStatusEvent $event")
        if (event.uid == lastUid) {
            log("RxlStatusEvent UID Matched ${event.uid} == $lastUid ")
            events.add(Event(list.size.plus(1), exercise!!.duration.actionTime, event.time))
            lightOnDynamic()
        } else {
            log("RxlStatusEvent UID NOT Matched >> ${event.uid} == $lastUid ")
        }
    }

    fun getHits() {
        val b = StringBuilder()
        b.append("Total events")
        b.append("\n")
        b.append(events.size)
        b.append("Total time")
        b.append("\n")
        b.append(events.size)
        b.append("You hits")
        b.append("\n")
        b.append(events.size)
    }


    fun test(s: String) {
        log("test $s")
    }
}