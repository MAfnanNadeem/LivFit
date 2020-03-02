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
import life.mibo.hardware.events.ProximityEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hexa.R
import life.mibo.hexa.core.toIntOrZero
import life.mibo.hexa.events.NotifyEvent
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.models.rxl.RxlExercises
import life.mibo.hexa.pods.rxl.RXLManager
import life.mibo.hexa.pods.rxl.RxlLight
import life.mibo.hexa.pods.rxl.RxlProgram
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.MessageDialog
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.rxl.impl.CourseCreateImpl
import life.mibo.hexa.ui.rxl.impl.ReflexDialog
import life.mibo.hexa.ui.select_program.ProgramDialog
import life.mibo.hexa.utils.Constants
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

    private var program: life.mibo.hexa.models.rxl.RxlProgram? = null
    //val manager = RXLManager.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        delegate = CourseCreateImpl(context!!, this)
        //delegate.listener = this

        val arg = arguments
        if (arg != null) {
            program = arg.getSerializable(Constants.BUNDLE_DATA) as life.mibo.hexa.models.rxl.RxlProgram?
            setProgram()
        }
        navigate(Navigator.HOME_VIEW, true)

        btn_start_now?.setOnClickListener {
            startNowClicked()
        }

        btn_start_hit?.setOnClickListener {
            startHitClicked()
        }

        switch_sensor?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tv_switch_sensor?.text = getString(R.string.proximity_on)
                changeSensor(200)
            } else {
                tv_switch_sensor?.text = getString(R.string.proximity_off)
                changeSensor(0)
            }
        }

        SessionManager.getInstance().userSession.isRxl = true
        iv_select_color?.setOnClickListener {
            showColors()
        }
        tv_customize?.setOnClickListener {
            navigate(Navigator.RXL_COURSE_CREATE, program)
        }
        //setPickers()
        getPods()

        changeSensor(0)
        setSlider()
        activity?.title = program?.name

    }


    private fun setProgram() {
        program?.let {
            tv_select_stations?.text = "${it.workStation}"
            tv_select_cycles?.text = "${it.cycle}"
            tv_select_duration?.text = "${it.totalDuration} sec"
            tv_select_delay?.text = "${it.pause} sec"
            tv_select_lights?.text = "${it.logicType()}"
            tv_select_action?.text = "${it.action} sec"
            tv_select_players?.text = "${it.pods}"
            tv_desc?.text = "${it.description}"
            //val images = it.image!!.split(",")
            //images[0].replace("[", "")
            //images[images.size - 1].replace("]", "")


        }
    }

    private fun setSlider() {
        val list = arrayListOf(
            R.drawable.ic_reflex_random_icon,
            R.drawable.ic_reflex_sequence,
            R.drawable.ic_reflex_focus_only
        )
        iv_icon_giff.setImages(list)
    }

    private fun setPickers() {
        tv_select_stations?.setOnClickListener {
            //delegate.showDialog(CourseCreateImpl.Type.STATIONS)
        }
        tv_select_cycles?.setOnClickListener {
            //  delegate.showDialog(CourseCreateImpl.Type.CYCLES)
        }
        tv_select_duration?.setOnClickListener {
            //  delegate.showDialog(CourseCreateImpl.Type.DURATION)
        }

        tv_select_delay?.setOnClickListener {
            // delegate.showDialog(CourseCreateImpl.Type.DELAY)
        }
        tv_select_lights?.setOnClickListener {
            // delegate.showDialog(CourseCreateImpl.Type.LIGHT_LOGIC)
        }
        tv_select_players?.setOnClickListener {
            //delegate.showDialog(CourseCreateImpl.Type.ACTION)
        }
        iv_select_color?.setOnClickListener {
            showColors()
        }
    }

    private fun changeSensor(value: Int) {

        val list = SessionManager.getInstance().userSession.devices
        if (list.size > 0) {
            Observable.fromIterable(list)
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
                        log("ProximityEvent onError", e)
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

        colorDialog =
            ProgramDialog(context!!, ArrayList(), object : ItemClickListener<Program> {

                override fun onItemClicked(item: Program?, position: Int) {
                    //Toasty.info(context!!, "$position").show()
                    log("ProgramDialog Colors color = ${item?.id}  position $position")

                    item?.id?.let {
                        iv_select_color?.visibility = View.VISIBLE
                        iv_select_color?.circleColor = it
                        selectedColor = it
                        selectedColorId = position
                    }
                }

            }, ProgramDialog.COLORS)

        colorDialog?.showColors()
    }

    private fun getPods() {
        //tv_desc?.text = ""
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
        //startUnitTest()
        checkStartCondition {
            log("checkStartCondition meet")
            startProgram(true, it)
        }

        if (DEBUG) {
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
        log("isRandom " + isRandom())
        //log("isRandom2 " + tv_select_lights.text?.toString()?.toLowerCase()?.contains("random"))
        checkStartCondition { it ->
            startProgram(false, it)
        }

    }

    private fun startProgram(tap: Boolean, devices: ArrayList<Device>) {
        val uids = ArrayList<String>()
        devices.forEach { d ->
            uids.add(d.uid)
        }

        RXLManager.getInstance().with(
            RxlProgram.getExercise(
                getDuration(),
                getAction(),
                getPause(),
                getCycles(),
                selectedColor,
                selectedColorId,
                uids,
                getLightLogic()
            )
        ).addDevices(devices).withListener(this).start(tap)

        //if (tap)
        //   Toasty.info(context!!, "Tap Reaction Light to start").show()
    }

    // Todo test, disable in production
    private fun startUnitTest(devices: ArrayList<Device>) {
        RXLManager.getInstance().withListener(this).startTest(
            RxlProgram.getExercise(
                getDuration(), getAction(),
                getPause(), getCycles(),
                selectedColor, selectedColorId,
                ArrayList<String>(), getLightLogic()
            )
        )
    }

    private var size = 2
    private fun checkStartCondition(action: (ArrayList<Device>) -> Unit) {
        val list = SessionManager.getInstance().userSession.devices
        if (list.size < size) {
            MessageDialog.info(
                context!!,
                "RXL Requirement",
                getString(R.string.three_pods_required)
            )
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
                action.invoke(pods)
                if (pods.size != getProgramPods())
                    Toasty.info(
                        context!!,
                        "This exercise is designed for " + getProgramPods() + " but you have connected only " + pods.size,
                        Toasty.LENGTH_LONG
                    ).show()
            } else {
                MessageDialog.info(
                    context!!,
                    "RXL Requirement",
                    "Selected light logic is currently not supported \n\n Choose Random or Sequence"
                )
            }
            //RXLManager.getInstance().with(PodExercise.getExercise1()).addDevices(pods).start()
//            RXLManager.getInstance()
//                .with(PodExercise.getExercise(getDuration(), 3, getPause(), getCycles()))
//                .addDevices(pods).sendColor(null)
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
                tv_select_duration?.text = item.title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.ACTION.type -> {
                tv_select_players?.text = item.title?.replace("seconds", "sec")
            }
        }
    }

    private fun isRandom(): Boolean =
        tv_select_lights.text?.toString()?.toLowerCase()?.contains("random") ?: false

    private fun getLightLogic(): RxlLight {
        return when (tv_select_lights.text?.toString()?.toLowerCase()) {
            "random" ->
                RxlLight.RANDOM
            "focus" ->
                RxlLight.FOCUS
            "all at once" ->
                RxlLight.ALL_AT_ONCE
            else ->
                RxlLight.SEQUENCE
        }
    }


    private fun getProgramPods(): Int {
        return getInt(tv_select_pods?.text)
    }

    private fun getCycles(): Int {
        return getInt(tv_select_cycles?.text)
    }

    private fun getDuration(): Int {
        return getInt(tv_select_duration?.text)
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
        //progress(0, 100, 0, 2)
        activity?.runOnUiThread {
            btn_start_now?.isEnabled = true
            btn_start_hit?.isEnabled = true
            //tv_desc?.text = "Completed..."
            MessageDialog.info(requireContext(), "Completed", "Exercise finished "+ RXLManager.getInstance().getHits())
            progressBar!!.visibility = View.GONE
            tv_cycle_count?.text = ""
        }

//        Single.just("").delay(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
//            .doOnSuccess {
//                progressBar!!.visibility = View.GONE
//            }.subscribe()

    }

    override fun onCycleStart(cycle: Int, duration: Int) {
        log("onCycleStart")
        activity?.runOnUiThread {
            btn_start_now?.isEnabled = false
            btn_start_hit?.isEnabled = false
            tv_cycle_count?.text = "$cycle"
        }
        progress(0, 100, duration.times(1000), 1)
    }

    override fun onCycleEnd(cycle: Int) {

        log("onCycleEnd")
    }

    override fun onCyclePaused(cycle: Int, time: Int) {
        log("onCyclePaused")
        progress(0, 100, time.times(1000), 2)
        activity?.runOnUiThread {
            // tv_desc?.text = "Pausing..."
        }
    }

    override fun onCycleResumed(cycle: Int) {
        log("onCycleResumed")
    }

    override fun onPod(podId: Int, time: Int) {
        log("onPod")
    }

    override fun onTapColorSent() {
        Toasty.info(context!!, getString(R.string.hit_to_start_rxl)).show()
    }

    var lastFrom = -1
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
                    progressBar!!.visibility = View.GONE
                    return@doOnSuccess
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

