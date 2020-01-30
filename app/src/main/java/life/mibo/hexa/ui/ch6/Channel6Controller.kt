package life.mibo.hexa.ui.ch6

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.*
import life.mibo.hardware.models.User
import life.mibo.hardware.models.UserSession
import life.mibo.hardware.models.program.Block
import life.mibo.hardware.models.program.Circuit
import life.mibo.hardware.models.program.Program
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.core.toIntOrZero
import life.mibo.hexa.models.base.ResponseData
import life.mibo.hexa.models.create_session.*
import life.mibo.hexa.models.session.Report
import life.mibo.hexa.models.user_details.UserDetails
import life.mibo.hexa.models.user_details.UserDetailsPost
import life.mibo.hexa.ui.ch6.adapter.Channel6Listener
import life.mibo.hexa.ui.ch6.adapter.Channel6Model
import life.mibo.hexa.ui.ch6.adapter.ChannelAdapter
import life.mibo.hexa.ui.main.MessageDialog
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.utils.Toasty
import life.mibo.hexa.utils.Utils
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class Channel6Controller(val fragment: Channel6Fragment, val observer: ChannelObserver) :
    Channel6Listener,
    Channel6Fragment.Listener {


    override fun onBackPressed(): Boolean {
        if (isSessionActive) {
            MessageDialog(
                fragment.context!!,
                fragment.getString(R.string.stop_session),
                fragment.getString(R.string.stop_session_message),
                fragment.getString(R.string.stop_anyway),
                fragment.getString(R.string.dialog_continue),
                object : MessageDialog.Listener {
                    override fun onClick(button: Int) {
                        if (button == MessageDialog.NEGATIVE)
                            exerciseCompleted(true)
                    }

                }).show()
            return false
        }
        return true
    }

    private fun sessionCompleteDialog(msg: String?) {
        val d = MessageDialog(
            fragment.context!!,
            fragment.getString(R.string.session_completed),
            msg ?: "Congrats you have completed session",
            "",
            "close",
            object : MessageDialog.Listener {
                override fun onClick(button: Int) {
                    navigatetoHome()
                }
            })
        d.setCancelable(false)
        d.show()
    }

    fun closeDialog() {
        AlertDialog.Builder(fragment.context!!).setTitle("Stop Session?")
            .setMessage("Are you sure to want stop current session?")
            .setNeutralButton("Continue", null)
            .setPositiveButton("Stop Session") { i, j ->


            }.create().show()
    }

    //var list = ArrayList<Channel6Model>()
    var isSessionActive = false
    var progressBar: ProgressBar? = null
    var tvTimer: TextView? = null
    var adapter: ChannelAdapter? = null
    private lateinit var viewModel: Channel6ViewModel
    private lateinit var userUid: String
    var recyclerView: RecyclerView? = null
    private var manager: CommunicationManager? = null
    var isConnected = false
    var pauseDuration: Int = 0
    var actionDuration: Int = 0
    val plays = BooleanArray(7)

    // TODO Overrides
    override fun onViewCreated(view: View) {

        viewModel = ViewModelProviders.of(this.fragment).get(Channel6ViewModel::class.java)
        userUid =  SessionManager.getInstance().userSession?.booster?.uid ?: ""

        setRecycler(view.findViewById(R.id.recyclerView))
        progressBar = view.findViewById(R.id.progressBar)
        tvTimer = view.findViewById(R.id.tv_timer)

        //EventBus.getDefault().postSticky(SendProgramEvent(SessionManager.getInstance().userSession?.currentSessionProgram, uid))
        if (SessionManager.getInstance().userSession != null) {
            if (SessionManager.getInstance().userSession.user != null) {
                sendProgramToAllBoosters(SessionManager.getInstance().userSession.user)
                if (SessionManager.getInstance().userSession.booster != null && SessionManager.getInstance().userSession.program != null) {
                    isConnected = true
                    pauseDuration =
                        SessionManager.getInstance().userSession.program.blocks[0].pauseDuration.valueInteger
                    actionDuration =
                        SessionManager.getInstance().userSession.program.blocks[0].actionDuration.valueInteger
                }
            }

        }

        if (!isConnected)
            Toasty.info(this.fragment.context!!, "Device is not connected!", Toasty.LENGTH_SHORT, false).show()

    }

    override fun onStop() {
        //cancelTimer()
        disposable?.dispose()
    }

    override fun onMainPlusClicked() {
        if (isConnected)
            onPlusClicked()
    }

    override fun onMainMinusClicked() {
        if (isConnected)
            onMinusClicked()
    }

    fun updatePlayButton() {

    }

    override fun onMainPlayClicked() {
        if (isConnected) {
            SessionManager.getInstance().userSession.program.id?.let {
                if (isBooked) {
                    //pause // restart
                    if (SessionManager.getInstance().userSession.currentSessionStatus == 2)
                        pauseUserSession()
                    else if (SessionManager.getInstance().userSession.currentSessionStatus == 1)
                        sendStartToAllBoosters(SessionManager.getInstance().userSession.user)
                } else {
                    bookSession(it.toIntOrZero())
                }
            }
        } else {
            Toasty.info(this.fragment.context!!, "No device found!", Toasty.LENGTH_SHORT, false).show()
        }
    }

    override fun onMainStopClicked() {
        if (isConnected)
            onBackPressed()
        //stopUserSession(userUid)
    }

    override fun onClick(data: Channel6Model) {

    }

    override fun onPlusClicked(data: Channel6Model) {
        if (isConnected)
            onMusclePlusClicked(data.id)
    }

    override fun onMinusClicked(data: Channel6Model) {
        if (isConnected)
            onMuscleMinusClicked(data.id)
    }

    override fun onPlayPauseClicked(data: Channel6Model, isPlay: Boolean) {
        if (isConnected)
            onMusclePlayStopClicked(data.id, isPlay)
    }

    // TODO hit session report API.
    override fun exerciseCompleted(userStopped: Boolean) {
        //API
        if (userStopped)
            stopUserSession(userUid)
        saveSessionApi()

        isSessionActive = false
        //then
        //fragment.navigate(Navigator.CLEAR_HOME, null)
        cancelTimer()
    }

    private fun navigatetoHome() {
        fragment.navigate(Navigator.CLEAR_HOME, null)
    }


    // TODO Functions
    // 6ch
    // 1- Lower back  2- Shoulder  3- Upper back   4- Abs  5- Chest 6- Arms
    private fun setRecycler(recycler: RecyclerView) {

        // if (list == null)
        //    list = ArrayList()
        // list.clear()
        val list = ArrayList<Channel6Model>()
        //val c = Channel6Model(1, R.drawable.ic_channel_abdomen, 1, 2)
        //c.percentChannel?.observe(this)
        list.add(Channel6Model(1, R.drawable.ic_channel_back_neck, 0, 0))
        list.add(Channel6Model(2, R.drawable.ic_channel_glutes, 0, 0))
        list.add(Channel6Model(3, R.drawable.ic_channel_thighs, 0, 0))
        list.add(Channel6Model(4, R.drawable.ic_channel_abdomen, 0, 0))
        list.add(Channel6Model(5, R.drawable.ic_channel_chest, 0, 0))
        list.add(Channel6Model(6, R.drawable.ic_channel_biceps, 0, 0))

//        viewModel.list.observe(this, Observer {
//            it.addAll(list)
//        })

        recycler.layoutManager = GridLayoutManager(fragment.context, 1)
        adapter = ChannelAdapter(list, false)
        adapter?.setListener(this)
        //val manager = GridLayoutManager(this@DeviceScanFragment.activity, 1)
        //recycler.layoutManager = manager
        recycler.adapter = adapter
        //Toasty.warning(this@DeviceScanFragment.context, "")
        //Toasty.warning(this@DeviceScanFragment.context!!, "Configuration changes $isLand").show()
//        viewModel.list.observe(this, Observer {
//            log("data changed")
//            adapter?.notifyDataSetChanged()
//        })

    }



    var disposable: Disposable? = null
//    @SuppressLint("SetTextI18n")
//    fun countDown(end: Long) {
//
//        disposable = Observable.interval(1, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext {
//                // tvTimer?.text = "${end.minus(it)} sec"
//                tvTimer?.text = String.format("%02d:%02d", end.minus(it) / 60, end.minus(it) % 60)
//            }.takeUntil { a ->
//                a == end
//            }.doOnComplete {
//                tvTimer?.text = "Completed"
//                disposable?.dispose()
//            }.subscribe()
//    }

//    fun startSession(u: User) {
//        log("startSession " + u.debugLevels())
//        sendChannelLevelsToAllBoosters(u)
//        try {
//            Thread.sleep(800)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//        log("startSession sendReStartToAllBoosters")
//        sendReStartToAllBoosters(u)
//        try {
//            Thread.sleep(600)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//        log("startSession sendStartToAllBoosters")
//        sendStartToAllBoosters(u)
//        SessionManager.getInstance().userSession.currentSessionStatus = 2
//        SessionManager.getInstance()
//            .userSession.startTimer(SessionManager.getInstance().userSession.program.duration.value.toLong())
//        // startTimer(SessionManager.getInstance().session.currentSessionProgram.duration.valueInt)
//    }

    private var startTime = ""
    // TODO start session, start timer
    private fun startUserSession(uid: String) {
        if (SessionManager.getInstance().userSession.booster == null) {
            Toasty.warning(
                this.fragment.activity!!,
                fragment.getString(R.string.no_device_connected)
            ).show()
        }
        startTime = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date())
        log("sendPlaySignals " + SessionManager.getInstance().userSession?.user?.debugLevels())
        Observable.timer(0, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting SendChannelsLevelEvent")
                sendChannelLevelsToAllBoosters(SessionManager.getInstance().userSession.user)
            }.subscribe()

        Observable.timer(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting again device")
                sendReStartToAllBoosters(SessionManager.getInstance().userSession.user)

            }.subscribe()
        Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting again device")
                sendStartToAllBoosters(SessionManager.getInstance().userSession.user)
                SessionManager.getInstance().userSession.isBooster = true
                //EventBus.getDefault().postSticky(SendDevicePlayEvent(uid))
            }.subscribe()

        //EventBus.getDefault().postSticky(SendDevicePlayEvent(uid))

        //SessionManager.getInstance().userSession.device.isStarted = true

//        if (SessionManager.getInstance().userSession.currentSessionStatus != 1 && SessionManager.getInstance().userSession.user.isActive) {
//            log("sendPlaySignals starting session ")
//            //EventBus.getDefault().postSticky(SendDevicePlayEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
//        }

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

    private fun sendProgramToAllBoosters(u: User) {
        if (SessionManager.getInstance().userSession.currentSessionStatus == 0) {
            EventBus.getDefault().postSticky(
                SendProgramEvent(
                    SessionManager.getInstance().userSession.program,
                    userUid
                )
            )
        }
    }

    private fun sendStartToAllBoosters(u: User) {
        if (u.isActive) {
            SessionManager.getInstance().userSession.booster.isStarted = true
            EventBus.getDefault().postSticky(SendDevicePlayEvent(userUid))
            SessionManager.getInstance().userSession.currentSessionStatus = 2
            isPaused = true;
            observer.updatePlayButton(isPaused);
        }
    }

    private fun pauseUserSession() {
        if (SessionManager.getInstance().userSession.user.isActive) {
//            SessionManager.getInstance().userSession.booster.isStarted = true
//            EventBus.getDefault().postSticky(SendDevicePlayEvent(userUid))
//            SessionManager.getInstance().userSession.currentSessionStatus = 1
//            isPaused = false;
//            observer.updatePlayButton(isPaused);
//            log("pauseUserSession")
            Toasty.info(fragment?.requireContext(), "Pause functionality is remaining").show()
        }

    }
    private fun sendReStartToAllBoosters(u: User) {
        if (u.isActive) {
            SessionManager.getInstance().userSession.booster.isStarted = true
            EventBus.getDefault().postSticky(SendDeviceStartEvent(userUid))
        }
    }

    private fun sendChannelLevelsToAllBoosters(user: User) {
        log("startSession sendChannelLevelsToAllBoosters")
        EventBus.getDefault()
            .postSticky(SendChannelsLevelEvent(user.currentChannelLevels, userUid))
    }

    private fun stopUserSession(uid: String) {
        if (SessionManager.getInstance().userSession.booster == null) {
            Toasty.warning(
                this.fragment.activity!!,
                "No Device Connected",
                Toasty.LENGTH_SHORT,
                false
            ).show()
            return
        }
        log("sendStopSignals " + SessionManager.getInstance().userSession?.user?.debugLevels())
        SessionManager.getInstance().userSession.booster.isStarted = false
        EventBus.getDefault().postSticky(SendDeviceStopEvent(uid))
        SessionManager.getInstance().userSession.user.mainLevel = 0
        disposable?.dispose()
        //isBooked = false
        //progress(0, 0, 0)
    }

    // group control power clicked
    private fun sendCircuitToAllBoosters(u: User) {
        EventBus.getDefault().postSticky(SendCircuitEvent(Circuit(), userUid))
    }

    private fun sendStopToAllBoosters(u: User) {
        SessionManager.getInstance().userSession.booster.isStarted = false
        EventBus.getDefault().postSticky(
            SendDeviceStopEvent(
                SessionManager.getInstance().userSession.booster.uid
            )
        )
    }

    private fun onMuscleMinusClicked(id: Int) {
        if (SessionManager.getInstance().userSession.user.checkAndDecreaseChannel(id)) {
            EventBus.getDefault().postSticky(
                SendChannelsLevelEvent(
                    SessionManager.getInstance().userSession.user.currentChannelLevels,
                    userUid
                )
            )
        }
        //updateItem(id)
        log("onMuscleMinusClicked Minus group $id")
    }

    private fun onMusclePlusClicked(id: Int) {
        if (SessionManager.getInstance().userSession.user.checkAndIncreaseChannel(id)) {
            EventBus.getDefault().postSticky(
                SendChannelsLevelEvent(
                    SessionManager.getInstance().userSession.user.currentChannelLevels,
                    userUid
                )
            )
        }

        log("onMusclePlusClicked Plus group $id")
        // updateItem(id)
    }


    private fun onMusclePlayStopClicked(id: Int, play: Boolean) {
        log("onMusclePlayStopClicked $play $id")
        if (SessionManager.getInstance().userSession.booster.isStarted) {
            SessionManager.getInstance().userSession.user.currentChannelLevels[id - 1] = 0
            EventBus.getDefault().postSticky(
                SendChannelsLevelEvent(
                    SessionManager.getInstance().userSession.user.currentChannelLevels,
                    userUid
                )
            )
            plays[id] = false
            //updateItem(id)
            //sendStopToAllBoosters(SessionManager.getInstance().userSession.user)
        } else {
            plays[id] = true
            sendStartToAllBoosters(SessionManager.getInstance().userSession.user)
            //sendStopToAllBoosters(SessionManager.getInstance().userSession.user)
        }

        log("plays... " + plays.contentToString())
    }

    fun getManager(): CommunicationManager {
        if (manager == null)
            manager = CommunicationManager.getInstance()
        return manager!!
    }

    private fun onPlusClicked() {
        log("onPlusClicked")
        onMainPlusMinusClicked(true)

    }

    private fun onMinusClicked() {
        log("onMinusClicked")
        onMainPlusMinusClicked(false)
    }

    private fun onMinusClicked2() {
        Observable.fromArray(adapter!!.list!!).flatMapIterable { x -> x }
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

    private fun onMainPlusMinusClicked(isPlus: Boolean) {
        var sendMain = false
        if (isPlus) {
            if (Utils.checkLimitValues(SessionManager.getInstance().userSession.user.mainLevel)) {
                SessionManager.getInstance().userSession.user.incrementMainLevelUser()
                sendMain = true
            }
        } else {
            SessionManager.getInstance().userSession.user.decrementMainLevelUser()
            sendMain = true
        }
        if (sendMain) {
            Handler().postDelayed({
                EventBus.getDefault().postSticky(
                    SendMainLevelEvent(
                        SessionManager.getInstance().userSession.user.mainLevel,
                        userUid
                    )
                )
                log("onMainPlusMinusClicked level " + SessionManager.getInstance().userSession.user.mainLevel)
            }, 200)
        }
    }

    // TODO Event bus events

    var isChannelClicked = false
    // for main click
    fun onDevicePlayPauseEvent(event: DevicePlayPauseEvent) {
        log("onDevicePlayPauseEvent")
        if (isChannelClicked) {
            isChannelClicked = false
            return
        }
//        EventBus.getDefault().postSticky(
//            SendProgramEvent(
//                SessionManager.getInstance().userSession.currentSessionProgram,
//                SessionManager.getInstance().userSession.device.uid
//            )
//        )
        if (SessionManager.getInstance().userSession.booster.isStarted) {
            disposable?.dispose()
            disposable = null
            startTimer(SessionManager.getInstance().userSession.program.duration.valueInt)
            isSessionActive = true
            log("onDevicePlayPauseEvent play button enable")
            Observable.fromArray(adapter!!.list!!).flatMapIterable { x -> x }
                .subscribeOn(Schedulers.computation()).delay(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Channel6Model> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: Channel6Model) {
                        log("onPlayClicked " + SessionManager.getInstance().userSession)
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
            disposable?.dispose()
            log("onDevicePlayPauseEvent play button disable")
            Observable.fromArray(adapter!!.list!!).flatMapIterable { x -> x }
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
        if (event.uid == userUid) {

        }
    }

    var buttonId = 0
    fun onGetMainLevelEvent(event: GetMainLevelEvent) {
        log("onGetMainLevelEvent $event")
        //val items = ArrayList<Channel6Model>()

        Observable.fromArray(adapter?.list!!).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Channel6Model) {
                    t.percentMain = event.level
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()
                }
            })

        //updateMainLevel(event.level)

    }

    fun onGetLevelsEvent(event: GetLevelsEvent) {
        log("onGetLevelsEvent ${event.uid}")
        val items = ArrayList<Channel6Model>()

        Observable.fromCallable {
            val levels = SessionManager.getInstance().userSession.user.currentChannelLevels

            log("onGetLevelsEvent $levels")
            adapter!!.list!!.forEach {
                items.add(it.from(levels[it.id - 1]))
            }
//            items.forEach { item ->
//                item.percentChannel = levels[item.id - 1]
//            }
            Observable.just("complete")
        }.doOnError {
            log("onGetLevelsEvent onError ${it.message}")
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("onGetLevelsEvent doOnComplete")
                adapter?.updateList(items)
            }
            .subscribe()
    }

    fun onGetProgramStatusEvent(event: ProgramStatusEvent) {
        log("onGetProgramStatusEvent $event")
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
            log("UpdateStatus.. getProgramStatusTime  " + event.remainingTime)
            updateStatus(event.actionTime, event.pauseTime)
            // log("onGetProgramStatusEvent session time " + SessionManager.getInstance().userSession.currentSessionTimer)
            //updateStatus(event.remainingProgramTime, event.remainingProgramAction, event.remainingProgramPause, event.currentBlock, event.currentProgram, event.uid)
        }
    }


    // TODO Updates

    // timer
    private var totalDuration = 0L
    private var currentDuration = 0L
    private var countTimer: CountDownTimer? = null
    private fun startTimer(seconds: Int) {
        // SessionManager.getInstance().userSession.startTimer(SessionManager.getInstance().userSession.program.duration.value.toLong())
        totalDuration = seconds.toLong()
        cancelTimer()
        countTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(it: Long) {
                log("onTimerUpdate onTick $seconds : $it")
                onTimerUpdate(it.div(1000))
            }

            override fun onFinish() {
                onTimerUpdate(0L)
                cancelTimer()
            }
        }
        countTimer?.start()
    }

    internal fun cancelTimer() {
        if (countTimer != null) {
            countTimer?.cancel()
            updateTime(SessionManager.getInstance().userSession.currentSessionTimer)
        }
    }

    fun onTimerUpdate(time: Long) {
        log("onTimerUpdate $time")
        if (time == 0L) {
            tvTimer?.text = "Completed"
            //exerciseCompleted(false)
            SessionManager.getInstance().userSession.currentSessionStatus =
                UserSession.SESSION_FINISHED;
            currentDuration = totalDuration
            isSessionActive = false
            saveSessionApi()

        } else {
            currentDuration = time
            // tvTimer?.text = "${end.minus(it)} sec"
            tvTimer?.text = String.format("%02d:%02d", time / 60, time % 60)
        }

    }

    @MainThread
    private fun updateTime(timer: Int) {
        log("updateTime currentSessionTimer  $timer")
    }

    fun updateMainLevel(level: Int) {
        log("updateMainLevel $level")
        Observable.fromArray(adapter!!.list!!).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Channel6Model) {
                    t.percentMain = level
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()
                }
            })

        //txtMainLevel.setText("$level %")
        //levelaux = level
        //if (levels != null)
        //   updateLevelsUI()
    }

    private var levels: IntArray? = null
    fun updateLevels(levels: IntArray?) {
        log("updateLevels ${levels?.contentToString()}")
        this.levels = levels
        //updateLevelsUI()
        if (levels != null) {
            adapter!!.list!!.forEachIndexed { i, item ->
                item.percentChannel = levels[i]
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun updateItem(id: Int) {
        adapter!!.list!!.forEachIndexed { i, item ->
            if (item.id == id) {
                item.percentChannel =
                    SessionManager.getInstance().userSession.user.currentChannelLevels[i]
                //if (inc) item.incChannelPercent()
                //else item.decChannelPercent()
                adapter?.notifyItemChanged(i)
                return@forEachIndexed
            }
        }
    }

    fun updateItem(id: Int, level: Int) {
        adapter!!.list!!.forEachIndexed { i, item ->
            if (item.id == id) {
                item.percentMain = level
                adapter?.notifyItemChanged(i)
                return@forEachIndexed
            }
        }
    }

    fun rxQueue() {
        //val b = PublishSubject
    }

    fun log(msg: String, throwable: Throwable? = null) {
        Logger.e("${this.javaClass.canonicalName} : $msg", throwable)
    }

    // update progress/led here
    var remainingProgramTime: Int = 0
    var remainingProgramAction: Int = 0
    var remainingProgramPause: Int = 0
    var currentBlock: Int = 0
    var currentProgram: Int = 0
    var program: Program? = null


    private var mCurrentBlock = 0
    private var mCurrentTimerBlock: Long = 0

    private fun updateStatus(
        remainingTime: Int,
        action: Int,
        pause: Int,
        currentBlock: Int,
        currentProgram: Int,
        uid: String?
    ) {
        log("UpdateStatus.. time: $remainingTime , pause: $pause , action: $action , block: $currentBlock , program: $currentProgram")

        if (pause > 0) {
            progress(1, 100, pauseDuration)
        } else if (action > 0) {
            progress(100, 0, actionDuration)
        }
    }

    private fun updateStatus(action: Int, pause: Int) {
        log("UpdateStatus.. pause: $pause , action: $action  status=" + SessionManager.getInstance().userSession.booster?.deviceSessionTimer)

        if (pause > 0) {
            progress(100, 1, pauseDuration)
        } else if (action > 0) {
            progress(1, 99, actionDuration)
        }
    }

    var lastFrom = -1
    fun progress(valueFrom: Int, valueTo: Int, duration: Int) {
        if (lastFrom == valueFrom)
            return
        lastFrom = valueFrom
       // Observable.fromCallable {  }
        Observable.empty<String>().observeOn(AndroidSchedulers.mainThread()).doOnComplete {
            if (duration == 0)
                progressBar!!.visibility = View.GONE
            else
                progressBar!!.visibility = View.VISIBLE

            if (valueFrom == 100)
                progressBar!!.progressTintList = ColorStateList.valueOf(Color.GREEN)
            else
                progressBar!!.progressTintList = ColorStateList.valueOf(Color.RED)

            ObjectAnimator.ofInt(progressBar, "progress", valueFrom, valueTo)
                .setDuration(duration.toLong())
                .start()
        }.subscribe()

        //Functions.emptyConsumer(), Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION, Functions.emptyConsumer()
        // Functions.emptyConsumer(), Functions.emptyConsumer(), onComplete, Functions.EMPTY_ACTION


        //val animator = ObjectAnimator.ofInt(progressBar, "progress", valueFrom, valueTo).setDuration(duration);
        //animator.start()
    }

    var mHandler: Handler? = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val block: Block = program?.blocks?.get(mCurrentBlock) ?: return;
            if (mCurrentTimerBlock > Integer.parseInt(program!!.blocks[mCurrentBlock].pauseDuration.value)) {
                if (mCurrentTimerBlock >= Integer.parseInt(block.pauseDuration.value) +
                    Integer.parseInt(block.actionDuration.value) +
                    Integer.parseInt(block.downRampDuration.value) +
                    Integer.parseInt(block.upRampDuration.value)
                ) {
                    mCurrentTimerBlock = mCurrentTimerBlock - 100
                } else {
                    progressBar!!.progressTintList = ColorStateList.valueOf(Color.RED)
                    progressBar!!.progress = ((mCurrentTimerBlock.toDouble() - Integer.parseInt(
                        block.pauseDuration.value
                    ).toDouble()) / (Integer.parseInt(
                        block.actionDuration.value
                    ) +
                            Integer.parseInt(block.downRampDuration.value) +
                            Integer.parseInt(block.upRampDuration.value)).toDouble() * 100.0).toInt()
//                    txtBarValue.setTextColor(Color.RED)
//                    txtBarValue.setText(
//                        "" + ((mCurrentTimerBlock - Integer.parseInt(
//                            mProgram.getBlocks().get(
//                                mCurrentBlock
//                            ).getPauseDuration().getValue()
//                        )) / 1000 + 1)
//                    )

                }

            } else if (mCurrentTimerBlock <= Integer.parseInt(block.pauseDuration.value)) {
                progressBar!!.progressTintList = ColorStateList.valueOf(Color.GREEN)
                progressBar!!.progress =
                    100 - (mCurrentTimerBlock.toDouble() / Integer.parseInt(
                        block.pauseDuration.value
                    ).toDouble() * 100.0).toInt()
                // txtBarValue.setTextColor(Color.GREEN)
                // txtBarValue.setText("" + ((Integer.parseInt(mProgram.getBlocks().get(mCurrentBlock).getPauseDuration().getValue()) - mCurrentTimerBlock) / 1000 + 1))

            }

        }
    }

    class TickHandler(val tvTimer: TextView?, val end: Long) {

        val lastTick = AtomicLong(0L)
        var disposable: Disposable? = null

        fun start() {
            disposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    tvTimer?.text = "$it sec"
                }.takeUntil { a ->
                    lastTick.getAndIncrement()
                    a == end
                }.doOnComplete {
                    tvTimer?.text = "Completed"
                    disposable?.dispose()
                }.subscribe()
        }

        fun resume() {


        }

        fun stop() {
            disposable?.dispose()
        }
    }

    // APIs
    var sessionId = ""
    var isBooked = false
    var isPaused = true

    private fun bookSession(programId: Int) {
        val member = Prefs.get(fragment.context).member ?: return

        fragment.getDialog()?.show()
        SimpleDateFormat.getDateInstance()
        val post = Data(
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            SimpleDateFormat("hh:mm:ss").format(Date()), member.id, programId
        )

        API.request.getApi().bookSession(BookSessionPost(post = post, auth = member.accessToken!!))
            .enqueue(object : Callback<BookSession> {

                override fun onFailure(call: Call<BookSession>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                }

                override fun onResponse(
                    call: Call<BookSession>,
                    response: Response<BookSession>
                ) {
                    fragment.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            Toasty.info(
                                fragment.requireContext(),
                                "${data.data?.message}",
                                Toasty.LENGTH_SHORT,
                                false
                            ).show()
                            sessionId = "${data.data?.sessionID}"
                            Prefs.get(fragment.context).set("member_sessionId", sessionId)
                            isSessionActive = true
                            startMemberSession()
                            isBooked = true

                        } else if (data.status.equals("error", true)) {
                            Toasty.error(
                                fragment.requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()

                            MiboEvent.log("bookAndStartConsumerSession :: error $data")
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                    }
                }
            })
    }


    private fun saveSessionApi() {
        val member = Prefs.get(fragment.context).member ?: return

        fragment.getDialog()?.show()

        val calories = calculateCalories(
            SessionManager.getInstance().userSession.program.borgRating,
            getWeight().toDouble(),
            getTime()
        )

        val session = Session(
            calories, adapter?.getChannels(), getTime().toInt(),
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            member.id, SessionManager.getInstance().userSession.program.name, 1, 1,
            sessionId.toIntOrZero(), startTime, 0
        )


        API.request.getApi()
            .saveSessionReport(SaveSessionPost(post = session, auth = member.accessToken!!))
            .enqueue(object : Callback<ResponseData> {

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    log("SaveSession Error : " + t?.message)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    fragment.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
//                            Toasty.success(
//                                fragment.requireContext(),
//                                "${data.response?.message}",
//                                Toasty.LENGTH_SHORT,
//                                false
//                            ).show()
                            sessionCompleteDialog(data.response?.message)

                        } else if (data.status.equals("error", true)) {
                            Toasty.error(
                                fragment.requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()

                            MiboEvent.log("saveSessionReport :: error $data")
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        log("SaveSession : " + response?.errorBody()?.toString())
                    }
                }
            })
    }

    private fun startMemberSession() {
        startUserSession(userUid)
    }


    // Fernando code
    fun getCalories() {

//        if(SessionManager.getInstance().getSession().getCurrentSessionProgram().getBorgRating()> 6) {
//            double metFromBORG =((((double) SessionManager.getInstance().getSession().getCurrentSessionProgram().getBorgRating()-6.0)
//                    /14.0)*10.0);
//            txtCalories.setText("" + (int) ((double) Integer.parseInt(user.getMedicalHistory().getWeight()) * (double) 60 *
//                    metFromBORG*
//                    (double) 3.5 * ((double) user.getUserSessionTimer() / (double) 60 / (double) 60) / (double) 200
//            ));
//        }
//
//        if (SessionManager.getInstance().session.currentSessionProgram.borgRating > 6) {
//            val metFromBORG = (SessionManager.getInstance().session.currentSessionProgram.borgRating.toDouble() - 6.0) / 14.0 * 10.0
//            txtCalories?.text = "" + (Integer.parseInt(user.medicalHistory.weight).toDouble() * 60.toDouble() *
//                    metFromBORG *
//                    3.5 * (user.userSessionTimer.toDouble() / 60.toDouble() / 60.toDouble()) / 200.toDouble()).toInt()
//        }
//
//        if (SessionManager.getInstance().session.currentSessionProgram.borgRating > 6) {
//            val metFromBORG = (SessionManager.getInstance().session.currentSessionProgram.borgRating.toDouble() - 6.0) / 14.0 * 10.0
//            txtCalories?.text = "" + (Integer.parseInt(user.medicalHistory.weight).toDouble() * 60.toDouble() *
//                    metFromBORG *
//                    3.5 * (user.userSessionTimer.toDouble() / 60.toDouble() / 60.toDouble()) / 200.toDouble()).toInt()
//        }
//
//
//        setCaloriesBurnt( (int) ((double) Integer.parseInt(user.getMedicalHistory().getWeight()) * (double) 60 *
//                ((double) SessionManager.getInstance().getSession().getCurrentSessionProgram().getBorgRating()
//                        )*
//                (double) 3.5 * ((double) user.getUserSessionTimer() / (double) 60 / (double) 60) / (double) 200));

    }

    //Calories Calculate
    //(((BORG RATING+17%)*weight(kg))*2(equal to 2 hours workout in a gym))+10%(afterburn)=total calories burned during a 20 minute session
    private fun calculateCalories(borg: Int, weight: Double, time: Long): Int {
        //borg 12, weight 88.0, time 16    borgRate  4.285714285714286

        try {
            //val borg = SessionManager.getInstance().userSession.program.borgRating
            log("calculateCalories borg $borg, weight $weight, time $time")
            val t: Double = time.toDouble().div(60).div(60).div(200)
            val burnt: Double = if (borg > 6) {
                val borgRate = (borg - 6.0) / 14.0 * 10.0
                log("calculateCalories borgRate  $borgRate")
                val w = weight.times(60).times(borgRate).times(3.5)
                //(weight * 60 * borgRate * 3.5 * (time / 60 / 60) / 200)
                w.times(t)
            } else {
                val w = weight.times(60).times(borg).times(3.5)
                //(weight * 60 * borg * 3.5 * (time / 60 / 60) / 200)
                w.times(t)
            }

            log("calculateCalories t $t")
            log("calculateCalories times ${t.times(weight).times(60).times(borg).times(3.5)}")
            log("calculateCalories CaloriesBurnt  $burnt")

            //user.medicalHistory.weight
            //user.userSessionTimer.toDouble()
            return burnt.plus(0.5).roundToInt()
        } catch (e: Exception) {
            MiboEvent.log("ERROR: calculateCalories borgRate $borg, weight $weight, time $time :: " + e?.message)
        }

        return 0
        //calculateCalories(SessionManager.getInstance().userSession.program.borgRating, getWeight().toDouble(), getTime())
    }

    private var userWeight: Int = 0
    private fun getWeight(): Int {
        if (userWeight != 0)
            return userWeight;
        val s: Report? = Prefs.get(fragment.context).getJson(Prefs.SESSION, Report::class.java)
        if (s?.sessionMemberReports != null) {
            s.weight?.let {
                userWeight = it.toIntOrZero()
                return userWeight
            }
        }

        val member = Prefs.get(fragment.context).member ?: return userWeight

        fragment.getDialog()?.show()
        val session = UserDetailsPost("${member.id}", member.accessToken)
        API.request.getApi().userDetails(session).enqueue(object : Callback<UserDetails> {
            override fun onFailure(call: Call<UserDetails>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, "Unable to connect").show()
            }

            override fun onResponse(call: Call<UserDetails>, response: Response<UserDetails>) {

                val data = response.body()
                if (data != null && data.status.equals("success")) {
                    data.data?.medicalHistory?.weight?.let {
                        userWeight = it.toIntOrZero()
                        //return userWeight
                    }
                } else {

                    val err = data?.errors?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.context!!, R.string.error_occurred).show()
                    else Toasty.error(fragment.context!!, err, Toasty.LENGTH_LONG).show()
                }
                fragment.getDialog()?.dismiss()
            }
        })

        return userWeight
    }

    private fun getTime(): Long {
        if (totalDuration == currentDuration)
            return totalDuration;
        tvTimer?.text?.let {
            if (it == "Completed")
                return totalDuration
        }
        return totalDuration.minus(currentDuration)
    }



}