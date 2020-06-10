/*
 *  Created by Sumeet Kumar on 5/27/20 9:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/12/20 2:53 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.weight

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_weight_compare.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.weight.CompareMemberWeight
import life.mibo.android.models.weight.CompareWeightResponse
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class WeightCompareFragment() : BaseFragment() {

    var unit: String = "kg"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weight_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_title.visibility = View.GONE

        val pref = Prefs.get(context)
        var gndr = getString(R.string.gender_male)
        if ("female" == pref?.member?.gender?.toLowerCase())
            gndr = getString(R.string.gender_female)
        var weight = pref["user_weight"]
        var height = pref["user_height"]
        if(weight.isNullOrEmpty())
            weight = "0"
        if(height.isNullOrEmpty())
            height = "0"
        tv_gender?.text = "$gndr"
        tv_weight?.text = "$weight"
        tv_height?.text = "$height"

        try {
            loadImage(imageViewProfile, pref?.member?.profileImg, gndr)
            tv_name?.text = pref.member?.firstName + " " + pref.member?.lastName
        } catch (e: java.lang.Exception) {
            MiboEvent.log(e)
            //tv_name?.text = "${pref["user_date"]}"
        }

        val member = pref?.member
        getWeightCompare(member?.id(), member?.accessToken)
        //getChartPrefs(it.toLowerCase(), chart)
    }

    private fun loadImage(iv: ImageView?, url: String?, gender: String) {
        Utils.loadImage(iv, url, gender?.toLowerCase() == "male")
    }

    fun getWeightCompare(memberId: String?, token: String?) {
        if (memberId == null || token == null)
            return
        getDialog()?.show()
        API.request.getApi()
            .compareMemberWeight(CompareMemberWeight(CompareMemberWeight.Data(memberId), token))
            .enqueue(object :
                Callback<CompareWeightResponse> {

                override fun onFailure(call: Call<CompareWeightResponse>, t: Throwable) {
                    getDialog()?.dismiss()
                    t.printStackTrace()
                    context?.let {
                        Toasty.error(it, R.string.unable_to_connect).show()
                    }
                }

                override fun onResponse(
                    call: Call<CompareWeightResponse>,
                    response: Response<CompareWeightResponse>
                ) {

                    getDialog()?.dismiss()
                    val data = response.body()
                    if (data != null && data.status.equals("success", true)) {
                        parseChartData(data)
                    } else {
                        val err = data?.errors?.get(0)?.message
                        if (err.isNullOrEmpty())
                            Toasty.error(requireContext(), R.string.error_occurred).show()
                        else Toasty.error(requireContext(), err, Toasty.LENGTH_LONG).show()
                        checkSession(data)
                    }

                }
            })
    }


    private fun parseChartData(data: CompareWeightResponse) {
        val list = data.data

        if (list != null) {
            val entries = ArrayList<BarEntry>()
            val dates = ArrayList<String>()
            val parser = SimpleDateFormat("yyyy-mm-dd")
            val formater = SimpleDateFormat("dd/mm")
            var count = 1.0f
            for (weight in list) {
                entries.add(BarEntry(count, weight?.weight?.toFloat() ?: 0f))
                try {
                    dates.add(formater.format(parser.parse(weight?.createdAt?.date)))
                } catch (e: Exception) {

                }
                count++
            }
            Logger.e("getChartPrefs $count list.size ${entries.size} ${dates.size}")
            setupChart(barChart, entries, dates, getString(R.string.weight), 0xFF0B369B.toInt())
        } else {
            barChart?.setNoDataText(getString(R.string.no_data_found))
            barChart?.setNoDataTextColor(Color.LTGRAY)
        }


    }


    private fun setupChart(
        chart: BarChart?,
        list: ArrayList<BarEntry>,
        dates: ArrayList<String>,
        title: String,
        baseColor: Int
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
                xAxis.position = XAxisPosition.BOTTOM
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
                leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
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
                l.form = LegendForm.CIRCLE
                l.formSize = 20f
                l.textSize = 18f
                l.textColor = Color.WHITE
                l.xEntrySpace = 4f

//                val mv = XYMarkerView(this, xAxisFormatter)
//                mv.setChartView(chart) // For bounds control
//                chart.marker = mv // Set the marker to the chart

                set1 = BarDataSet(list, title)
                set1.setDrawIcons(false)
                set1.setColor(baseColor, 200)
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

}