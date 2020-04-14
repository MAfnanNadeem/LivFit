/*
 *  Created by Sumeet Kumar on 1/12/20 8:07 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 8:07 AM
 *  Mibo Hexa - app 
 */

package life.mibo.android.ui.rxl

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_rxl_test.*
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.ColorReceivedEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.android.R
import life.mibo.android.core.toIntOrZero
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Toasty
import life.mibo.views.ColorSeekBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class RxlTestFragment : BaseFragment() {

    var selected: Device? = null
    var selectedId = -1

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View? {
        super.onCreateView(i, c, b)

        val view: View? = i.inflate(R.layout.fragment_rxl_test, c, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorSelectListener(color: Int) {
                if (edittext_cmd!!.text?.isEmpty()!!) {
                    Toasty.info(context!!, "Please enter time").show()
                    //progress(3000)
                    return
                }
                tv_device_color?.setTextColor(color)
                val rxt = SessionManager.getInstance().userSession.rxt
                if (rxt != null) {
                    onRxtColorChange(rxt, color)
                    return
                }
                if (selectPosition != -1 && selectPosition < list.size) {
                    val d: Device? = list[selectPosition]
                    d?.let {
                        it.colorPalet = color
                        EventBus.getDefault().postSticky(ChangeColorEvent(it, it.uid, getTime()))
                    }
                } else {
                    for (d in list) {
                        d.let {
                            it.colorPalet = color
                            EventBus.getDefault()
                                .postSticky(ChangeColorEvent(it, it.uid, getTime()))
                        }
                    }
                }
                progress(lastTime)
            }

            override fun onColorChangeListener(color: Int) {

            }

        })

        button_lights_on.setOnClickListener {

        }

        button_lights_off.setOnClickListener {

        }

        button_commands.setOnClickListener {

        }

        button_disconnect.setOnClickListener {
            navigate(Navigator.DISCONNECT, SessionManager.getInstance().userSession.booster)
        }

        button_device_change.setOnClickListener {

            showRxlDialog()
        }
        list.addAll(SessionManager.getInstance().userSession.rxl)

        if (list.isNotEmpty())
            SessionManager.getInstance().userSession.isRxl = true

        //progressBar.progress = 50f
    }

    fun onRxtColorChange(device: Device, color: Int) {
        log("onRxtColorChange $color")
        var tileId = edittext_type?.text?.toString()?.toIntOrZero()
        device.colorPalet = color
        device.data = tileId
        EventBus.getDefault()
            .postSticky(ChangeColorEvent(device, device.uid, getTime()))
    }

    private var lastTime = 0
    fun getTime(): Int {
        if (edittext_cmd!!.text?.isNotEmpty()!!) {
            try {
                lastTime = edittext_cmd!!.text!!.toString().toInt()
                return lastTime
            } catch (e: Exception) {

            }
        }
        return 1000
    }
    val list = ArrayList<Device>()

    var selectPosition = -1
    private fun showRxlDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Choose an animal")
        //val list = SessionManager.getInstance().userSession.rxl
        val array = arrayOfNulls<String>(list.size)
       // val array = ArrayList<String>()
        list.forEachIndexed { i, d ->
            array[i] = " $i ${d.name}"
        }


        builder.setSingleChoiceItems(array, 0) { dialog, which ->
            selectPosition = which
            log("selectPosition $selectPosition")
        }

        builder.setPositiveButton("CLOSE") { dialog, which ->
            // user clicked OK
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    var animator: ObjectAnimator? = null
    fun progress(duration: Int) {
        Observable.empty<String>().observeOn(AndroidSchedulers.mainThread()).doOnComplete {
            animator = ObjectAnimator.ofFloat(progressBar, "progress", 100f, 0f)
                .setDuration(duration.toLong())

            animator?.start()
            log("progressBar $duration")
        }.subscribe()
    }

    @Subscribe
    fun onEvent(event: ColorReceivedEvent) {
        //progress(lastTime)
    }

    @Subscribe
    fun onEvent(event: RxlStatusEvent) {
        Observable.just("").observeOn(AndroidSchedulers.mainThread()).doOnComplete {
            animator?.end()
            Toasty.info(
                this@RxlTestFragment.context!!,
                event.timeString,
                Toasty.LENGTH_LONG,
                false
            ).show()
        }.subscribe()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    inner class ProgressBarAnimation(
        private val progressBar: ProgressBar,
        private val from: Float,
        private val to: Float
    ) : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar.progress = value.toInt()
        }

    }

}