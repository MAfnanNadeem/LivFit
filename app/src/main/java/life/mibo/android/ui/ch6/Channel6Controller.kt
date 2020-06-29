package life.mibo.android.ui.ch6

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.fitness.FitnessActivities
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.core.toIntOrZero
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.base.ResponseStatus
import life.mibo.android.models.biometric.Biometric
import life.mibo.android.models.create_session.*
import life.mibo.android.models.login.Member
import life.mibo.android.models.muscle.Muscle
import life.mibo.android.models.trainer.SaveTrainerSessionReport
import life.mibo.android.models.trainer.StartTrainerSession
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.ch6.adapter.Channel6Listener
import life.mibo.android.ui.ch6.adapter.ChannelAdapter
import life.mibo.android.ui.fit.GoogleFit
import life.mibo.android.ui.login.LoginActivity
import life.mibo.android.ui.main.MessageDialog
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.rxl.adapter.ScoreAdapter
import life.mibo.android.ui.rxl.impl.ScoreDialog
import life.mibo.android.utils.Toasty
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.*
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.User
import life.mibo.hardware.models.UserSession
import life.mibo.hardware.models.program.Circuit
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class Channel6Controller(val fragment: Channel6Fragment, val observer: ChannelObserver) :
    Channel6Listener,
    Channel6Fragment.Listener {


    override fun onBackPressed(): Boolean {
        if (isSessionActive) {
            if (isTrainer) {
                sessionCompleteTrainerDialog(1)
                return false
            }
            MessageDialog(
                fragment.requireContext(),
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
            fragment.requireContext(),
            fragment.getString(R.string.session_completed),
            msg ?: "Congrats you have completed session",
            "",
            "close",
            object : MessageDialog.Listener {
                override fun onClick(button: Int) {
                    navigateToHome()
                }
            })
        d.setCancelable(false)
        d.show()
    }

    fun onBoosterAlaram(code: Int) {

    }

    private var isTrainerDialog = false
    private fun sessionCompleteTrainerDialog(type: Int) {
        if (isTrainerDialog)
            return
        isTrainerDialog = true
        log("sessionCompleteTrainerDialog")
        val cancel: Boolean
        val title: String?
        val msg: String?
        if (type == 1) {
            cancel = false
            title = fragment.getString(R.string.stop_session)
            msg = fragment.getString(R.string.stop_session_message)
        } else {
            cancel = true
            title = SessionManager.getInstance().userSession.program?.name
            msg = fragment.getString(R.string.session_completed)
        }

        val time = getProgramTime()
        val calories = calculateCalories(
            SessionManager.getInstance().userSession.program.borgRating,
            getWeight().toDouble(),
            time
        )

        val di = FeedbackDialog(
            fragment.requireContext(),
            object : ItemClickListener<FeedbackDialog.Feedback> {
                override fun onItemClicked(item: FeedbackDialog.Feedback?, position: Int) {
                    // Toasty.info(fragment.requireContext(), "clicked $item").show()
                    try {
                        if (type == 1)
                            fragment.event(
                                "stopped",
                                "sessionId=$trainerSessionId userId=$trainerUserId",
                                "duration=$time  calories=$calories"
                            )
                        else fragment.event(
                            "complete",
                            "sessionId=$trainerSessionId userId=$trainerUserId",
                            "duration=$time  calories=$calories"
                        )
                    } catch (e: java.lang.Exception) {

                    }
                    isTrainerDialog = false
                    if (item != null) {
                        if (item.id >= 1) {
                            //exerciseCompleted(true)
                            cancelTimer()
                            stopUserSession(userUid)
                            isSessionActive = false
                            //demoSave()
                            saveTrainerSessionApi(
                                trainerUserId,
                                item.rating,
                                item.feedback,
                                calories,
                                time
                            )

                            //cancelTimer()
                            return
                        }
                    }
                }

            },
            cancel, title, msg, time, "$calories"
        )
        di.setOnDismissListener {
            isTrainerDialog = false
            log("sessionCompleteTrainerDialog dismissed $isTrainerDialog")
        }
        di.show()
    }

    fun demoSave() {
        Toasty.info(fragment.requireContext(), "Demo Saved!", Toasty.LENGTH_LONG).show()
        // navigateToHome()

    }

    private fun sessionCompleteDialog2(name: String?, time: String?, calory: String?) {
        val list = ArrayList<ScoreAdapter.ScoreItem>()

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
        list.add(
            ScoreAdapter.ScoreItem(
                1,
                "",
                "$calory",
                R.drawable.ic_booster_energy,
                R.drawable.rxl_score_hits,
                0,
                R.string.calories_burnt
            )
        )

        val dialog = ScoreDialog(fragment.requireContext(), name, list)
        dialog.listener = object : ItemClickListener<ScoreAdapter.ScoreItem> {
            override fun onItemClicked(item: ScoreAdapter.ScoreItem?, position: Int) {
                navigateToHome()
            }
        }
        dialog.show()
        EventBus.getDefault().postSticky(SendDeviceStopEvent(userUid))
    }

    fun closeDialog() {
        AlertDialog.Builder(fragment.requireContext()).setTitle("Stop Session?")
            .setMessage("Are you sure to want stop current session?")
            .setNeutralButton("Continue", null)
            .setPositiveButton("Stop Session") { i, j ->


            }.create().show()
    }

    //var list = ArrayList<Channel6Model>()
    var eventTag = "myboost_session"
    var isSessionActive = false
    var progressBar: ProgressBar? = null
    var tvTimer: TextView? = null
    var adapter: ChannelAdapter? = null
    private lateinit var viewModel: Channel6ViewModel
    private lateinit var userUid: String
    var recyclerView: RecyclerView? = null

    //private var manager: CommunicationManager? = null
    private var isConnected = false
    var pauseDuration: Int = 0
    var actionDuration: Int = 0
    private val plays = BooleanArray(10)

    fun getBoosterUid() = userUid

    // TODO Overrides
    override fun onViewCreated(view: View, bundle: Bundle) {

        viewModel = ViewModelProviders.of(this.fragment).get(Channel6ViewModel::class.java)
        userUid = SessionManager.getInstance().userSession?.booster?.uid ?: ""

        setRecycler(view.findViewById(R.id.recyclerView), bundle)
        progressBar = view.findViewById(R.id.progressBar)
        tvTimer = view.findViewById(R.id.tv_timer)

        //EventBus.getDefault().postSticky(SendProgramEvent(SessionManager.getInstance().userSession?.currentSessionProgram, uid))
        if (SessionManager.getInstance().userSession != null) {
            if (SessionManager.getInstance().userSession.user != null) {
                sendProgramToBooster()
                programDuration = SessionManager.getInstance().userSession.program.durationSeconds
                tvTimer?.text =
                    String.format("%02d:%02d", programDuration / 60, programDuration % 60)
                //programDuration = SessionManager.getInstance().userSession.program?.duration?.valueInt ?: 0
                if (SessionManager.getInstance().userSession.booster != null && SessionManager.getInstance().userSession.program != null) {
                    isConnected = true
                    pauseDuration =
                        SessionManager.getInstance().userSession.program.blocks[0].pauseDuration.valueInteger
                    actionDuration =
                        SessionManager.getInstance().userSession.program.blocks[0].actionDuration.valueInteger
                    checkBattery(SessionManager.getInstance().userSession.booster)
                }
                checkAndUpdateValues()
            }
        }

        if (!isConnected) {
            Toasty.snackbar(tvTimer, fragment.getString(R.string.device_not_connected))
        }

    }

    private fun checkBattery(device: Device?) {
        device?.let {
            if (it.batteryLevel < 30) {
                Toasty.closeSnackbar(
                    this.fragment.view,
                    R.string.low_battery
                )

            }
        }
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
        log("onPlayClicked paused: $isPlaying : " + SessionManager.getInstance().userSession.currentSessionStatus)
        if (isConnected) {
            SessionManager.getInstance().userSession.program.id?.let {
                if (isBooked) {
                    //pause // restart
                    pauseResumeClicked()
//                    if (SessionManager.getInstance().userSession.currentSessionStatus == 2) {
//                        pauseUserSession()
//                    } else if (SessionManager.getInstance().userSession.currentSessionStatus == 1) {
//                        startUserSession(SessionManager.getInstance().userSession.user)
//                    } else {
//                        Toasty.snackbar(
//                            fragment.view,
//                            fragment.getString(R.string.booster_no_response)
//                        )
//                    }
                } else {
                    bookSession(it.toIntOrZero())
                }
            }
        } else {
            Toasty.info(
                this.fragment.requireContext(),
                R.string.no_device_connected,
                Toasty.LENGTH_SHORT,
                false
            ).show()
        }
    }

    override fun onMainStopClicked() {
        if (isConnected)
            onBackPressed()
        //stopUserSession(userUid)
    }

    override fun onClick(data: Muscle) {

    }

    override fun onPlusClicked(data: Muscle) {
        log("onPlusClicked channel : $data")
        if (isConnected)
            onMusclePlusClicked(data.getChanneld(), data.getGovern())
    }

    override fun onMinusClicked(data: Muscle) {
        log("onMinusClicked channel : $data")
        if (isConnected)
            onMuscleMinusClicked(data.getChanneld(), data.getGovern())
    }

    override fun onPlayPauseClicked(data: Muscle, isPlay: Boolean) {
        if (isConnected)
            onMusclePlayStopClicked(data.getChanneld(), isPlay)
    }

    // TODO hit session report API.
    override fun exerciseCompleted(userStopped: Boolean) {
        //progress(0, 100, 0)

        //API
        if (userStopped)
            stopUserSession(userUid)
        saveSessionApi()

        isSessionActive = false
        //then
        //fragment.navigate(Navigator.CLEAR_HOME, null)
        cancelTimer()
    }

    private fun navigateToHome() {
        fragment.navigate(Navigator.CLEAR_HOME, null)
    }


    private var isTrainer = false
    private var trainerSessionId: Int = 0
    private var trainerUserId: Int = 0
    private var trainerUserWeight: Int = 0
    // TODO Functions
    // 6ch
    // 1- Lower back  2- Shoulder  3- Upper back   4- Abs  5- Chest 6- Arms
    private fun setRecycler(recycler: RecyclerView, bundle: Bundle) {

        // if (list == null)
        //    list = ArrayList()
        // list.clear()
        val list = ArrayList<Muscle>()
        if (bundle.getBoolean("is_trainer", false)) {
            trainerSessionId = bundle.getInt("session_id", 0)
            trainerUserId = bundle.getInt("userId_id", 0)
            val weight = bundle.getString("user_weight", "0")
            val unit = bundle.getString("user_weight_unit", "kg")
            try {
                if (unit.toLowerCase().contains("lb") || unit.toLowerCase()?.contains("lbs"))
                    trainerUserWeight = Calculate.poundToKg(weight?.toInt())
                else trainerUserWeight = weight?.toInt() ?: 0
            } catch (e: java.lang.Exception) {
                try {
                    if (unit.toLowerCase().contains("lb") || unit.toLowerCase()?.contains("lbs"))
                        trainerUserWeight =
                            Calculate.poundToKg(weight.replace("[^0-9]".toRegex(), "").toInt())
                    else trainerUserWeight = weight.replace("[^0-9]".toRegex(), "").toInt()
                } catch (e: java.lang.Exception) {

                }

            }
            isTrainer = true
            list.addAll(getTrainerList())
            recycler.layoutManager = GridLayoutManager(fragment.context, 1)
            adapter = ChannelAdapter(list, false)
            adapter?.setListener(this)
            log("setRecycler trainerSessionId $trainerSessionId - trainerUserId $trainerUserId trainerUserWeight $trainerUserWeight")
            //val manager = GridLayoutManager(this@DeviceScanFragment.activity, 1)
            //recycler.layoutManager = manager
            recycler.adapter = adapter
            return
        }


        var serialize = bundle.getSerializable("program_channels")
        if (serialize is Collection<*>) {
            list.addAll(serialize as Collection<Muscle>)
        }
//        list.add(Channel6Model(1, R.drawable.ic_channel_back_neck, 0, 0))
//        list.add(Channel6Model(2, R.drawable.ic_channel_glutes, 0, 0))
//        list.add(Channel6Model(3, R.drawable.ic_channel_thighs, 0, 0))
//        list.add(Channel6Model(4, R.drawable.ic_channel_abdomen, 0, 0))
//        list.add(Channel6Model(5, R.drawable.ic_channel_chest, 0, 0))
//        list.add(Channel6Model(6, R.drawable.ic_channel_biceps, 0, 0))


        recycler.layoutManager = GridLayoutManager(fragment.context, 1)
        adapter = ChannelAdapter(list, false)
        adapter?.setListener(this)
        //val manager = GridLayoutManager(this@DeviceScanFragment.activity, 1)
        //recycler.layoutManager = manager
        recycler.adapter = adapter
    }

    fun getTrainerList(): ArrayList<Muscle> {

//        bindMuscleGroup(muscleGroup1, 1, R.drawable.img_frontlegs);
//        bindMuscleGroup(muscleGroup2, 2, R.drawable.img_backlegs);
//        bindMuscleGroup(muscleGroup3, 3, R.drawable.img_glu);
//        bindMuscleGroup(muscleGroup4, 4, R.drawable.img_lower_back);
//        bindMuscleGroup(muscleGroup5, 5, R.drawable.img_uper_back);
//        bindMuscleGroup(muscleGroup6, 6, R.drawable.img_shoulder2);
//        bindMuscleGroup(muscleGroup7, 7, R.drawable.img_abs);
//        bindMuscleGroup(muscleGroup8, 8, R.drawable.img_pec);
//        bindMuscleGroup(muscleGroup9, 9, R.drawable.img_arms);
//        bindMuscleGroup(muscleGroup10, 10, R.drawable.img_fullbodyback);

        val list = ArrayList<Muscle>()
        list.add(Muscle(1, "", "", 1, 100, R.drawable.ic_channel_fron_leg))
        list.add(Muscle(2, "", "", 2, 100, R.drawable.ic_channel_back_leg))
        list.add(Muscle(3, "", "", 3, 100, R.drawable.ic_channel_glutes))
        list.add(Muscle(4, "", "", 4, 100, R.drawable.ic_channel_lower_back))
        list.add(Muscle(5, "", "", 5, 100, R.drawable.ic_channel_upper_back))
        list.add(Muscle(6, "", "", 6, 100, R.drawable.ic_channel_neck))
        list.add(Muscle(7, "", "", 7, 100, R.drawable.ic_channel_abs))
        list.add(Muscle(8, "", "", 8, 100, R.drawable.ic_channel_chest))
        list.add(Muscle(9, "", "", 9, 100, R.drawable.ic_channel_biceps))
        list.add(Muscle(10, "", "", 10, 100, R.drawable.ic_channel_calfs))
        return list
    }

    fun checkAndUpdateValues() {
        try {
            Observable.fromIterable(adapter!!.list).doOnNext {
                SessionManager.getInstance().userSession.user.checkAndSetChannelValue(
                    it.getChanneld().minus(1), it.channelValue
                )
            }.doOnError {

            }.subscribe()
        } catch (e: java.lang.Exception) {

        }
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
//            }.takeUntil { DialogListener ->
//                DialogListener == end
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
//        log("startSession sendReStartToBooster")
//        sendReStartToBooster(u)
//        try {
//            Thread.sleep(600)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//        log("startSession sendStartToBooster")
//        sendStartToBooster(u)
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
                this.fragment.requireContext(),
                fragment.getString(R.string.no_device_connected)
            ).show()
            return
        }
        startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        // log("sendPlaySignals " + SessionManager.getInstance().userSession?.user?.debugLevels())
        Observable.timer(0, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting SendChannelsLevelEvent")
                sendChannelLevelsToAllBoosters(SessionManager.getInstance().userSession.user)
            }.subscribe()

        Observable.timer(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("starting again device")
                sendReStartToBooster(SessionManager.getInstance().userSession.user)

            }.subscribe()
        Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("BEEP 1 starting again device")
                SessionManager.getInstance().userSession.isBooster = true
                startUserSession(SessionManager.getInstance().userSession.user)
                //EventBus.getDefault().postSticky(SendDevicePlayEvent(uid))
            }.subscribe()

        Observable.timer(4, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("BEEP 2....... isStartedSession $isStartedSession")
                if (!isStartedSession) {
                    log("BEEP 2....... isStartedSession $isStartedSession")
                    startUserSession(SessionManager.getInstance().userSession.user)
                }
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

    var isProgramSent = false
    private fun sendProgramToBooster() {
        //log("sendProgramToBooster SendProgramEvent "+SessionManager.getInstance().userSession?.program?.duration)

        Single.fromCallable {
            if (SessionManager.getInstance().userSession.currentSessionStatus == 0) {
                EventBus.getDefault().postSticky(
                    SendProgramEvent(
                        SessionManager.getInstance().userSession.program,
                        userUid
                    )
                )
                log("sendProgramToBooster SendProgramEvent sent")
                isProgramSent = true
            }
        }.subscribeOn(Schedulers.io()).delay(100, TimeUnit.MILLISECONDS).subscribe()

        Single.fromCallable {
            EventBus.getDefault().postSticky(
                SendMainLevelEvent(
                    SessionManager.getInstance().userSession.user.mainLevel,
                    userUid
                )
            )
            log("SendMainLevelEvent onMainPlusMinusClicked level " + SessionManager.getInstance().userSession.user.mainLevel)
        }.subscribeOn(Schedulers.io()).delay(1000, TimeUnit.MILLISECONDS).subscribe()
    }


    private var isStartedSession = false
    private fun startUserSession(u: User) {
        if (u.isActive) {
            SessionManager.getInstance().userSession.booster.isStarted = true
            EventBus.getDefault().postSticky(SendDevicePlayEvent(userUid))
            //DevicePlayPauseEvent
            SessionManager.getInstance().userSession.currentSessionStatus = 2
           // isPaused = true;
           // observer.updatePlayButton(isPaused);
            //isStartedSession = true
        } else {
            Toasty.snackbar(fragment.view, fragment.getString(R.string.booster_inactive))
        }
    }


    private fun pauseUserSession() {
        if (SessionManager.getInstance().userSession.user.isActive) {
            //SendMainLevelEvent(SessionManager.getInstance().userSession.user)

            SessionManager.getInstance().userSession.booster.isStarted = false
            EventBus.getDefault()
                .postSticky(SendDeviceStopEvent(SessionManager.getInstance().userSession.booster.uid))
            // SessionManager.getInstance().userSession.user.mainLevel = 0

            SessionManager.getInstance().userSession.currentSessionStatus = 1
            SessionManager.getInstance().userSession.program?.setDuration(currentDuration.toInt())
            log("sendProgramToBooster SendProgramEvent again... $currentDuration")
            EventBus.getDefault().postSticky(
                SendProgramEvent(
                    SessionManager.getInstance().userSession.program, userUid
                )
            )
            cancelTimer()
            //countTimer?.onFinish()
//            SessionManager.getInstance().userSession.booster.isStarted = true
//            EventBus.getDefault().postSticky(SendDevicePlayEvent(userUid))
//            SessionManager.getInstance().userSession.currentSessionStatus = 1
            isPlaying = false;
            observer.updatePlayButton(isPlaying);
            Single.just(lastFrom).observeOn(AndroidSchedulers.mainThread())
                .delay(200, TimeUnit.MILLISECONDS).doOnSuccess {
                    fragment?.activity?.runOnUiThread {
                        progressBar!!.visibility = View.GONE
                    }
                    lastFrom = -1
                }.subscribe()

//            log("pauseUserSession")
            // Toasty.info(fragment?.requireContext(), "Pause functionality is remaining").show()

        } else {
            Toasty.snackbar(fragment.view, fragment.getString(R.string.booster_inactive))
        }

    }

    private fun sendReStartToBooster(u: User) {
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
        log("stopUserSession uid $uid")
        if (SessionManager.getInstance().userSession.booster == null) {
            Toasty.warning(
                this.fragment.requireActivity(),
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
        isStartedSession = false
        //isBooked = false
        //progress(0, 0, 0)
    }

    // group control power clicked
    private fun sendCircuitToAllBoosters(u: User) {
        EventBus.getDefault().postSticky(SendCircuitEvent(Circuit(), userUid))
    }

    private fun sendStopToBooster(u: User) {
        SessionManager.getInstance().userSession.booster.isStarted = false
        EventBus.getDefault().postSticky(
            SendDeviceStopEvent(
                SessionManager.getInstance().userSession.booster.uid
            )
        )
    }

    private fun onMuscleMinusClicked(id: Int, govern: Int) {
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

    private fun onMusclePlusClicked(id: Int, govern: Int) {
        if (SessionManager.getInstance().userSession.user.checkAndIncreaseChannel(id, govern)) {
            EventBus.getDefault().postSticky(
                SendChannelsLevelEvent(
                    SessionManager.getInstance().userSession.user.currentChannelLevels,
                    userUid
                )
            )
        }

        log("onMusclePlusClicked Plus group $id $govern")
        // updateItem(id)
    }


    private fun onMusclePlayStopClicked(id: Int, play: Boolean) {
        try {
            log("onMusclePlayStopClicked $play $id")
            if (SessionManager.getInstance().userSession.booster.isStarted) {
                SessionManager.getInstance().userSession.user.currentChannelLevels[id - 1] = 0
                EventBus.getDefault().postSticky(
                    SendChannelsLevelEvent(
                        SessionManager.getInstance().userSession.user.currentChannelLevels,
                        userUid
                    )
                )
                plays[id - 1] = false
                //updateItem(id)
                //sendStopToBooster(SessionManager.getInstance().userSession.user)
            } else {
                plays[id - 1] = true
                startUserSession(SessionManager.getInstance().userSession.user)
                //sendStopToBooster(SessionManager.getInstance().userSession.user)
            }

            log("plays... " + plays.contentToString())
        } catch (e: java.lang.Exception) {

        }

    }

//    fun getManager(): CommunicationManager {
//        if (manager == null)
//            manager = CommunicationManager.getInstance()
//        return manager!!
//    }

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
            .subscribe(object : io.reactivex.Observer<Muscle> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Muscle) {
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
        log("onMainPlusMinusClicked $isPlus")
        //var update = false
        var update = if (isPlus) {
            SessionManager.getInstance().userSession.user.incrementMainLevelUser()
        } else {
            SessionManager.getInstance().userSession.user.decrementMainLevelUser()
        }
        if (update) {
            Single.fromCallable {
                EventBus.getDefault().postSticky(
                    SendMainLevelEvent(
                        SessionManager.getInstance().userSession.user.mainLevel,
                        userUid
                    )
                )
                log("SendMainLevelEvent onMainPlusMinusClicked level " + SessionManager.getInstance().userSession.user.mainLevel)
            }.subscribeOn(Schedulers.io()).delay(200, TimeUnit.MILLISECONDS).subscribe()
//            Handler().postDelayed({
//
//                log("onMainPlusMinusClicked level " + SessionManager.getInstance().userSession.user.mainLevel)
//            }, 200)
        }
    }


    // TODO NEW
    var isDevicesPlaying = false
    private fun pauseResumeClicked() {
        log("pauseResume clicked $isDevicesPlaying")
        if (isSessionActive) {
            if (isDevicesPlaying) {
                isDevicesPlaying = false
                Single.just("").doOnSuccess {
                    CommunicationManager.getInstance().onDeviceResumePauseEvent(
                        DevicePauseResumeEvent(userUid, 1)
                    )
                }.subscribe()
            } else {
                isDevicesPlaying = true
                Single.just("").doOnSuccess {
                    CommunicationManager.getInstance().onDeviceResumePauseEvent(
                        DevicePauseResumeEvent(userUid, 0)
                    )
                }.subscribe()
            }
        }
    }

    fun onDeviceDisconnected(code: Int) {
        log("onDeviceDisconnected onBoosterAlarm session is active")
        onPauseFromDevice(false)
    }

    fun onResumeFromDevice(fromDevice: Boolean) {
        isStartedSession = true
        isSessionActive = true
        log("onResumeFromDevice ")
        disposable?.dispose()
        disposable = null
        if (currentDuration > 0)
            startTimer(currentDuration.toInt(), "1")
        else startTimer(programDuration, "3")


        SessionManager.getInstance().userSession.booster.isStarted = true
        SessionManager.getInstance().userSession.currentSessionStatus = 2
        Observable.fromIterable(adapter!!.list!!).subscribeOn(Schedulers.computation())
            .delay(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Muscle> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Muscle) {
                    //log("onPlayClicked " + SessionManager.getInstance().userSession)
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
    }

    fun onPauseFromDevice(fromDevice: Boolean) {
        disposable?.dispose()
        log("onPauseFromDevice")
        Observable.fromArray(adapter!!.list!!).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Muscle> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Muscle) {
                    t.isPlay = true
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()
                    SessionManager.getInstance().userSession.booster.isStarted = false
                    // SessionManager.getInstance().userSession.user.mainLevel = 0
                    SessionManager.getInstance().userSession.currentSessionStatus = 1
                    cancelTimer()
                    Single.just(lastFrom).observeOn(AndroidSchedulers.mainThread())
                        .delay(200, TimeUnit.MILLISECONDS).doOnSuccess {
                            fragment?.activity?.runOnUiThread {
                                progressBar!!.visibility = View.GONE
                            }
                            lastFrom = -1
                        }.subscribe()

                }
            })
    }

    //


    // TODO Event bus events

    // var isChannelClicked = false

    // for main click
    fun onDevicePlayPauseEvent(event: DevicePlayPauseEvent) {
        log("onDevicePlayPauseEvent isPlaying $isPlaying")
        isStartedSession = true
//        if (isChannelClicked) {
//            isChannelClicked = false
//            return
//        }

//        if (MiboApplication.DEBUG) {
//            fragment?.activity?.runOnUiThread {
//                val p = SessionManager.getInstance().userSession.program
//                Toasty.snackbar(
//                    tvTimer,
//                    "time: " + p.duration?.valueInt + " Block: " + p.blocks?.size
//                )
//            }
//        }
        //        EventBus.getDefault().postSticky(
//            SendProgramEvent(
//                SessionManager.getInstance().userSession.currentSessionProgram,
//                SessionManager.getInstance().userSession.device.uid
//            )
//        )
        if (SessionManager.getInstance().userSession.booster.isStarted) {
            onResumeFromDevice(false)
        } else {
            onPauseFromDevice(false)
        }

    }

    var buttonId = 0
    fun onGetMainLevelEvent(event: GetMainLevelEvent) {
        log("onGetMainLevelEvent $event")
        //val items = ArrayList<Channel6Model>()

        Observable.fromArray(adapter?.list!!).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Muscle> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Muscle) {
                    t.mainValue = event.level
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
        val items = ArrayList<Muscle>()

        Observable.fromCallable {
                val levels = SessionManager.getInstance().userSession.user.currentChannelLevels

                log("onGetLevelsEvent $levels")
                adapter!!.list!!.forEach {
                    var i = it.getChanneld().minus(1)
                    if (i < 0)
                        i = 0
                    items.add(it.from(levels[i]))
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
        log("onGetProgramStatusEvent $isTimerStarted")
        EventBus.getDefault().removeStickyEvent(event)
        if (SessionManager.getInstance().userSession.booster.isStarted) {
            if (SessionManager.getInstance().userSession.user.tsLastStatus + 100 < System.currentTimeMillis()) {// sepueden ejecutar varios antes de hacer esto?
                SessionManager.getInstance().userSession.user.tsLastStatus =
                    System.currentTimeMillis()
                // Log.e("GroupController","event status");
                log("UpdateStatus.. getProgramStatusTime  " + event?.remainingTime)
                updateStatus(event.actionTime, event.pauseTime)
                // log("onGetProgramStatusEvent session time " + SessionManager.getInstance().userSession.currentSessionTimer)
                //updateStatus(event.remainingProgramTime, event.remainingProgramAction, event.remainingProgramPause, event.currentBlock, event.currentProgram, event.uid)
            }
            if (!isTimerStarted)
                startTimer(programDuration, "2")
        } else {
            fragment?.activity?.runOnUiThread {
                progressBar!!.visibility = View.GONE
            }
        }
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


    }


    // TODO Updates

    // timer
    private var programDuration = 0

    //private var totalDuration = 0L
    private var currentDuration: Long = 0L
    private var countTimer: CountDownTimer? = null
    var isTimerStarted = false
    private fun startTimer(seconds: Int, tag: String) {
        log("$tag : startTimer.............. $seconds $isTimerStarted")
        if (isTimerStarted)
            return
        fragment?.activity?.runOnUiThread {
            //totalDuration = seconds.toLong()
            cancelTimer()
            countTimer = null
            isTimerStarted = true
            isPlaying = true;
            isDevicesPlaying = true
            observer.updatePlayButton(isPlaying);
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
        // SessionManager.getInstance().userSession.startTimer(SessionManager.getInstance().userSession.program.duration.value.toLong())

    }

    internal fun cancelTimer() {
        log("cancelTimer ")
        if (countTimer != null) {
            fragment?.activity?.runOnUiThread {
                isDevicesPlaying = false
                countTimer?.cancel()
                //updateTime(SessionManager.getInstance().userSession.currentSessionTimer)
                isTimerStarted = false
                isPlaying = false;
                observer.updatePlayButton(isPlaying);
            }
        }
    }


    fun onTimerUpdate(time: Long) {
        log("onTimerUpdate $time")
        if (time == 0L) {
            SessionManager.getInstance().userSession.currentSessionStatus =
                UserSession.SESSION_FINISHED;
            pauseResumeClicked()
            fragment?.activity?.runOnUiThread {
                tvTimer?.text = fragment.getString(R.string.completed)
                currentDuration = 0
                isSessionActive = false
                isTimerStarted = false
                exerciseCompleted(true)

            }

        } else {
            currentDuration = time
            // tvTimer?.text = "${end.minus(it)} sec"
            tvTimer?.text = String.format("%02d:%02d", time / 60, time % 60)
            isTimerStarted = true
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
            .subscribe(object : io.reactivex.Observer<Muscle> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Muscle) {
                    t.mainValue = level
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
                item.channelValue = levels[i]
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun updateItem(id: Int) {
        adapter!!.list!!.forEachIndexed { i, item ->
            if (item.id == id) {
                item.channelValue =
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
                item.mainValue = level
                adapter?.notifyItemChanged(i)
                return@forEachIndexed
            }
        }
    }

    fun rxQueue() {
        //val MyWebViewClient = PublishSubject
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
        log("UpdateStatus.. pause: $pause , action: $action")

        if (pause > 0) {
            progress(100, 1, pause)
        } else if (action > 0) {
            progress(1, 99, action)
        }
    }

    private var lastFrom = -1
    private var lastDuration = -1
    fun progress(valueFrom: Int, valueTo: Int, duration: Int) {
        log("UpdateStatus progress from $valueFrom to $valueTo duration $duration")
        if (lastFrom == valueFrom && duration < lastDuration) {
            log("UpdateStatus progress return.........")
            return
        }
        lastDuration = duration
        lastFrom = valueFrom
        // Observable.fromCallable {  }
        Single.just(duration).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
                log("UpdateStatus progress observeOn.........$it")
                if (it == 0) {
                    progressBar!!.visibility = View.GONE
                    return@doOnSuccess
                } else
                    progressBar!!.visibility = View.VISIBLE

                if (valueFrom == 100)
                    progressBar!!.progressTintList = ColorStateList.valueOf(Color.GREEN)
                else
                    progressBar!!.progressTintList = ColorStateList.valueOf(Color.RED)

                ObjectAnimator.ofInt(progressBar, "progress", valueFrom, valueTo)
                    .setDuration(it.toLong())
                    .start()
            }.subscribe()

        //Functions.emptyConsumer(), Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION, Functions.emptyConsumer()
        // Functions.emptyConsumer(), Functions.emptyConsumer(), onComplete, Functions.EMPTY_ACTION


        //val animator = ObjectAnimator.ofInt(progressBar, "progress", valueFrom, valueTo).setDuration(duration);
        //animator.start()
    }

//    var program: Program? = null
//    var mHandler: Handler? = @SuppressLint("HandlerLeak")
//    object : Handler() {
//        override fun handleMessage(msg: Message) {
//            val block: Block = program?.blocks?.get(mCurrentBlock) ?: return;
//            if (mCurrentTimerBlock > Integer.parseInt(program!!.blocks[mCurrentBlock].pauseDuration.value)) {
//                if (mCurrentTimerBlock >= Integer.parseInt(block.pauseDuration.value) +
//                    Integer.parseInt(block.actionDuration.value) +
//                    Integer.parseInt(block.downRampDuration.value) +
//                    Integer.parseInt(block.upRampDuration.value)
//                ) {
//                    mCurrentTimerBlock = mCurrentTimerBlock - 100
//                } else {
//                    progressBar!!.progressTintList = ColorStateList.valueOf(Color.RED)
//                    progressBar!!.progress = ((mCurrentTimerBlock.toDouble() - Integer.parseInt(
//                        block.pauseDuration.value
//                    ).toDouble()) / (Integer.parseInt(
//                        block.actionDuration.value
//                    ) +
//                            Integer.parseInt(block.downRampDuration.value) +
//                            Integer.parseInt(block.upRampDuration.value)).toDouble() * 100.0).toInt()
////                    txtBarValue.setTextColor(Color.RED)
////                    txtBarValue.setText(
////                        "" + ((mCurrentTimerBlock - Integer.parseInt(
////                            mProgram.getBlocks().get(
////                                mCurrentBlock
////                            ).getPauseDuration().getValue()
////                        )) / 1000 + 1)
////                    )
//
//                }
//
//            } else if (mCurrentTimerBlock <= Integer.parseInt(block.pauseDuration.value)) {
//                progressBar!!.progressTintList = ColorStateList.valueOf(Color.GREEN)
//                progressBar!!.progress =
//                    100 - (mCurrentTimerBlock.toDouble() / Integer.parseInt(
//                        block.pauseDuration.value
//                    ).toDouble() * 100.0).toInt()
//                // txtBarValue.setTextColor(Color.GREEN)
//                // txtBarValue.setText("" + ((Integer.parseInt(mProgram.getBlocks().get(mCurrentBlock).getPauseDuration().getValue()) - mCurrentTimerBlock) / 1000 + 1))
//
//            }
//
//        }
//    }
//
//    class TickHandler(val tvTimer: TextView?, val end: Long) {
//
//        val lastTick = AtomicLong(0L)
//        var disposable: Disposable? = null
//
//        fun start() {
//            disposable = Observable.interval(1, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext {
//                    tvTimer?.text = "$it sec"
//                }.takeUntil { DialogListener ->
//                    lastTick.getAndIncrement()
//                    DialogListener == end
//                }.doOnComplete {
//                    tvTimer?.text = "Completed"
//                    disposable?.dispose()
//                }.subscribe()
//        }
//
//        fun resume() {
//
//
//        }
//
//        fun stop() {
//            disposable?.dispose()
//        }
//    }

    // TODO APIs
    var sessionId = ""
    var isBooked = false
    var isPlaying = false

    private fun bookSession(programId: Int) {
        val member = Prefs.get(fragment.context).member ?: return

        if (isTrainer) {
            startTrainerSession(programId, member)
            return
        }

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
                    Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
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
                            fragment.event(
                                "started_user",
                                "success",
                                "---"
                            )
                            Toasty.info(
                                fragment.requireContext(),
                                "${data.data?.message}",
                                Toasty.LENGTH_SHORT,
                                false
                            ).show()
                            sessionId = "${data.data?.sessionID}"
                            Prefs.get(fragment.context).set("member_sessionId", sessionId)
                            //isSessionActive = true
                            startMemberSession()
                            isBooked = true

                        } else if (data.status.equals("error", true)) {
                            Toasty.error(
                                fragment.requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()

                            MiboEvent.log("bookAndStartConsumerSession :: error $data")
                            if (data.errors?.get(0)?.code == 401) {
                                try {
                                    fragment.startActivity(
                                        Intent(
                                            fragment.activity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    fragment.activity?.finish()
                                } catch (e: Exception) {
                                    MiboEvent.log(e)
                                }
                            }
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                    }
                }
            })
    }

    private fun startTrainerSession(programId: Int, member: Member) {

        fragment.getDialog()?.show()
        SimpleDateFormat.getDateInstance()
        val post = StartTrainerSession.Data(member.locationID, trainerSessionId, 1, member.id)

        //StartTrainerSession

        API.request.getTrainerApi()
            .startTrainerSession(StartTrainerSession(post, member.accessToken!!))
            .enqueue(object : Callback<ResponseStatus> {

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    fragment.event(
                        "started_failed",
                        "failed",
                        "---"
                    )
                }

                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    fragment.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            fragment.event(
                                "started",
                                "success",
                                "---"
                            )

                            //sessionId = "${data.data?.sessionID}"
                            Prefs.get(fragment.context).set("member_sessionId", trainerSessionId)
                            //isSessionActive = true
                            startMemberSession()
                            isBooked = true

                        } else if (data.status.equals("error", true)) {
                            fragment.event(
                                "started_error",
                                "error",
                                "---"
                            )
                            Toasty.error(
                                fragment.requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()

                            MiboEvent.log("bookAndStartConsumerSession :: error $data")
                            if (data.errors?.get(0)?.code == 401) {
                                try {
                                    fragment.startActivity(
                                        Intent(
                                            fragment.activity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    fragment.activity?.finish()
                                } catch (e: Exception) {
                                    MiboEvent.log(e)
                                }
                            }
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                    }
                }
            })
    }



    private fun saveSessionApi(complete: Int = 1) {

        if (isTrainer) {
            sessionCompleteTrainerDialog(2)
            return
        }

        val member = Prefs.get(fragment.context).member ?: return

        fragment.getDialog()?.show()

        val calories = calculateCalories(
            SessionManager.getInstance().userSession.program.borgRating,
            getWeight().toDouble(),
            getProgramTime()
        )

        val session = Session(
            calories, adapter?.getChannels(), getProgramTime(),
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()),
            member.id, SessionManager.getInstance().userSession.program.name, complete, 1,
            sessionId.toIntOrZero(), startTime, 0
        )

        API.request.getApi()
            .saveSessionReport(SaveSessionPost(post = session, auth = member.accessToken!!))
            .enqueue(object : Callback<ResponseData> {

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    log("SaveSession Error : " + t?.message)
                    t.printStackTrace()
                    fragment.event(
                        "user_api_failed",
                        "failed",
                        "---"
                    )
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    fragment.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            fragment.event(
                                "user_api_success",
                                "success",
                                "---"
                            )
//                            Toasty.success(
//                                fragment.requireContext(),
//                                "${data.response?.message}",
//                                Toasty.LENGTH_SHORT,
//                                false
//                            ).show()
                            //sessionCompleteDialog(data.response?.message)
                            sessionCompleteDialog2(
                                SessionManager.getInstance().userSession.program?.name,
                                "" + getProgramTime(),
                                "$calories"
                            )

                        } else if (data.status.equals("error", true)) {
                            fragment.event(
                                "user_api_error",
                                "error",
                                "---"
                            )
                            Toasty.error(
                                fragment.requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()
                            MiboEvent.log("saveSessionReport :: error $data")

                            if (data.errors?.get(0)?.code == 401) {
                                try {
                                    fragment.startActivity(
                                        Intent(
                                            fragment.activity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    fragment.activity?.finish()
                                } catch (e: Exception) {
                                    MiboEvent.log(e)
                                }
                            }
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        log("SaveSession : " + response?.errorBody()?.toString())
                    }
                }
            })
    }


    private fun saveTrainerSessionApi(
        userId: Int,
        userRating: Int?,
        feedback: String?,
        calories: Int,
        time: Int
    ) {

        // log("saveTrainerSessionApi $userId : $userRating : $feedback")
        val trainer = Prefs.get(fragment.context).member ?: return
        fragment.getDialog()?.show()

//        val calories = calculateCalories(
//            SessionManager.getInstance().userSession.program.borgRating,
//            getWeight().toDouble(),
//            getProgramTime()
//        )

        var trainerFeedback = feedback ?: ""
        if (trainerFeedback.trim().length < 2)
            trainerFeedback = "N/A"

        val m = SaveTrainerSessionReport.Member(
            calories, adapter?.getChannels(),
            userId, 0, 0, 0,
            trainerFeedback, userRating?.plus(1), listOf(0, 0, 0, 0, 0)
        );

        val sessionData = SaveTrainerSessionReport.Data(
            1,
            time,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            trainer.locationID,
            Collections.singletonList(m),
            SessionManager.getInstance().userSession.program?.name,
            "$trainerSessionId",
            startTime,
            trainer.id(),
            trainerFeedback
        )

        val post = SaveTrainerSessionReport(sessionData, trainer?.accessToken)

        //log("saveTrainerSessionApi user > $calories :  - ${getProgramTime()}, $m")
        // log("saveTrainerSessionApi sessionData > $sessionData")
        // log("saveTrainerSessionApi SessionReport > $post")

        API.request.getTrainerApi()
            .saveTrainerSession(post)
            .enqueue(object : Callback<ResponseStatus> {

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    log("SaveSession Error : " + t?.message)
                    t.printStackTrace()
                    fragment.event(
                        "api_failed",
                        "reason=$t",
                        "duration=$time feedback=$feedback calories=$calories"
                    )
                }

                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    fragment.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            fragment.event(
                                "api_success",
                                "success",
                                "duration=$time feedback=$feedback calories=$calories"
                            )
//                            Toasty.success(
//                                fragment.requireContext(),
//                                "${data.response?.message}",
//                                Toasty.LENGTH_SHORT,
//                                false
//                            ).show()
                            // sessionCompleteDialog(data.response?.message)
                            navigateToHome()
                            return


                        } else if (data.status.equals("error", true)) {
                            fragment.event(
                                "api_error",
                                "success",
                                "duration=$time feedback=$feedback calories=$calories"
                            )
                            Toasty.error(
                                fragment.requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()
                            MiboEvent.log("saveSessionReport :: error $data")

                            if (data.errors?.get(0)?.code == 401) {
                                try {
                                    fragment.startActivity(
                                        Intent(
                                            fragment.activity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    fragment.activity?.finish()
                                } catch (e: Exception) {
                                    MiboEvent.log(e)
                                }
                            }
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        log("SaveSession : " + response?.errorBody()?.toString())
                    }
                }
            })

        googleFitEnd(time, feedback, calories)
    }

    private var sessionStartTime: Long = 0L

    private fun googleFitStart() {
        try {
            sessionStartTime = System.currentTimeMillis()
            GoogleFit(this.fragment).subscribeWithSession(
                null,
                SessionManager.getInstance().userSession.program?.name,
                "$programDuration",
                sessionStartTime,
                FitnessActivities.CIRCUIT_TRAINING
            )
            MiboEvent.event(
                "googlefit_session_start",
                "success",
                "duration=$programDuration"
            )
        } catch (e: Exception) {
            MiboEvent.log(e)
            MiboEvent.event(
                "googlefit_session_start_failed",
                "success $e",
                "duration=$programDuration"
            )
        }
    }

    private fun googleFitEnd(time: Int?, feedback: String?, calories: Int?) {
        try {
            GoogleFit(this.fragment).unsubscribeWithSession(
                null,
                SessionManager.getInstance().userSession.program?.name,
                sessionStartTime
            )
            MiboEvent.event(
                "googlefit_session_end",
                "success",
                "duration=$time feedback=$feedback calories=$calories"
            )
        } catch (e: Exception) {
            MiboEvent.log(e)
            MiboEvent.event(
                "googlefit_session_end_failed",
                "success $e",
                "duration=$time feedback=$feedback calories=$calories"
            )
        }
    }

    private fun startMemberSession() {
        startUserSession(userUid)
        googleFitStart()
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
    private fun calculateCalories(borg: Int, weight: Double, time: Int): Int {
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
            fragment.log("ERROR: calculateCalories borgRate $borg, weight $weight, time $time :: " + e?.message)
        }

        return 0
        //calculateCalories(SessionManager.getInstance().userSession.program.borgRating, getWeight().toDouble(), getTime())
    }

    private var userWeight: Int = 0

    private fun getWeight(): Int {
        if (isTrainer)
            return trainerUserWeight
        if (userWeight != 0)
            return userWeight;
        try {
            val w = Prefs.get(fragment.context)["user_weight"]
            userWeight = w.replace("[^0-9]".toRegex(), "").toInt()
            return userWeight;
        } catch (e: java.lang.Exception) {
            userWeight = 0
        }
        getBioMetric("$userWeight")
        return userWeight
    }

    private fun getBioMetric(weight: String?) {
        if (weight != null && weight.isNotEmpty())
            return
        val member = Prefs.get(fragment.context).member
        val memberId = member?.id() ?: ""
        val token = member?.accessToken ?: ""
        API.request.getApi().getMemberBiometrics(MemberPost(memberId, token, "GetMemberBiometrics"))
            .enqueue(object : retrofit2.Callback<Biometric> {
                override fun onFailure(call: Call<Biometric>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<Biometric>, response: Response<Biometric>
                ) {
                    try {
                        val body = response?.body()
                        if (body != null && body.isSuccess()) {
                            val list = body.data
                            list.let {
                                parseBiometric(it)
                            }
                        }
                    } catch (e: Exception) {
                        MiboEvent.log(e)
                    }

                }

            })
    }

    fun parseBiometric(bio: List<Biometric.Data?>?) {
        if (bio != null) {
            try {
                val data = bio[bio.size - 1]
                if (data?.weight != null) {
                    userWeight = data.weight!!.toInt()
                }
                Prefs.get(fragment.context)["user_weight"] = "${data!!.weight} KG"
                Prefs.get(fragment.context)["user_height"] = "${data!!.height} CM"
                Prefs.get(fragment.context)["user_date"] =
                    "${data.createdAt?.date?.split(" ")?.get(0)}"
            } catch (e: Exception) {
                // Prefs.get(fragment.context)["user_date"] = "${data.createdAt?.date}"
            }

        }
    }

//    private fun getTime(): Long {
//        log("getTime : total : $totalDuration current: $currentDuration prg $programDuration")
//        try {
//            if (totalDuration == currentDuration)
//                return totalDuration;
//            tvTimer?.text?.let {
//                if (it == fragment.getString(R.string.completed))
//                    return totalDuration
//            }
//        } catch (e: java.lang.Exception) {
//
//        }
//        return totalDuration.minus(currentDuration)
//    }

    private fun getProgramTime(): Int {
        fragment.log("getProgramTime programDuration $programDuration  currentDuration $currentDuration")
        return try {
            programDuration.minus(currentDuration).toInt()
        } catch (e: java.lang.Exception) {
            programDuration
        }

    }


}