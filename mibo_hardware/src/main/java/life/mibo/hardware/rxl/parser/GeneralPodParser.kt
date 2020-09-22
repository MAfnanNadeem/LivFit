/*
 *  Created by Sumeet Kumar on 3/2/20 11:59 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/25/20 8:45 AM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.parser

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.rxl.Event
import life.mibo.hardware.rxl.program.RxlCycle
import life.mibo.hardware.rxl.program.RxlPlayer
import life.mibo.hardware.rxl.program.RxlProgram
import java.util.concurrent.TimeUnit

class GeneralPodParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "GeneralPodParser") {


    fun create() {

    }

    override fun stop() {
        super.stop()
        onProgramEndMulti()
    }


    override fun onProgramStart() {
        super.onProgramStart()
        val seq = program.sequence()
        log("child onProgramStart $seq")
        if (seq.isNullOrEmpty()) {
            for (p in program.players) {
                p.defaultSeq()
            }
        } else {
            if (seq.length == 1) {
                for (p in program.players) {
                    p.defaultSeq()
                }
            } else {
                // init sequence from api
                val s = seq.split(",")
                for (p in program.players) {
                    p.createSeq(s)
                }
            }

        }

        log("child createSequence")
    }

    fun createSequence() {

    }

    fun nextEvent(event: RxlStatusEvent) {

//        players.forEach {
//            if (it.id == event.data) {
//                if (delayTime > 0) {
//                    Single.timer(delayTime.toLong(), TimeUnit.MILLISECONDS).doOnSuccess { _ ->
//                        lightOnSequence(it)
//                    }.subscribe()
//                } else {
//                    lightOnSequence(it)
//                }
//                return@forEach
//            }
//        }

    }
//
//    fun hasNextCycle(): Boolean {
//        if (cycles > currentCycle) {
//            currentCycle++
//            return true
//        }
//        return false
//    }

//    override fun startExercise() {
//        startInternal()
//    }

    var isResuming = false;
    override fun onResumeCycle() {
        super.onResumeCycle()
        log("onResumeCycle....")
        isResuming = true
        onProgramStartMulti(speed, null)
        var timer = 0
        //isResuming = false
    }

    override fun onCycleStart(player: RxlPlayer) {
        log("child STARTING PROGRAM player...........${player.id}")
        onProgramStartMulti(1.0f, player)
        //sendNextLight(player)
    }

    @Synchronized
    override fun onCycleStart() {
        log("child STARTING PROGRAM isTap $isTap")
        onProgramStartMulti(1.0f, null)
    }


    @Synchronized
    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        log("child nextLightEvent called")
        if (player.lastUid == event.uid) {
            // log("RxlStatusEvent UID Matched ${player.lastUid} == $event.uid ")
            player.events.add(
                Event(
                    player.events.size + 1,
                    blockAction,
                    event.time
                )
            )
            if (blockDelay > MIN_DELAY) {
                Single.timer(blockDelay.toLong(), TimeUnit.MILLISECONDS).doOnSuccess {
                    sendNextLight(player)
                }.doOnError {
                    sendNextLight(player)
                }.subscribe()
            } else {
                sendNextLight(player)
            }
        }


//        for (p in players) {
//            if (p.id == player.id) {
//                lightOnSequence(player)
//                break
//            }
//        }

    }

    private fun sendNextLight(player: RxlPlayer) {
        log("sendNextLight start " + player.id)
        when (blockLogic) {
            1 -> {
                lightOnSequence(player)
            }
            2 -> {
                nextRandomLight(player)
            }
            else -> {
                lightOnSequence(player)
            }
        }
        log("sendNextLight end " + player.id)
    }

    private fun lightOnSequence(player: RxlPlayer) {
        log("child lightOnSequence lastPod ${player.lastPod}, size ${player.pods.size}")
        val pod = player.pods[player.nextSeq()]
        player.lastUid = pod.uid
        listener.sendColorEvent(pod, player.color, blockAction, player.id, true)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        player.incSeq()
    }

    private fun nextRandomLight(player: RxlPlayer) {
        log("child lightOnRandom $player")

        val id = player.nextRandom()
        val pod = player.pods[id]
        player.lastUid = pod.uid
        listener.sendColorEvent(pod, player.color, blockAction, player.id, true)
        //EventBus.getDefault().postSticky(PodEvent(d.uid, exercise?.colors!!.activeColor, exercise?.duration!!.actionTime, false))
        //player.inc()
    }


    override fun completeCycle() {
        super.completeCycle()
        log("completeCycle")
//        if (cycles > currentCycle) {
//            currentCycle++
//            //pauseCycle(0, getPause())
//            log("completeCycle start new cycle")
//            listener.nextCycle(currentCycle, pauseTime, duration)
//            //resumeObserver(currentCycle, getPause(), duration)
//        } else {
//            log("completeCycle end program...")
//            listener.endProgram(0, 0)
//        }
    }


    // Multi

    private var currentBlock = -1
    private var currentRxtBlock: RxlCycle? = null
    private var currentRound = -1
    private var totalRounds = -1
    private var blockSecLeft = 0L

    //private var duration = -1
    private var blockAction = 1000
    private var blockDelay = -1
    private var blockSize = -1
    private var blockLogic = 1

    //private var tilesSize = -1
    private var speed: Float = 1.0f
    private var blocks = ArrayList<RxlCycle>()
    private var islandId = 0
    var isBlockRunning = false


    fun onProgramStartMulti(speed: Float, player: RxlPlayer?) {
        this.speed = speed
        islandId = 1
        log("$islandId onProgramStart")
        blocks.clear()
        blocks.addAll(program.getCycles())
        //tiles.clear()
        //tiles.addAll(island.tiles)
        // duration = island.getDuration()
        blockSize = blocks.size
        onStartProgramMultiInternal(player)
    }

    fun onProgramEndMulti() {
        log("$islandId onProgramEnd")
        release()
    }

    private fun onStartProgramMultiInternal(player: RxlPlayer?) {
        startNextBlock(player)
    }

    // Private
    private fun onBlockStart(block: RxlCycle, player: RxlPlayer?) {
        log("$islandId onBlockStart $block - $player")
        listener.onBlockStart(currentBlock, currentRound)
        lastSeq = 0
        isStarted = true
        if (player != null) {
            sendNextLight(player)
            Thread.sleep(THREAD_SLEEP)
        } else {
            if (isTap) {
                for (p in getPlayers()) {
                    if (p.isTapReceived) {
                        if (!p.isStarted) {
                            log("child STARTING PROGRAM starting player $p")
                            p.isStarted = true
                            //p.lastPod = 250
                            sendNextLight(p)
                        }
                        Thread.sleep(THREAD_SLEEP)
                    }
                }
            } else {
                for (p in getPlayers()) {
                    //p.lastPod = 250
                    sendNextLight(p)
                    Thread.sleep(THREAD_SLEEP)
                }
            }

        }
    }

    private fun onBlockEnd(block: RxlCycle) {
        log("$islandId onBlockEnd $block")
        //isStarted = false
        listener.onBlockEnd(currentBlock, currentRound)
        startNextDelay()
        //currentBlock++
        //currentRound++
    }

    private fun onDelayStart() {
        log("$islandId onDelayStart")
        listener.onBlockStart(currentBlock, -1)
    }

    private fun onDelayEnd() {
        log("$islandId onDelayEnd")
        if (isStarted)
            startNextBlock(null)
    }

    private fun startNextBlock(player: RxlPlayer?) {
        log("startNextBlock isResuming $isResuming")
        if (isResuming) {
            startBlockObservers(currentRxtBlock!!, player)
            return
        }
        log("startNextBlock $currentBlock : $currentRound : $totalRounds")
        if (currentRxtBlock == null || currentBlock == -1) {
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
        //if (currentRxtBlock != null)
        startBlockObservers(currentRxtBlock!!, player)
    }

    private fun createBlock() {
        if (isPaused) {
            // reused old variables
        }
        blockSecLeft = 0
        pauseBlockTimeLeft = 0
        currentRxtBlock = blocks[currentBlock]
        currentRound = 1
        totalRounds = currentRxtBlock!!.repeat
        blockLogic = currentRxtBlock!!.getLogicType()
        blockDelay = currentRxtBlock!!.actionDelay
        blockAction = currentRxtBlock!!.cycleAction
        //blockAction = 300
        pattern = currentRxtBlock!!.sequence ?: ""
        //createSequence(island.tiles)
        //initRandomSeq()
    }


    private fun startNextDelay() {
        if (blockDelay > 0)
            startDelayObservers(blockDelay.toLong())
        else
            onDelayEnd()
    }

    // Observers
    var blockDisposable: Disposable? = null
    var delayDisposable: Disposable? = null
    private fun startBlockObservers(block: RxlCycle, player: RxlPlayer?) {
        if (isBlockRunning && !isResuming) {
            onBlockStart(block, player)
            return
        }
        var blockTime_ =
            if (isResuming) pauseBlockTimeLeft.toLong() else block.cycleDuration.toLong()
        if (blockTime_ < 0)
            blockTime_ = -blockTime_
        log("startBlockObservers block $currentBlock : round $currentRound : total $totalRounds : blockLogic  $blockLogic -- ${block.cycleDuration} -- blockTime_ $blockTime_ - pauseBlockTimeLeft $pauseBlockTimeLeft")
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

    var pauseBlockTimeLeft = 0
    override fun paused(pause: Boolean) {
        log("Super pause override $pause ")
        super.paused(pause)
        if (pause) {
            log("Super pause override pauseBlockTimeLeft $pauseBlockTimeLeft ")
            log("Super pause override cycleDuration ${currentRxtBlock?.cycleDuration} ")
            log("Super pause override blockSecLeft $blockSecLeft ")
            pauseBlockTimeLeft = if (pauseBlockTimeLeft > 0) {
                pauseBlockTimeLeft.minus(blockSecLeft.toInt())
            } else {
                currentRxtBlock?.cycleDuration?.minus(blockSecLeft.toInt()) ?: 0
            }
            blockSecLeft = 0L
            blockDisposable?.dispose()
            delayDisposable?.dispose()
            log("Super pause override pauseBlockTimeLeft $pauseBlockTimeLeft ")
        }
    }

    private var pattern = ""

    //private var sequence = IntArray(0)
    // private var sequence = Array(0) { "" }
    private var lastSeq = -1
    // private var isRand: Boolean = false

    //private var isHopscotch: Boolean = false
    // private var isTwice: Boolean = false
    // private var random: Random? = null


}