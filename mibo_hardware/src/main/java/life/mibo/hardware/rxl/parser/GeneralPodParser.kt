/*
 *  Created by Sumeet Kumar on 3/2/20 11:59 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/25/20 8:45 AM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.parser

import android.util.SparseArray
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.MIBO
import life.mibo.hardware.core.Utils
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.DelayColorEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hardware.rxl.Delay
import life.mibo.hardware.rxl.Event
import life.mibo.hardware.rxl.RXLManager
import life.mibo.hardware.rxl.program.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GeneralPodParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "GeneralPodParser") {

    private var isResuming = false;
    private var isProgramRunning = false;
    private var isCircuitMode = false;

    private var colors = ArrayList<RxlColor>()

    // Circuit
    private var workoutCurrentIndex = -1
    private var workoutsSize = -1
    private var workoutDuration = -1
    private var workoutPause = -1
    private var rxlWorkout: RxlCycle? = null
    private var circuits = ArrayList<RxlCycle>()
    private var workoutkSecLeft = 0L

    // Block
    private var currentBlock = -1
    private var currentRxlBlock: RxlCycle? = null
    private var currentRound = -1
    private var totalRounds = -1
    private var blockSecLeft = 0L

    //private var duration = -1
    private var blockAction = 1000
    private var blockPause = 0
    private var blockActionDelay = -1
    private var blockSize = -1
    //private var blockLogic = 1

    //private var tilesSize = -1
    private var speed: Float = 1.0f
    private var blocks = ArrayList<RxlCycle>()
    private var isBlockRunning = false
    private var isWorkoutRunning = false

    private var blockDisposable: Disposable? = null
    private var delayDisposable: Disposable? = null
    private var programDisposable: Disposable? = null
    private var programDelayDisposable: Disposable? = null

    private var pauseBlockTimeLeft = 0
    private var pattern = ""
    private var colorSent = false
    private var lastSeq = -1


    // Override Methods
    override fun stop() {
        super.stop()
        onProgramEndMulti()
    }


    override fun onProgramStart() {
        super.onProgramStart()
        log("child onProgramStart")
    }

    override fun onResumeCycle() {
        super.onResumeCycle()
        log("onResumeCycle....")
        isResuming = true
        onProgramStartInternal(speed, null)
        var timer = 0
        //isResuming = false
    }

    override fun onCycleStart(player: RxlPlayer) {
        log("child STARTING PROGRAM player...........${player.id}")
        isProgramRunning = true
        onProgramStartInternal(1.0f, player)
        //sendNextLight(player)
    }

    @Synchronized
    override fun onCycleStart() {
        log("child STARTING PROGRAM isTap $isTap")
        isProgramRunning = true
        onProgramStartInternal(1.0f, null)
    }

    // Receive tap event here
    @Synchronized
    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        log("onNextEvent isStarted $isStarted - $event")
        if (isStarted) {
            if (lightLogic == 5) {
                tapAtAllLight(player, event)
                log("onNextEvent tapAtAllLight return")
                return
            }
            if (player.lastUid == event.uid) {
                player.lastUid = ""
                // log("RxlStatusEvent UID Matched ${player.lastUid} == $event.uid ")
                if (blockActionDelay > MIN_DELAY) {
                    Single.timer(blockActionDelay.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                        sendNextLight(player)
                    }.doOnError {
                        sendNextLight(player)
                    }.subscribe()
                } else {
                    sendNextLight(player)
                }
                if (lightLogic == 3) {
                    if (player.lastFocusUid == event.uid)
                        player.events.add(Event(player.events.size + 1, blockAction, event.time))
                    else player.events.add(Event(player.events.size + 1, blockAction, 0))
                } else player.events.add(Event(player.events.size + 1, blockAction, event.time))
            }
        }

//        for (p in players) {
//            if (p.id == player.id) {
//                lightOnSequence(player)
//                break
//            }
//        }

    }


    override fun completeCycle() {
        log("completeCycle--------------------")
        super.completeCycle()
        isProgramRunning = false
    }

    private fun setColors() {
        val list = Utils.getColors()
        colors.clear()
        colors.addAll(list)
    }


    fun onProgramStartInternal(speed: Float, player: RxlPlayer?) {
        this.speed = speed
        log("${player?.id} onProgramStart")
        isCircuitMode = program.isCircuitMode
        if (isCircuitMode) {

            return
        }
        blocks.clear()
        blocks.addAll(program.getCycles())
        //tiles.clear()
        //tiles.addAll(island.tiles)
        // duration = island.getDuration()
        blockSize = blocks.size
        onStartProgramMultiInternal(player)
    }

    fun onProgramEndMulti() {
        log("onProgramEnd")
        release()
    }

    private fun onStartProgramMultiInternal(player: RxlPlayer?) {
        startNextBlock(player)
    }

    // Multi Workouts (Circuits)
//
//    private fun startWorkoutObservers(index: Int, duration: Long, player: RxlPlayer?) {
//        log("startBlockObservers --- block $currentBlock : round $currentRound : total $totalRounds : duration $duration - pauseBlockTimeLeft $pauseBlockTimeLeft")
//        programDisposable =
//            Observable.interval(0, 1, TimeUnit.SECONDS).take(duration)
//                .doOnNext {
//                    log("startBlockObservers doOnNext $it")
//                    workoutkSecLeft = it
//                }.doOnComplete {
//                    log("blockDisposable doOnSuccess")
//                    isWorkoutRunning = false
//                    onWorkoutEnd(index, player)
//                }.doOnError {
//                    isWorkoutRunning = false
//                    log("blockDisposable doOnError $it")
//                }.doOnSubscribe {
//                    log("blockDisposable doOnSubscribe $it")
//                    isWorkoutRunning = true
//                    onWorkoutStart(index, player)
//                }.subscribe()
//
//        isResuming = false
//    }
//
//
//    private fun startWorkoutDelayObservers(delay: Long) {
//        log("startWorkoutDelayObservers Block Delay Started ---------- $delay")
//        programDelayDisposable =
//            Single.timer(delay, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).doOnSuccess {
//                log("startWorkoutDelayObservers doOnSuccess")
//                onWorkoutDelayEnd()
//            }.doOnError {
//                log("startWorkoutDelayObservers doOnError $it")
//            }.doOnSubscribe {
//                log("startWorkoutDelayObservers doOnSubscribe $it")
//                onWorkoutDelayStart()
//                //blinkDelay(delay)
//            }.subscribe()
//    }
//
//    private fun onWorkoutStart(index: Int, player: RxlPlayer?) {
//        log("onBlockStart $index - $player")
//        listener.onBlockStart(currentBlock, currentRound)
//        isStarted = true
//        if (player != null) {
//            sendNextLight(player)
//            Thread.sleep(THREAD_SLEEP)
//        } else {
//            if (isTap) {
//                for (p in getPlayers()) {
//                    if (p.isTapReceived) {
//                        if (!p.isStarted) {
//                            log("child STARTING PROGRAM starting player $p")
//                            p.isStarted = true
//                            p.lastPod = 250
//                            sendNextLight(p)
//                        }
//                        Thread.sleep(THREAD_SLEEP)
//                    }
//                }
//            } else {
//                for (p in getPlayers()) {
//                    p.lastPod = 250
//                    sendNextLight(p)
//                    Thread.sleep(THREAD_SLEEP)
//                }
//            }
//
//        }
//    }
//
//    private fun onWorkoutEnd(index: Int, player: RxlPlayer?) {
//        log("onWorkoutEnd $index $player")
//        isStarted = false
//        listener.onBlockEnd(currentBlock, currentRound)
//        startNextDelay()
//        //currentBlock++
//        //currentRound++
//    }
//
//    private fun onWorkoutDelayStart() {
//        log("onWorkoutDelayStart")
//        listener.onBlockStart(currentBlock, -1)
//    }
//
//    private fun onWorkoutDelayEnd() {
//        log("onWorkoutDelayEnd")
//        if (isProgramRunning)
//            startNextWorkout(null)
//    }
//
//    private fun startNextWorkout(player: RxlPlayer?) {
//        log(" --------------------------- startNextBlock isResuming $isResuming ---------------------------")
//        if (isResuming) {
//            startBlockObservers(currentRxlBlock!!, player)
//            return
//        }
//        log("startNextBlock $currentBlock : $currentRound : $totalRounds")
//        if (currentRxlBlock == null || currentBlock == -1) {
//            currentBlock = 0
//            createWorkout()
//        } else {
//            if (currentRound < totalRounds) {
//                currentRound++
//
//            } else {
//                currentBlock++
//                if (currentBlock < blockSize) {
//                    createWorkout()
//                } else {
//                    currentBlock = 0
//                    createWorkout()
//                }
//            }
//        }
//        //if (currentRxlBlock != null)
//        startBlockObservers(currentRxlBlock!!, player)
//    }
//
//    fun createWorkout() {
//        blocks.clear()
//        blocks.addAll(program.getCycles())
//        //tiles.clear()
//        //tiles.addAll(island.tiles)
//        // duration = island.getDuration()
//        blockSize = blocks.size
//    }

    // Multi Blocks
    private fun onBlockStart(block: RxlCycle, player: RxlPlayer?) {
        log("onBlockStart $block - $player")
        listener.onBlockStart(currentBlock, currentRound)
        isStarted = true
        if (player != null) {
            sendNextLight(player)
            Thread.sleep(THREAD_SLEEP)
        } else {
            log("onBlockStart player is NULL isInternalStarted $isInternalStarted")
            if (isTap) {
                for (p in getPlayers()) {
                    log("onBlockStart player is NULL :: $p")
                    if (p.isTapReceived) {
                        if (p.isStarted) {
                            sendNextLight(p)
                        } else {
                            log("child STARTING PROGRAM starting player $p")
                            p.isStarted = true
                            p.lastPod = 250
                            sendNextLight(p)
                        }
                        Thread.sleep(THREAD_SLEEP)
                    }
                }
            } else {
                for (p in getPlayers()) {
                    p.lastPod = 250
                    sendNextLight(p)
                    Thread.sleep(THREAD_SLEEP)
                }
            }
        }
    }

    private fun onBlockEnd(block: RxlCycle) {
        log("onBlockEnd $block")
        isStarted = false
        listener.onBlockEnd(currentBlock, currentRound)
        startNextDelay()
        //currentBlock++
        //currentRound++
    }

    private fun onDelayStart() {
        log("onDelayStart")
        listener.onBlockStart(currentBlock, -1)
    }

    private fun onDelayEnd() {
        log("onDelayEnd")
        if (isProgramRunning)
            startNextBlock(null)
    }

    private fun startNextBlock(player: RxlPlayer?) {
        log(" --------------------------- startNextBlock isResuming $isResuming ---------------------------")
        if (isResuming) {
            startBlockObservers(currentRxlBlock!!, player)
            return
        }
        log("startNextBlock $currentBlock : $currentRound : $totalRounds")
        if (currentRxlBlock == null || currentBlock == -1) {
            currentBlock = 0
            createBlock()
        } else {
            if (currentRound < totalRounds) {
                currentRound++

            } else {
                currentBlock++
                if (currentBlock < blockSize) {
                    createBlock()
                } else {
                    currentBlock = 0
                    createBlock()
                }
            }
        }
        //if (currentRxlBlock != null)
        startBlockObservers(currentRxlBlock!!, player)
    }

    private fun createBlock() {
        if (isPaused) {
            // reused old variables
        }
        blockSecLeft = 0
        pauseBlockTimeLeft = 0
        lastSeq = 0
        currentRxlBlock = blocks[currentBlock]
        currentRound = 1
        totalRounds = currentRxlBlock!!.repeat
        lightLogic = getLogicType(currentRxlBlock?.lightType)
        blockActionDelay = currentRxlBlock!!.actionDelay
        blockAction = currentRxlBlock!!.cycleAction
        blockPause = currentRxlBlock!!.cyclePause
        //blockAction = 300
        pattern = currentRxlBlock!!.sequence ?: ""
        initBlock()
        log("createBlock created $currentRxlBlock :: blockLogic $lightLogic")
        //initRandomSeq()
    }

    fun initBlock() {
        if (!isTap) {
            for (p in program.players) {
                p.reset()
            }
        }
        if (lightLogic == 1) {
            for (p in program.players) {
                p.createSeq(pattern)
            }
        } else if (lightLogic == 3 || lightLogic == 4) {
            colorSent = false
            setColors()
        }
    }

    fun getLogicType(type: RxlLight?): Int {
        return when (type) {
            RxlLight.SEQUENCE -> 1
            RxlLight.RANDOM -> 2
            RxlLight.FOCUS -> 3
            RxlLight.ALL_AT_ONCE_TAP_ONE -> 4
            RxlLight.ALL_AT_ONCE_TAP_ALL -> 5
            RxlLight.TAP_AT_ALL -> 5
            else -> 1
        }
    }

    private fun startNextDelay() {
        if (blockPause > 0)
            startDelayObservers(blockPause.toLong())
        else
            onDelayEnd()
    }

    // Observers
    private fun startBlockObservers(block: RxlCycle, player: RxlPlayer?) {
        if (isBlockRunning && !isResuming) {
            log("----------- NOT HAPPEN------------- isBlockRunning && !isResuming")
            onBlockStart(block, player)
            return
        }
        var blockTime_ =
            if (isResuming) pauseBlockTimeLeft.toLong() else block.cycleDuration.toLong()
        if (blockTime_ < 0)
            blockTime_ = -blockTime_
        log("startBlockObservers --- block $currentBlock : round $currentRound : total $totalRounds : blockLogic  $lightLogic -- ${block.cycleDuration} -- blockTime_ $blockTime_ - pauseBlockTimeLeft $pauseBlockTimeLeft")
        blockDisposable =
            Observable.interval(0, 1, TimeUnit.SECONDS).take(blockTime_)
                .doOnNext {
                    log("startBlockObservers doOnNext $it")
                    blockSecLeft = it
                }.doOnComplete {
                    log("blockDisposable doOnSuccess")
                    isBlockRunning = false
                    onBlockEnd(block)
                }.doOnError {
                    isBlockRunning = false
                    log("blockDisposable doOnError $it")
                }.doOnSubscribe {
                    log("blockDisposable doOnSubscribe $it")
                    isBlockRunning = true
                    onBlockStart(block, player)
                }.subscribe()

        isResuming = false
//        blockDisposable = Single.timer(block.cycleDuration.toLong(), TimeUnit.SECONDS)
//            .subscribeOn(Schedulers.newThread()).doOnSuccess {
//                log("blockDisposable doOnSuccess")
//                isBlockRunning = false
//                onBlockEnd(block)
//            }.doAfterSuccess {
//                log("blockDisposable doAfterSuccess")
//            }.doOnError {
//                isBlockRunning = false
//                log("blockDisposable doOnError $it")
//            }.doOnSubscribe {
//                log("blockDisposable doOnSubscribe $it")
//                isBlockRunning = true
//                onBlockStart(block, player)
//            }.subscribe()
    }


    private fun startDelayObservers(delay: Long) {
        log("startDelayObservers Block Delay Started ---------- $delay")
        delayDisposable =
            Single.timer(delay, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).doOnSuccess {
                log("delayDisposable doOnSuccess")
                onDelayEnd()
            }.doOnError {
                log("delayDisposable doOnError $it")
            }.doOnSubscribe {
                log("delayDisposable doOnSubscribe $it")
                onDelayStart()
                //blinkDelay(delay)
            }.subscribe()
    }


    private fun release() {
        isBlockRunning = false
        isStarted = false
        blockDisposable?.dispose()
        delayDisposable?.dispose()
    }

    override fun paused(pause: Boolean) {
        log("Super pause override $pause ")
        super.paused(pause)
        if (pause) {
            log("Super pause override pauseBlockTimeLeft $pauseBlockTimeLeft ")
            log("Super pause override cycleDuration ${currentRxlBlock?.cycleDuration} ")
            log("Super pause override blockSecLeft $blockSecLeft ")
            pauseBlockTimeLeft = if (pauseBlockTimeLeft > 0) {
                pauseBlockTimeLeft.minus(blockSecLeft.toInt())
            } else {
                currentRxlBlock?.cycleDuration?.minus(blockSecLeft.toInt()) ?: 0
            }
            blockSecLeft = 0L
            blockDisposable?.dispose()
            delayDisposable?.dispose()
            log("Super pause override pauseBlockTimeLeft $pauseBlockTimeLeft ")
        }
    }


    //private var sequence = IntArray(0)
    // private var sequence = Array(0) { "" }
    // private var isRand: Boolean = false

    //private var isHopscotch: Boolean = false
    // private var isTwice: Boolean = false
    // private var random: Random? = null

    /// TODO Lights Logics

    private fun sendNextLight(player: RxlPlayer) {
        log("sendNextLight blockLogic $lightLogic player=" + player.id)
        when (lightLogic) {
            1 -> {
                lightOnSequence(player)
            }
            2 -> {
                nextRandomLight(player)
            }
            3 -> {
                nextFocusLight(player)
            }
            4 -> {
                lightOnAllAtOnce(player)
                //onNextAllATOnce()
            }
            5 -> {
                tapAtAllLight(player, null)
            }
            else -> {
                lightOnSequence(player)
            }
        }
        log("sendNextLight end " + player.id)
    }

    // Sequence
    private fun lightOnSequence(player: RxlPlayer) {
        log("SEQUENCE ----- lastPod ${player.lastPod}")
        val pod = player.pods[player.nextSeq()]
        player.lastUid = pod.uid
        sendColor(pod, player.color, blockAction, player.id, true)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        player.incSeq()
    }

    // Random
    private fun nextRandomLight(player: RxlPlayer) {
        log("RANDOM ----- ${player.id}")

        val id = player.nextRandom()
        val pod = player.pods[id]
        player.lastUid = pod.uid
        sendColor(pod, player.color, blockAction, player.id, true)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        //player.inc()
    }

    // Focus
    private fun isNextFocus(): Boolean {
        //return Random.nextInt(50) % 2 == 0
        return Random.nextInt(50) % 3 == 0
    }

    private var lastFocusUid = ""
    private fun nextFocusLight(player: RxlPlayer) {
        //if (colorSent)
        //     return
        colorSent = true
        log("FOCUS ----- $player")

        val id = player.nextRandom()
        val pod = player.pods[id]
        player.isFocus = isNextFocus()
        player.lastFocusUid = ""
        log("child nextFocusLight isNextFocus ${player.isFocus}")
        if (player.isFocus) {
            // focusValid = true
            player.lastUid = pod.uid
            player.lastFocusUid = pod.uid
            sendColor(pod, player.color, blockAction, player.id, true)
            //sendToFocusLight(player)
        } else {
            //focusValid = false
            var c = Random.nextInt(colors.size)
            //var c = colors[i].activeColor
            log("nextRandomColor i $c , colorPosition ${player.colorId}")
            if (c == player.colorId && c < colors.size - 2)
                c++
            sendColor(pod, colors[c].activeColor, blockAction, player.id, true)
            player.lastUid = pod.uid
            player.colorId = c

            //sendToNonFocusLight(player)
        }
    }

    // All At Once
    private val DELAY: Int = 100

    @Synchronized
    override fun onAllATOnce(event: RxlStatusEvent, id: Int) {
        log("child: onNextAllATOnce >>> $id : $event ")
        when (id) {
            in 1..50 -> {
                playerEventAllATOnce(players[0], event)
            }
            in 51..100 -> {
                playerEventAllATOnce(players[1], event)
            }
            in 101..150 -> {
                playerEventAllATOnce(players[2], event)
            }
            in 151..200 -> {
                playerEventAllATOnce(players[3], event)
            }
        }

    }

    private fun playerEventAllATOnce(player: RxlPlayer, event: RxlStatusEvent) {
        log("child playerEvent player ${player.id} ${player.lastUid} :: ${player.lastPod}  = ${event.data}")
        if (player.lastPod == event.data) {
            if (player.lastFocusUid == event.uid)
                player.events.add(Event(player.events.size + 1, blockAction, event.time))
            else player.events.add(Event(player.events.size + 1, blockAction, 0))

            nextAllATOnce(player)
//            if (player.lastUid == event.uid) {
//                //colorSent = false
//                log("playerEvent UID Matched  ")
//            } else {
//                log("playerEvent UID NOT Matched >> ${player.lastUid}  == ${event.uid}")
//                nextAllATOnce(player)
//                player.events.add(Event(player.events.size + 1, blockAction, event.time, false))
//                //player.wrongEvents.add(Event(player.wrongEvents.size + 1, actionTime, event.time))
//            }
        } else {
            log("child playerEvent player already received...........")
        }
    }

    private fun nextAllATOnce(player: RxlPlayer) {
        log("nextAllATOnce nextLight")
        if (blockActionDelay > MIN_DELAY) {
            turnOffAllATOnce(player)
            Single.timer(blockActionDelay.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                lightOnAllAtOnce(player)
            }.doOnError {
                MIBO.log("AllAtOnceParser onNext error: $it")
                lightOnAllAtOnce(player)
            }.subscribe()
        } else {
            lightOnAllAtOnce(player)
        }
    }

    private fun turnOffAllATOnce(player: RxlPlayer) {
        log("turnOffAllATOnce")
        Observable.fromIterable(player.pods).subscribeOn(Schedulers.io()).doOnNext { device ->
            sendColor(device, player.color, 0, 0, false)
            Thread.sleep(10)
            //log("nextFocusEvent onChangeColorEvent Observable ON = ${device.uid}")
        }.doOnError {
            log("turnOffAllATOnce doOnError $it")
            it?.printStackTrace()
        }.doOnComplete {
            log("turnOffAllATOnce doOnComplete")
        }.subscribe()
    }

    private fun getPlayerId(player: RxlPlayer): Int {
        player.inc()
        when (player.id) {
            1 -> {
                if (player.lastPod >= 50)
                    player.lastPod = 1
            }
            2 -> {
                if (player.lastPod >= 100)
                    player.lastPod = 51
            }
            3 -> {
                if (player.lastPod >= 150)
                    player.lastPod = 101
            }
            4 -> {
                if (player.lastPod >= 200)
                    player.lastPod = 151
            }
        }

        return player.lastPod

    }

    @Synchronized
    private fun lightOnAllAtOnce(player: RxlPlayer) {
        //val id = player.nextRandom()
        val uid = player.randomPod()?.uid
        val id = getPlayerId(player)
        player.lastFocusUid = ""
        log("lightOnAllAtOnce ----- player ${player.id}, uid $uid, getPlayerId $id, action $blockAction")
        Observable.fromIterable(player.pods).subscribeOn(Schedulers.io()).doOnNext { device ->
            if (uid == device.uid) {
                player.lastUid = device.uid
                player.lastFocusUid = device.uid
                //player.isFocus = true
                sendDelayColor(device, player.color, blockAction, id, DELAY, true)
            } else {
                //player.isFocus = false
                sendDelayColor(
                    device,
                    nextRandomColor(player),
                    blockAction,
                    id,
                    DELAY,
                    false
                )
            }
            log("lightOnAllAtOnce sendDelayColorEvent to player ${player.id} - ${device.uid}")
            Thread.sleep(10)
            //log("nextFocusEvent onChangeColorEvent Observable ON = ${device.uid}")
        }.doOnError {
            log("onNextAllATOnce doOnError $it")
            it?.printStackTrace()
        }.doOnComplete {
            log("onNextAllATOnce doOnComplete")
        }.subscribe()
    }

    private fun nextRandomColor(player: RxlPlayer): Int {
        var i = Random.nextInt(colors.size)
        //var c = colors[i].activeColor
        log("nextRandomColor i $i , colorPosition ${player.colorId}")
        if (i == player.colorId && i < colors.size - 2)
            i++
        return colors[i].activeColor
    }

    // Tap All


    private fun tapAtAllLight(player: RxlPlayer, event: RxlStatusEvent?) {
        log("tapAtAllLight $player : event : $event")
        if (event == null) {
            turnOnPods(player)
            return
        }

        if (player.lastUid == event.uid) {
            log("tapAtAllLight MATCHED player.lastUid != event.uid")
            player.lastUid = ""
            // log("RxlStatusEvent UID Matched ${player.lastUid} == $event.uid ")
            if (blockActionDelay > MIN_DELAY) {
                Single.timer(blockActionDelay.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                    turnOnPods(player)
                }.doOnError {
                    turnOnPods(player)
                }.subscribe()
            } else {
                turnOnPods(player)
            }
        }
        player.events.add(Event(player.events.size + 1, blockAction, event.time))

    }

    fun turnOnPods(player: RxlPlayer) {
        log("nextFocusEvent turnOnPods")
        var count = 1;
        val lastPod = player.lastPod()?.uid
        Observable.fromIterable(player.pods).subscribeOn(Schedulers.io()).doOnNext { pod ->
            try {
                player.lastUid = pod.uid
                //player.lastUid = lastPod
                sendColor(
                    pod,
                    player.color,
                    blockAction.times(count),
                    player.id,
                    pod.uid == lastPod
                )
                count++
                Thread.sleep(10)
            } catch (ignore: Exception) {
            }

        }.doOnError {

        }.doOnComplete {
        }.subscribe()
    }


    // Send Color
    private var colorDisposable = SparseArray<Disposable?>()

    fun sendColor(d: Device?, color: Int, action: Int, playerId: Int, observe: Boolean = false) {
        log("sendColor --- $action - player $playerId")
        d?.let {
            it.colorPalet = color
            CommunicationManager.getInstance()
                .onChangeColorEventRxl(ChangeColorEvent(it, it.uid, action, playerId))
        }

        if (observe)
            delayObserver(
                Delay(d?.uid, action.plus(RXLManager.DELAY_OBSERVE).toLong(), playerId)
            )
        //lastPod++
    }

    fun sendDelayColor(
        d: Device?, color: Int, action: Int,
        playerId: Int, delay: Int, observe: Boolean = false
    ) {
        log("sendDelayColor --- action $action : delay $delay playerId $playerId")
        if (d == null)
            return
        d.colorPalet = color
        CommunicationManager.getInstance()
            .onDelayColorEvent(DelayColorEvent(d, d.uid, action, playerId, delay))
        if (observe)
            delayObserver(Delay(d?.uid, action.plus(RXLManager.DELAY_OBSERVE).toLong(), playerId))
        //lastPod++
    }

    private fun delayObserver(delay: Delay) {
        log("delayObserver $delay")
        //colorDisposable?.dispose()
        //colorDisposable = null
        colorDisposable.get(delay.playerId)?.dispose()
        //colorDisposable.get(playerId) = null


        colorDisposable.put(
            delay.playerId,
            Single.just(delay).delay(delay.action, TimeUnit.MILLISECONDS).doOnSuccess { it ->
                val event =
                    RxlStatusEvent(byteArrayOf(0, 3, 0, 0, it.playerId.toByte(), 0, 0, 0), it.uid)
                log("delayObserver dispose --------- NOT HAPPEN ------- RxlStatusEvent ${event.data} size: ${players.size} : lightLogic $lightLogic")
                //log("onEvent RxlStatusEvent2 ${event.data} size: ${players.size}")
                for (p in players) {
                    if (p.id == event.data) {
                        onNext(p, event)
                        break
                    }
                }

            }.subscribe()
        )
    }

}