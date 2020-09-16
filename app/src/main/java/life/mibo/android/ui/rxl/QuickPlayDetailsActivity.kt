package life.mibo.android.ui.rxl

//import life.mibo.hexa.pods.rxl.RXLHelper
//import life.mibo.hexa.pods.rxl.RxlListener
//import life.mibo.hexa.pods.rxl.program.RxlLight
//import life.mibo.hexa.pods.rxl.program.RxlPlayer
//import life.mibo.hexa.pods.rxl.program.RxlProgram
//import life.mibo.hardware.rxl.RXLManager
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.SparseArray
import android.view.MenuItem
import android.view.View.*
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.util.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_quickplay_detail_play.*
import life.mibo.android.R
import life.mibo.android.core.API.Companion.request
import life.mibo.android.core.Prefs
import life.mibo.android.core.toIntOrZero
import life.mibo.android.events.NotifyEvent
import life.mibo.android.models.base.ResponseStatus
import life.mibo.android.models.program.Program
import life.mibo.android.models.workout.RXL
import life.mibo.android.models.workout.SaveMemberScores
import life.mibo.android.models.workout.SaveMemberScores.Score
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.main.MessageDialog
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.rxl.adapter.PlayersAdapter
import life.mibo.android.ui.rxl.adapter.RxlBlocksAdapter
import life.mibo.android.ui.rxl.adapter.ScoreAdapter
import life.mibo.android.ui.rxl.impl.CourseCreateImpl
import life.mibo.android.ui.rxl.impl.ReflexDialog
import life.mibo.android.ui.rxl.impl.ScoreDialog
import life.mibo.android.ui.select_program.ProgramDialog
import life.mibo.android.utils.Constants
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.ProximityEvent
import life.mibo.hardware.events.RxlBlinkEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceTypes
import life.mibo.hardware.rxl.RXLManager
import life.mibo.hardware.rxl.RxlListener
import life.mibo.hardware.rxl.program.RxlCycle
import life.mibo.hardware.rxl.program.RxlPlayer
import life.mibo.hardware.rxl.program.RxlProgram
import life.mibo.views.anim.AnimateView
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class QuickPlayDetailsActivity : BaseActivity(), RxlListener, CourseCreateImpl.Listener {

    private lateinit var delegate: CourseCreateImpl

    // private var program: life.mibo.android.models.rxl.RxlProgram? = null
    private var rxlProgramNew: RXL? = null
    private var isUser = false

    //private val rxlPlayers = ArrayList<RxlPlayer>()
    private val rxlPlayers = SparseArray<RxlPlayer>()
    private val userPods = ArrayList<Device>()
    private val selectedPlayers = ArrayList<PlayersAdapter.PlayerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.fragment_quickplay_detail_play)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }


        //program = intent?.getSerializableExtra(Constants.BUNDLE_DATA) as life.mibo.android.models.rxl.RxlProgram?
        rxlProgramNew = intent?.getSerializableExtra(Constants.BUNDLE_DATA) as RXL?
        if (rxlProgramNew == null) {
            finish()
            return
        }
        //isUser = !program?.memberId.isNullOrEmpty()
        isUser = false
        updateToolbar()
        createView()
        log("program >> $rxlProgramNew")
        //iv_icon.setFreezesAnimation()

        val i = intent?.getIntExtra("from_user_int", 5) ?: 1
        if (i != 10) {
            finish()
        }

    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun updateToolbar() {
        try {
            val appbar = findViewById<AppBarLayout?>(R.id.appBar)
            if (isUser) {
                //lp.height = resources.getDimension(R.dimen.toolbar_height);

                val heightDp = Utils.dpToPixel(56, this) + getStatusBarHeight()
                val lp =
                    appbar?.layoutParams as CoordinatorLayout.LayoutParams
                lp.height = heightDp.toInt()


                // appbar?.setExpanded(false, false)
                //appbar?.isEnabled = false
                //collapsingToolbar?.isEnabled = false
                giffVideoView?.visibility = GONE
                //simple_drawee_view?.visibility = View.GONE
                appbar?.setExpanded(false, false)
                // appbar?.setLiftable(false)
                nestedScrollView?.isNestedScrollingEnabled = false
                // disableCollapseBar()

                //val behavior = lp.behavior as AppBarLayout.Behavior?
                //behavior?.onNestedFling(coordinatorLayout, appbar, appbar, 0f, 10000f, true)
            } else {

                val heightDp = resources.displayMetrics.heightPixels.times(0.85f)
                val lp =
                    appbar?.layoutParams as CoordinatorLayout.LayoutParams
                lp.height = heightDp.toInt()
                // iv_icon_giff?.setImageResource(R.drawable.rxl_agility_test_1)
                try {
                    //loadGlide(program?.tutorial)
                    loadGlide(rxlProgramNew?.getLinks())
                } catch (e: java.lang.Exception) {
                }
            }
        } catch (e: Exception) {
            MiboEvent.log(e)
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        //if (rxlProgramNew?.lightLogic() == life.mibo.android.pods.rxl.program.RxlLight.SEQUENCE) {
        //   checkSequenceIndicators()
        //   }
    }

    private fun loadGlide(url: List<String>?) {
        log("loadGlide $url")
        val list = ArrayList<String>()
        if (url.isNullOrEmpty()) {
            list.add("")
        } else {
            url.forEach {
                list.add(it)
            }
        }

        giffVideoView.attach(this, list)
    }


    private fun createView() {
        delegate = CourseCreateImpl(this, this)

        //delegate.listener = this


        //createTestDevices()
        SessionManager.getInstance().userSession.isRxl = true
        setProgram()
        setPlayers()
        //navigate(Navigator.HOME_VIEW, true)

        btn_start_now?.setOnClickListener {
            printPods()
            //startNowTest()
            //testDialog()
            startNowClicked()
        }

//        btn_start_now?.setOnLongClickListener {
//            printPods()
//            startNowLongTest()
//            return@setOnLongClickListener true
//            // startNowClicked()
//        }

        btn_start_hit?.setOnClickListener {
            startHitClicked()
        }

        btn_stop?.setOnClickListener {
            canBack()
        }

        btn_pause?.setOnClickListener {
            if (isPaused)
                resumeProgram()
            else pauseProgram()
        }

//        switch_sensor?.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                tv_switch_sensor?.text = getString(R.string.proximity_on)
//                changeSensor(200)
//            } else {
//                tv_switch_sensor?.text = getString(R.string.proximity_off)
//                changeSensor(0)
//            }
//        }


//        iv_select_color?.setOnClickListener {
//            showColors()
//        }
        tv_customize?.setOnClickListener {
            //navigate(Navigator.RXL_COURSE_CREATE, program)
            if (isSessionActive)
                return@setOnClickListener
            setResult(3)
            finish()
        }
        //setPickers()
        //getPods()

        if ("proximity" == rxlProgramNew?.tapProximity?.toLowerCase()) {
            val i = getInt(rxlProgramNew?.proximityValue)
            log("changeSensor proximity $i")
            //if (i < 30)
            //changeSensor(i.times(10))
            //else
            changeSensor(i)
        } else {
            log("changeSensor tap " + rxlProgramNew?.proximityValue)
            changeSensor(0)
        }
        //setSlider()

        if (MiboApplication.DEBUG) {
//            tv_select_action?.setOnClickListener {
//                delegate.showDialog(CourseCreateImpl.Type.ACTION)
//            }
//
//            tv_select_pause?.setOnClickListener {
//                delegate.showDialog(CourseCreateImpl.Type.DELAY)
//            }
//
//            tv_customize?.setOnClickListener {
//                changeSensor(100)
//            }
        }
    }

    private fun startHitClicked() {
        //startUnitTest()
        checkStartCondition {
            log("checkStartCondition meet")
            startProgram(true, it)
        }
    }


    private fun startNowClicked() {

        //log("isRandom2 " + tv_select_lights.text?.toString()?.toLowerCase()?.contains("random"))
        //if (checkPlayersPods())
        checkStartCondition {
            startProgram(false, it)
        }

    }

    //Todo TEST
    private fun startNowTest() {

//        //log("isRandom2 " + tv_select_lights.text?.toString()?.toLowerCase()?.contains("random"))
//        //if (checkPlayersPods())
//        checkStartCondition {
//            RXLManager.getInstance().with(
//                RxlProgram.getExercise(
//                    getDuration(),
//                    getAction(),
//                    getPause(),
//                    getCycles(),
//                    0,
//                    getSequence(),
//                    rxlPlayers,
//                    getLightLogic()
//                )
//            ).withListener(this).start(false)
//        }

    }

    private fun startNowLongTest() {

        //changeSensor(100)
//        log("isRandom " + isRandom())
//        //log("isRandom2 " + tv_select_lights.text?.toString()?.toLowerCase()?.contains("random"))
//        //if (checkPlayersPods())
//        checkStartCondition {
//            RXLManager.getInstance().with(
//                RxlProgram.getExercise(
//                    getDuration(),
//                    getAction(),
//                    getPause(),
//                    getCycles(),
//                    0,
//                    getSequence(),
//                    rxlPlayers,
//                    getLightLogic()
//                )
//            ).withListener(this).start(false)
//        }

    }

    var currentProgram: RxlProgram? = null

    //val players = ArrayList<RxlPlayer>()
    private fun startProgram(tap: Boolean, devices: ArrayList<Device>) {
//        val uids = ArrayList<String>()
//        devices.forEach { d ->
//            uids.add(d.uid)
//        }

        //val players = SessionManager.getInstance().userSession.rxlPlayers
//        for (i in 0 until rxlPlayers.size()) {
//            //val key = rxlPlayers.keyAt(i)
//            val value: RxlPlayer? = rxlPlayers.valueAt(i)
//            log("printPods RxlPlayer $value")
//            value?.pods?.forEach {
//                log("RxlPlayer ${value.id} assigned ${it.uid}")
//            }
//        }


//        currentProgram = RxlProgram.getExercise(
//            getDuration(),
//            getAction(),
//            getPause(),
//            getCycles(),
//            0,
//            getSequence(),
//            rxlPlayers,
//            getLightLogic()
//        )
        currentProgram = getExercise()

        try {
            currentProgram?.let {
                timeTotalDuration = it.getDuration()
                RXLManager.getInstance().with(it).withListener(this).start(tap)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            // TODO check later, issue faced NetworkOnMainThreadException
            //life.mibo.hardware.network.TCPClient.sendMessage

            MiboEvent.log(e)
            Single.fromCallable {
                currentProgram?.let {
                    RXLManager.getInstance().with(it).withListener(this).start(tap)
                }
            }.subscribeOn(Schedulers.io()).doOnError {
                MiboEvent.log("startProgram fromCallable error: $e")
            }.subscribe()
        }


//        RXLManager.getInstance().with(
//            RxlProgram.getExercise(
//                getDuration(),
//                getAction(),
//                getPause(),
//                getCycles(),
//                0,
//                rxlPlayers,
//                getLightLogic()
//            )
//        ).addDevices(devices).withListener(this).start(tap)

        //if (tap)
        //   Toasty.info(context!!, "Tap Reaction Light to start").show()
    }

    fun getExercise(): RxlProgram {
        val program = RxlProgram()
        if (rxlProgramNew != null) {
            program.totalDuration = rxlProgramNew!!.duration()
            val blocks = rxlProgramNew!!.blocks
            if (blocks != null)
                for (b in blocks) {
                    if (b != null) {
                        program.addCycle(
                            RxlCycle(
                                b.getDuration(),
                                b.getAction(),
                                0,
                                b.getDelay(),
                                b.pattern,
                                RxlUtils.getLogicType(b.getLogicType()), b.getRounds()
                            )
                        )
                    }
                }
        }
        program.addPlayers(rxlPlayers)
        return program
    }

    private fun turnOffAll() {
        Observable.fromIterable(userPods).subscribeOn(Schedulers.io()).doOnNext {
            CommunicationManager.getInstance()
                .onChangeColorEvent(ChangeColorEvent(it, it.uid, 0, 0))
            Thread.sleep(10)
        }.doOnError {
            MiboEvent.log(it)
        }.doOnComplete {

        }.subscribe()
    }

    private fun checkSequenceIndicators() {
        val pods = getProgramConnectedPods()
        Observable.fromIterable(pods).subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Device> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Device) {
                    log("blinkPods RxlPlayer sent blink command ${t.uid}")
                    EventBus.getDefault()
                        .postSticky(RxlBlinkEvent(t.uid, 500, 500, 15, t.colorPalet))
                    //delay(20)
                    Thread.sleep(20)
                }

                override fun onError(e: Throwable) {
                    MiboEvent.log(e)
                }

            })
    }

    private fun setProgram() {
        toolbar?.title = rxlProgramNew?.name
        collapsingToolbar?.title = rxlProgramNew?.name
        if (isUser) {
            toolbar_collapsed_title?.text = rxlProgramNew?.name
            toolbar_collapsed_title?.visibility = VISIBLE
            collapsingToolbar?.invalidate()
            toolbar_collapsed_title?.invalidate()
        }
//        program?.let {
//            //tv_select_stations?.text = "${it.workingStations}"
//            tv_select_cycles?.text = "${it.cycle}"
//            tv_select_duration?.text = "${it.totalDuration} sec"
//            tv_select_pause?.text = "${it.pause} sec"
//            //tv_select_delay?.text = "0 sec"
//            //tv_select_lights?.text = "${it.lightsLogic}"
//            tv_select_action.text = "${it.action} sec"
//            //tv_select_pause?.text = "${it.actionDuration} sec"
//            //tv_select_players?.text = "${it.numberOfPlayers}"
//            //tv_desc?.text = "${it.description}"
//            //val images = it.image!!.split(",")
//            //images[0].replace("[", "")
//            //images[images.size - 1].replace("]", "")
//
//        }

        rxlProgramNew?.let {
//            tv_select_cycles?.text = "0"
//            tv_select_duration?.text = "0 sec"
//            tv_select_pause?.text = "0 sec"
//            tv_select_action.text = "0 sec"
            tv_cycle_timer
            setAdapters(it.blocks)

        }


    }

    fun setAdapters(blocks: List<RXL.RXLBlock?>?) {
        if(blocks == null)
            return

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = RxlBlocksAdapter(ArrayList(blocks))
    }


    var playersCount = 0

    @Synchronized
    private fun setPlayers() {
        log("setPlayers ")
        rxlPlayers.clear()
        userPods.clear()
        selectedPlayers.clear()

        val list = SessionManager.getInstance().userSession.devices
        val colors = Utils.getColors();
        val players = SessionManager.getInstance().userSession.rxlPlayers
        var playerSize = 0
        if (players is List<*>)
            playerSize = players.size
        // log("setPlayers > program?.pods ${program?.pods}  connected ${list.size}")
        // userDevices.addAll(SessionManager.getInstance().userSession.devices)
        log("setPlayers playerSize $playerSize")

        if (playerSize == 1) {
            if (rxlProgramNew?.pods() == list.size) {
                log("setPlayers pods match with program")
                list.forEachIndexed { i, it ->
                    if (it.isPod) {
                        it.colorPalet = colors[i].id!!
                        userPods.add(it)
                    }
                }
            } else {
                log("setPlayers pods NOT match with program")
                if (list.size > rxlProgramNew?.pods()!!) {
                    for (i in 0 until rxlProgramNew?.pods()!!) {
                        list[i].let {
                            if (it.isPod) {
                                it.colorPalet = colors[i].id!!
                                userPods.add(it)
                            }
                        }
                    }
                } else if (list.size > 0) {
                    log("setPlayers pods NOT match, adding all pods")
                    list.forEachIndexed { i, it ->
                        if (it.isPod) {
                            it.colorPalet = colors[i].id!!
                            userPods.add(it)
                        }
                    }
                }

            }
        } else {
            log("setPlayers multi players")
            if (list.size > 0) {
                list.forEachIndexed { i, it ->
                    if (it.isPod) {
                        it.colorPalet = colors[i].id!!
                        userPods.add(it)
                    }
                }
            }
        }

        // log("setPlayers > user?.pods ${userPods?.size}")
//        if (list.size > 0) {
//            list.forEachIndexed { i, it ->
//                if (it.isPod) {
//                    it.colorPalet = colors[i].id!!
//                    userPods.add(it)
//                }
//            }
//        }
        //TODO test
        if (isTest && userPods.size == 0) {
            userPods.clear()
            userPods.add(getTestDevice(11))
            userPods.add(getTestDevice(12))
            userPods.add(getTestDevice(13))
            userPods.add(getTestDevice(14))
            userPods.add(getTestDevice(15))
            userPods.add(getTestDevice(16))
            userPods.add(getTestDevice(17))
            userPods.add(getTestDevice(18))
            userPods.add(getTestDevice(19))
            userPods.add(getTestDevice(20))
            log("createTestDevices created...... ${SessionManager.getInstance().userSession.devices.size}")
        }


        log("createView players >> $players")
        if (players is List<*>) {
            log("setPlayers players >>> ${players.size}")
            players.forEach {
                if (it is PlayersAdapter.PlayerItem) {
                    selectedPlayers.add(it)
                }
            }
            playersCount = selectedPlayers.size
            log("createView selectedPlayers ${selectedPlayers.size}, rxlPlayers ${players.size}")

            log("setPlayers >> playersCount $playersCount")
            if (playersCount > 0) {

                //val pods = getPods()
                val total = userPods.size

                val podsSize = total.div(playersCount)
                groupPlayer1.visibility = GONE
                groupPlayer2.visibility = GONE
                groupPlayer3.visibility = GONE
                groupPlayer4.visibility = GONE


                selectedPlayers?.forEach { player ->
                    setPlayer(player, player.id, podsSize, total)

                }

                Single.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        updateBlink()
                    }.subscribe()
            }
        }

        log("setPlayers finished...... >> $players")
//        if (players is List<*>) {
//            log("setPlayers >>> ${players.size}")
//            val list = ArrayList<PlayersAdapter.PlayerItem>()
//            for (p in players) {
//                list.add(p as PlayersAdapter.PlayerItem)
//            }
//            playersCount = list.size
//            log("setPlayers >> playersCount $playersCount")
//            if (playersCount > 0) {
//
//                val pods = getPods()
//
//                val podsSize = pods.div(playersCount)
//                groupPlayer1.visibility = GONE
//                groupPlayer2.visibility = GONE
//                groupPlayer3.visibility = GONE
//                groupPlayer4.visibility = GONE
//
//
//                list?.forEachIndexed { index, player ->
//                    setPlayer(player, index, podsSize, pods)
//
//                }
//            }
//        }
    }

    private fun setPlayer(player: PlayersAdapter.PlayerItem?, type: Int, pods: Int, max: Int) {
        log("setPlayer >>> $type  , $pods")
        player?.let {
            when (type) {
                1 -> {
                    groupPlayer1.visibility = VISIBLE
                    players_1_color?.circleColor = it.playerColor
                    players_1_name.text = it.playerName
                    if (playersCount > 1) {
                        players_1_pods?.setOnClickListener {
                            if (isSessionActive)
                                return@setOnClickListener
                            delegate.showPlayers(CourseCreateImpl.Type.PLAYER_1, max)
                        }
                    } else {
                        // players_one_pods?.setCompoundDrawables(null, null, null, null)

                    }
                    players_1_light?.setColorFilter(
                        Color.GRAY,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    players_1_light?.isEnabled = false
                    players_1_light?.setOnClickListener {
                        blinkPods(1)
                    }
                    players_1_pods.text = "$pods"
                    //players_1_pods.text = "$max"
                    //playersCount = type
                    selectedColor = player.playerColor
                    selectedColorId = player.playerColorId
                    //selectedColorId
                }
                2 -> {
                    groupPlayer2.visibility = VISIBLE
                    players_2_color?.circleColor = it.playerColor
                    players_2_name.text = it.playerName
                    players_2_pods?.setOnClickListener {
                        if (isSessionActive)
                            return@setOnClickListener
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_2, max)
                    }
                    players_2_pods.text = "$pods"
                    //players_2_pods.text = "0"
                    //playersCount = type
                    players_2_light?.setColorFilter(
                        Color.GRAY,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    players_2_light?.isEnabled = false
                    players_2_light?.setOnClickListener {
                        blinkPods(2)
                    }

                }
                3 -> {
                    groupPlayer3.visibility = VISIBLE
                    players_3_color?.circleColor = it.playerColor
                    players_3_name.text = it.playerName
                    players_3_pods?.setOnClickListener {
                        if (isSessionActive)
                            return@setOnClickListener
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_3, max)
                    }
                    players_3_pods.text = "$pods"
                    // players_3_pods.text = "0"
                    //playersCount = type
                    players_3_light?.setColorFilter(
                        Color.GRAY,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    players_3_light?.isEnabled = false
                    players_3_light?.setOnClickListener {
                        blinkPods(3)
                    }

                }
                4 -> {
                    groupPlayer4.visibility = VISIBLE
                    players_4_color?.circleColor = it.playerColor
                    players_4_name.text = it.playerName
                    players_4_pods?.setOnClickListener {
                        if (isSessionActive)
                            return@setOnClickListener
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_4, max)
                    }
                    players_4_pods.text = "$pods"
                    //players_4_pods.text = "0"
                    // playersCount = type
                    players_4_light?.setColorFilter(
                        Color.GRAY,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    players_4_light?.isEnabled = false
                    players_4_light?.setOnClickListener {
                        blinkPods(4)
                    }

                }
                else -> {

                }
            }
        }
    }

    private fun blinkPods(playerId: Int) {
        log("blinkPods $playerId")
        if (isSessionActive)
            return

        var player: RxlPlayer? = null

        for (i in 0 until rxlPlayers.size()) {
            val value: RxlPlayer? = rxlPlayers.valueAt(i)
            log("blinkPods RxlPlayer $value")
            if (value?.id == playerId) {
                player = value
                break
            }
        }
        log("blinkPods >>>> $player")
        player?.pods?.let {
            if (it.size > 0) {
                Observable.fromIterable(it).subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<Device> {
                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: Device) {
                            log("blinkPods RxlPlayer sent blink command ${t.uid}")
                            EventBus.getDefault()
                                .postSticky(RxlBlinkEvent(t.uid, 200, 200, 3, player.color))
                            //delay(20)
                            Thread.sleep(20)
                        }

                        override fun onError(e: Throwable) {

                        }

                    })
            }
        }
    }

    @Synchronized
    private fun updateBlink() {
        updateBlinkImages(getAssignedPods())
    }

    private fun updateBlinkImages(assigned: Int) {
        val enable = updatePlayers(userPods, assigned)

        log("updateBlink >> enable $enable size ${rxlPlayers.size}")
        for (i in 0 until rxlPlayers.size()) {
            //val key = rxlPlayers.keyAt(i)
            val value: RxlPlayer? = rxlPlayers.valueAt(i)
            log("updateBlink >> enable $enable value: RxlPlayer?  $value")
            value?.let {
                when (it.id) {
                    1 -> {
                        players_1_light?.isEnabled = enable
                        if (enable) {
                            players_1_light?.setColorFilter(
                                it.color,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        } else {
                            players_1_light?.setColorFilter(
                                Color.GRAY,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    2 -> {
                        players_2_light?.isEnabled = enable
                        if (enable) {
                            players_2_light?.setColorFilter(
                                it.color,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        } else {
                            players_2_light?.setColorFilter(
                                Color.GRAY,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    3 -> {
                        players_3_light?.isEnabled = enable
                        if (enable) {
                            players_3_light?.setColorFilter(
                                it.color,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        } else {
                            players_3_light?.setColorFilter(
                                Color.GRAY,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    4 -> {
                        players_4_light?.isEnabled = enable
                        if (enable) {
                            players_4_light?.setColorFilter(
                                it.color,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        } else {
                            players_4_light?.setColorFilter(
                                Color.GRAY,
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    else -> {
                        log("updateBlink else.......... $it")
                        return
                    }
                }
            }

        }


    }

    //TODO Delete
    fun blinkOld(enable: Boolean) {
        if (rxlPlayers.size > 0) {
            players_1_light?.isEnabled = enable
            if (enable) {
                players_1_light?.setColorFilter(
                    rxlPlayers[0].color,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                players_1_light?.setColorFilter(
                    Color.GRAY,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

        if (rxlPlayers.size > 1) {
            players_2_light?.isEnabled = enable
            val player: RxlPlayer? = rxlPlayers.get(2, null)
            player?.let {

            }
            if (enable) {
                players_2_light?.setColorFilter(
                    rxlPlayers[1].color,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                players_2_light?.setColorFilter(
                    Color.GRAY,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

        if (rxlPlayers.size > 2) {
            players_3_light?.isEnabled = enable
            val player: RxlPlayer? = rxlPlayers.get(2, null)
            player?.let {

            }

            if (enable) {
                players_3_light?.setColorFilter(
                    rxlPlayers[2].color,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                players_3_light?.setColorFilter(
                    Color.GRAY,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

        if (rxlPlayers.size > 3) {
            players_4_light?.isEnabled = enable
            if (enable) {
                players_4_light?.setColorFilter(
                    rxlPlayers[3].color,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                players_4_light?.setColorFilter(
                    Color.GRAY,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun printPods() {
        try {
            log("printPods start >>>>>>>>>>>>>>>")
            for (i in 0 until rxlPlayers.size()) {
                //val key = rxlPlayers.keyAt(i)
                val value: RxlPlayer? = rxlPlayers.valueAt(i)
                log("printPods RxlPlayer $value")
                value?.pods?.forEach {
                    log("RxlPlayer ${value.id} assigned ${it.uid}")
                }
            }
            log("printPods end >>>>>>>>>>>>>>>")
        } catch (e: Exception) {
        }
    }

    // assigning pods as FIFO
    private fun updatePlayers(connected: ArrayList<Device>, assigned: Int): Boolean {
        if (connected.size == assigned && connected.size > 0) {
            log("updatePlayers players >>> ${selectedPlayers.size}")

            if (selectedPlayers.size > 0) {
                if (selectedPlayers.size == 1) {
                    val pl = selectedPlayers[0]
                    rxlPlayers.put(
                        pl.id,
                        RxlPlayer(
                            pl.id, pl.playerName, pl.playerColor,
                            pl.playerColorId, connected.size, connected
                        )
                    )
                } else {
                    var from = 0
                    var to: Int
                    selectedPlayers.forEach {
                        to = getPlayerPods(it.id)
                        val cp = createPlayer(it, connected, from, to)
                        if (cp != null)
                            rxlPlayers.put(it.id, cp)
                        from = from.plus(to)
                    }
                }
                log("createPlayers Player assigned :: playersMap ${rxlPlayers.size}, rxlPlayers ${selectedPlayers.size}")
                log("createPlayers Player assigned : playersMap: $rxlPlayers")
                return true
            }

        }
        return false
    }

    private fun createPlayer(
        player: PlayersAdapter.PlayerItem, pods: ArrayList<Device>, from: Int, to: Int
    ): RxlPlayer? {

        log("createPlayer player $player, pods.size ${pods.size}, from $from, to $to")
        //var to = getPlayerPods(player.id)
        log("createPlayer : to $to")
        val end = to.plus(from)
        log("createPlayer : end $end")
        if (end <= pods.size && end > from) {
            val list = ArrayList<Device>(pods.size)
            pods.forEachIndexed { index, device ->
                // if(index > to)
                //     break
                if (index in from until end) {
                    list.add(device)
                }


            }
            log("createPlayer : user devices size = ${list.size} : from $from, to $end")
            return RxlPlayer(
                player.id, player.playerName, player.playerColor,
                player.playerColorId, list.size, list
            )
            //for (i in from..to) {
            //  list.add(pods[i])
            //}

        }

        log("createPlayer : something went wrong, return NULL from $from, to $to size ${pods.size}")

        return null
    }

    // Todo delete
    fun createPlayers(): Boolean {

        //val playersMap = SparseArray<RxlPlayer>()
        val playersMap = ArrayList<RxlPlayer?>()
        //val totalPods = ArrayList<String>()
        val pods = ArrayList<Device>()

        if (userPods.size > 0) {
            userPods.forEach {
                if (it.isPod) {
                    pods.add(it)
                }
            }
        }

        var assigned = 0

        if (pods.size > 0) {

            when (playersCount) {
                1 -> {
                    assigned = getInt(players_1_pods.text)
                }
                2 -> {
                    assigned = getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                }
                3 -> {
                    assigned = getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                        .plus(getInt(players_3_pods.text))
                }
                4 -> {
                    assigned = getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                        .plus(getInt(players_3_pods.text)).plus(getInt(players_4_pods.text))
                }
            }


            if (pods.size == assigned) {

                val rxlPlayers = SessionManager.getInstance().userSession.rxlPlayers
                log("players >> $rxlPlayers")
                if (rxlPlayers is List<*>) {
                    log("create players >>> ${rxlPlayers.size}")
                    var from = 0
                    var to = 0
                    rxlPlayers.forEach {
                        if (it is PlayersAdapter.PlayerItem) {
                            // playersMap.put(it.id, createPlayer(it, pods, from))
                            to = getPlayerPods(it.id)
                            playersMap.add(createPlayer(it, pods, from, to))
                            from = to

                        }
                    }
                }

            } else {
                Toasty.error(
                    this,
                    Html.fromHtml(String.format(getString(R.string.assigned_pods), pods, assigned))
                ).show()
            }

        }

        // rxlPlayers.add(createRxlPlayer(player))

        return false
    }

    // Todo delete
    private fun createRxlPlayer(
        player: PlayersAdapter.PlayerItem?,
        type: Int,
        pods: Int,
        max: Int
    ) {
        log("setPlayer >>> $type  , $pods")
        player?.let {
            when (type.plus(1)) {
                1 -> {
                    groupPlayer1.visibility = VISIBLE
                    players_1_color?.circleColor = it.playerColor
                    players_1_name.text = it.playerName
                    if (playersCount > 1) {
                        players_1_pods?.setOnClickListener {
                            delegate.showPlayers(CourseCreateImpl.Type.PLAYER_1, max)
                        }
                    } else {
                        // players_one_pods?.setCompoundDrawables(null, null, null, null)

                    }
                    //players_one_pods.text = "$pods"
                    players_1_pods.text = "$max"
                    //playersCount = type
                    selectedColor = player.playerColor
                    selectedColorId = player.playerColorId
                    //selectedColorId
                }
                2 -> {
                    groupPlayer2.visibility = VISIBLE
                    players_2_color?.circleColor = it.playerColor
                    players_2_name.text = it.playerName
                    players_2_pods?.setOnClickListener {
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_2, max)
                    }
                    //players_2_pods.text = "$pods"
                    players_2_pods.text = "0"
                    //playersCount = type

                }
                3 -> {
                    groupPlayer3.visibility = VISIBLE
                    players_3_color?.circleColor = it.playerColor
                    players_3_name.text = it.playerName
                    players_3_pods?.setOnClickListener {
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_3, max)
                    }
                    //players_3_pods.text = "$pods"
                    players_3_pods.text = "0"
                    //playersCount = type
                }
                4 -> {
                    groupPlayer4.visibility = VISIBLE
                    players_4_color?.circleColor = it.playerColor
                    players_4_name.text = it.playerName
                    players_4_pods?.setOnClickListener {
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_4, max)
                    }
                    //players_4_pods.text = "$pods"
                    players_4_pods.text = "0"
                    // playersCount = type
                }
                else -> {

                }
            }
        }
    }

    private fun setSlider() {
        val list = arrayListOf(
            R.drawable.ic_reflex_random_icon,
            R.drawable.ic_reflex_sequence,
            R.drawable.ic_reflex_focus_only
        )
        // iv_icon.setImages(list)
    }

    private fun setPickers() {
//        tv_select_cycles?.setOnClickListener {
//            if (isSessionActive)
//                return@setOnClickListener
//            delegate.showDialog(CourseCreateImpl.Type.CYCLES)
//        }
//
//        tv_select_duration?.setOnClickListener {
//            if (isSessionActive)
//                return@setOnClickListener
//            delegate.showDialog(CourseCreateImpl.Type.DURATION)
//        }

    }

    private fun changeSensor(value: Int) {
        log("changeSensor $value")
        if (userPods.size > 0) {
            Observable.fromIterable(userPods)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Device> {

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Device) {
                        try {
                            if (t.isPod) {
                                EventBus.getDefault().postSticky(ProximityEvent(t.uid, value))
                                Thread.sleep(50)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onError(e: Throwable) {
                        log("ProximityEvent onError" + e.message)
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        if (value > 0) {
                            Snackbar.make(
                                nestedScrollView,
                                R.string.sensor_enable,
                                Snackbar.LENGTH_SHORT
                            ).setAction(R.string.close, null).show()
                        }
//                        Toasty.info(
//                            this@ReflexDetailsFragment.context!!,
//                            "Proximity " + if (value > 0) "Enabled" else "Disabled"
//                        ).show()
                    }
                })
        }
    }


    var colorDialog: ProgramDialog? = null
    var selectedColor: Int = Color.GREEN
    var selectedColorId: Int = 1
    private fun showColors() {
        var isDialog = false
        colorDialog?.let {
            isDialog = true
            it.showColors()
            return
        }
        if (isDialog)
            return

        colorDialog = ProgramDialog(this, ArrayList(), object : ItemClickListener<Program> {

            override fun onItemClicked(item: Program?, position: Int) {
                //Toasty.info(context!!, "$position").show()
                log("ProgramDialog Colors color = ${item?.id}  position $position")

                item?.id?.let {
                    // iv_select_color?.visibility = View.VISIBLE
                    //iv_select_color?.circleColor = it
                    selectedColor = it
                    selectedColorId = position
                }
            }

        }, ProgramDialog.COLORS)

        colorDialog?.showColors()
    }

    var isTest = false

    private fun getTestDevice(id: Int) =
        Device("$id", "name $id", "${id.times(id)}", DeviceTypes.RXT_WIFI)

    private fun createTestDevices() {
        log("createTestDevices ...... $isTest")
        if (isTest) {
            userPods.clear()
            userPods.add(getTestDevice(1))
            userPods.add(getTestDevice(12))
            userPods.add(getTestDevice(13))
            userPods.add(getTestDevice(14))
            userPods.add(getTestDevice(15))
            userPods.add(getTestDevice(16))
            userPods.add(getTestDevice(17))
            userPods.add(getTestDevice(18))
            userPods.add(getTestDevice(19))
            userPods.add(getTestDevice(21))
            log("createTestDevices created...... ${SessionManager.getInstance().userSession.devices.size}")
        }
    }

    private fun getPods(): Int {
        //tv_desc?.text = ""
//        val list = SessionManager.getInstance().userSession.devices
//        if (list.size > 0) {
//            val pods = ArrayList<Device>()
//            list.forEach {
//                if (it.isPod) {
//                    pods.add(it)
//                }
//            }
//            tv_select_pods?.text = "${pods.size}"
//        } else {
//            tv_select_pods?.text = "0"
//        }


        val list = SessionManager.getInstance().userSession.devices
        if (list.size > 0) {
            val pods = ArrayList<Device>()
            list.forEach {
                if (it.isPod) {
                    pods.add(it)
                }
            }

            return pods.size
        }

        return 0
    }

    private fun getProgramConnectedPods(): ArrayList<Device> {
        return userPods
    }

    private fun checkPlayersPods(): Boolean {
        //val pods = getPods()

        val size = getAssignedPods()

        //createPlayers(pods, size)
        if (userPods.size == size) {
            tv_required_pods?.visibility = GONE
            return true
        }
        tv_required_pods?.visibility = VISIBLE
        tv_required_pods?.text =
            Html.fromHtml(String.format(getString(R.string.assigned_pods), userPods.size, size))
//        MessageDialog.info(
//            this, "RXL Requirement",
//            "Devices are mis-configured, total available devices are $pods and assigned devices to players are $size "
//        )

        return false
    }

    // Todo test, disable in production
    private fun startUnitTest(devices: ArrayList<Device>) {
//        RXLManager.getInstance().withListener(this).startTest(
//            RxlProgram.getExercise(
//                getDuration(), getAction(),
//                getPause(), getCycles(),
//                selectedColor, selectedColorId,
//                ArrayList<String>(), getLightLogic()
//            )
//        )
    }

    private var size = 1
    private fun checkStartCondition(action: (ArrayList<Device>) -> Unit) {
        log("checkStartCondition")
        //val list = SessionManager.getInstance().userSession.devices
        turnOffAll()
//        if (userPods.size < size) {
//            MessageDialog.info(this, "RXL Requirement", getString(R.string.three_pods_required))
//            return
//        }
//        val pods = ArrayList<Device>()
//        list.forEach {
//            if (it.isPod) {
//                pods.add(it)
//            }
//        }

        if (userPods.size >= size) {
            if (isValidLogic()) {
                if (checkPlayersPods()) {
                    action.invoke(userPods)
                } else {
                    log("checkPlayersPods false ")
                    if (MiboApplication.DEBUG) {
                        Toasty.info(this, " Pods mis configured DEBUG MODE").show()
                        action.invoke(userPods)
                    }
                }
//                if (userPods.size != getProgramPods())
//                    Toasty.info(
//                        this,
//                        getString(R.string.exercise_designed) + getProgramPods() + getString(R.string.connected_error) + userPods.size,
//                        Toasty.LENGTH_LONG
//                    ).show()
            } else {
                Toasty.info(this, getString(R.string.logic_not_supported)).show()
                log("logic_not_supported ")
//                MessageDialog.info(
//                    this, "RXL Requirement",
//                    "Selected light logic is currently not supported \n\n Choose Random or Sequence"
//                )
            }
            //RXLManager.getInstance().with(PodExercise.getExercise1()).addDevices(pods).start()
//            RXLManager.getInstance()
//                .with(PodExercise.getExercise(getDuration(), 3, getPause(), getCycles()))
//                .addDevices(pods).sendColor(null)
            return
        } else {
            log("userPods.size < size ${userPods.size} : $size")
//            this.let {
//                MessageDialog.info(
//                    it,
//                    getString(R.string.rxl_title),
//                    getString(R.string.three_pods_required)
//                )
//            }

        }
    }

    private fun isValidLogic(): Boolean {
        return true
//        return when (rxlProgramNew?.type) {
//            1 -> {
//                true
//            }
//            2 -> {
//                true
//            }
//            3 -> {
//                true
//            }
//            4 -> {
//                true
//            }
//            5 -> {
//                true
//            }
//            else -> {
//                false
//            }
//        }
        //    getLightLogic() == RxlLight.SEQUENCE || getLightLogic() == RxlLight.RANDOM || getLightLogic() == RxlLight.FOCUS || getLightLogic() == RxlLight.ALL_AT_ONCE
    }

    override fun onDialogItemSelected(item: ReflexDialog.Item, type: Int) {
        log("onDialogItemSelected $type $item")
        when (type) {
            CourseCreateImpl.Type.STATIONS.type -> {
                // tv_select_stations?.text = item.title
            }
            CourseCreateImpl.Type.CYCLES.type -> {
                //tv_select_cycles?.text = "${item.title}"
            }
            CourseCreateImpl.Type.PODS.type -> {
                // tv_select_pods?.text = item.title
            }
            CourseCreateImpl.Type.ACTION.type -> {
                // tv_select_pods?.text = item.title
               // tv_select_action?.text = item.title
            }

            CourseCreateImpl.Type.DELAY.type -> {
               // tv_select_pause?.text = item.title
//                if (item.title?.startsWith("No Delay"))
//                    tv_select_delay?.text = "0 sec"
//                else tv_select_delay?.text = item.title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.DURATION.type -> {
               // tv_select_duration?.text = item.title?.replace("seconds", "sec")
            }


            CourseCreateImpl.Type.PLAYER_1.type -> {
                players_1_pods.text = "${item.title}"
                updateBlink()
            }
            CourseCreateImpl.Type.PLAYER_2.type -> {
                players_2_pods.text = "${item.title}"
                updateBlink()
            }
            CourseCreateImpl.Type.PLAYER_3.type -> {
                players_3_pods.text = "${item.title}"
                updateBlink()
            }
            CourseCreateImpl.Type.PLAYER_4.type -> {
                players_4_pods.text = "${item.title}"
                updateBlink()
            }
        }
    }


    fun getInt(string: CharSequence?): Int {
        return try {
            string?.replace(Regex("\\D+"), "")!!.toIntOrZero()
        } catch (e: Exception) {
            0
        }
    }

    private fun getAssignedPods(): Int {
        return try {
            return when (playersCount) {
                1 -> {
                    getInt(players_1_pods.text)
                }
                2 -> {
                    getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                }
                3 -> {
                    getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                        .plus(getInt(players_3_pods.text))
                }
                4 -> {
                    getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                        .plus(getInt(players_3_pods.text)).plus(getInt(players_4_pods.text))
                }
                else -> {
                    0
                }
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun getPlayerPods(player: Int): Int {
        log("getPlayerPods: player $player")
        return when (player) {
            1 -> {
                getInt(players_1_pods.text)
            }
            2 -> {
                getInt(players_2_pods.text)
            }
            3 -> {
                getInt(players_3_pods.text)
            }
            4 -> {
                getInt(players_4_pods.text)
            }
            else -> {
                0
            }
        }
    }

    val builder = StringBuilder("")

    //@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(notify: NotifyEvent) {
        log("onEvent $notify")
        val data = notify.data
        if (data is String) {
            builder.append("\n")
            builder.append(data)
            //tv_desc.text = builder
        }
        //if(notify.id == RXLManager.REFLEX.plus(1))
        //   builder.clear()
    }

    override fun onStart() {
        super.onStart()
        //register(this)
        RXLManager.getInstance().register()
        //RXLManager.getInstance().register()
        // EventBus.getDefault().register(this)
    }

    override fun onStop() {
        turnOffAll()
        cancelTimer()
        //unregister(this)
        //RXLManager.getInstance().unregister()
        //EventBus.getDefault().unregister(this)
        giffVideoView?.onDestroy()
        super.onStop()
    }

    override fun onDestroy() {
        RXLManager.getInstance().unregister()
        //RXLManager.getInstance().unregister()
        super.onDestroy()
    }

    // @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onEvent(event: RxlStatusEvent) {
        log("RxlStatusEvent $event")
        //RXLManager.getInstance().onEvent(event)
        //observe()
    }

    var disposable: Disposable? = null

    private fun endProgram(user: Boolean) {
        try {
            //RXLManager.getInstance().stopProgram()
            RXLManager.getInstance().stopProgram()
        } catch (e: java.lang.Exception) {
            MiboEvent.log("Error occurred while stopping RXL Program user=$user" + e?.message)
            MiboEvent.log(e)
        }
    }

    override fun onExerciseStart() {
        log("onExerciseStart")
    }

    override fun onExerciseEnd() {
        log("onExerciseEnd")
        //progress(0, 100, 0, 2)
        isSessionActive = false
        runOnUiThread {
            constraint_bottom_stop?.visibility = INVISIBLE
            constraint_bottom?.visibility = VISIBLE
            btn_start_now?.isEnabled = true
            btn_start_hit?.isEnabled = true
            //tv_desc?.text = "Completed..."
            val list = ArrayList<RxlPlayer>()
            RXLManager.getInstance().getPlayers()?.forEach {
                list.add(it)
            }
            showScoreDialog(list)
            saveScore(list)
//            MessageDialog.info(
//                this,
//                "Completed",
//                "Exercise finished " + RXLHelper.getInstance().getScore()
//            )
            cancelTimer()
            progressBar!!.visibility = GONE
            tv_cycle_heading?.visibility = INVISIBLE
            tv_cycle_count?.text = ""
            tv_cycle_timer?.text = ""
        }

//        Single.just("").delay(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
//            .doOnSuccess {
//                progressBar!!.visibility = View.GONE
//            }.subscribe()

    }

    override fun onCycleStart(cycle: Int, duration: Int) {
        log("onCycleStart")
        isSessionActive = true
        runOnUiThread {
            constraint_bottom_stop?.visibility = VISIBLE
            constraint_bottom?.visibility = INVISIBLE
            // btn_pause?.isEnabled = false
            //btn_start_now?.isEnabled = false
            //btn_start_hit?.isEnabled = false
            AnimateView.addAnimTo(btn_pause).setScaleForPopOutAnim(0f, 0f)
            btn_pause?.isEnabled = true
            isPaused = false
            //tv_cycle_heading?.visibility = VISIBLE
            //tv_cycle_count?.text = "$cycle"
            btn_pause?.setText(R.string.pause)
            btn_pause?.background = null
            btn_pause?.setBackgroundResource(R.drawable.bg_button_reflex_red)
            progressBar?.visibility = INVISIBLE
            startTimer(duration)
        }

        // progress(0, 100, duration.times(1000), 1)
    }

    override fun onCyclePaused(cycle: Int, time: Int) {
        log("onCyclePaused")
        runOnUiThread {
            btn_pause?.isEnabled = false
        }
        progress(0, 100, time, 2)
    }

    override fun onCycleResumed(cycle: Int) {
        // not calling
        log("onCycleResumed")
    }

    override fun onCycleEnd(cycle: Int) {

        log("onCycleEnd")
    }

    override fun onPod(podId: Int, time: Int) {
        log("onPod")
    }

    override fun onTapColorSent(playerId: Int) {
        Toasty.info(this, getString(R.string.hit_to_start_rxl)).show()
    }

    override fun onExerciseResumed(cycle: Int, totalTime: Int, remaining: Int) {
        log("onExerciseResumed ")
        runOnUiThread {
            btn_pause?.setText(R.string.pause)
            btn_pause?.background = null
            btn_pause?.setBackgroundResource(R.drawable.bg_button_reflex_red)
        }
        runOnUiThread {
            resumeTimer(remaining)
        }
        //animator?.resume()
//        val i = lastAnimator2
//        if (i is Int) {
//            log("onExerciseResumed lastAnimator2 is Int... $i :: lastAnimator $lastAnimator  lastAnimator2 $lastAnimator2 ")
//            progress(i, 100, remaining.times(1000), 1)
//        } else {
//            val p = remaining.div(getDuration().toFloat()).times(100f)
//            log("onExerciseResumed percent $p :: lastAnimator $lastAnimator  lastAnimator2 $lastAnimator2 ")
//            progress(100 - p.toInt(), 100, remaining.times(1000), 1)
//        }
    }

    //var lastAnimator = 0f
    // var lastAnimator2: Any? = null
    override fun onExercisePaused(cycle: Int, totalTime: Int, remaining: Int) {
        log("onExercisePaused ")
        runOnUiThread {
            btn_pause?.setText(R.string.resume)
            btn_pause?.background = null
            btn_pause?.setBackgroundResource(R.drawable.bg_button_reflex_green)
            //lastAnimator = animator?.animatedFraction ?: 0f
            // lastAnimator2 = animator?.animatedValue
            //animator?.cancel()
            pauseTimer(remaining)
        }
    }

    override fun onBlockStart(block: Int, cycle: Int) {
        runOnUiThread {
            tv_cycle_heading?.visibility = VISIBLE
            tv_cycle_heading?.text = "Block ${block.plus(1)} - Cycle $cycle"
            //tv_cycle_count?.text = "Cycle $cycle"

        }
    }

    override fun onBlockEnd(block: Int, cycle: Int) {

    }

    private var isTimer = true;
    private var countTimer: CountDownTimer? = null

    private fun startTimer(time: Int) {
        log("startTimer $time")
        startCountDown(time)
    }

    private fun pauseTimer(time: Int) {
        log("pauseTimer $time")
        cancelTimer()
    }

    private fun resumeTimer(time: Int) {
        log("resumeTimer $time")
        startCountDown(time)
    }

    private fun onTimerUpdate(time: Long) {
        tv_cycle_timer?.text = String.format("%02d:%02d", time / 60000, time/1000 % 60 )
    }

    internal fun cancelTimer() {
        countTimer?.cancel()
    }

    private var timeTotalDuration = 0
    private var timeElapsed = 0L

    private fun startCountDown(seconds: Int) {
        cancelTimer()
        countTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(it: Long) {
                log("CountDownTimer $it")
                onTimerUpdate(it)
                timeElapsed = it
            }

            override fun onFinish() {
                onTimerUpdate(0L)
                cancelTimer()
                timeElapsed = timeTotalDuration.toLong()
            }
        }
        countTimer?.start()
    }



    // var lastFrom = -1
    var animator: ObjectAnimator? = null
    fun progress(valueFrom: Int, valueTo: Int, duration: Int, type: Int) {
        Single.just("this").observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            log("progress $valueFrom : $valueTo :: $duration")
            if (duration == 0) {
                progressBar!!.visibility = GONE
                return@doOnSuccess
            } else
                progressBar!!.visibility = VISIBLE

            if (type == 1)
                progressBar!!.progressTintList = ColorStateList.valueOf(Color.GREEN)
            else
                progressBar!!.progressTintList = ColorStateList.valueOf(Color.RED)

            animator = ObjectAnimator.ofInt(progressBar, "progress", valueFrom, valueTo)
                .setDuration(duration.toLong())
            animator?.start()
        }.subscribe()
    }

    private var isSessionActive = false
    override fun onBackPressed() {
        if (canBack()) {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (canBack())
                finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private var isPaused = false
    private fun pauseProgram() {
        log("pauseProgram....")
        isPaused = true
        //val clone = currentProgram
        animator?.cancel()
        RXLManager.getInstance().pauseProgram()
    }

    private fun resumeProgram() {
        log("resumeProgram....")
        currentProgram?.let {
            RXLManager.getInstance().withListener(this).resumeProgram()
            isPaused = false
        }


    }

    private fun canBack(): Boolean {
        if (isSessionActive) {
            MessageDialog(
                this,
                getString(R.string.stop_session),
                getString(R.string.stop_session_message_rxl),
                getString(R.string.stop_anyway),
                getString(R.string.dialog_continue),
                object : MessageDialog.Listener {
                    override fun onClick(button: Int) {
                        if (button == MessageDialog.NEGATIVE)
                            endProgram(true)
                    }

                }).show()
            return false
        }
        return true
    }

    fun checkSession(): Boolean = isSessionActive

    fun testDialog() {
//        val players = ArrayList<RxlPlayer>()
//        val p = RxlPlayer(1, "Test Name", Color.RED, 0, 0, ArrayList())
//
//        for (i in 1..100) {
//            p.events.add(Event(i, 2000, if (i % 3 == 0) 3 else 0, false))
//        }
//        players.add(p)
//        val p2 = p as RxlPlayer
//        p2.color = Color.GREEN
//        players.add(p2)
//        p2.color = Color.BLUE
//        players.add(p2)
//        p2.color = Color.MAGENTA
//        players.add(p2)
//        showScoreDialog(players)
    }

    private fun showScoreDialog(players: ArrayList<RxlPlayer>?) {
        if (players == null)
            return
        //val players = RXLHelper.getInstance().getPlayers() ?: return
        val list = ArrayList<ScoreAdapter.ScoreItem>()
        val time = RXLManager.getInstance().getProgram()?.totalDuration()
        val size = players.size
        //val programName = RXLHelper.getInstance().getProgram()?

        list.add(
            ScoreAdapter.ScoreItem(
                0,
                "",
                "$time",
                R.drawable.rxl_score_pods_time,
                R.drawable.rxl_score_time,
                0,
                R.string.total_time
            ).initial(true)
        )
        players.forEach {
            var hits = 0
            var missed = 0
            it.events.forEach { ev ->
                if (ev.tapTime > 1)
                    hits++
                else
                    missed++
            }

            log("showScoreDialog player: ${it.id}, ${it.events.size} hit $hits : miss $missed")

            if (size == 1) {
                list.add(
                    ScoreAdapter.ScoreItem(
                        1,
                        "",
                        "$hits",
                        R.drawable.rxl_score_pods_hit,
                        R.drawable.rxl_score_hits,
                        0,
                        R.string.hits
                    )
                )
                list.add(
                    ScoreAdapter.ScoreItem(
                        2,
                        "",
                        "$missed",
                        R.drawable.rxl_score_pods_missed,
                        R.drawable.rxl_score_missed,
                        0,
                        R.string.missed
                    )
                )
            } else {
                list.add(
                    ScoreAdapter.ScoreItem(
                        it.id, it.name, "$hits", it.color, "$missed", 0, 0, false
                    )
                )
            }
        }

        if (list.size > 1) {
            val dialog = ScoreDialog(this, rxlProgramNew?.name, list)
            dialog.show()
            // val height = resources.displayMetrics.heightPixels * 0.8;
            // log("showScoreDialog ${dialog.window?.attributes?.height} == $height")
            // log("showScoreDialog <> ${dialog.window?.decorView?.height} == $height")
            //if (dialog.window?.attributes?.height ?: 0 > height)
            //  dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height.toInt())
        }
    }


    fun saveScore(players: ArrayList<RxlPlayer>?) {
        log("saveScore timeElapsed $timeElapsed : $timeTotalDuration")
        if(MiboApplication.DEBUG)
            return
        if (players == null)
            return
        val trainer = Prefs.get(this).member ?: return
        //getDialog().show();
        var loc = trainer.locationID
        if (loc == null) loc = "0"
        // list = ArrayList<ScoreItem>()
        val scores: MutableList<Score?> = java.util.ArrayList()
        val date =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(LocalDateTime.now())

        for (i in players) {
            var hits = 0
            var missed = 0
            i.events.forEach { ev ->
                if (ev.tapTime > 1)
                    hits++
                else
                    missed++
            }
            //list.add(ScoreItem(i.id, i.name, "$hits", "$missed", 0, hits.plus(missed)))
            scores.add(
                Score(
                    date,
                    "rxl",
                    "" + hits,
                    loc,
                    "" + trainer.id(),
                    "" + missed,
                    "" + hits.plus(missed),
                    "" + trainer.id(),
                    "0",
                    "-",
                    "" + rxlProgramNew?.id,
                    ""+timeElapsed
                )
            )
        }

        //ateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());


        if (scores.isEmpty()) return
        val post = SaveMemberScores(
            SaveMemberScores.Data(scores),
            trainer.accessToken
        )
        request.getApi().saveScore(post)
            .enqueue(object : Callback<ResponseStatus?> {
                override fun onResponse(
                    call: Call<ResponseStatus?>,
                    response: Response<ResponseStatus?>
                ) {
                    getDialog()!!.dismiss()
                    //Toasty.info(getContext(), "Response " + response.body()).show();
                    val body = response.body()
                    if (body != null && body.isSuccess()) {
                        Toasty.info(this@QuickPlayDetailsActivity, "Session Saved!").show()
                    }
                }

                override fun onFailure(
                    call: Call<ResponseStatus?>,
                    t: Throwable
                ) {
                    getDialog()!!.dismiss()
                    Toasty.info(this@QuickPlayDetailsActivity, "Error " + t.message).show()
                }
            })
    }
}
