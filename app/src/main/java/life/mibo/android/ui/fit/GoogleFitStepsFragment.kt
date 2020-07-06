/*
 *  Created by Sumeet Kumar on 6/3/20 2:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/14/20 10:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_fit_steps.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.fit.fitbit.Fitbit
import life.mibo.android.ui.fit.fitbit.StepsData
import life.mibo.android.ui.home.HomeItem
import life.mibo.hardware.core.Logger
import okhttp3.Call
import okhttp3.Response
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class GoogleFitStepsFragment : BaseFragment() {

    val TAG = "GoogleFitFragment"

    companion object {
        fun create(type: Int): GoogleFitStepsFragment {
            val frg = GoogleFitStepsFragment()
            val b = Bundle()
            b.putInt("type_type", type)
            frg.arguments = b
            return frg
        }
    }

    private var helper: FitnessHelper? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_fit_steps, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getInt("type_type", 0) ?: 0
        if (type == Fitbit.FITBIT)
            setupFitbit()
        else
            setupGoogle()
    }

    fun setProgress(steps: Int, stepsGoal: Int, heart: String) {
        log("setProgress $steps : $stepsGoal heart$heart")
        activity?.runOnUiThread {
            tv_steps?.text = "$steps"
            tv_points?.text = "$heart"
            tv_steps_goal?.text = "/$stepsGoal"
            if (stepsGoal > 0) {
                var p = steps.div(stepsGoal)
                //log("getStepsGoal percent $p")
                p = p.times(100)
                //log("getStepsGoal percent ${p}")
                //p = p.plus(50)
                //  log("getStepsGoal percent ${p}")
                // circleProgressView?.setCircleColor(ContextCompat.getColor(requireContext(), R.color.textColorApp))
                circleProgressView?.setPercentage(p)
                //getProgress(data)
            } else {
                circleProgressView?.setPercentage(33)
            }
        }
    }

    private fun setupGoogle() {

        tv_week?.setOnClickListener {
            if (fitConnected)
                weekClicked()
        }

        tv_month?.setOnClickListener {
            if (fitConnected)
                monthClicked()
        }

        if (getGoogleFit().isConnected()) {
            getGoogleFit().readDailySteps(object : FitnessHelper.Listener<Int> {
                override fun onComplete(success: Boolean, data: Int?, ex: Exception?) {
                    if (data != null) {
                        tv_steps?.text = "$data"
                        getProgress(data)
                        weekClicked()
                    }
                }

            })

            getGoogleFit().readDailyPoints(object : FitnessHelper.Listener<Int> {
                override fun onComplete(success: Boolean, data: Int?, ex: Exception?) {
                    // log("readDailyPoints $data : $ex")
                    tv_points?.text = "$data"
                }

            })

//            getFit().scanBle(object : BleScanCallback() {
//                override fun onScanStopped() {
//                    log("scanSensors onScanStopped")
//                }
//
//                override fun onDeviceFound(p0: BleDevice?) {
//                    log("scanSensors onDeviceFound $p0")
//                }
//
//            })
//
//            getFit().scanSensors(OnSuccessListener<List<DataSource>> {
//                log("scanSensors OnSuccessListener $it")
//            }, OnFailureListener {
//                log("scanSensors OnFailureListener $it")
//            })
        } else {
            fitConnected = false
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogPhoto)
            builder.setTitle(R.string.google_fit)
            builder.setMessage(R.string.google_fit_connect)
            builder.setPositiveButton(R.string.yes_text) { dialog, which ->
                dialog?.dismiss()
                navigate(0, HomeItem(HomeItem.Type.MY_ACCOUNT))
            }
            builder.setNegativeButton(R.string.no_text) { dialog, which ->
                dialog?.dismiss()
            }

            builder.show()
        }
    }

    var fitConnected = true
    fun getProgress(steps: Int) {
        getGoogleFit().getStepsGoal(object : FitnessHelper.Listener<Double> {
            override fun onComplete(success: Boolean, data: Double?, ex: Exception?) {
                if (data != null && steps > 0) {
                    tv_steps_goal?.text = "/${data.toInt()}"
                    var p = steps.div(data)
                    //log("getStepsGoal percent $p")
                    p = p.times(100)
                    //log("getStepsGoal percent ${p}")
                    //p = p.plus(50)
                    //  log("getStepsGoal percent ${p}")
                    // circleProgressView?.setCircleColor(ContextCompat.getColor(requireContext(), R.color.textColorApp))
                    circleProgressView?.setPercentage(p.toInt())
                }
            }

        })
    }

    fun proanim(p: Float) {
        val anim = ValueAnimator.ofFloat(0f, p)
        anim?.addUpdateListener {

        }
        anim?.start()
    }

    var isWeekMode: Boolean = false
    fun weekClicked() {
        if (isWeekMode)
            return
        isWeekMode = true
        tv_week?.setTextColor(Color.WHITE)
        tv_week?.setBackgroundResource(R.drawable.button_primary_selector)
        tv_month?.setTextColor(Color.DKGRAY)
        tv_month?.background = null

        if (getGoogleFit().isConnected()) {
            getGoogleFit().readyWeekly(OnSuccessListener<DataReadResponse> {
                parseData(it)
            })
        }
    }

    fun monthClicked() {
        if (isWeekMode) {
            isWeekMode = false
            tv_month?.setTextColor(Color.WHITE)
            tv_month?.setBackgroundResource(R.drawable.button_primary_selector)
            tv_week?.setTextColor(Color.DKGRAY)
            tv_week?.background = null

            if (getGoogleFit().isConnected()) {
                getGoogleFit().readyMonthly(OnSuccessListener<DataReadResponse> {
                    parseData(it)
                })
            }
        }
    }

    private fun getGoogleFit(): GoogleFit {
        if (helper == null)
            helper = FitnessHelper(this)
        return helper!!.getGoogleFit()
    }

    private fun getFitbit(): Fitbit {
        if (helper == null)
            helper = FitnessHelper(this)
        return helper!!.getFitBit()
    }


    var isLine = false

    fun parseData(dataReadResult: DataReadResponse) {
        val dateFormat: DateFormat = DateFormat.getDateTimeInstance()
        val dateFormat2: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")
        // val dateFormat2: DateTimeFormatter =
        //    DateTimeFormatter.ofLocalizedDate(org.threeten.bp.format.FormatStyle.SHORT)

        // val list = ArrayList<Entry>()
        val list = ArrayList<BarEntry>()
        val dates = ArrayList<String>()
        var count = 0f
        if (dataReadResult.buckets.size > 0) {
            //GoogleFit.log("Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                val dataSets = bucket.dataSets

                for (dataSet in dataSets) {
                    // GoogleFit.log("Data returned for Data type: " + dataSet.dataType.name)

                    for (dp in dataSet.dataPoints) {
                        // GoogleFit.log("DataBucket point: $dp")
                        //  GoogleFit.log("\tType: " + dp.dataType.name)
                        //  GoogleFit.log("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                        //   GoogleFit.log("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                        //var fData = ""
                        for (field in dp.dataType.fields) {
                            // fData += "Field: " + field.name + " Value: " + dp.getValue(field)
                            //GoogleFit.log(fData)
                            list.add(BarEntry(count, dp.getValue(field).asInt().toFloat()))
                            //dates.add(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                            //dates.add(dateFormat2.format(org.threeten.bp.LocalDate.ofEpochDay(dp.getStartTime(TimeUnit.MILLISECONDS))))
                            dates.add(
                                dateFormat2.format(
                                    org.threeten.bp.LocalDate.ofEpochDay(
                                        dp.getStartTime(
                                            TimeUnit.DAYS
                                        )
                                    )
                                )
                            )
                            count++
                        }

//                        list.add(
//                            Item(
//                                fData,
//                                "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)),
//                                "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
//                            )
//                        )
                    }
                }
            }
        }


        if (dataReadResult.dataSets.size > 0) {
            // list.add(Item("END-------------", "", ""))
            // GoogleFit.log("Number of returned DataSets is: " + dataReadResult.dataSets.size)
            for (dataSet in dataReadResult.dataSets) {
                for (dp in dataSet.dataPoints) {
                    //  GoogleFit.log("DataSet point:")
                    //   GoogleFit.log("\tType: " + dp.dataType.name)
                    //  GoogleFit.log("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                    //  GoogleFit.log("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                    //  var fData = ""
                    for (field in dp.dataType.fields) {
                        //   fData += "Field: " + field.name + " Value: " + dp.getValue(field)
                        //   GoogleFit.log(fData)
                        list.add(BarEntry(count, dp.getValue(field).asInt().toFloat()))
                        // dates.add(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                        dates.add(
                            dateFormat2.format(
                                org.threeten.bp.LocalDate.ofEpochDay(
                                    dp.getStartTime(
                                        TimeUnit.DAYS
                                    )
                                )
                            )
                        )
                        count++
                    }
//                    list.add(
//                        Item(
//                            fData,
//                            "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)),
//                            "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
//                        )
//                    )
                }
            }
        }
        // [END parse_read_data_result]

        // recycler_view?.layoutManager = LinearLayoutManager(context)
        // recycler_view?.adapter = HistoryAdapters(list, null)

        setupBarChart(
            lineChart,
            list,
            dates,
            "",
            ContextCompat.getColor(this.requireContext(), R.color.textColorApp)
        )

    }

    private fun setupBarChart(
        chart: BarChart?,
        list: ArrayList<BarEntry>,
        dates: ArrayList<String>,
        title: String,
        color: Int
    ) {
        Logger.e("setupChart chart $chart size ${list.size}")
        if (chart != null) {

            //val set1 = LineDataSet
            val xFormat: ValueFormatter = MyDateFormatter(dates)

            //chart.setOnChartValueSelectedListener(this)

            //chart.setDrawGridBackground(false)
            //chart.setDrawValueAboveBar(false)
            chart.description.isEnabled = false
            // chart.setMaxVisibleValueCount(60)
            chart.setPinchZoom(false)
            chart.setTouchEnabled(true)
            chart.setScaleEnabled(false)

            chart.setDrawGridBackground(false)
            // chart.setDrawYLabels(false);

            // chart.setDrawYLabels(false);
            //bottom
            // val xFormat: ValueFormatter = MyDateFormatter(dates)
            val xAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            //xAxis.typeface = tfLight
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f // only intervals of 1 day
            xAxis.textColor = Color.GRAY
            //xAxis.labelCount = 7
            xAxis.valueFormatter = xFormat

            //left
            val yFormat: ValueFormatter = MyYFormatter()
            val leftAxis = chart.axisLeft
            //leftAxis.setLabelCount(8, false)
            leftAxis.valueFormatter = yFormat
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 10f
            leftAxis.setDrawZeroLine(true)
            leftAxis.setDrawAxisLine(true)
            leftAxis.setDrawGridLines(false)
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
            leftAxis.textColor = Color.GRAY

            chart.axisRight.isEnabled = false


            chart.legend?.isEnabled = false

            //chart.setDrawValueAboveBar(true)
            val marker = MyMarkerView(requireContext(), yFormat, getString(R.string.steps))
            chart.marker = marker

            //val set1 = LineDataSet()
            val set1 = BarDataSet(list, title)
            set1.setDrawIcons(false)
            //set1.DashPathEffect
            set1.color = color
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.isHighlightEnabled = true
            chart.highlightValue(null)
            // chart.isHighlightFullBarEnabled = true
            // chart.isHighlightPerDragEnabled = false
            //chart.isHighlightPerTapEnabled = false
            //data.setDrawValues(true)


            //data.setValueTextSize(10f)
            // data.setValueTextColor(Color.WHITE)
            data.barWidth = 0.5f
            data.setDrawValues(false)
            chart.data = data
            chart.animateXY(500, 500)
        }
    }

//    private fun setupLineChart(
//        chart: LineChart?,
//        list: ArrayList<Entry>,
//        dates: ArrayList<String>,
//        title: String,
//        color: Int
//    ) {
//        Logger.e("setupChart chart $chart size ${list.size}")
//        if (chart != null) {
//
//            //val set1 = LineDataSet
//            val xFormat: ValueFormatter = MyDateFormatter(dates)
//
//            //chart.setOnChartValueSelectedListener(this)
//
//            //chart.setDrawGridBackground(false)
//            //chart.setDrawValueAboveBar(false)
//            chart.description.isEnabled = false
//            // chart.setMaxVisibleValueCount(60)
//            chart.setPinchZoom(false)
//            chart.setTouchEnabled(false)
//            chart.setScaleEnabled(false)
//
//            chart.setDrawGridBackground(false)
//            // chart.setDrawYLabels(false);
//
//            // chart.setDrawYLabels(false);
//            //bottom
//            // val xFormat: ValueFormatter = MyDateFormatter(dates)
//            val xAxis = chart.xAxis
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            //xAxis.typeface = tfLight
//            xAxis.setDrawGridLines(false)
//            xAxis.granularity = 1f // only intervals of 1 day
//            xAxis.textColor = Color.GRAY
//            //xAxis.labelCount = 7
//            xAxis.valueFormatter = xFormat
//
//            //left
//            val yFormat: ValueFormatter = MyYFormatter()
//            val leftAxis = chart.axisLeft
//            //leftAxis.setLabelCount(8, false)
//            leftAxis.valueFormatter = yFormat
//            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
//            leftAxis.spaceTop = 10f
//            leftAxis.setDrawZeroLine(true)
//            leftAxis.setDrawAxisLine(true)
//            leftAxis.setDrawGridLines(false)
//            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
//            leftAxis.textColor = Color.GRAY
//
//            chart.axisRight.isEnabled = false
//
//
//            chart.legend?.isEnabled = false
//
//            //val set1 = LineDataSet()
//            val set1 = LineDataSet(list, title)
//            set1.setDrawIcons(false)
//
//            set1.color = color
//            val dataSets = ArrayList<ILineDataSet>()
//            dataSets.add(set1)
//            val data = LineData(dataSets)
//            //data.setValueTextSize(10f)
//            // data.setValueTextColor(Color.WHITE)
//            //data.= 0.5f
//            data.setDrawValues(false)
//            chart.data = data
//            chart.animateXY(500, 500)
//        }
//    }
//
//    private fun setupChart(
//        chart: LineChart?,
//        list: ArrayList<Entry>,
//        dates: ArrayList<String>,
//        title: String,
//        color: Int
//    ) {
//        Logger.e("setupChart chart $chart size ${list.size}")
//        if (chart != null) {
//
//            val set1: LineDataSet
//            val xFormat: ValueFormatter = MyDateFormatter(dates)
//            if (chart.data != null &&
//                chart.data.dataSetCount > 0
//            ) {
//                set1 = chart.data.getDataSetByIndex(0) as LineDataSet
//                set1.values.clear()
//                set1.values.addAll(list)
//                chart.xAxis.valueFormatter = xFormat
//                //chart.data.notifyDataChanged()
//                chart.notifyDataSetChanged()
//                chart.post {
//                    chart.invalidate()
//                    //chart.animateXY(500, 500)
//                }
//            } else {
//
//                //chart.setOnChartValueSelectedListener(this)
//
//                //chart.setDrawGridBackground(false)
//                //chart.setDrawValueAboveBar(false)
//                chart.description.isEnabled = false
//                // chart.setMaxVisibleValueCount(60)
//                chart.setPinchZoom(false)
//                chart.setTouchEnabled(false)
//                chart.setScaleEnabled(false)
//
//                chart.setDrawGridBackground(false)
//                // chart.setDrawYLabels(false);
//
//                // chart.setDrawYLabels(false);
//                //bottom
//                // val xFormat: ValueFormatter = MyDateFormatter(dates)
//                val xAxis = chart.xAxis
//                xAxis.position = XAxis.XAxisPosition.BOTTOM
//                //xAxis.typeface = tfLight
//                xAxis.setDrawGridLines(false)
//                xAxis.granularity = 1f // only intervals of 1 day
//                xAxis.textColor = Color.GRAY
//                //xAxis.labelCount = 7
//                xAxis.valueFormatter = xFormat
//
//                //left
//                val yFormat: ValueFormatter = MyYFormatter()
//                val leftAxis = chart.axisLeft
//                //leftAxis.setLabelCount(8, false)
//                leftAxis.valueFormatter = yFormat
//                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
//                leftAxis.spaceTop = 10f
//                leftAxis.setDrawZeroLine(true)
//                leftAxis.setDrawAxisLine(true)
//                leftAxis.setDrawGridLines(false)
//                leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
//                leftAxis.textColor = Color.GRAY
//
//                chart.axisRight.isEnabled = false
////                val rightAxis = chart.axisRight
////                rightAxis.setDrawGridLines(false)
////                rightAxis.setLabelCount(8, false)
////                rightAxis.valueFormatter = custom
////                rightAxis.spaceTop = 15f
////                rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
//
//
//                chart.legend?.isEnabled = false
////                val l = chart.legend
////                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
////                l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
////                l.orientation = Legend.LegendOrientation.HORIZONTAL
////                l.setDrawInside(false)
////                l.form = Legend.LegendForm.CIRCLE
////                l.formSize = 20f
////                l.textSize = 18f
////                l.textColor = Color.GRAY
////                l.xEntrySpace = 4f
//
//
////                val mv = XYMarkerView(this, xAxisFormatter)
////                mv.setChartView(chart) // For bounds control
////                chart.marker = mv // Set the marker to the chart
//
//                set1 = LineDataSet(list, title)
//                set1.setDrawIcons(false)
//
//                set1.color = color
//                val dataSets = ArrayList<ILineDataSet>()
//                dataSets.add(set1)
//                val data = LineData(dataSets)
//                //data.setValueTextSize(10f)
//                // data.setValueTextColor(Color.WHITE)
//                //data.= 0.5f
//                data.setDrawValues(false)
//                chart.data = data
//                chart.animateXY(500, 500)
//                Logger.e("setupChart chart update........")
//            }
//
//        }
//    }
//
//    private fun setupChart(
//        chart: BarChart?,
//        list: ArrayList<BarEntry>,
//        dates: ArrayList<String>,
//        title: String,
//        color: Int
//    ) {
//        Logger.e("setupChart chart $chart size ${list.size}")
//        if (chart != null) {
//
//            val set1: BarDataSet
//
//            if (chart.data != null &&
//                chart.data.dataSetCount > 0
//            ) {
//                set1 = chart.data.getDataSetByIndex(0) as BarDataSet
//                set1.values = list
//                chart.data.notifyDataChanged()
//                chart.notifyDataSetChanged()
//            } else {
//
//                //chart.setOnChartValueSelectedListener(this)
//
//                chart.setDrawBarShadow(false)
//                chart.setDrawValueAboveBar(false)
//                chart.description.isEnabled = false
//                // chart.setMaxVisibleValueCount(60)
//                chart.setPinchZoom(false)
//                chart.setTouchEnabled(false)
//                chart.setScaleEnabled(false)
//
//                chart.setDrawGridBackground(false)
//                // chart.setDrawYLabels(false);
//
//                // chart.setDrawYLabels(false);
//                //bottom
//                val xFormat: ValueFormatter = MyDateFormatter(dates)
//                val xAxis = chart.xAxis
//                xAxis.position = XAxis.XAxisPosition.BOTTOM
//                //xAxis.typeface = tfLight
//                xAxis.setDrawGridLines(false)
//                xAxis.granularity = 1f // only intervals of 1 day
//                xAxis.textColor = Color.WHITE
//                xAxis.labelCount = 7
//                xAxis.valueFormatter = xFormat
//
//                //left
//                val yFormat: ValueFormatter = MyYFormatter()
//                val leftAxis = chart.axisLeft
//                leftAxis.setLabelCount(8, false)
//                leftAxis.valueFormatter = yFormat
//                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
//                leftAxis.spaceTop = 10f
//                leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
//                leftAxis.textColor = Color.WHITE
//
//                chart.axisRight.isEnabled = false
////                val rightAxis = chart.axisRight
////                rightAxis.setDrawGridLines(false)
////                rightAxis.setLabelCount(8, false)
////                rightAxis.valueFormatter = custom
////                rightAxis.spaceTop = 15f
////                rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
//
//
//                val l = chart.legend
//                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//                l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//                l.orientation = Legend.LegendOrientation.HORIZONTAL
//                l.setDrawInside(false)
//                l.form = Legend.LegendForm.CIRCLE
//                l.formSize = 20f
//                l.textSize = 18f
//                l.textColor = Color.WHITE
//                l.xEntrySpace = 4f
//
////                val mv = XYMarkerView(this, xAxisFormatter)
////                mv.setChartView(chart) // For bounds control
////                chart.marker = mv // Set the marker to the chart
//
//                set1 = BarDataSet(list, title)
//                set1.setDrawIcons(false)
//                set1.setColor(color, 200)
//                val dataSets = ArrayList<IBarDataSet>()
//                dataSets.add(set1)
//                val data = BarData(dataSets)
//                //data.setValueTextSize(10f)
//                // data.setValueTextColor(Color.WHITE)
//                data.barWidth = 0.5f
//                data.setDrawValues(false)
//                chart.data = data
//                chart.animateXY(500, 500)
//                Logger.e("setupChart chart update........")
//            }
//
//        }
//    }


    class MyYFormatter() : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            //return super.getFormattedValue(value.toInt())
            return value?.toInt().toString()
        }
    }

    class MyDateFormatter(var dates: ArrayList<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val i = value.toInt()
            if (i < dates.size)
                return dates[i]
            return super.getFormattedValue(value)
        }
    }

    // TODO FITBIT
    private var isFitbitConnected: Boolean = false
    fun setupFitbit() {
        isFitbitConnected = true
        tv_week?.setOnClickListener {
            getFitbitWeekSteps()
        }

        tv_month?.setOnClickListener {
            getFitbitMonthSteps()
        }

        val cal = Calendar.getInstance()
        val date = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
        getFitbit().getSteps(date, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                log("onFailure $e")
            }

            override fun onResponse(call: Call, response: Response) {
                isFitbitConnected = true
                log("onResponse $$call")
                val body = response.body?.string()
                log("onResponse string ${body}")
                if (body?.length ?: 0 > 1) {
                    try {
                        val data: StepsData = Gson().fromJson(body, StepsData::class.java)
                        val list = data.list
                        if (list != null && list.isNotEmpty()) {
                            val steps = list[0]
                            setProgress(steps!!.value?.toInt()!!, 0, "0")
                            log("data >> $data")
                        }

                    } catch (e: Exception) {

                    }
                }
            }

        })

        cal.set(Calendar.DAY_OF_MONTH, -30)
        val start = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
        getFitbit().getSteps(start, date, object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                log("onFailure Month $e")
            }

            override fun onResponse(call: Call, response: Response) {
                isFitbitConnected = true
                log("onResponse Month $$call")
                val body = response.body?.string()
                log("onResponse string ${body}")
                if (body?.length ?: 0 > 1) {
                    try {
                        val data: StepsData = Gson().fromJson(body, StepsData::class.java)
                        log("data2 >> $data")
                        backupSteps.clear()
                        backupSteps.addAll(data.list!!)
                        getFitbitWeekSteps()
                    } catch (e: Exception) {

                    }
                }
            }

        })
    }

    val backupSteps = ArrayList<StepsData.Step?>()

    private fun getFitbitWeekSteps() {
        if (isWeekMode)
            return
        isWeekMode = true
        tv_week?.setTextColor(Color.WHITE)
        tv_week?.setBackgroundResource(R.drawable.button_primary_selector)
        tv_month?.setTextColor(Color.DKGRAY)
        tv_month?.background = null

        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd")
        //val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")
        val dateFormat = SimpleDateFormat("dd/MM")
        val today = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
        val end = cal.time

        cal.set(Calendar.WEEK_OF_YEAR, -1)
        val start = cal.time

        val list = ArrayList<BarEntry>()
        val dates = ArrayList<String>()

        var count = 0f
        for (i in backupSteps) {
            i?.let {
                val date = format.parse(it.dateTime)
                if (date.after(start) && date.before(end)) {
                    list.add(BarEntry(count, it.value?.toFloatOrNull() ?: 0.0f))
                    dates.add(dateFormat.format(date))
                    count++
                }
            }
        }

        setupBarChart(
            lineChart,
            list,
            dates,
            "",
            ContextCompat.getColor(this.requireContext(), R.color.textColorApp)
        )

    }

    private fun getFitbitMonthSteps() {
        if (isWeekMode) {
            isWeekMode = false
            tv_month?.setTextColor(Color.WHITE)
            tv_month?.setBackgroundResource(R.drawable.button_primary_selector)
            tv_week?.setTextColor(Color.DKGRAY)
            tv_week?.background = null

            val list = ArrayList<BarEntry>()
            val dates = ArrayList<String>()
            val dateFormat = SimpleDateFormat("dd/MM")
            val format = SimpleDateFormat("yyyy-MM-dd")
            var count = 0f
            for (i in backupSteps) {
                i?.let {
                    //val date = format.parse(it.dateTime)
                    list.add(BarEntry(count, it.value?.toFloatOrNull() ?: 0.0f))
                    dates.add(dateFormat.format(format.parse(it.dateTime)))
                    count++
                }
            }

            setupBarChart(
                lineChart,
                list,
                dates,
                "",
                ContextCompat.getColor(this.requireContext(), R.color.textColorApp)
            )

        }
    }

    fun setupSHealth() {

    }

}