package life.mibo.hexa.ui.ch6

import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_channel6.*
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.*
import life.mibo.hardware.models.ButtonsConstants.BUTTON_MINUS_MAIN_DEVICE_CONTROL
import life.mibo.hardware.models.ButtonsConstants.BUTTON_PLUS_MAIN_DEVICE_CONTROL
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.User
import life.mibo.hardware.models.UserSession
import life.mibo.hardware.models.program.Circuit
import life.mibo.hardware.models.program.Program
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.ch6.adapter.Channel6Listener
import life.mibo.hexa.ui.ch6.adapter.Channel6Model
import life.mibo.hexa.ui.ch6.adapter.ChannelAdapter
import life.mibo.hexa.utils.Toasty
import life.mibo.hexa.utils.Utils.checkLimitValues
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


class Channel6Fragment : BaseFragment(), Channel6Listener {

    private lateinit var viewModel: Channel6ViewModel
    private lateinit var controller: Channel6Controller
    private lateinit var userId: String
    var recyclerView: RecyclerView? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        viewModel =
            ViewModelProviders.of(this).get(Channel6ViewModel::class.java)
        val root = i.inflate(R.layout.fragment_channel6, c, false)
        //  val textView: TextView = root.findViewById(R.id.text_dashboard)
        recyclerView = root.findViewById(R.id.recyclerView)
        viewModel.text.observe(this, Observer {
            //    textView.text = ""//it
        })
        this.activity?.actionBar?.hide()
        setRecycler(recyclerView!!)
        retainInstance = true
        //SessionManager.getInstance().session = Session()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Channel6Controller(this@Channel6Fragment)
        userId = Prefs.get(this@Channel6Fragment.activity)["user_uid"]
        iv_plus?.setOnClickListener {
            onPlusClicked()
        }

        iv_minus?.setOnClickListener {
            onMinusClicked()
        }

        iv_play?.setOnClickListener {
            startSession(SessionManager.getInstance().userSession.user)
            //onPlayClicked()
        }

        iv_plus?.setOnLongClickListener {
            Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                    log("starting again device")
                    EventBus.getDefault().postSticky(SendDeviceStartEvent(userId))
                }.subscribe()

            true
        }

        iv_stop?.setOnClickListener {
            onStopClicked()
        }

        EventBus.getDefault().postSticky(
            SendProgramEvent(
                SessionManager.getInstance().userSession?.currentSessionProgram,
                SessionManager.getInstance().userSession?.device?.uid
            )
        )

        SessionManager.getInstance().userSession?.listener = object : UserSession.Sessionlistener {
            override fun SendProgramEvent(program: Program?, uid: String?) {
                log("UserSession.Sessionlistener SendProgramEvent")
                EventBus.getDefault().postSticky(SendProgramEvent(program, uid))
            }

            override fun SendChannelsLevelEvent(channels: IntArray?, uid: String?) {
                log("UserSession.Sessionlistener SendChannelsLevelEvent")
                EventBus.getDefault().postSticky(SendChannelsLevelEvent(channels, uid));
            }

            override fun SessionFinishEvent() {
                log("UserSession.Sessionlistener SessionFinishEvent")
                EventBus.getDefault().postSticky(SessionFinishEvent());
            }

            override fun ChangeColorEvent(device: Device?, uid: String?) {
                log("UserSession.Sessionlistener ChangeColorEvent")
                EventBus.getDefault().postSticky(ChangeColorEvent(device, uid));
            }

        }
    }


    private var manager: CommunicationManager? = null
    fun getManager(): CommunicationManager {
        if (manager == null)
            manager = CommunicationManager.getInstance()
        return manager!!
    }

    var list = ArrayList<Channel6Model>()
    var adapter: ChannelAdapter? = null
    fun setRecycler(view: RecyclerView) {

        if (list == null)
            list = ArrayList()
        list.clear()

        //val c = Channel6Model(1, R.drawable.ic_channel_abdomen, 1, 2)
        //c.percentChannel?.observe(this)
        list.add(Channel6Model(1, R.drawable.ic_channel_abdomen, 1, 2))
        list.add(Channel6Model(2, R.drawable.ic_channel_back_neck, 0, 0))
        list.add(Channel6Model(3, R.drawable.ic_channel_biceps, 0, 0))
        list.add(Channel6Model(4, R.drawable.ic_channel_chest, 0, 0))
        list.add(Channel6Model(5, R.drawable.ic_channel_glutes, 0, 0))
        list.add(Channel6Model(6, R.drawable.ic_channel_thighs, 0, 0))

//        viewModel.list.observe(this, Observer {
//            it.addAll(list)
//        })

        if (isLand)
            view.layoutManager = GridLayoutManager(this@Channel6Fragment.activity, 6)
        else
            view.layoutManager = GridLayoutManager(this@Channel6Fragment.activity, 1)
        adapter = ChannelAdapter(list, isLand)
        adapter?.setListener(this)
        //val manager = GridLayoutManager(this@DeviceScanFragment.activity, 1)
        //view.layoutManager = manager
        view.adapter = adapter
        //Toasty.warning(this@DeviceScanFragment.context, "")
        //Toasty.warning(this@DeviceScanFragment.context!!, "Configuration changes $isLand").show()
//        viewModel.list.observe(this, Observer {
//            log("data changed")
//            adapter?.notifyDataSetChanged()
//        })

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDevicePlayPauseEvent(event: DevicePlayPauseEvent) {
        log("onDevicePlayPauseEvent")
        EventBus.getDefault().postSticky(
            SendProgramEvent(
                SessionManager.getInstance().userSession.currentSessionProgram,
                SessionManager.getInstance().userSession.device.uid
            )
        )
        if (SessionManager.getInstance().userSession.device.isStarted) {
            Observable.fromArray(list).flatMapIterable { x -> x }
                .subscribeOn(Schedulers.computation()).delay(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Channel6Model> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: Channel6Model) {
                        log("onPlayClicked " + SessionManager.getInstance().session)
                        //mViewMvc.updatePlayPause(SessionManager.getInstance().getSession().getUserSelected().getUserBooster().getIsStarted())
                        t.isPlay = false
                    }

                    override fun onError(e: Throwable) {
                        log("iv_plus onError", e)
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        adapter?.notifyDataSetChanged()

                    }
                })
        } else {
            Observable.fromArray(list).flatMapIterable { x -> x }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Channel6Model> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Channel6Model) {
                        t.isPlay = true
                    }

                    override fun onError(e: Throwable) {
                        log("iv_plus onError", e)
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
        if (event.uid == userId) {

        }
    }

    private fun onPlayClicked() {
        sendPlaySignals(userId)

    }

    private fun sendProgramToAllBoosters(u: User) {
        EventBus.getDefault().postSticky(
            SendProgramEvent(
                SessionManager.getInstance().userSession.currentSessionProgram,
                userId
            )
        )
    }

    private fun sendCircuitToAllBoosters(u: User) {
        EventBus.getDefault().postSticky(SendCircuitEvent(Circuit(), userId))
    }

    private fun sendStartToAllBoosters(u: User) {
        if (u.isActive) {
            u.userBooster.isStarted = true
            EventBus.getDefault().postSticky(SendDevicePlayEvent(userId))
        }
    }

    private fun sendReStartToAllBoosters(u: User) {
        if (u.isActive) {
            u.userBooster.isStarted = true
            EventBus.getDefault().postSticky(SendDeviceStartEvent(userId))
        }
    }

    private fun sendChannelLevelsToAllBoosters(user: User) {
        log("startSession sendChannelLevelsToAllBoosters")
        EventBus.getDefault()
            .postSticky(SendChannelsLevelEvent(user.currentChannelLevels, userId))
    }

    private fun sendStopToAllBoosters(u: User) {
        u.userBooster.isStarted = false
        EventBus.getDefault().postSticky(
            SendDeviceStopEvent(
                u.userBooster.uid
            )
        )
    }

    fun startSession(u: User) {
        log("startSession " + u.debugLevels())
        sendChannelLevelsToAllBoosters(u)
        try {
            Thread.sleep(800)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        log("startSession sendReStartToAllBoosters")
        sendReStartToAllBoosters(u)
        try {
            Thread.sleep(600)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        log("startSession sendStartToAllBoosters")
        sendStartToAllBoosters(u)
        SessionManager.getInstance().userSession.currentSessionStatus = 2
        SessionManager.getInstance()
            .userSession.startTimer(SessionManager.getInstance().userSession.currentSessionProgram.duration.value.toLong())
        // startTimer(SessionManager.getInstance().session.currentSessionProgram.duration.valueInt)
    }

    private var cTimer: CountDownTimer? = null
    internal fun startTimer(s: Int) {
        cancelTimer()
        cTimer = object : CountDownTimer(s * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateTime(SessionManager.getInstance().session.currentSessionTimer)
            }

            override fun onFinish() {
                cancelTimer()
            }
        }
        cTimer?.start()
    }

    private fun updateTime(timer: Int) {

    }

    //cancel timer
    internal fun cancelTimer() {
        if (cTimer != null) {
            cTimer?.cancel()
            updateTime(SessionManager.getInstance().session.currentSessionTimer)
        }
    }

    private fun sendPlaySignals(uid: String) {
        if (SessionManager.getInstance().userSession.device == null) {
            Toasty.warning(this@Channel6Fragment.activity!!, "No Device Connected").show()
        }
        log("sendPlaySignals " + SessionManager.getInstance().userSession?.user?.debugLevels())
        Observable.timer(0, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting SendChannelsLevelEvent")
                EventBus.getDefault()
                    .postSticky(
                        SendChannelsLevelEvent(
                            SessionManager.getInstance().userSession.user.currentChannelLevels,
                            uid
                        )
                    )
            }.subscribe()

        Observable.timer(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting again device")
                if (SessionManager.getInstance().userSession.user.isActive) {
                    SessionManager.getInstance().userSession.device.isStarted = true
                    EventBus.getDefault().postSticky(SendDeviceStartEvent(uid))
                }

            }.subscribe()
        Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting again device")
                if (SessionManager.getInstance().userSession.user.isActive) {
                    SessionManager.getInstance().userSession.device.isStarted = true
                    EventBus.getDefault().postSticky(SendDevicePlayEvent(uid))
                    SessionManager.getInstance().userSession.currentSessionStatus = 2
                }
                //EventBus.getDefault().postSticky(SendDevicePlayEvent(uid))
            }.subscribe()

        //EventBus.getDefault().postSticky(SendDevicePlayEvent(uid))

        SessionManager.getInstance().userSession.device.isStarted = true

        if (SessionManager.getInstance().userSession.currentSessionStatus != 1 && SessionManager.getInstance().userSession.user.isActive) {
            log("sendPlaySignals starting session ")
            //EventBus.getDefault().postSticky(SendDevicePlayEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
        }

//        if (SessionManager.getInstance().session.currentSessionStatus == 0 || SessionManager.getInstance().session.currentSessionStatus == 3) {
//            log("sendPlaySignals session not starting ")
//        } else {
//            if (SessionManager.getInstance().session.userSelected.userBooster.isStarted) {
//                log("sendPlaySignals stopping session ")
//                EventBus.getDefault()
//                    .postSticky(SendDeviceStopEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
//                SessionManager.getInstance().session.userSelected.mainLevel = 0
//                SessionManager.getInstance()
//                    .session.userSelected.userBooster.isStarted = false
//            } else {
//                if (SessionManager.getInstance().session.currentSessionStatus != 1 && SessionManager.getInstance().session.userSelected.isActive) {
//                    log("sendPlaySignals starting session ")
//                    EventBus.getDefault()
//                        .postSticky(SendDevicePlayEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
//                    SessionManager.getInstance()
//                        .session.userSelected.userBooster.isStarted = true
//                }
//            }
//            // SessionManager.getInstance().getSession().getUserSelected().setActive(true);
//        }
    }

    private fun sendStopSignals(uid: String) {
        if (SessionManager.getInstance().userSession.device == null) {
            Toasty.warning(this@Channel6Fragment.activity!!, "No Device Connected").show()
        }
        log("sendStopSignals " + SessionManager.getInstance().userSession?.user?.debugLevels())
        EventBus.getDefault().postSticky(SendDeviceStopEvent(uid))
        SessionManager.getInstance().userSession.user.mainLevel = 0
        SessionManager.getInstance().userSession.device.isStarted = false

    }

    private fun onStopClicked() {
        sendStopSignals(userId)

    }

    private fun onPlusClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                    log("iv_plus onSubscribe")
                }

                override fun onNext(t: Channel6Model) {
                    log("iv_plus onNext")
//                        t.percentChannel?.observe(this@Channel6Fragment, Observer {
//                            it.plus(1)
//                        })
                    t.incChannelPercent()
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    log("iv_plus onComplete")
                    adapter?.notifyDataSetChanged()
                }
            })
    }

    private fun onMinusClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Channel6Model) {
                    t.decChannelPercent()
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()

                }
            })
    }

    override fun onClick(data: Channel6Model) {

    }

    override fun onPlusClicked(data: Channel6Model) {
        onMusclePlusClicked(data.id)
    }

    override fun onMinusClicked(data: Channel6Model) {
        onMuscleMinusClicked(data.id)
    }

    override fun onPlayPauseClicked(data: Channel6Model, isPlay: Boolean) {
        onMuscleStopClicked(data.id)
    }

    fun onMuscleMinusClicked(id: Int) {
        SessionManager.getInstance().session.userSelected.decrementChannelLevelUserSelected(id)

        EventBus.getDefault().postSticky(
            SendChannelsLevelEvent(
                SessionManager.getInstance().session.userSelected.currentChannelLevels,
                SessionManager.getInstance().session.userSelected.userBooster.uid
            )
        )
        log("onMuscleMinusClicked Minus group $id")
    }

    fun onMusclePlusClicked(id: Int) {
        SessionManager.getInstance().session.userSelected.incrementChannelLevelUserSelected(id)

        EventBus.getDefault().postSticky(
            SendChannelsLevelEvent(
                SessionManager.getInstance().session.userSelected.currentChannelLevels,
                SessionManager.getInstance().session.userSelected.userBooster.uid
            )
        )
        log("onMusclePlusClicked Plus group $id")
    }

    fun onMuscleStopClicked(id: Int) {
        SessionManager.getInstance().session.userSelected.currentChannelLevels[id - 1] = 0
        EventBus.getDefault().postSticky(
            SendChannelsLevelEvent(
                SessionManager.getInstance().session.userSelected.currentChannelLevels,
                SessionManager.getInstance().session.userSelected.userBooster.uid
            )
        )
        log("onMuscleStopClicked Stop group $id")
    }

    private var isLand = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isLand = newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE
        //Toasty.warning(this@DeviceScanFragment.context!!, "Configuration changes $isLand").show()
        setRecycler(recyclerView!!)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        //setRecycler(recyclerView!!)
    }

    fun updateMainLevel(level: Int) {
        log("update main levels $level")
        //txtMainLevel.setText("$level %")
        //levelaux = level
        //if (levels != null)
        //   updateLevelsUI()
    }

    private var levels: IntArray? = null
    fun updateLevels(levels: IntArray?) {
        this.levels = levels
        //updateLevelsUI()
        if (levels != null) {
            list.forEachIndexed { i, item ->
                item.percentChannel = levels[i]
            }
            adapter?.notifyDataSetChanged()
        }
    }

    var buttonId = 0
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onGetMainLevelEvent(event: GetMainLevelEvent) {
        var sendMain = false
        if (buttonId == BUTTON_PLUS_MAIN_DEVICE_CONTROL) {
            if (!checkLimitValues(SessionManager.getInstance().session.userSelected.mainLevel)) {
                SessionManager.getInstance().session.userSelected.incrementMainLevelUser()
                sendMain = true
            }
        }
        if (buttonId == BUTTON_MINUS_MAIN_DEVICE_CONTROL) {
            SessionManager.getInstance().session.userSelected.decrementMainLevelUser()
            sendMain = true
        }
        if (sendMain) {
            Handler(activity!!.mainLooper).postDelayed(Runnable {
                EventBus.getDefault().postSticky(
                    SendMainLevelEvent(
                        SessionManager.getInstance().userSession.user.mainLevel,
                        SessionManager.getInstance().userSession.device.uid
                    )
                )
            }, 200)
        } else {
//            Handler(activity!!.mainLooper).postDelayed(Runnable {
//                EventBus.getDefault().postSticky(
//                    SendMainLevelEvent(
//                        SessionManager.getInstance().userSession.user.mainLevel,
//                        SessionManager.getInstance().userSession.device.uid
//                    )
//                )
//            }, 200)
        }
        updateMainLevel(event.level)
        log("onGetMainLevelEvent ${event?.level}")
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onGetProgramStatusEvent(event: ProgramStatusEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        //        if(event.getUid().equals(SessionManager.getInstance().getSession().getUserSelected().getUserBooster().getUid())) {
        //            if(tsLastStatus+100 < System.currentTimeMillis()) {// sepueden ejecutar varios antes de hacer esto?
        //                tsLastStatus = System.currentTimeMillis();
        //               // Log.e("GroupController","event status");
        //                new Handler(Looper.getMainLooper()).post(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        mViewMvc.updateStatus(event.getRemainingProgramTime(), event.getRemainingProgramAction(), event.getRemainingProgramPause(), event.getUid());
        //                    }
        //                });
        //            }
        //        }

        if (SessionManager.getInstance().userSession.user.tsLastStatus + 100 < System.currentTimeMillis()) {// sepueden ejecutar varios antes de hacer esto?
            SessionManager.getInstance().userSession.user.tsLastStatus = System.currentTimeMillis()
            // Log.e("GroupController","event status");
            updateStatus(
                event.remainingProgramTime, event.remainingProgramAction,
                event.remainingProgramPause, event.currentBlock, event.currentProgram, event.uid
            )
        }

    }

    private fun updateStatus(
        remainingProgramTime: Int,
        remainingProgramAction: Int,
        remainingProgramPause: Int,
        currentBlock: Int,
        currentProgram: Int,
        uid: String?
    ) {

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    interface Listener {
        fun onMainPlusClicked()
        fun onMainMinusClicked()
        fun onMainPlayClicked()
        fun onMainStopClicked()
    }
}