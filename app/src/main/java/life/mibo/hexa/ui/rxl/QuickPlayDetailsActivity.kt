package life.mibo.hexa.ui.rxl

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_quickplay_detail_play.*
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ProximityEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceTypes
import life.mibo.hexa.BuildConfig
import life.mibo.hexa.R
import life.mibo.hexa.core.toIntOrZero
import life.mibo.hexa.events.NotifyEvent
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.pods.rxl.RXLManager
import life.mibo.hexa.pods.rxl.RxlLight
import life.mibo.hexa.pods.rxl.RxlPlayer
import life.mibo.hexa.pods.rxl.RxlProgram
import life.mibo.hexa.ui.base.BaseActivity
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.MessageDialog
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.ui.rxl.adapter.PlayersAdapter
import life.mibo.hexa.ui.rxl.impl.CourseCreateImpl
import life.mibo.hexa.ui.rxl.impl.ReflexDialog
import life.mibo.hexa.ui.select_program.ProgramDialog
import life.mibo.hexa.utils.Constants
import life.mibo.hexa.utils.Toasty
import org.greenrobot.eventbus.EventBus


class QuickPlayDetailsActivity : BaseActivity(), RXLManager.Listener, CourseCreateImpl.Listener {

    private lateinit var delegate: CourseCreateImpl
    private var program: life.mibo.hexa.models.rxl.RxlProgram? = null
    private var isUser = false
    private val rxlPlayers = ArrayList<RxlPlayer>()
    private val userDevices = ArrayList<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.fragment_quickplay_detail_play)
        setSupportActionBar(toolbar)
        createTestDevices()
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }


        program =
            intent?.getSerializableExtra(Constants.BUNDLE_DATA) as life.mibo.hexa.models.rxl.RxlProgram?
        isUser = !program?.memberId.isNullOrEmpty()
        updateToolbar()
        createView()
        log("program >> $program")
        //iv_icon.setFreezesAnimation()

    }

    private fun updateToolbar() {
        val appbar = findViewById<AppBarLayout?>(R.id.appBar)
        if (isUser) {
//            // val heightDp = Utils.dpToPixel(72, this)
//            // appbar?.setExpanded(false, false)
//            //appbar?.isEnabled = false
//            //collapsingToolbar?.isEnabled = false
//            // collapsingToolbar?.
//            val lp =
//                appbar?.layoutParams as CoordinatorLayout.LayoutParams
//            //lp.height = appbar.minimumHeightForVisibleOverlappingContent
//            iv_icon?.visibility = View.GONE
//            // val cbar = findViewById<AppBarLayout?>(R.id.collapsingToolbar)
//            //CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
//            //val lp = appbar?.layoutParams as CoordinatorLayout.LayoutParams
//            // val lp = collapsingToolbar?.layoutParams as AppBarLayout.LayoutParams
//            // lp.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
////            val behavior = appbar?.layoutParams as CoordinatorLayout.LayoutParams
////            behavior.behavior = AppBarLayout.Behavior()
////            appbar?.postDelayed(Runnable {
////                (behavior.behavior as AppBarLayout.Behavior).onNestedFling(
////                    coordinatorLayout,
////                    appBar,
////                    appBar,
////                    0f,
////                    10000f,
////                    true
////                )
////
////            }, 200)


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
                loadGlide(program?.tutorial)
//                if (!program?.tutorial.isNullOrEmpty()) {
//                    loadGlide(program?.tutorial?.get(0) ?: "")
//                } else {
//                    loadGlide("")
//                }
            } catch (e: java.lang.Exception) {
                // loadGlide("")
            }
        }
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

    private fun loadGlide(url: String?) {
        val request = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(500, 500)
        simple_drawee_view?.visibility = View.VISIBLE
        if (url.isNullOrEmpty()) {
            Glide.with(this).asGif().load(R.drawable.rxl_agility_test_1)
                .addListener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        log("Glide onLoadFailed")
                        return true
                    }

                    override fun onResourceReady(
                        resource: GifDrawable?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        resource?.startFromFirstFrame()
                        log("Glide onResourceReady")

                        return false
                    }

                })
                .apply(request)
                .into(simple_drawee_view)
            return
        }

        val type = url.substring(url.lastIndexOf("."))
        if (type.contains("gif")) {

            Glide.with(this).asGif().load(url)
                .addListener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        log("Glide onLoadFailed")
                        return true
                    }

                    override fun onResourceReady(
                        resource: GifDrawable?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        log("Glide onResourceReady")

                        return false
                    }

                })
                //.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(request)
                .placeholder(R.drawable.dialog_spinner)
                .into(simple_drawee_view)

        } else if (type.contains("mp4")) {
            Glide.with(this).asGif().load(R.drawable.rxl_agility_test_1)
                .apply(request)
                .into(simple_drawee_view)
        }

    }

    private fun disableCollapseBar() {
        val params = appBar.layoutParams as CoordinatorLayout.LayoutParams
        if (params.behavior == null)
            params.behavior = AppBarLayout.Behavior()
        val behaviour = params.behavior as AppBarLayout.Behavior
        behaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
        //appbar?.isActivated = false

        behaviour.onNestedFling(coordinatorLayout, appBar, collapsingToolbar, 0f, 10000f, true)
        val lp = collapsingToolbar?.layoutParams as AppBarLayout.LayoutParams
        lp.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        // lp.scrollFlags = -1
    }

    private fun createView() {
        delegate = CourseCreateImpl(this, this)

        //delegate.listener = this

        userDevices.clear()
        userDevices.addAll(SessionManager.getInstance().userSession.devices)
        SessionManager.getInstance().userSession.isRxl = true
        setProgram()
        //navigate(Navigator.HOME_VIEW, true)

        btn_start_now?.setOnClickListener {
            startNowClicked()
        }

        btn_start_hit?.setOnClickListener {
            startHitClicked()
        }

        btn_stop?.setOnClickListener {
            canBack()
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
            setResult(3)
            finish()
        }
        setPickers()
        //getPods()

        if ("proximity" == program?.tapProximity?.toLowerCase()) {
            log("changeSensor proximity " + program?.proximityValue)
            changeSensor(getInt(program?.proximityValue).times(10))
        } else {
            log("changeSensor tap " + program?.proximityValue)
            changeSensor(0)
        }
        //setSlider()

        if (BuildConfig.DEBUG) {
            tv_select_action?.setOnClickListener {
                delegate.showDialog(CourseCreateImpl.Type.ACTION)
            }

            tv_select_pause?.setOnClickListener {
                delegate.showDialog(CourseCreateImpl.Type.DELAY)
            }
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

        log("isRandom " + isRandom())
        //log("isRandom2 " + tv_select_lights.text?.toString()?.toLowerCase()?.contains("random"))
        //if (checkPlayersPods())
        checkStartCondition {
            startProgram(false, it)
        }

    }

    //val players = ArrayList<RxlPlayer>()
    private fun startProgram(tap: Boolean, devices: ArrayList<Device>) {
//        val uids = ArrayList<String>()
//        devices.forEach { d ->
//            uids.add(d.uid)
//        }

        val players = SessionManager.getInstance().userSession.rxlPlayers

        RXLManager.getInstance().with(
            RxlProgram.getExercise(
                getDuration(),
                getAction(),
                getPause(),
                getCycles(),
                selectedColor,
                selectedColorId,
                devices,
                getLightLogic()
            )
        ).addDevices(devices).withListener(this).start(tap)

        //if (tap)
        //   Toasty.info(context!!, "Tap Reaction Light to start").show()
    }


    private fun setProgram() {
        toolbar?.title = program?.name
        collapsingToolbar?.title = program?.name
        program?.let {
            //tv_select_stations?.text = "${it.workingStations}"
            tv_select_cycles?.text = "${it.cycle}"
            tv_select_duration?.text = "${it.totalDuration} sec"
            tv_select_pause?.text = "${it.pause} sec"
            //tv_select_delay?.text = "0 sec"
            //tv_select_lights?.text = "${it.lightsLogic}"
            tv_select_action.text = "${it.action} sec"
            //tv_select_pause?.text = "${it.actionDuration} sec"
            //tv_select_players?.text = "${it.numberOfPlayers}"
            //tv_desc?.text = "${it.description}"
            //val images = it.image!!.split(",")
            //images[0].replace("[", "")
            //images[images.size - 1].replace("]", "")

            setPlayers()
        }
    }


    var playersCount = 0
    private fun setPlayers() {
        rxlPlayers.clear()
        val players = SessionManager.getInstance().userSession.rxlPlayers
        log("setPlayers >> $players")
        if (players is List<*>) {
            log("setPlayers >>> ${players.size}")
            val list = ArrayList<PlayersAdapter.PlayerItem>()
            for (p in players) {
                list.add(p as PlayersAdapter.PlayerItem)
            }
            playersCount = list.size
            log("setPlayers >> playersCount $playersCount")
            if (playersCount > 0) {

                val pods = getPods()

                val podsSize = pods.div(playersCount)
                groupPlayer1.visibility = GONE
                groupPlayer2.visibility = GONE
                groupPlayer3.visibility = GONE
                groupPlayer4.visibility = GONE


                list?.forEachIndexed { index, player ->
                    setPlayer(player, index, podsSize, pods)

                }
            }
        }
    }

    private fun setPlayer(player: PlayersAdapter.PlayerItem?, type: Int, pods: Int, max: Int) {
        log("setPlayer >>> $type  , $pods")
        player?.let {
            when (type.plus(1)) {
                1 -> {
                    groupPlayer1.visibility = View.VISIBLE
                    players_1_color?.circleColor = it.playerColor
                    players_1_name.text = it.playerName
                    if (playersCount > 1) {
                        players_1_pods?.setOnClickListener {
                            delegate.showPlayers(CourseCreateImpl.Type.PLAYER_1, max)
                        }
                    } else {
                        // players_one_pods?.setCompoundDrawables(null, null, null, null)

                    }
                    players_1_light?.setOnClickListener {
                        blinkPods(1)
                    }
                    //players_one_pods.text = "$pods"
                    players_1_pods.text = "$max"
                    //playersCount = type
                    selectedColor = player.playerColor
                    selectedColorId = player.playerColorId
                    //selectedColorId
                }
                2 -> {
                    groupPlayer2.visibility = View.VISIBLE
                    players_2_color?.circleColor = it.playerColor
                    players_2_name.text = it.playerName
                    players_2_pods?.setOnClickListener {
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_2, max)
                    }
                    //players_2_pods.text = "$pods"
                    players_2_pods.text = "0"
                    //playersCount = type
                    players_2_light?.setOnClickListener {
                        blinkPods(2)
                    }

                }
                3 -> {
                    groupPlayer3.visibility = View.VISIBLE
                    players_3_color?.circleColor = it.playerColor
                    players_3_name.text = it.playerName
                    players_3_pods?.setOnClickListener {
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_3, max)
                    }
                    //players_3_pods.text = "$pods"
                    players_3_pods.text = "0"
                    //playersCount = type
                    players_3_light?.setOnClickListener {
                        blinkPods(3)
                    }

                }
                4 -> {
                    groupPlayer4.visibility = View.VISIBLE
                    players_4_color?.circleColor = it.playerColor
                    players_4_name.text = it.playerName
                    players_4_pods?.setOnClickListener {
                        delegate.showPlayers(CourseCreateImpl.Type.PLAYER_4, max)
                    }
                    //players_4_pods.text = "$pods"
                    players_4_pods.text = "0"
                    // playersCount = type
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

    }

    private fun updateBlink() {

    }

    private fun updateBlink(enable: Boolean) {

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

    // assigning pods as FIFO
    private fun createPlayers(connected: ArrayList<Device>, assigned: Int): Boolean {
        val playersMap = ArrayList<RxlPlayer?>()
        if (connected.size == assigned && connected.size > 0) {

            val rxlPlayers = SessionManager.getInstance().userSession.rxlPlayers
            log("players >> $rxlPlayers")
            if (rxlPlayers is List<*>) {
                log("players >>> ${rxlPlayers.size}")
                var from = 0
                var to: Int
                rxlPlayers.forEach {
                    if (it is PlayersAdapter.PlayerItem) {
                        // playersMap.put(it.id, createPlayer(it, pods, from))
                        to = getPlayerPods(it.id)
                        playersMap.add(createPlayer(it, connected, from, to))
                        from = to

                    }
                }

                log("createPlayers Player assigned :: playersMap ${playersMap.size}, rxlPlayers ${rxlPlayers.size}")
                log("createPlayers Player assigned : playersMap: $playersMap")
            }

        } else {
            Toasty.error(
                this,
                Html.fromHtml(
                    String.format(
                        getString(R.string.assigned_pods),
                        connected.size,
                        assigned
                    )
                )
            ).show()
        }
        return false
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


    private fun createPlayer(
        player: PlayersAdapter.PlayerItem, pods: ArrayList<Device>, from: Int, to: Int
    ): RxlPlayer? {

        log("createPlayer player $player, pods.size ${pods.size}, from $from, to $to")
        val devices = ArrayList<Device>()
        //var to = getPlayerPods(player.id)
        log("createPlayer : to $to")
        val end = to.plus(from)
        log("createPlayer : end $end")
        if (end < pods.size && end > from) {
            val list = ArrayList<Device>(pods.size)
            pods.forEachIndexed { index, device ->
                // if(index > to)
                //     break
                if (index in from..end) {
                    list.add(device)
                }


            }
            log("createPlayer : user devices size = ${list.size} : from $from, to $to")
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

        if (userDevices.size > 0) {
            userDevices.forEach {
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
                    log("players >>> ${rxlPlayers.size}")
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
                    groupPlayer1.visibility = View.VISIBLE
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
                    groupPlayer2.visibility = View.VISIBLE
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
                    groupPlayer3.visibility = View.VISIBLE
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
                    groupPlayer4.visibility = View.VISIBLE
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
        tv_select_cycles?.setOnClickListener {
            delegate.showDialog(CourseCreateImpl.Type.CYCLES)
        }

        tv_select_duration?.setOnClickListener {
            delegate.showDialog(CourseCreateImpl.Type.DURATION)
        }

    }

    private fun changeSensor(value: Int) {
        log("changeSensor $value")
        if (userDevices.size > 0) {
            Observable.fromIterable(userDevices)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Device> {

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

    var isTest = true

    private fun getTestDevice(id: Int) =
        Device("$id", "name $id", "${id.times(id)}", DeviceTypes.RXT_WIFI)

    private fun createTestDevices() {
        log("createTestDevices ...... $isTest")
        if (isTest) {
            userDevices.clear()
            userDevices.add(getTestDevice(1))
            userDevices.add(getTestDevice(12))
            userDevices.add(getTestDevice(13))
            userDevices.add(getTestDevice(14))
            userDevices.add(getTestDevice(15))
            userDevices.add(getTestDevice(16))
            userDevices.add(getTestDevice(17))
            userDevices.add(getTestDevice(18))
            userDevices.add(getTestDevice(19))
            userDevices.add(getTestDevice(21))
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

    private fun checkPlayersPods(): Boolean {
        //val pods = getPods()
        val pods = ArrayList<Device>()
        val list = SessionManager.getInstance().userSession.devices
        if (list.size > 0) {
            list.forEach {
                if (it.isPod) {
                    pods.add(it)
                }
            }
        }
        var size = 0

        when (playersCount) {
            1 -> {
                size = getInt(players_1_pods.text)
            }
            2 -> {
                size = getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
            }
            3 -> {
                size = getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                    .plus(getInt(players_3_pods.text))
            }
            4 -> {
                size = getInt(players_1_pods.text).plus(getInt(players_2_pods.text))
                    .plus(getInt(players_3_pods.text)).plus(getInt(players_4_pods.text))
            }
        }
        if (pods.size == size) {
            createPlayers(pods, size)
            tv_required_pods?.visibility = GONE
            return true
        }
        tv_required_pods?.visibility = VISIBLE
        tv_required_pods?.text =
            Html.fromHtml(String.format(getString(R.string.assigned_pods), pods, size))
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

    private var size = 2
    private fun checkStartCondition(action: (ArrayList<Device>) -> Unit) {
        val list = SessionManager.getInstance().userSession.devices
        if (list.size < size) {
            MessageDialog.info(this, "RXL Requirement", getString(R.string.three_pods_required))
            return
        }
        val pods = ArrayList<Device>()
        list.forEach {
            if (it.isPod) {
                pods.add(it)
            }
        }

        if (pods.size >= size) {
            if (getLightLogic() == RxlLight.SEQUENCE || getLightLogic() == RxlLight.RANDOM) {
                if (checkPlayersPods())
                    action.invoke(pods)
                if (pods.size != getProgramPods())
                    Toasty.info(
                        this,
                        "This exercise is designed for " + getProgramPods() + " but you have connected only " + pods.size,
                        Toasty.LENGTH_LONG
                    ).show()
            } else {
                if (getLightLogic() == RxlLight.FOCUS || getLightLogic() == RxlLight.ALL_AT_ONCE) {
                    if (checkPlayersPods())
                        action.invoke(pods)
                    Toasty.info(this, "Selected logic is experimental").show()
                }
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
            this.let {
                MessageDialog.info(
                    it,
                    getString(R.string.rxl_title),
                    getString(R.string.three_pods_required)
                )
            }
//            Toasty.warning(
//                context!!,
//                getString(R.string.three_pods_required),
//                Toasty.LENGTH_SHORT,
//                false
//            ).show()
        }
    }

    override fun onDialogItemSelected(item: ReflexDialog.Item, type: Int) {
        log("onDialogItemSelected $type $item")
        when (type) {
            CourseCreateImpl.Type.STATIONS.type -> {
                // tv_select_stations?.text = item.title
            }
            CourseCreateImpl.Type.CYCLES.type -> {
                tv_select_cycles?.text = "${item.title}"
            }
            CourseCreateImpl.Type.PODS.type -> {
                // tv_select_pods?.text = item.title
            }
            CourseCreateImpl.Type.ACTION.type -> {
                // tv_select_pods?.text = item.title
                tv_select_action?.text = item.title
            }

            CourseCreateImpl.Type.DELAY.type -> {
                tv_select_pause?.text = item.title
//                if (item.title?.startsWith("No Delay"))
//                    tv_select_delay?.text = "0 sec"
//                else tv_select_delay?.text = item.title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.DURATION.type -> {
                tv_select_duration?.text = item.title?.replace("seconds", "sec")
            }


            CourseCreateImpl.Type.PLAYER_1.type -> {
                players_1_pods.text = "${item.title}"
            }
            CourseCreateImpl.Type.PLAYER_2.type -> {
                players_2_pods.text = "${item.title}"
            }
            CourseCreateImpl.Type.PLAYER_3.type -> {
                players_3_pods.text = "${item.title}"
            }
            CourseCreateImpl.Type.PLAYER_4.type -> {
                players_4_pods.text = "${item.title}"
            }
        }
    }

    private fun isRandom(): Boolean =
        program?.logicType()?.toLowerCase()?.contains("random") ?: false

    private fun getLightLogic(): RxlLight {
        program?.let {
            return it.lightLogic()
        }
        return RxlLight.UNKNOWN
    }


    private fun getProgramPods(): Int {
        //return getInt(tv_select_pods?.text)
        return program?.pods ?: 0
    }

    private fun getCycles(): Int {
        return getInt(tv_select_cycles?.text)
    }

    private fun getDuration(): Int {
        return getInt(tv_select_duration?.text)
    }

    private fun getPause(): Int {
        //return getInt(tv_select_delay?.text)
        return getInt(tv_select_pause?.text)
    }

    private fun getAction(): Int {
        return getInt(tv_select_action?.text)
    }

    fun getInt(string: CharSequence?): Int {
        return try {
            string?.replace(Regex("\\D+"), "")!!.toIntOrZero()
        } catch (e: Exception) {
            0
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
        // EventBus.getDefault().register(this)
    }

    override fun onStop() {
        //unregister(this)
        //RXLManager.getInstance().unregister()
        //EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        RXLManager.getInstance().unregister()
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
            MessageDialog.info(
                this,
                "Completed",
                "Exercise finished " + RXLManager.getInstance().getHits()
            )
            progressBar!!.visibility = GONE
            tv_cycle_heading?.visibility = INVISIBLE
            tv_cycle_count?.text = ""
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
            btn_pause?.isEnabled = false
            //btn_start_now?.isEnabled = false
            //btn_start_hit?.isEnabled = false
            tv_cycle_heading?.visibility = VISIBLE
            tv_cycle_count?.text = "$cycle"
        }
        progress(0, 100, duration.times(1000), 1)
    }

    override fun onCyclePaused(cycle: Int, time: Int) {
        log("onCyclePaused")
        progress(0, 100, time.times(1000), 2)
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

    override fun onTapColorSent() {
        Toasty.info(this, getString(R.string.hit_to_start_rxl)).show()
    }

    // var lastFrom = -1
    fun progress(valueFrom: Int, valueTo: Int, duration: Int, type: Int) {
        //  if (lastFrom == valueFrom)
        //      return
        //  lastFrom = valueFrom
        // Observable.fromCallable {  }
//        activity?.runOnUiThread {
//
//        }
        Single.just("this").subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
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

                ObjectAnimator.ofInt(progressBar, "progress", valueFrom, valueTo)
                    .setDuration(duration.toLong())
                    .start()
            }.subscribe()
    }


    var isSessionActive = false
    override fun onBackPressed() {
        if (canBack())
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (canBack())
                finish()
        }
        return super.onOptionsItemSelected(item)
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


}
