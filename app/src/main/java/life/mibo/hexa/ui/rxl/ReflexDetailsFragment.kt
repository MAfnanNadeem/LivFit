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
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hexa.R
import life.mibo.hexa.core.toIntOrZero
import life.mibo.hexa.events.NotifyEvent
import life.mibo.hexa.pods.PodExercise
import life.mibo.hexa.pods.RXLManager
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.MessageDialog
import life.mibo.hexa.ui.rxl.impl.CourseCreateImpl
import life.mibo.hexa.ui.rxl.impl.ReflexDialog
import life.mibo.hexa.utils.Toasty
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

    lateinit var deligate: CourseCreateImpl
    //val manager = RXLManager.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deligate = CourseCreateImpl(this, this)
        //deligate.listener = this

        btn_start_now?.setOnClickListener {
            startNowClicked()
        }

        btn_start_hit?.setOnClickListener {
            startHitClicked()
        }

        tv_select_stations?.setOnClickListener {
            deligate.showDialog(CourseCreateImpl.Type.STATIONS)
        }
        tv_select_cycles?.setOnClickListener {
            deligate.showDialog(CourseCreateImpl.Type.CYCLES)
        }
        tv_select_time?.setOnClickListener {
            deligate.showDialog(CourseCreateImpl.Type.DURATION)
        }

        tv_select_delay?.setOnClickListener {
            deligate.showDialog(CourseCreateImpl.Type.DELAY)
        }
        tv_select_lights?.setOnClickListener {
            deligate.showDialog(CourseCreateImpl.Type.LIGHT_LOGIC)
        }
        tv_select_players?.setOnClickListener {
            deligate.showDialog(CourseCreateImpl.Type.PLAYERS)
        }
        SessionManager.getInstance().userSession.isRxl = true
        getPods()
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
        //manager.lightOnSequence()
        RXLManager.getInstance().test("test from ReflexDetailsFragment")

//        for (i in 1..50) {
//            log("Random text " + Random.nextInt(4))
//        }
        //RXLManager.getInstance().startTest(30, 5, 2, 4)
        RXLManager.getInstance().withListener(this).startTest(getDuration(), getCycles(), getAction(), getPause(), isRandom())
        //RXLManager.getInstance().with(PodExercise.getExercise(getDuration(), getAction(), getPause(),getCycles()))
        //    .addDevices(SessionManager.getInstance().userSession.devices).sendColor(null)

        //RXLManager.getInstance().with(PodExercise.getExercise(getDuration(), 3, getPause(),getCycles()))
        //  .addDevices(SessionManager.getInstance().userSession.devices).start()
    }


    private fun startNowClicked() {
        val list = SessionManager.getInstance().userSession.devices
        if (list.size < 3) {
            Toasty.warning(
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
                .with(PodExercise.getExercise(getDuration(), getAction(), getPause(), getCycles()))
                .addDevices(pods).withListener(this).start(isRandom())
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
        }
    }

    fun isRandom(): Boolean = tv_select_lights.text?.toString().equals("random", true)

    fun getCycles(): Int {
        return getInt(tv_select_cycles?.text)
    }

    fun getDuration(): Int {
        return getInt(tv_select_time?.text)
    }

    fun getPause(): Int {
        return getInt(tv_select_delay?.text)
    }

    fun getAction(): Int {
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
        //EventBus.getDefault().register(this)
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
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
    }

    override fun onExerciseEnd() {
        log("onExerciseEnd")
        progress(0, 100, 0, 2)
        activity?.runOnUiThread {
            tv_desc?.text = "Completed..."
            MessageDialog.info(requireContext(), "Completed", "Exercise finished")
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

