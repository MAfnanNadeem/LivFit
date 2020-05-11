/*
 *  Created by Sumeet Kumar on 5/4/20 4:21 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/4/20 1:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.heart_rate

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_hr_monitor.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.models.session.Report
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.heart_rate.chart.ChartData
import life.mibo.android.utils.Constants
import life.mibo.android.utils.Toasty
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.events.HeartRateEvent
import java.util.concurrent.TimeUnit


class HeartRateTabMonitor : BaseFragment() {

    companion object {
        fun create(type: Int): HeartRateTabMonitor {
            val fragment = HeartRateTabMonitor()
            val args = Bundle()
            args.putInt("type_", type)
            fragment.arguments = args
            return fragment
        }
    }


    var type: Int = 0
    var chart: LineChart? = null
    var noDataText: TextView? = null
    var hrValue: TextView? = null
    lateinit var chartData: ChartData
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        log("onCreateView")
        val root = inflater.inflate(R.layout.fragment_hr_monitor, container, false)
        chart = root.findViewById(R.id.lineChart)
        noDataText = root.findViewById(R.id.lineChart_no_data)
        hrValue = root.findViewById(R.id.hr_value)
        type = arguments?.getInt("type_") ?: 0
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        chartData = ChartData()
        //chartData.loadChart(chart)

        constraintLayout1?.visibility = View.VISIBLE
        val report: Report? = Prefs.get(context).getJson(Prefs.SESSION, Report::class.java)
        val list = report?.heartRate
        var total = 0
        if (list != null && list.isNotEmpty()) {
            list.forEach {
                total += it?.toInt()!!
            }

            hrValue?.text = "${total.div(list.size)}"
            animate(hexaImage)
            circleProgressView?.progress = 50f
        }

//        val d = Single.just(type).delay(300, TimeUnit.MILLISECONDS)
//            .observeOn(AndroidSchedulers.mainThread()).subscribe { t_ ->
//               // chartData.getHeartRate(chart, type)
//            }

        //animate(hr_heart_image)
        start_heartrate?.setOnClickListener {
            startMonitor()
        }
    }

    var isMonitoring = false
    private fun startMonitor() {
        log("startMonitor $isMonitoring")
        if (isMonitoring) {
            CommunicationManager.getInstance().stopHrMonitor("")
            Single.fromCallable {
                log("fromCallable $isMonitoring")
            }.delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    isMonitoring = false
                    log("doOnSuccess $isMonitoring")
                    start_heartrate?.setText(R.string.start_heart_rate)
                    hexaImage?.clearAnimation()
                    calculatePeak()
                    log("doOnSuccess2 $isMonitoring")
                }.subscribe()
        } else {
            CommunicationManager.getInstance().startHrMonitor("")

        }
    }

    fun calculatePeak() {
        if (hrList.size > 0) {
            try {
                var peak = 0f
                var total = 0f;
                for (f in hrList) {
                    if (f > peak)
                        peak = f
                    total += f
                }

                val avg = total.div(hrList.size)
                Toasty.info(
                    requireContext(),
                    "Peak $peak  Average $avg",
                    Toasty.LENGTH_LONG
                ).show()
                hr_value?.text = String.format("%.2f", avg)
                circleProgressView?.progress = avg
                scaleDown?.end()

            } catch (e: java.lang.Exception) {

            }
        }
    }

    var scaleDown : ObjectAnimator? = null
    fun animate(iv: ImageView?) {
        if (iv == null)
            return
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            iv,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleDown?.duration = 310

        scaleDown?.repeatCount = ObjectAnimator.INFINITE
        // scaleDown.repeatCount = 10
        scaleDown?.repeatMode = ObjectAnimator.REVERSE

        scaleDown?.start()
    }

    //@Subscribe
    fun onEvent(event: HeartRateEvent) {
        // log("HeartRateEvent $event")
        if (!isMonitoring) {
            isMonitoring = true
            activity?.run {
                start_heartrate?.text = getString(R.string.stop)
                hr_value?.text = "${event.hr}"
                circleProgressView?.progress = event.hr?.toFloat()
                animate(hexaImage)
            }
        }
        activity?.run {
            try {
                updateChart(event.hr?.toFloat())
            } catch (e: java.lang.Exception) {

            }
        }
    }

    var hrList = ArrayList<Float>()
    fun updateChart(value: Float) {
        log("HeartRateEvent updateChart $value")
        hrList.add(value)
        //chart?.lineData
        if (chart?.data != null) {
            val last = chart!!.data.getDataSetByIndex(0).entryCount
            //last.addEntry(Entry(value, last.entryCount.toFloat()))
            //log("HeartRateEvent last $last")
            chart?.data?.addEntry(Entry(last.toFloat(), value), 0)
            chart?.notifyDataSetChanged();
            chart?.invalidate()
        } else {

            val entries = ArrayList<Entry>()
            //entries.add(Entry(value, 1f))
            entries.add(Entry(1f, value))

            chart?.description?.isEnabled = false
            chart?.setTouchEnabled(true)



            chart?.dragDecelerationFrictionCoef = 0.9f

            chart?.isDragEnabled = false
            chart?.setScaleEnabled(false)
            chart?.setDrawGridBackground(false)
            chart?.isHighlightPerDragEnabled = false
            chart?.setBackgroundColor(Color.WHITE)
            //chart.setViewPortOffsets(0f, 0f, 0f, 0f)

            // get the legend (only possible after setting data)
            val l = chart?.legend
            l?.isEnabled = true

            val xAxis = chart?.xAxis
            xAxis?.position = XAxis.XAxisPosition.BOTTOM
            //xAxis.typeface = typeface
            xAxis?.textSize = 10f
            xAxis?.textColor = Color.WHITE
            xAxis?.setDrawAxisLine(false)
            xAxis?.setDrawGridLines(false)
            xAxis?.textColor = Constants.PRIMARY
            xAxis?.setCenterAxisLabels(true)
            xAxis?.granularity = 1f // one hour
            //xAxis.valueFormatter = dayFormatter


            val leftAxis = chart?.axisLeft
            leftAxis?.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            //leftAxis.textColor = ColorTemplate.getHoloBlue()
            leftAxis?.setDrawGridLines(false)
            leftAxis?.isGranularityEnabled = true
//        leftAxis.axisMinimum = 0f
//        leftAxis.axisMaximum = 170f
            leftAxis?.yOffset = -9f
            leftAxis?.textColor = Constants.PRIMARY
            // leftAxis?.valueFormatter = rateFormatter

            val rightAxis = chart?.axisRight
            rightAxis?.isEnabled = false


            //
//        var text = legendText
//        if (type == 1) {
//            text = "Week"
//        } else if (type == 2) {
//            text = "Month"
//        }
            val set1 = LineDataSet(entries, "HR Monitor")
            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.valueTextColor = ColorTemplate.getHoloBlue()
            set1.lineWidth = 1.5f
            set1.setDrawCircles(false)
            set1.setDrawValues(false)
            set1.setDrawFilled(true)
            set1.isHighlightEnabled = false
            set1.fillAlpha = 50
            set1.fillColor = Constants.PRIMARY
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            //set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)
            set1.color = Constants.PRIMARY

            val data = LineData(set1)
            data.setValueTextColor(Color.WHITE)

            data.setValueTextSize(9f)
            chart?.animateXY(800, 500)
            chart?.data = data
            //chart.lineData = data
            chart?.invalidate()
        }
    }

    var visible = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        log("isVisibleToUser $isVisibleToUser")
        visible = isVisibleToUser
        if (isVisibleToUser) {
            try {
                //EventBus.getDefault().register(this)
                log("isVisibleToUser register")
            } catch (e: Exception) {
                log("isVisibleToUser e: $e")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onStop() {
        log("onStop")
        try {
            // EventBus.getDefault().unregister(this)
        } catch (e: Exception) {

        }
        super.onStop()
    }

}