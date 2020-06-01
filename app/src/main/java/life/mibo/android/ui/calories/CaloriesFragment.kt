/*
 *  Created by Sumeet Kumar on 1/9/20 9:58 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 9:58 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calories

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_calories_compare.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import java.text.SimpleDateFormat


class CaloriesFragment : BaseFragment(), CaloriesObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: CaloriesController
    //var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_calories_compare, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    var isMale = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = CaloriesController(this@CaloriesFragment, this)
        controller.getCalories()
        tv_title.visibility = View.GONE

        val pref = Prefs.get(context)
        var gndr = getString(R.string.gender_male)
        if ("female" == pref["user_gender"]?.toLowerCase()) {
            gndr = getString(R.string.gender_female)
            isMale = false
        }
        var weight = pref["user_weight"]
        var height = pref["user_height"]
        if (weight.isNullOrEmpty())
            weight = "0"
        if (height.isNullOrEmpty())
            height = "0"
        tv_gender?.text = "$gndr"
        tv_weight?.text = "$weight"
        tv_height?.text = "$height"

        try {
            tv_name?.text = pref.member?.firstName + " " + pref.member?.lastName
            Utils.loadImage(imageViewProfile, pref?.member?.profileImg, isMale)
        } catch (e: java.lang.Exception) {
            MiboEvent.log(e)

        }

    }

    override fun onDataReceived(list: ArrayList<CaloriesData>) {
       // val data = life.mibo.hexa.ui.heart_rate.chart.ChartData()
        var total = 0
        //val entries = java.util.ArrayList<Entry>()
//        list.forEachIndexed { index, caloriesData ->
//            total += caloriesData.caloriesBurnt ?: 0
//        }

        parseChartData(list)

        //hr_value.text = "$total"
        //hr_value_symbol?.visibility = View.VISIBLE
        //controller.loadChart(lineChart, list, "")
    }


    private fun parseChartData(list: ArrayList<CaloriesData>?) {
        // val list = data.data

        if (list != null && list.size > 0) {
            val entries = ArrayList<BarEntry>()
            val dates = ArrayList<String>()
            val parser = SimpleDateFormat("yyyy-mm-dd")
            val formater = SimpleDateFormat("dd/mm")
            var count = 1.0f
            for (cal in list) {
                entries.add(BarEntry(count, cal?.caloriesBurnt?.toFloat() ?: 0f))
                try {
                    dates.add(formater.format(parser.parse(cal?.startDatetime?.split(" ")?.get(0))))
                } catch (e: Exception) {

                }
                count++
            }
            Logger.e("getChartPrefs $count list.size ${entries.size} ${dates.size}")
            //FE9001
            //C21F2A
            setupChart(barChart, entries, dates, getString(R.string.calories))
        } else {
            barChart?.setNoDataText(getString(R.string.no_data_found))
            barChart?.setNoDataTextColor(Color.LTGRAY)
        }


    }


    private fun setupChart(
        chart: BarChart?,
        list: ArrayList<BarEntry>,
        dates: ArrayList<String>,
        title: String
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
                set1.setColor(0xFFFE9001.toInt(), 200)
                set1.setGradientColor(0xFFFE9001.toInt(),  0xFFC21F2A.toInt())
                //set1.setColors(0xFFFE9001.toInt(),  0xFFC21F2A.toInt(), 200)
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

    fun getFloat(f: Double?): Float {
        f?.let {
            return it.toFloat()
        }
        return 0f
    }

    fun getFloat(f: String?): Float {
        f?.let {
            return it.toFloat()
        }
        return 0f
    }

    class MyYFormatter() : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return super.getFormattedValue(value)
        }
    }

    class MyDateFormatter(var dates: ArrayList<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val i = value.toInt().minus(1)
            if (i < dates.size)
                return dates[i]
            return super.getFormattedValue(value)
        }
    }



    override fun onStop() {
        super.onStop()
        //controller.onStop()
    }

}