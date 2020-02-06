/*
 *  Created by Sumeet Kumar on 1/26/20 5:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/26/20 5:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_rxl_details.*
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.ProximityEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hexa.BuildConfig
import life.mibo.hexa.R
import life.mibo.hexa.core.toIntOrZero
import life.mibo.hexa.events.NotifyEvent
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.pods.RXLManager
import life.mibo.hexa.pods.RxlProgram
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.MessageDialog
import life.mibo.hexa.ui.rxl.impl.CourseCreateImpl
import life.mibo.hexa.ui.rxl.impl.ReflexDialog
import life.mibo.hexa.ui.select_program.ProgramDialog
import life.mibo.hexa.utils.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class ReflexDetailsFragment : BaseFragment(), CourseCreateImpl.Listener, RXLManager.Listener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rxl_details, container, false)
    }

    private lateinit var delegate: CourseCreateImpl
    //val manager = RXLManager.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        delegate = CourseCreateImpl(this, this)
        //delegate.listener = this

        btn_start_now?.setOnClickListener {
            startNowClicked()
        }

        btn_start_hit?.setOnClickListener {
            //startHitClicked()
        }

        tv_select_stations?.setOnClickListener {
            //delegate.showDialog(CourseCreateImpl.Type.STATIONS)
        }
        tv_select_cycles?.setOnClickListener {
            delegate.showDialog(CourseCreateImpl.Type.CYCLES)
        }
        tv_select_time?.setOnClickListener {
            delegate.showDialog(CourseCreateImpl.Type.DURATION)
        }

        tv_select_delay?.setOnClickListener {
            delegate.showDialog(CourseCreateImpl.Type.DELAY)
        }
        tv_select_lights?.setOnClickListener {
            delegate.showDialog(CourseCreateImpl.Type.LIGHT_LOGIC)
        }
        tv_select_players?.setOnClickListener {
            delegate.showDialog(CourseCreateImpl.Type.ACTION)
        }
        iv_select_color?.setOnClickListener {
            showColors()
        }
        SessionManager.getInstance().userSession.isRxl = true
        getPods()
        switch_sensor?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView?.text = "Sensor On   "
                changeSensor(200)
            } else {
                buttonView?.text = "Sensor Off    "
                changeSensor(0)
            }
        }

        changeSensor(0)
    }

    private fun changeSensor(value: Int) {

        val list = SessionManager.getInstance().userSession.devices
        if (list.size > 0) {
            val pods = ArrayList<Device>()
            list.forEach {
                if (it.isPod) {
                    pods.add(it)
                }
            }

            if (pods.isNotEmpty()) {
                Observable.fromArray(pods).flatMapIterable { x -> x }
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : io.reactivex.Observer<Device> {

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: Device) {
                            try {
                                EventBus.getDefault().postSticky(ProximityEvent(t.uid, value))
                                Thread.sleep(50)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun onError(e: Throwable) {
                            log("ProximityEvent onError", e)
                            e.printStackTrace()
                        }

                        override fun onComplete() {
                            Toasty.info(
                                this@ReflexDetailsFragment.context!!,
                                "Sensor " + if (value > 0) "Enabled" else "Disabled"
                            ).show()
                        }
                    })
            }
        }
    }


    var colorDialog: ProgramDialog? = null
    var selectedColor: Int = Color.GREEN
    private fun showColors() {
        var isDialog = false
        colorDialog?.let {
            isDialog = true
            it.showColors()
            return
        }
        if (isDialog)
            return

        colorDialog =
            ProgramDialog(context!!, ArrayList(), object : ItemClickListener<Program> {

                override fun onItemClicked(item: Program?, position: Int) {
                    //Toasty.info(context!!, "$position").show()

                    item?.id?.let {
                        iv_select_color?.visibility = View.VISIBLE
                        iv_select_color?.circleColor = it
                        selectedColor = it
                    }
                }

            }, ProgramDialog.COLORS)

        colorDialog?.showColors()
    }

    private fun getPods() {
        tv_desc?.text = ""
        val list = SessionManager.getInstance().userSession.devices
        if (list.size > 0) {
            val pods = ArrayList<Device>()
            list.forEach {
                if (it.isPod) {
                    pods.add(it)
                }
            }
            tv_select_pods?.text = "${pods.size}"
        } else {
            tv_select_pods?.text = "0"
        }


    }

    private fun startHitClicked() {
        if (BuildConfig.DEBUG) {
            //manager.lightOnSequence()
            RXLManager.getInstance().test("test from ReflexDetailsFragment")

//        for (i in 1..50) {
//            log("Random text " + Random.nextInt(4))
//        }
            //RXLManager.getInstance().startTest(30, 5, 2, 4)
//            RXLManager.getInstance().withListener(this).startTest(
//                RxlProgram.getExercise(
//                    getDuration(),
//                    getAction(),
//                    getPause(),
//                    getCycles(),
//                    selectedColor,
//                    isRandom()
//                )
//            )
            //RXLManager.getInstance().with(PodExercise.getExercise(getDuration(), getAction(), getPause(),getCycles()))
            //    .addDevices(SessionManager.getInstance().userSession.devices).sendColor(null)

            //RXLManager.getInstance().with(PodExercise.getExercise(getDuration(), 3, getPause(),getCycles()))
            //  .addDevices(SessionManager.getInstance().userSession.devices).start()
        }

    }


    private fun startNowClicked() {
        val list = SessionManager.getInstance().userSession.devices
        if (list.size < 3) {
            Toasty.info(
                context!!,
                getString(R.string.three_pods_required),
                Toasty.LENGTH_SHORT,
                false
            ).show()
            return
        }
        val pods = ArrayList<Device>()
        list.forEach {
            if (it.isPod) {
                pods.add(it)
            }
        }
        if (pods.size > 2) {
            //RXLManager.getInstance().with(PodExercise.getExercise1()).addDevices(pods).start()
//            RXLManager.getInstance()
//                .with(PodExercise.getExercise(getDuration(), 3, getPause(), getCycles()))
//                .addDevices(pods).sendColor(null)
            RXLManager.getInstance()
                .with(
                    RxlProgram.getExercise(
                        getDuration(), getAction(), getPause(),
                        getCycles(), selectedColor, isRandom()
                    )
                )
                .addDevices(pods).withListener(this).startNow()
            Toasty.info(context!!, "Tap Reaction Light to start").show()
            return
        } else {
            context?.let {
                MessageDialog.info(it, "Reaction Lights", getString(R.string.three_pods_required))
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
                tv_select_stations?.text = item.title
            }
            CourseCreateImpl.Type.CYCLES.type -> {
                tv_select_cycles?.text = item.title
            }
            CourseCreateImpl.Type.PODS.type -> {
                tv_select_pods?.text = item.title
            }
            CourseCreateImpl.Type.LIGHT_LOGIC.type -> {
                tv_select_lights?.text = item.title
                if (item.title == "Focus" || item.title == "All at once") {
                    Toasty.info(context!!, "${item.title} this option is not supported yet").show()
                }
            }
            CourseCreateImpl.Type.PLAYERS.type -> {
                tv_select_players?.text = item.title
            }
            CourseCreateImpl.Type.DELAY.type -> {
                if (item.title?.startsWith("No Delay"))
                    tv_select_delay?.text = "0 sec"
                else tv_select_delay?.text = item.title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.DURATION.type -> {
                tv_select_time?.text = item.title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.ACTION.type -> {
                tv_select_players?.text = item.title?.replace("seconds", "sec")
            }
        }
    }

    private fun isRandom(): Boolean = tv_select_lights.text?.toString().equals("random", true)

    private fun getCycles(): Int {
        return getInt(tv_select_cycles?.text)
    }

    private fun getDuration(): Int {
        return getInt(tv_select_time?.text)
    }

    private fun getPause(): Int {
        return getInt(tv_select_delay?.text)
    }

    private fun getAction(): Int {
        return getInt(tv_select_players?.text)
    }

    fun getInt(string: CharSequence?): Int {
        return try {
            string?.replace(Regex("\\D+"), "")!!.toIntOrZero()
        } catch (e: Exception) {
            0
        }
    }

    val builder = StringBuilder("")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(notify: NotifyEvent) {
        log("onEvent $notify")
        val data = notify.data
        if (data is String) {
            builder.append("\n")
            builder.append(data)
            tv_desc.text = builder
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
    fun observe() {
        disposable?.dispose()
        disposable = Single.just("i").delay(5, TimeUnit.SECONDS).doOnSuccess {
            onEvent(RxlStatusEvent(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0), "000000"))
            log("RxlStatusEvent observe")

        }.subscribe()
    }

    override fun onExerciseStart() {
        log("onExerciseStart")
        activity?.runOnUiThread {
            btn_start_now?.isEnabled = false
        }
    }

    override fun onExerciseEnd() {
        log("onExerciseEnd")
        progress(0, 100, 0, 2)
        activity?.runOnUiThread {
            btn_start_now?.isEnabled = true
            tv_desc?.text = "Completed..."
            MessageDialog.info(requireContext(), "Completed", "Exercise finished "+ RXLManager.getInstance().getHits())
        }

    }

    override fun onCycleStart(cycle: Int, duration: Int) {
        log("onCycleStart")
        progress(0, 100, duration.times(1000), 1)
        activity?.runOnUiThread {
            tv_desc?.text = "Cycle $cycle"
        }
    }

    override fun onCycleEnd(cycle: Int) {

        log("onCycleEnd")
    }

    override fun onCyclePaused(cycle: Int, time: Int) {
        log("onCyclePaused")
        progress(0, 100, time.times(1000), 2)
        activity?.runOnUiThread {
            tv_desc?.text = "Pausing..."
        }
    }

    override fun onCycleResumed(cycle: Int) {
        log("onCycleResumed")
    }

    override fun onPod(podId: Int, time: Int) {
        log("onPod")
    }

    var lastFrom = -1
    fun progress(valueFrom: Int, valueTo: Int, duration: Int, type: Int) {
        //  if (lastFrom == valueFrom)
        //      return
        //  lastFrom = valueFrom
        // Observable.fromCallable {  }
        activity?.runOnUiThread {

        }
        Observable.just("this").subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                log("progress $valueFrom : $valueTo :: $duration")
                if (duration == 0) {
                    progressBar!!.visibility = View.GONE
                    return@doOnComplete
                } else
                    progressBar!!.visibility = View.VISIBLE

                if (type == 1)
                    progressBar!!.progressTintList = ColorStateList.valueOf(Color.GREEN)
                else
                    progressBar!!.progressTintList = ColorStateList.valueOf(Color.RED)

                ObjectAnimator.ofInt(progressBar, "progress", valueFrom, valueTo)
                    .setDuration(duration.toLong())
                    .start()
            }.subscribe()
    }


}

