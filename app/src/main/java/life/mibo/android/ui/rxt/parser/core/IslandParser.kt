package life.mibo.android.ui.rxt.parser.core

import android.graphics.Color
import android.os.Looper
import android.util.SparseArray
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.ui.rxt.parser.RxtBlock
import life.mibo.android.ui.rxt.parser.RxtIsland
import life.mibo.android.ui.rxt.parser.RxtProgram
import life.mibo.android.ui.rxt.parser.RxtTile
import life.mibo.android.ui.rxt.score.ScoreItem
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.RxtStatusEvent
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class IslandParser(val island: RxtIsland, var listener: IslandListener? = null) {

    var islandId = 0;
    var islandId2 = 0;
    var lastTile = 0;
    var secondTile = -1

    private var isStarted: Boolean = false
    private var events = ArrayList<RxtStatusEvent>()
    //private var wrongEvents = ArrayList<Event>()

    private var currentProgram = -1
    private var currentBlock = -1
    private var currentRxtBlock: RxtBlock? = null
    private var currentRound = -1
    private var totalRounds = -1

    //private var duration = -1
    private var blockAction = 1000
    private var blockActionDelay = 0
    private var blockDelay = -1
    private var programDelay = 0
    private var blockSize = -1
    private var blockLogic = 1
    private var isAutoBlock = false


    private var tilesSize = -1
    private var speed: Float = 1.0f
    private var blocks = ArrayList<RxtBlock>()
    private var programs = ArrayList<RxtProgram>()
    private var isCircuit = false
    // private var tiles = ArrayList<RxtTile>()

    // Public
    private var isProgramRunning = false
    fun onProgramStart(speed: Float) {
        this.speed = speed;
        islandId = island.id
        islandId2 = island.id.plus(2)
        log("$islandId onProgramStart")
        events.clear()
        if (island.isCircuit) {
            isCircuit = true
            startCircuit()
            return
        }
        isProgramRunning = true
        blocks.clear()
        blocks.addAll(island.program.blocks)
        //tiles.clear()
        //tiles.addAll(island.tiles)
        // duration = island.getDuration()
        blockSize = blocks.size
        tilesSize = island.tiles.size
        lastTile = 0;
        //listener?.onProgramStart(null)
        onStartProgramInternal()
    }

    private fun startCircuit() {
        log("startCircuit..........")
        programs.addAll(island.programs)
        tilesSize = island.tiles.size
        lastTile = 0;
        if (programs.size > 0) {
            currentProgram = 0;
            isProgramRunning = true
            startProgramObservers(programs[currentProgram])
        }
    }

    private fun createCircuit(program: RxtProgram) {
        log("createCircuit..........$program")
        blocks.clear()
        blocks.addAll(program.blocks)
        //tiles.clear()
        //tiles.addAll(island.tiles)
        // duration = island.getDuration()
        blockSize = blocks.size
        currentBlock = 0;
        currentRound = 1;
        lastTile = 0;
        //listener?.onProgramStart(null)
        onStartProgramInternal()
    }

    private fun onCircuitProgramStart(rxtProgram: RxtProgram) {
        log("onCircuitProgramStart..........")
        isProgramRunning = true
        isStarted = true
        createCircuit(rxtProgram)
        listener?.onCircuitProgramStart(rxtProgram.name, currentProgram, 0)
    }

    private fun onCircuitProgramEnd() {
        isProgramRunning = false
        isStarted = false
        lastTile = -1
        listener?.onCircuitProgramEnd("", currentProgram, 0)
        blockDisposable?.dispose()
        val rxt = programs.get(currentProgram)
        programDelay = rxt.workoutPause
        log("onCircuitProgramEnd.......... pause $programDelay")
        if (programDelay > 0)
            startProgramPauseObservers(programDelay)
        else {
            onCircuitProgramPauseEnd()
        }
    }

    private fun onCircuitProgramPauseStart() {
        log("onCircuitProgramPauseStart..........")
        listener?.onCircuitProgramStart("Pause $programDelay sec", currentProgram, programDelay)
    }

    private fun onCircuitProgramPauseEnd() {
        log("onCircuitProgramPauseEnd..........")
        listener?.onCircuitProgramEnd("", currentProgram, programDelay)
        if (currentProgram < programs.size) {
            currentProgram++
            if (currentProgram >= programs.size) {
                log("onCircuitProgram Loop Completed....................")
                currentProgram = 0
                isStarted = false
                release()
                return
            }
            startProgramObservers(programs[currentProgram])
        } else {
            log("onCircuitProgram Loop Size exceed?????????????????????? $currentProgram")
        }

    }

    fun onProgramEnd() {
        log("$islandId onProgramEnd")
        isStarted = false
        //listener?.onProgramEnd(null)
        release()
    }

    private fun onStartProgramInternal() {
        startNextBlock()
    }

    // Private
    private fun onBlockStart(block: RxtBlock) {
        log("$islandId onBlockStart $block")
        lastSeq = 0
        isStarted = true
        sendColor(nextTile(), Color.GREEN, blockAction, islandId, true)
        listener?.onBlockStart(currentBlock, currentRound)
    }

    private fun onBlockEnd(block: RxtBlock?) {
        log("$islandId onBlockEnd $block")
        isStarted = false
        //listener?.onBlockEnd(currentBlock, currentRound)
        startNextDelay()
        //currentBlock++
        //currentRound++
    }

    private fun onDelayStart() {
        log("$islandId onDelayStart")
    }

    private fun onDelayEnd() {
        log("$islandId onDelayEnd")
        if (isProgramRunning)
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
        blockDelay = currentRxtBlock!!.blockPause
        blockActionDelay = currentRxtBlock!!.delay
        //blockActionDelay = 2
        blockAction = currentRxtBlock!!.action
        pattern = currentRxtBlock!!.pattern
        isAutoBlock = false
        //isActionDelay = blockActionDelay > 0
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
            createSequence(island.tiles)
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

    // TODO Observers
    var blockDisposable: Disposable? = null
    var delayDisposable: Disposable? = null
    var programDisposable: Disposable? = null
    var programPause: Disposable? = null
    private fun startProgramObservers(rxtProgram: RxtProgram) {
        log("startProgramObservers rxt $currentProgram : ${rxtProgram.workoutDuration}")
        programDisposable = Single.timer(rxtProgram.workoutDuration.toLong(), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io()).doOnSuccess {
                log("programDisposable doOnSuccess")
                onCircuitProgramEnd()
            }.doAfterSuccess {
                log("programDisposable doAfterSuccess")
            }.doOnError {
                log("programDisposable doOnError $it")
            }.doOnSubscribe {
                log("programDisposable doOnSubscribe $it")
                onCircuitProgramStart(rxtProgram)
            }.subscribe()
    }

    private fun startProgramPauseObservers(pause: Int) {
        log("startProgramPauseObservers rxt $currentProgram : ${pause}")
        programPause = Single.timer(pause.toLong(), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io()).doOnSuccess {
                log("programDisposable doOnSuccess")
                onCircuitProgramPauseEnd()
            }.doAfterSuccess {
                log("programDisposable doAfterSuccess")
            }.doOnError {
                log("programDisposable doOnError $it")
            }.doOnSubscribe {
                log("programDisposable doOnSubscribe")
                onCircuitProgramPauseStart()
            }.subscribe()
    }

    private fun startBlockObservers(block: RxtBlock) {
        log("startBlockObservers block $currentBlock : round $currentRound : total $totalRounds : blockLogic  $blockLogic")
        if (blockLogic == 1 && block.duration == 0) {
            startAutoBlockObservers(block)
            return
        }
        blockDisposable = Single.timer(block.duration.toLong(), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io()).doOnSuccess {
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

    private fun startAutoBlockObservers(block: RxtBlock) {
        log("startAutoBlockObservers............")
        isAutoBlock = true
        blockDisposable = Single.fromCallable {
            onBlockStart(block)
            log("startAutoBlockObservers............ fromCallable")
            return@fromCallable ""
        }.subscribeOn(Schedulers.io()).doOnError {
        }.doOnDispose {
            log("startAutoBlockObservers............ fromCallable doOnDispose")
        }.subscribe()
    }

    private fun startDelayObservers(delay: Long) {
        delayDisposable =
            Single.timer(delay, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).doOnSuccess {
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

    private fun blinkAll(delay: Long) {
        try {
            if (delay > 2) {
                val uid = island?.tiles?.get(0).uid
                Single.fromCallable {
                    CommunicationManager.getInstance()
                        .onRxtBlinkAll(ChangeColorEvent(uid, "2", Color.RED, 500, 500))
                    ""
                }.subscribeOn(Schedulers.io()).doOnError { }.subscribe()
            }
        } catch (e: java.lang.Exception) {

        }
    }

    private fun blinkDelay(delay: Long) {
        log("blinkDelay delay $delay")
        try {
            if (delay > 1) {
                val time = delay.times(1000).toInt()
                sendColor(island.next(0), 0xAA0000FF.toInt(), time.minus(300), islandId, false)
                lastTile = -1
            }
        } catch (e: java.lang.Exception) {
            log("blinkDelay error $e")
            e.printStackTrace()
        }
    }

    private fun release() {
        programDisposable?.dispose()
        programPause?.dispose()
        blockDisposable?.dispose()
        delayDisposable?.dispose()
    }

    private var colorDisposable = SparseArray<Disposable?>()
    private fun delayObserver(d: RxtTile, action: Int, id: Int) {
        log("delayObserver $d : $id")
        colorDisposable.get(id)?.dispose()

        colorDisposable.put(
            id,
            Single.just(action).delay(action.plus(300).toLong(), TimeUnit.MILLISECONDS)
                .doOnSuccess {
                    log("delayObserver dispatch $id")
                    onNext(
                        RxtStatusEvent(
                            byteArrayOf(
                                -64,
                                4,
                                d.tileId.toByte(),
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

    private fun delayObserver2(d: RxtTile, action: Int, id: Int) {
        log("delayObserver2 $d : $id")
        colorDisposable.get(id)?.dispose()

        colorDisposable.put(
            id,
            Single.just(action).delay(action.plus(300).toLong(), TimeUnit.MILLISECONDS)
                .doOnSuccess {
                    log("delayObserver2 dispatch $id")
                    onNext2(
                        RxtStatusEvent(
                            byteArrayOf(
                                -64,
                                4,
                                d.tileId.toByte(),
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
    private var isActionDelay: Boolean = false

    //private var isHopscotch: Boolean = false
    private var isTwice: Boolean = false
    private var random: Random? = null

    fun getAction(): Int {
        return blockAction.times(speed).toInt()
    }

    fun onNext(event: RxtStatusEvent) {
        log("OnNext $event isTwice $isTwice")
        // events.add(event)
        if (isStarted) {
            lastTile = -1
            if (isTwice) {
                colorDisposable.get(islandId)?.dispose()
                isTwice = false
            } else sendColor(nextTile(), island.color, getAction(), islandId, true)

            events.add(event)
        }
    }

    fun onNext2(event: RxtStatusEvent) {
        log("OnNext2 $event isTwice $isTwice")
        if (isStarted) {
            log("OnNext2 secondTile :: $secondTile event.tile ${event.tile}")
            secondTile = -1
            colorDisposable.get(islandId2)?.dispose()
            if (isTwice) {
                isTwice = false
            } else sendColor(nextTile(), island.color, getAction(), islandId, true)

            events.add(event)
        }
    }

    @Synchronized
    private fun sendColor(d: RxtTile?, color: Int, action: Int, id: Int, observe: Boolean = false) {
        if (d == null)
            return
        log("SendColor $d : action  = $action : island = $id")
        lastTile = d.tileId
        changeColor(ChangeColorEvent(d.uid, "" + d.tileId, color, action, id))
        if (observe)
            delayObserver(d, action, id)
    }

    @Synchronized
    private fun sendSecondColor(
        d: RxtTile,
        color: Int,
        action: Int,
        id: Int,
        observe: Boolean = false
    ) {
        log("sendSecondColor $d : action  = $action : island = $id")
        isTwice = true;
        secondTile = d.tileId
        changeColor(ChangeColorEvent(d.uid, "" + d.tileId, color, action, id))
        if (observe)
            delayObserver2(d, action, id)
    }

    private fun changeColor(event: ChangeColorEvent) {
        log("changeColor $event")
        try {
            CommunicationManager.getInstance().onChangeRxtColorEvent(event, "RXTTest ChangeRxtColor ${event.tileId}")
        } catch (e: Exception) {
            log("changeColor error $e")
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Single.fromCallable {
                    CommunicationManager.getInstance().onChangeRxtColorEvent(event)
                    return@fromCallable ""
                }.subscribeOn(Schedulers.io()).subscribe()
            } else {
                CommunicationManager.getInstance().onChangeRxtColorEvent(event)
            }
        }
    }

    private fun createSequence(tiles: List<RxtTile>) {
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
            "0"
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

    private fun nextTileDelay(delay: Long, tile: RxtTile?) {
        log("nextTileDelay............ $delay")
        delayDisposable = Single.just("").delay(delay, TimeUnit.SECONDS)
            .doOnSuccess {
                log("nextTileDelay............ fromCallable ")
                sendColor(tile, island.color, getAction(), islandId, true)
                return@doOnSuccess
            }.doOnError {
                log("nextTileDelay............ doOnError $it")
                sendColor(tile, island.color, getAction(), islandId, true)
            }.subscribe()
    }

    private fun nextTile(): RxtTile? {
        if (isActionDelay) {
            if (isRand) {
                nextTileDelay(blockActionDelay.toLong(), nextRandom())
            } else {
                nextTileDelay(blockActionDelay.toLong(), nextSeq())
            }
            return null
        }
        if (isRand)
            return nextRandom()
        return nextSeq()
    }

    private fun nextSeq(): RxtTile? {
        try {
            var id = "0"
            if (lastSeq < sequence.size) {
                id = sequence[lastSeq]
            } else {
                if (isAutoBlock) {
                    isAutoBlock = false
                    //log("------------------------Block sequence end------------------------ $isAutoBlock ")
                    blockDisposable?.dispose()
                    blockDisposable = null
                    onBlockEnd(currentRxtBlock)
                    return null
                }
                lastSeq = 0
                id = sequence[lastSeq]
            }
            log("nextTile next $id : lastPosition $lastSeq")
            lastSeq++
            if (id.contains("-")) {
                log("nextTile next is double........$id : lastPosition $lastSeq")
                val split = id.split("-")
                sendDoubleSeq(getInt(split[1]))
                return island.next(getInt(split[0]))
            }
            log("nextTile next is single...")
            return island.next(getInt(id))
        } catch (e: java.lang.Exception) {
            log("nextTile ERROR ... $e")
            e.printStackTrace()
        }
        return island.next(0)
    }

    fun sendDoubleSeq(seq: Int) {
        log("sendDoubleSeq start tile $seq")
        Single.just(seq).delay(100, TimeUnit.MILLISECONDS).doAfterSuccess {
            log("sendDoubleSeq end tile $seq : $it")
            sendSecondColor(
                island.next(it),
                island.color,
                getAction(),
                islandId2,
                true
            )
        }.doOnError {
            log("sendDoubleSeq error $it")
        }.subscribe();
    }

    private fun nextRandom(): RxtTile {
        try {
            var id = getRandom().nextInt(0, tilesSize)
            log("nextTile $id : $lastSeq")
            if (id == lastSeq)
                id = getRandom().nextInt(0, tilesSize)
            lastSeq = id
            return island.next(id)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return island.next(0)
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
        log("getScore $total")
        val list = ArrayList(events) // ThreadModificationError
        if (total > 0) {
            for (i in list) {
                if (i.time > 0)
                    hit++
                else
                    miss++
            }
        }

        return ScoreItem(1, island.name, "$hit", "$miss", island.color, total, 60)
    }

    val debug = MiboApplication.DEBUG
    fun log(msg: String) {
        if (debug)
            Logger.e("IslandParser $islandId", "RXTTest - $msg")
    }

}