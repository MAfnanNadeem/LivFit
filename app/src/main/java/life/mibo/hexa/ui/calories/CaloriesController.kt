/*
 *  Created by Sumeet Kumar on 1/12/20 3:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 3:15 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.calories

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.base.PostData
import life.mibo.hexa.models.calories.Calories
import life.mibo.hexa.models.calories.CaloriesData
import life.mibo.hexa.models.login.Member
import life.mibo.hexa.utils.Constants
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CaloriesController(val fragment: CaloriesFragment, val observer: CaloriesObserver) {

    fun getCalories() {
        val member =
            Prefs.get(this.fragment.context).getMember<Member?>(Member::class.java)
                ?: return
        fragment.getDialog()?.show()
        val post = PostData("${member.id}", member.accessToken, "CaloriesBurnt")
        API.request.getApi().getAllCaloriesBurnt(post).enqueue(object : Callback<Calories> {
            override fun onFailure(call: Call<Calories>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, "Unable to connect").show()
            }

            override fun onResponse(call: Call<Calories>, response: Response<Calories>) {

                fragment.getDialog()?.dismiss()
                val data = response.body()
                if (data != null && data.status.equals("success")) {
                    parseData(data)
                } else {
                    val err = data?.error?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.context!!, R.string.error_occurred).show()
                    else Toasty.error(fragment.context!!, err, Toasty.LENGTH_LONG).show()
                }


            }
        })
    }

    fun parseData(calories: Calories?) {
        val list = ArrayList<CaloriesData>()
        if (calories != null) {
            calories.data?.forEach {
                list.add(it!!)
            }
        }

        observer.onDataReceived(list)
    }

    var graphList = ArrayList<CaloriesData>()

    fun loadChart(chart: LineChart?, data: ArrayList<CaloriesData>?, legendText: String) {
        if (chart == null || data == null)
            return
        if (data.size == 0) {
            chart.setNoDataTextColor(Color.DKGRAY)
            chart.setNoDataText("No Data Available")
        }
        val list = ArrayList<Entry>()
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

        // set data
        chart.data = data
        chart.invalidate()
        Logger.e("loadChart chart.invalidate() ")
    }
}