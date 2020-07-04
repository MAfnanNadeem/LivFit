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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.fitness.data.BleDevice
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.request.BleScanCallback
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.fragment_fit_steps.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.home.HomeItem
import life.mibo.hardware.core.Logger
import org.threeten.bp.format.DateTimeFormatter
import java.text.DateFormat
import java.util.concurrent.TimeUnit

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

        // val type = arguments?.getInt("type_type", 0) ?: 0

        tv_week?.setOnClickListener {
            if (fitConnected)
                weekClicked()
        }

        tv_month?.setOnClickListener {
            if (fitConnected)
                monthClicked()
        }


        if (getFit().isConnected()) {
            getFit().readDailySteps(object : FitnessHelper.Listener<Int> {
                override fun onComplete(success: Boolean, data: Int?, ex: Exception?) {
                    if (data != null) {
                        tv_steps?.text = "$data"
                        getProgress(data)
                        weekClicked()
                    }
                }

            })

            getFit().readDailyPoints(object : FitnessHelper.Listener<Int> {
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
        getFit().getStepsGoal(object : FitnessHelper.Listener<Double> {
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

        if (getFit().isConnected()) {
            getFit().readyWeekly(OnSuccessListener<DataReadResponse> {
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

            if (getFit().isConnected()) {
                getFit().readyMonthly(OnSuccessListener<DataReadResponse> {
                    parseData(it)
                })
            }
        }
    }

    private fun getFit(): GoogleFit {
        if (helper == null)
            helper = FitnessHelper(this)
        return helper!!.getGoogleFit()
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

    private fun setupLineChart(
        chart: LineChart?,
        list: ArrayList<Entry>,
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
            chart.setTouchEnabled(false)
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

            //val set1 = LineDataSet()
            val set1 = LineDataSet(list, title)
            set1.setDrawIcons(false)

            set1.color = color
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)
            val data = LineData(dataSets)
            //data.setValueTextSize(10f)
            // data.setValueTextColor(Color.WHITE)
            //data.= 0.5f
            data.setDrawValues(false)
            chart.data = data
            chart.animateXY(500, 500)
        }
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
            chart.setTouchEnabled(false)
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

            //val set1 = LineDataSet()
            val set1 = BarDataSet(list, title)
            set1.setDrawIcons(false)

            set1.color = color
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            //data.setValueTextSize(10f)
            // data.setValueTextColor(Color.WHITE)
            data.barWidth = 0.5f
            data.setDrawValues(false)
            chart.data = data
            chart.animateXY(500, 500)
        }
    }

    private fun setupChart(
        chart: LineChart?,
        list: ArrayList<Entry>,
        dates: ArrayList<String>,
        title: String,
        color: Int
    ) {
        Logger.e("setupChart chart $chart size ${list.size}")
        if (chart != null) {

            val set1: LineDataSet
            val xFormat: ValueFormatter = MyDateFormatter(dates)
            if (chart.data != null &&
                chart.data.dataSetCount > 0
            ) {
                set1 = chart.data.getDataSetByIndex(0) as LineDataSet
                set1.values.clear()
                set1.values.addAll(list)
                chart.xAxis.valueFormatter = xFormat
                //chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
                chart.post {
                    chart.invalidate()
                    //chart.animateXY(500, 500)
                }
            } else {

                //chart.setOnChartValueSelectedListener(this)

                //chart.setDrawGridBackground(false)
                //chart.setDrawValueAboveBar(false)
                chart.description.isEnabled = false
                // chart.setMaxVisibleValueCount(60)
                chart.setPinchZoom(false)
                chart.setTouchEnabled(false)
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
//                val rightAxis = chart.axisRight
//                rightAxis.setDrawGridLines(false)
//                rightAxis.setLabelCount(8, false)
//                rightAxis.valueFormatter = custom
//                rightAxis.spaceTop = 15f
//                rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


                chart.legend?.isEnabled = false
//                val l = chart.legend
//                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//                l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//                l.orientation = Legend.LegendOrientation.HORIZONTAL
//                l.setDrawInside(false)
//                l.form = Legend.LegendForm.CIRCLE
//                l.formSize = 20f
//                l.textSize = 18f
//                l.textColor = Color.GRAY
//                l.xEntrySpace = 4f


//                val mv = XYMarkerView(this, xAxisFormatter)
//                mv.setChartView(chart) // For bounds control
//                chart.marker = mv // Set the marker to the chart

                set1 = LineDataSet(list, title)
                set1.setDrawIcons(false)

                set1.color = color
                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(set1)
                val data = LineData(dataSets)
                //data.setValueTextSize(10f)
                // data.setValueTextColor(Color.WHITE)
                //data.= 0.5f
                data.setDrawValues(false)
                chart.data = data
                chart.animateXY(500, 500)
                Logger.e("setupChart chart update........")
            }

        }
    }

    private fun setupChart(
        chart: BarChart?,
        list: ArrayList<BarEntry>,
        dates: ArrayList<String>,
        title: String,
        color: Int
    ) {
        Logger.e("setupChart chart $chart size ${list.size}")
        if (chart != null) {

            val set1: BarDataSet

            if (chart.data != null &&
                chart.data.dataSetCount > 0
            ) {
                set1 = chart.data.getDataSetByIndex(0) as BarDataSet
                set1.values = list
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
            } else {

                //chart.setOnChartValueSelectedListener(this)

                chart.setDrawBarShadow(false)
                chart.setDrawValueAboveBar(false)
                chart.description.isEnabled = false
                // chart.setMaxVisibleValueCount(60)
                chart.setPinchZoom(false)
                chart.setTouchEnabled(false)
                chart.setScaleEnabled(false)

                chart.setDrawGridBackground(false)
                // chart.setDrawYLabels(false);

                // chart.setDrawYLabels(false);
                //bottom
                val xFormat: ValueFormatter = MyDateFormatter(dates)
                val xAxis = chart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                //xAxis.typeface = tfLight
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f // only intervals of 1 day
                xAxis.textColor = Color.WHITE
                xAxis.labelCount = 7
                xAxis.valueFormatter = xFormat

                //left
                val yFormat: ValueFormatter = MyYFormatter()
                val leftAxis = chart.axisLeft
                leftAxis.setLabelCount(8, false)
                leftAxis.valueFormatter = yFormat
                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                leftAxis.spaceTop = 10f
                leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
                leftAxis.textColor = Color.WHITE

                chart.axisRight.isEnabled = false
//                val rightAxis = chart.axisRight
//                rightAxis.setDrawGridLines(false)
//                rightAxis.setLabelCount(8, false)
//                rightAxis.valueFormatter = custom
//                rightAxis.spaceTop = 15f
//                rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


                val l = chart.legend
                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                l.orientation = Legend.LegendOrientation.HORIZONTAL
                l.setDrawInside(false)
                l.form = Legend.LegendForm.CIRCLE
                l.formSize = 20f
                l.textSize = 18f
                l.textColor = Color.WHITE
                l.xEntrySpace = 4f

//                val mv = XYMarkerView(this, xAxisFormatter)
//                mv.setChartView(chart) // For bounds control
//                chart.marker = mv // Set the marker to the chart

                set1 = BarDataSet(list, title)
                set1.setDrawIcons(false)
                set1.setColor(color, 200)
                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(set1)
                val data = BarData(dataSets)
                //data.setValueTextSize(10f)
                // data.setValueTextColor(Color.WHITE)
                data.barWidth = 0.5f
                data.setDrawValues(false)
                chart.data = data
                chart.animateXY(500, 500)
                Logger.e("setupChart chart update........")
            }

        }
    }


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
}