/*
 *  Created by Sumeet Kumar on 9/8/20 5:45 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/8/20 5:45 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.core

import android.graphics.Color
import android.util.SparseArray
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.RxlStatusEvent
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PodParser(val rxlPlayer: RxlPlayer) {

    var islandId = 0;
    var lastTile = 0;
    var secondTile = -1

    private var listener: RxlProgramListener? = null;
    private var isStarted: Boolean = false
    private var events = ArrayList<RxlStatusEvent>()
    //private var wrongEvents = ArrayList<Event>()

    private var currentBlock = -1
    private var currentRxtBlock: RxlBlock? = null
    private var currentRound = -1
    private var totalRounds = -1

    //private var duration = -1
    private var blockAction = 1000
    private var blockDelay = -1
    private var blockSize = -1
    private var blockLogic = 1

    private var tilesSize = -1
    private var speed: Float = 1.0f
    private var blocks = ArrayList<RxlBlock>()
    // private var tiles = ArrayList<RxtTile>()

    // Public
    fun onProgramStart(speed: Float) {
        this.speed = speed;
        islandId = rxlPlayer.id
        log("$islandId onProgramStart")
        blocks.clear()
        blocks.addAll(rxlPlayer.program.blocks)
        //tiles.clear()
        //tiles.addAll(island.tiles)
        // duration = island.getDuration()
        blockSize = blocks.size
        tilesSize = rxlPlayer.pods.size
        lastTile = 0;
        listener?.onProgramStart(null)
        onStartProgramInternal()
    }

    fun onProgramEnd() {
        log("$islandId onProgramEnd")
        isStarted = false
        listener?.onProgramEnd(null)
        release()
    }

    fun onProgramPause() {
        log("$islandId onProgramEnd")
        isStarted = false
        release()
    }

    private fun onStartProgramInternal() {
        events.clear()
        startNextBlock()
    }

    // Private
    private fun onBlockStart(block: RxlBlock) {
        log("$islandId onBlockStart $block")
        lastSeq = 0
        isStarted = true
        sendColor(nextTile(), Color.GREEN, blockAction, islandId, true)
        listener?.onBlockStart(currentBlock, currentRound)
    }

    private fun onBlockEnd(block: RxlBlock) {
        log("$islandId onBlockEnd $block")
        isStarted = false
        listener?.onBlockEnd(currentBlock, currentRound)
        startNextDelay()
        //currentBlock++
        //currentRound++
    }

    private fun onDelayStart() {
        log("$islandId onDelayStart")
    }

    private fun onDelayEnd() {
        log("$islandId onDelayEnd")
        startNextBlock()
    }

    private fun startNextBlock() {
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
        startBlockObservers(currentRxtBlock!!)
    }

    private fun createBlock() {
        currentRxtBlock = blocks[currentBlock]
        currentRound = 1
        totalRounds = currentRxtBlock!!.round
        blockLogic = currentRxtBlock!!.logicType
        blockDelay = currentRxtBlock!!.delay
        blockAction = currentRxtBlock!!.action
        pattern = currentRxtBlock!!.pattern
        //createSequence(island.tiles)
        initRandomSeq()
    }

    private fun initRandomSeq() {
        isRand = isRandom()
        if (isRand) {
            random = Random.Default
            //var localRandom : ThreadLocalRandom
            // random = Random(tilesSize)
        } else {
            //isHopscotch = false
            createSequence(rxlPlayer.pods)
        }
    }

    private fun startNextDelay() {
        if (blockDelay > 0)
            startDelayObservers(blockDelay.toLong())
        else
            onDelayEnd()
    }

    fun isSequence() = blockLogic == 1
    fun isRandom() = blockLogic == 2

    // Observers
    var blockDisposable: Disposable? = null
    var delayDisposable: Disposable? = null
    private fun startBlockObservers(block: RxlBlock) {
        log("startBlockObservers block $currentBlock : round $currentRound : total $totalRounds : blockLogic  $blockLogic")
        if (block.duration == 0) {
            startAutoBlockObservers(block)
            return
        }
        blockDisposable = Single.timer(block.duration.toLong(), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread()).doOnSuccess {
                log("blockDisposable doOnSuccess")
                onBlockEnd(block)
            }.doAfterSuccess {
                log("blockDisposable doAfterSuccess")
            }.doOnError {
                log("blockDisposable doOnError $it")
            }.doOnSubscribe {
                log("blockDisposable doOnSubscribe $it")
                onBlockStart(block)
            }.subscribe()
    }

    private fun startAutoBlockObservers(block: RxlBlock) {
        blockDisposable = Single.timer(block.duration.toLong(), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread()).doOnSuccess {
                log("blockDisposable doOnSuccess")
                onBlockEnd(block)
            }.doAfterSuccess {
                log("blockDisposable doAfterSuccess")
            }.doOnError {
                log("blockDisposable doOnError $it")
            }.doOnSubscribe {
                log("blockDisposable doOnSubscribe $it")
                onBlockStart(block)
            }.subscribe()
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
                blinkDelay(delay)
            }.subscribe()
    }


    private fun blinkDelay(delay: Long) {
        log("blinkDelay delay $delay")
        try {
            if (delay > 1) {
                val time = delay.times(1000).toInt()
                sendColor(rxlPlayer.next(0), 0xAAFF0000.toInt(), time.minus(300), islandId, false)
                lastTile = -1
            }
        } catch (e: java.lang.Exception) {
            log("blinkDelay error $e")
            e.printStackTrace()
        }
    }

    private fun release() {
        blockDisposable?.dispose()
        delayDisposable?.dispose()
    }

    private var colorDisposable = SparseArray<Disposable?>()
    private fun delayObserver(d: RxlPod, action: Int, id: Int) {
        log("delayObserver $d : $id")
        colorDisposable.get(id)?.dispose()
        //colorDisposable.get(playerId) = null

        colorDisposable.put(
            id,
            Single.just(action).delay(action.plus(300).toLong(), TimeUnit.MILLISECONDS)
                .doOnSuccess {
                    log("delayObserver dispatch $id")
                    onNext(
                        RxlStatusEvent(
                            byteArrayOf(
                                -64,
                                4,
                                d.podId.toByte(),
                                0,
                                0,
                                id.toByte(),
                                0
                            ), d.uid
                        )
                    )
                }.subscribe()
        )
    }


    private var pattern = ""

    //private var sequence = IntArray(0)
    private var sequence = Array(0) { "" }
    private var lastSeq = -1
    private var isRand: Boolean = false

    //private var isHopscotch: Boolean = false
    private var isTwice: Boolean = false
    private var random: Random? = null

    fun getAction(): Int {
        return blockAction.times(speed).toInt()
    }

    fun onNext(event: RxlStatusEvent) {
        log("OnNext $event")
        // events.add(event)
        if (isStarted) {
            if (isTwice) {
                isTwice = false
            } else sendColor(nextTile(), rxlPlayer.color, getAction(), islandId, true)

            events.add(event)
            if (event.data == secondTile) {
                secondTile = -1
                colorDisposable.get(islandId.plus(1))?.dispose()
            }
        }

    }

    private fun sendColor(d: RxlPod, color: Int, action: Int, id: Int, observe: Boolean = false) {
        log("SendColor $d : action  = $action : island = $id")
        lastTile = d.podId
        if (observe)
            delayObserver(d, action, id)
        Single.fromCallable {
            CommunicationManager.getInstance()
                .onChangeColorEventRxl(ChangeColorEvent(d.uid, "0", color, action, id))
            return@fromCallable ""
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    private fun sendSecondColor(
        d: RxlPod,
        color: Int,
        action: Int,
        id: Int,
        observe: Boolean = false
    ) {
        log("sendSecondColor $d : action  = $action : island = $id")
        isTwice = true;
        secondTile = d.podId
        if (observe)
            delayObserver(d, action, id.plus(1)) // TODO remove plus 1 later
        Single.fromCallable {
            CommunicationManager.getInstance()
                .onChangeColorEventRxl(ChangeColorEvent(d.uid, "0", color, action, id))
            return@fromCallable ""
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    fun sendSecond(tile: RxlPod, color: Int, action: Int) {
        isTwice = true
        secondTile = tile.podId
        delayObserver(tile, action, islandId.plus(1))
        Single.fromCallable {
            CommunicationManager.getInstance()
                .onChangeColorEventRxl(
                    ChangeColorEvent(
                        tile.uid,
                        "0",
                        color,
                        action,
                        tile.podId
                    )
                )
            return@fromCallable ""
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    private fun createSequence(pods: List<RxlPod>) {
        log("createSequence $tilesSize : $pattern")
        lastSeq = 0
        if (pattern.isEmpty()) {
            val size = tilesSize
            //sequence = IntArray(size)
            sequence = Array(size) { "" }

            for (i in 0 until size) {
                sequence[i] = "$i"
            }
            log("createSequence pattern empty ${sequence.contentToString()}")

        } else {
            val s = pattern.split(",")
            //sequence = IntArray(s.size)
            sequence = Array(s.size) { "" }
            s.forEachIndexed { index, i ->
                //sequence[index] = getSeq(i)
                sequence[index] = this.getSeqStr(i)
            }
            log("createSequence seq ${sequence.contentToString()}")
        }
    }


    private fun getSeq(s: String?): Int {
        return try {
            s!!.toInt().minus(1)
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }

    private fun getSeqStr(s: String?): String {
        return try {
            if (s!!.contains("-")) {
                val list = s.split("-")
                "${list[0].toInt().minus(1)}-${list[1].toInt().minus(1)}"
            } else {
                "${s.toInt().minus(1)}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "1"
        }
    }

    private fun getInt(s: String?): Int {
        return try {
            s!!.toIntOrNull() ?: 1
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }


    private fun getRandom(): Random {
        if (random == null)
            random = Random.Default
        return random!!
    }

    private fun nextTile(): RxlPod {
        if (isRand)
            return nextRandom()
        return nextSeq()
    }

    private fun nextSeq(): RxlPod {
        try {
            val id = if (lastSeq < sequence.size) {
                sequence[lastSeq]
            } else {
                lastSeq = 0
                sequence[lastSeq]
            }
            log("nextTile next $id : lastPosition $lastSeq")
            lastSeq++
            if (id.contains("-")) {
                log("nextTile next is double........$id : lastPosition $lastSeq")
                val split = id.split("-")
                sendSecondColor(
                    rxlPlayer.next(getInt(split[0])),
                    rxlPlayer.color,
                    getAction(),
                    islandId.plus(1),
                    true
                )
                try {
                    Thread.sleep(15)
                } catch (e: Exception) {

                }
                return rxlPlayer.next(getInt(split[1]))
            }
            return rxlPlayer.next(getInt(id))
        } catch (e: java.lang.Exception) {
            log("nextTile ERROR ... $e")
            e.printStackTrace()
        }
        return rxlPlayer.next(0)
    }

    private fun nextRandom(): RxlPod {
        try {
            var id = getRandom().nextInt(0, tilesSize)
            log("nextTile $id : $lastSeq")
            if (id == lastSeq)
                id = getRandom().nextInt(0, tilesSize)
            lastSeq = id
            return rxlPlayer.next(id)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return rxlPlayer.next(0)
    }

//    private fun nextHopscotch(): RxtTile {
//        try {
//            val id = if (lastSeq < sequence.size) {
//                sequence[lastSeq]
//            } else {
//                lastSeq = 0
//                sequence[lastSeq]
//            }
//            log("nextTile next $id : lastPosition $lastSeq")
//            lastSeq++
//            return island.next(id)
//        } catch (e: java.lang.Exception) {
//            log("nextTile ERROR ... $e")
//            e.printStackTrace()
//        }
//        return island.next(0)
//    }

    fun getScore(): ScoreItem {
        var hit = 0
        var miss = 0
        val total = events.size
        val list = ArrayList(events) // ThreadModificationError
        if (total > 0) {
            for (i in list) {
                if (i.time > 0)
                    hit++
                else
                    miss++
            }
        }

        return ScoreItem(1, rxlPlayer.name, "$hit", "$miss", rxlPlayer.color, total, 60)
    }

    fun log(msg: String) {
        Logger.e("IslandParser $islandId", "RXLTest - $msg")
    }

}