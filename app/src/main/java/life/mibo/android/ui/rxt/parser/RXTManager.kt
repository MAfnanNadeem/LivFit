package life.mibo.android.ui.rxt.parser

import android.graphics.Color
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.android.ui.rxt.parser.core.IslandParser
import life.mibo.android.ui.rxt.parser.core.RxtListener
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.RxtStatusEvent
import life.mibo.android.ui.rxt.score.ScoreItem
import java.util.concurrent.TimeUnit

class RXTManager {

    companion object {
        val REFLEX = 10

        @Volatile
        private var INSTANCE: RXTManager? = null
        const val DEBUG = true

        // @Volatile
        //private var receivedFocusAll = false

        fun getInstance(): RXTManager =
                INSTANCE
                        ?: synchronized(this) {
                            Logger.e("RXTManager INSTANCE init ")
                            INSTANCE = RXTManager()
                            INSTANCE!!
                        }
    }

    private var islandsMap = HashMap<Int, RxtIsland>()
    private var islandParsers = ArrayList<IslandParser>()

    private var disposable: Disposable? = null
    // todo startObserver - start
    // private var publisher: PublishSubject<RxlStatusEvent>? = null//;.create<RxlStatusEvent>()

    private var isStarted = false
    private var isRunning = false

    var isPaused = false
    var isResumed = false
    var pauseDuration = 0L
    var remainDuration = 0
    private var durationSec: Long = 0

    private var listener: RxtListener? = null


    fun testProgram(): RxtProgram {
        val block1 = RxtBlock(500, 30, 1)
        block1.round = 2
        block1.delay = 5

        val block2 = RxtBlock(150, 20, 1)
        block2.round = 1
        block2.delay = 3

        val block3 = RxtBlock(2000, 15, 1)
        block3.round = 3
        block3.delay = 7

        return RxtProgram("Test Program", Color.BLUE, 0, arrayListOf(block1, block2, block3))
    }


    private fun reset() {
        isStarted = false
        isRunning = false
        isPaused = false
        remainDuration = 0
        //colorSent = false
        //isFocus = false
        //receivedFocusAll = false
        //actionTime = 0
        //focusCount = 0
        //publisher?.unsubscribeOn(Schedulers.io())
        //publisher = null
    }

    fun with(list: List<RxtIsland>): RXTManager {
        islandParsers.clear()
        for (i in list) {
            islandParsers.add(IslandParser(i))
        }
        return this
    }

    fun with(list: List<RxtIsland>, listener: RxtListener): RXTManager {
        islandParsers.clear()
        for (i in list) {
            islandParsers.add(IslandParser(i))
        }
        this.listener = listener
        return this
    }

    fun startNow(duration: Long) {
        durationSec = duration
        if (durationSec > 0) {
            startProgram()
        }
    }

    private var speed: Float = 1.0f
    fun startNow(duration: Long, speed: Float) {
        durationSec = duration
        this.speed = speed
        if (durationSec > 0) {
            startProgram()
        }
    }

    private fun onNext(it: RxtStatusEvent?) {
        log("onEvent onNext : $it")
        for (i in islandParsers) {
            if (i.islandId == it?.data) {
                log("onNext ID matched ${i.islandId}")
                if (i.lastTile == it.tile || i.secondTile == it.tile)
                    i.onNext(it)
                return
            } else {
                log("onNext ID NOT matched ${i.islandId} == ${it?.data} ${i.lastTile} or ${i.secondTile} == ${it?.tile}")
            }
        }
    }

    private fun startProgram() {
        log("startProgram >>> $isStarted")
        if (isStarted)
            return
        isStarted = true;
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS).take(durationSec)
                .subscribeOn(Schedulers.io()).doOnNext {
                    onTick(it)
                }.doOnComplete {
                    onExerciseComplete()
                }.doOnSubscribe {
                    onExerciseStart()
                }.doOnError {
                    onExerciseError(it)
                }.subscribe()

    }


    private fun onExerciseStart() {
        log("....................onExerciseStart.....................")
//        Observable.fromIterable(islandParsers).doOnNext {
//            log("fromIterable islandParsers start ${it.id}")
//            it.onProgramStart()
//            log("fromIterable islandParsers end ")
//        }.doOnComplete {
//            log("fromIterable islandParsers complete ")
//        }.subscribe()
        for (i in islandParsers) {
            log("fromIterable islandParsers start ${i.islandId}")
            i.onProgramStart(speed)
            log("fromIterable islandParsers end ")
        }
        listener?.startProgram(0, 0)
    }

    private fun onExerciseComplete() {
        log("....................onExerciseComplete.....................")
        for (i in islandParsers) {
            i.onProgramEnd()
        }
        if (isStarted)
            listener?.endProgram(0, 0)
        isStarted = false;
    }

    private fun onTick(time: Long) {
        log("onTick $time.....................")
        listener?.onTime(0, time)
    }

    private fun onExerciseError(e: Throwable?) {
        log("....................onExerciseError..................... $e")
        e?.printStackTrace()
    }

//    private fun createBlock(id: Int, block: RxtBlock): Disposable {
//        log("createBlock $id, $block")
//        return Observable.interval(0, 1, TimeUnit.SECONDS).take(block.duration.toLong())
//                .subscribeOn(Schedulers.io()).doOnNext {
//                    log("createBlock onNext time $it : $id")
//                    listener?.onTime(id, it)
//                    // pauseDuration = it
//                }.doOnComplete {
//                    log("createBlock >> doOnComplete cycle $id")
//                    blockCompleted(block, id)
//                }.doOnSubscribe {
//                    log("createBlock >> doOnSubscribe cycle $id")
//                    blockStarted(block, id)
//                }.doOnError {
//                    log("createBlock >> doOnError cycle $id ${it.message}")
//                    it.printStackTrace()
//                }.subscribe()
//    }
//
//    private fun createBlock(id: Int, block: RxtBlock, delay: Long): Disposable {
//        log("createBlock $id, $block")
//        return Observable.interval(delay, 1, TimeUnit.SECONDS).take(block.duration.toLong())
//                .subscribeOn(Schedulers.io()).doOnNext {
//                    log("createBlock onNext time $it : $id")
//                    listener?.onTime(id, it)
//                    // pauseDuration = it
//                }.doOnComplete {
//                    log("createBlock >> doOnComplete cycle $id")
//                    blockCompleted(block, id)
//                }.doOnSubscribe {
//                    log("createBlock >> doOnSubscribe cycle $id")
//                    blockStarted(block, id)
//                }.doOnError {
//                    log("createBlock >> doOnError cycle $id ${it.message}")
//                    it.printStackTrace()
//                }.subscribe()
//    }
//
//    fun blockStarted(block: RxtBlock, id: Int) {
////        log("blockStarted $id, $block")
////        for (i in islands) {
////            if (i.id == id) {
////                i.isStarted = true
////                sendColor(i.nextTile(), i.color(), 500, id, false)
////            }
////        }
//    }
//
//    fun blockCompleted(block: RxtBlock, id: Int) {
//        //log("blockCompleted $id, $block")
//        //isInternalStarted = false
//    }


    // END

    private fun dispose() {
        disposable?.dispose()
    }

    fun register() {

    }

    fun unregister() {
        // EventBus.getDefault().unregister(this)
        dispose()
        onExerciseComplete()
        isRunning = false
        isStarted = false;

    }


    private fun log(msg: String?) {
        Logger.e("RXTTest - $msg")
    }

    @Synchronized
    fun postDirect(event: RxtStatusEvent) {
        log("postDirect onNext $isStarted: $event")
        if (isStarted)
            onNext(event)
    }

    fun getScore(): ArrayList<ScoreItem> {
        val list = ArrayList<ScoreItem>()
        for (i in islandParsers) {
            list.add(i.getScore())
        }
        return list;
    }

}