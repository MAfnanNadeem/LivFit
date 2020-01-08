/*
 *  Created by Sumeet Kumar on 1/8/20 12:22 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 12:22 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.heart_rate.chart

import android.graphics.Color
import android.view.View
import android.widget.TextView

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import life.mibo.hardware.core.Logger
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.session.Report
import life.mibo.hexa.utils.Constants
import kotlin.random.Random


class ChartData {

    val days = arrayOf("SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI")
    var dayFormatter: ValueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            try {
                if (value < days.size)
                    return days[value.toInt()]
            } catch (e: java.lang.Exception) {

            }
            return "DAY $value"
        }

        override fun getFormattedValue(value: Float): String {
            return super.getFormattedValue(value)
        }
    }

    var rateFormatter: ValueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return "$value"
        }
    }

    fun getHeartRate(
        chart: LineChart?,
        noDataText: TextView?, type: Int = 0
    ) {
        if (chart == null)
            return
        val entries = ArrayList<Entry>()
        val report: Report? = Prefs.get(chart.context).getJson(Prefs.SESSION, Report::class.java)
        if (report != null) {
            val list = report?.heartRate
            if (list != null && list.size > 1) {
                list?.forEachIndexed { index, s ->
                    entries.add(Entry(index.toFloat(), getFloar(s)))
                }
            }
            if (entries.isEmpty()) {
                chart.setNoDataTextColor(Constants.PRIMARY)
                chart.setNoDataText("No Heart Rate Data Available")
                chart.invalidate()
                noDataText?.visibility = View.GONE
                return
                //fetchHeartRateData()
            } else {
                noDataText?.visibility = View.GONE
                // loadChart(entries, chart)
            }

        } else {
            //fetchHeartRateData()
        }
        loadChart(entries, chart, type)
    }

    fun getFloar(s: String?): Float {
        try {
            if (s != null)
                return s.toFloat()
        } catch (e: Exception) {

        }

        return 0f
    }

    fun loadChart(chart: LineChart?, type: Int = 0) {
        val r = Random(99)
        val list = ArrayList<Entry>()
        for (i in 1..20) {
            list.add(Entry(i.toFloat(), r.nextInt().toFloat()))
        }
        loadChart(list, chart, type)
    }

    fun loadChart(entries: ArrayList<Entry>?, chart: LineChart?, type: Int = 0) {
        if (chart == null || entries == null) {
            //Toasty.warning()
            return
        }
        Logger.e("loadChart " + entries.size)
        if (entries.size == 0) {
            chart.setNoDataTextColor(Color.DKGRAY)
            chart.setNoDataText("No Heart Rate")

        }

        chart.description.isEnabled = false

        chart.setTouchEnabled(true)



        chart.dragDecelerationFrictionCoef = 0.9f

        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = false


        chart.setBackgroundColor(Color.WHITE)
        //chart.setViewPortOffsets(0f, 0f, 0f, 0f)

        // get the legend (only possible after setting data)
        val l = chart.legend
        l.isEnabled = true

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //xAxis.typeface = typeface
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.textColor = Constants.PRIMARY
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f // one hour
        xAxis.valueFormatter = dayFormatter

        val leftAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(false)
        leftAxis.isGranularityEnabled = true
//        leftAxis.axisMinimum = 0f
//        leftAxis.axisMaximum = 170f
        leftAxis.yOffset = -9f
        leftAxis.textColor = Constants.PRIMARY
        leftAxis.valueFormatter = rateFormatter

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false


        //
        var text = "Today"
        if (type == 1)
            text = "Week"
        else if (type == 2)
            text = "Month"
        val set1 = LineDataSet(entries, text)
        set1.axisDependency = AxisDependency.LEFT
        set1.color = ColorTemplate.getHoloBlue()
        set1.valueTextColor = ColorTemplate.getHoloBlue()
        set1.lineWidth = 1.5f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.setDrawFilled(true)
        set1.isHighlightEnabled = false
        set1.fillAlpha = 50
        set1.fillColor = Constants.PRIMARY
        //set1.highLightColor = Color.rgb(244, 117, 117)
        set1.setDrawCircleHole(false)

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)

        data.setValueTextSize(9f)

        // set data
        chart.data = data
        chart.invalidate()
        Logger.e("loadChart chart.invalidate() ")
    }

}