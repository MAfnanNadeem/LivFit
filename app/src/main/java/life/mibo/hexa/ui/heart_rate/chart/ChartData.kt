/*
 *  Created by Sumeet Kumar on 1/8/20 12:22 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 12:22 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.heart_rate.chart

import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
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
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.calories.CaloriesData
import life.mibo.hexa.models.login.Member
import life.mibo.hexa.models.session.Report
import life.mibo.hexa.models.session.SessionDetails
import life.mibo.hexa.models.session.SessionReport
import life.mibo.hexa.models.weight.Data
import life.mibo.hexa.utils.Constants
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class ChartData {

    val days = arrayOf("SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI")
    var dayFormatter: ValueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            try {
                // if (value < days.size)
                // return days[value.toInt()]
                return getXValue(value.toInt())
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
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

    var timeThreshold = 0
    var startDate: Date? = null
    fun getHeartRate(chart: LineChart?, type: Int = 0) {
        if (chart == null)
            return
        val entries = ArrayList<Entry>()
        var text = ""
        val report: Report? = Prefs.get(chart.context).getJson(Prefs.SESSION, Report::class.java)
        if (report != null) {
            startDate = parseDate(report.sessionReports?.startDatetime)
            val end = parseDate(report.sessionReports?.endDatetime)


            text =
                report.sessionReports?.startDatetime + " - " + report.sessionReports?.endDatetime?.split(
                    " "
                )?.get(1)

            var duration = report.sessionReports?.duration
            val list = report.heartRate
            if (list != null && list.size > 1) {
                list?.forEachIndexed { index, s ->
                    entries.add(Entry(index.toFloat(), getFloar(s)))
                }

                timeThreshold = duration?.div(list.size) ?: 10
            }
            if (entries.isEmpty()) {
                chart.setNoDataTextColor(Constants.PRIMARY)
                chart.setNoDataText("No Heart Rate Data Available")
                chart.invalidate()
                fetchHeartRateData(chart, type)
                //getHeartRate(chart, type)
                //noDataText?.visibility = View.GONE
                return
                //fetchHeartRateData()
            } else {
                //noDataText?.visibility = View.GONE
                // loadChart(entries, chart)
            }

        } else {
            //fetchHeartRateData()
        }
        when (type) {
            0 -> {
                loadChart(entries, chart, text)
            }
            1 -> {
                chart.setNoDataTextColor(Constants.PRIMARY)
                chart.setNoDataText("No Heart Rate Data Available")
                chart.invalidate()
                //loadChart(entries, chart, type)
            }
            2 -> {
                chart.setNoDataTextColor(Constants.PRIMARY)
                chart.setNoDataText("No Heart Rate Data Available")
                chart.invalidate()
                //loadChart(entries, chart, type)
            }
        }
    }

    fun parseDate(string: String?): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            return format.parse(string)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getXValue(pos: Int): String {
        try {
            val cal = Calendar.getInstance()
            cal.time = startDate
            cal.add(Calendar.SECOND, timeThreshold.times(pos))
            return String.format("%02d:%02d", cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "$pos"
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
        loadChart(list, chart)
    }

    fun loadChart(
        entries: ArrayList<Entry>?,
        chart: LineChart?,
        legendText: String = ""
    ) {
        if (chart == null || entries == null) {
            //Toasty.warning()
            return
        }
        Logger.e("loadChart " + entries.size)
        if (entries.size == 0) {
            chart.setNoDataTextColor(Color.DKGRAY)
            chart.setNoDataText("No Data Available")
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
        //leftAxis.textColor = ColorTemplate.getHoloBlue()
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
//        var text = legendText
//        if (type == 1) {
//            text = "Week"
//        } else if (type == 2) {
//            text = "Month"
//        }
        val set1 = LineDataSet(entries, legendText)
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
        set1.color = Constants.PRIMARY

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)

        data.setValueTextSize(9f)
        //chart.animateX(600, Easing.Linear)
        chart.animateXY(800, 500)
        // set data
        chart.data = data
        chart.invalidate()
        Logger.e("loadChart chart.invalidate() ")
    }

    var weightFormator = SimpleDateFormat("dd/MM/yy")
    val dateFormater = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    var dataList = ArrayList<Data?>()
    fun loadWeightChart(list: List<Data?>, chart: LineChart?) {
        if (chart == null)
            return
        dataList.clear()
        //dataList.addAll(list)

        val entries = ArrayList<Entry>()

        list.forEachIndexed { index, s ->
            entries.add(Entry(index.toFloat(), s?.weight?.toFloat() ?: 0f))
            dataList.add(s)
        }
        if (entries.size == 0) {
            chart.setNoDataTextColor(Color.DKGRAY)
            chart.setNoDataText("No Heart Rate")
        }
        Logger.e("loadWeightChart Entries ${list.size} :: ${entries.size}")
        Logger.e("loadWeightChart Entries  $entries")

        chart.description.isEnabled = false

        chart.setTouchEnabled(true)



        chart.dragDecelerationFrictionCoef = 0.9f

        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = false


        chart.setBackgroundColor(Color.TRANSPARENT)
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
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {

                try {
                    Logger.e("loadWeightChart getAxisLabel $value")
                    if (value.toInt() < dataList.size)
                        return weightFormator.format(dateFormater.parse(dataList[value.toInt()]?.date))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return ""
            }
        }

        val leftAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        //leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(false)
        leftAxis.isGranularityEnabled = true
//        leftAxis.axisMinimum = 0f
//        leftAxis.axisMaximum = 170f
        leftAxis.yOffset = -9f
        leftAxis.textColor = Constants.PRIMARY
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return "$value"
            }
        }

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false


        val set1 = LineDataSet(entries, " ")
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
        set1.color = Constants.PRIMARY

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)

        data.setValueTextSize(9f)
        chart.animateY( 500)
        // set data
        chart.data = data
        chart.invalidate()
        Logger.e("loadChart chart.invalidate() ")
    }

    fun fetchHeartRateData(chart: LineChart?, type: Int) {
        val member =
            Prefs.get(chart?.context).member
                ?: return
        //chart.getDialog()?.show()
        val session = SessionDetails("${member.id}", member.accessToken)
        API.request.getApi().getSessionDetails(session).enqueue(object : Callback<SessionReport> {
            override fun onFailure(call: Call<SessionReport>, t: Throwable) {
                //fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(chart?.context!!, "Unable to connect").show()
            }

            override fun onResponse(call: Call<SessionReport>, response: Response<SessionReport>) {

                val data = response.body()
                if (data != null && data.status.equals("success")) {
                    Prefs.get(chart?.context).settJson(Prefs.SESSION, data.report)
                    //parseData(data)
                    val report: Report? = data.report
                    if (report != null) {
                        val entries = ArrayList<Entry>()
                        var text = ""
                        startDate = parseDate(report.sessionReports?.startDatetime)
                        val end = parseDate(report.sessionReports?.endDatetime)


                        text =
                            report.sessionReports?.startDatetime + " - " + report.sessionReports?.endDatetime?.split(
                                " "
                            )?.get(1)

                        var duration = report.sessionReports?.duration
                        val list = report.heartRate
                        if (list != null && list.size > 1) {
                            list?.forEachIndexed { index, s ->
                                entries.add(Entry(index.toFloat(), getFloar(s)))
                            }

                            timeThreshold = duration?.div(list.size) ?: 10
                        }
                        if (entries.isEmpty()) {
                            chart?.setNoDataTextColor(Constants.PRIMARY)
                            chart?.setNoDataText("No Heart Rate Data Available")
                            chart?.invalidate()
                            //fetchHeartRateData(chart, type)
                            //getHeartRate(chart, type)
                            //noDataText?.visibility = View.GONE
                            return
                            //fetchHeartRateData()
                        } else {
                            when (type) {
                                0 -> {
                                    loadChart(entries, chart, text)
                                }
                                1 -> {
                                    chart?.setNoDataTextColor(Constants.PRIMARY)
                                    chart?.setNoDataText("No Heart Rate Data Available")
                                    chart?.invalidate()
                                    //loadChart(entries, chart, type)
                                }
                                2 -> {
                                    chart?.setNoDataTextColor(Constants.PRIMARY)
                                    chart?.setNoDataText("No Heart Rate Data Available")
                                    chart?.invalidate()
                                    //loadChart(entries, chart, type)
                                }
                            }
                        }



                    }
                } else {

                    val err = data?.error?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(chart?.context!!, R.string.error_occurred).show()
                    else Toasty.error(chart?.context!!, err, Toasty.LENGTH_LONG).show()
                }
                //fragment.getDialog()?.dismiss()
            }
        })
    }

    var graphList = java.util.ArrayList<CaloriesData>()

    fun loadCaloriesChart(chart: LineChart?, data: java.util.ArrayList<CaloriesData>?, legendText: String) {
        if (chart == null || data == null)
            return
        if (data.size == 0) {
            chart.setNoDataTextColor(Color.DKGRAY)
            chart.setNoDataText("No Data Available")
        }
        val list = java.util.ArrayList<Entry>()
        graphList = data
        data.forEachIndexed { index, it ->
            list.add(Entry(index.toFloat(),it.caloriesBurnt!!.toFloat()))
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

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {

                try {
                    return graphList[value.toInt()].startDatetime!!.split(" ")[0]
                } catch (e: Exception) {

                }
                return ""
            }
        }

        val leftAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        //leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(false)
        leftAxis.isGranularityEnabled = true
//        leftAxis.axisMinimum = 0f
//        leftAxis.axisMaximum = 170f
        leftAxis.yOffset = -9f
        leftAxis.textColor = Constants.PRIMARY
        leftAxis.valueFormatter = object : ValueFormatter(){

        }


        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false


        //
//        var text = legendText
//        if (type == 1) {
//            text = "Week"
//        } else if (type == 2) {
//            text = "Month"
//        }
        val set1 = LineDataSet(list, legendText)
        set1.axisDependency = YAxis.AxisDependency.LEFT
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
        set1.color = Constants.PRIMARY

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)

        data.setValueTextSize(9f)
        chart.animateY( 500)
        // set data
        chart.data = data
        chart.invalidate()
        Logger.e("loadChart chart.invalidate() ")
    }

}