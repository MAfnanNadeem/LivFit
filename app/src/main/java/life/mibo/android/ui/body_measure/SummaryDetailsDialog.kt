/*
 *  Created by Sumeet Kumar on 4/27/20 8:42 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/25/20 4:18 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.models.biometric.Biometric
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.SummaryAdapter
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class SummaryDetailsDialog(
    var data: SummaryAdapter.Item?,
    var listner: ItemClickListener<SummaryAdapter.Item>?
) :
    DialogFragment() {

    var unit: String = "cm"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_body_summary_details2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var header: TextView? = view?.findViewById(R.id.tv_title)
        //var title: TextView? = view?.findViewById(R.id.tv_value_text)
        var value: TextView? = view?.findViewById(R.id.tv_value)
        var unit: TextView? = view?.findViewById(R.id.tv_value_unit)
        var gender: TextView? = view?.findViewById(R.id.tv_gender)
        var weight: TextView? = view?.findViewById(R.id.tv_weight)
        var height: TextView? = view?.findViewById(R.id.tv_height)
        var normal: TextView? = view?.findViewById(R.id.tv_value_normal)
        var tvName: TextView? = view?.findViewById(R.id.tv_name)
        var tvDate: TextView? = view?.findViewById(R.id.tv_date)
        var bgView: View? = view?.findViewById(R.id.constraintLayout)
        var image: ImageView? = view?.findViewById(R.id.image_circle)
        var profilePic: ImageView? = view?.findViewById(R.id.imageViewProfile)
        var chart: BarChart? = view?.findViewById(R.id.barChart)
        var back: View?? = view?.findViewById(R.id.btn_back)
        //var tabs: TabLayout? = view?.findViewById(R.id.tabLayout)
//        if (data?.title?.trim() == "BMI") {
//            data?.title = "BMI (Body Mass Index)"
//            normal?.text = getString(R.string.bmi_normal)
//        }
        if (data == null) {
            dismiss()
            return
        }
        val titleText =  getTitle(data?.title?.trim()?.toLowerCase())
        //title?.text = titleText
        value?.text = String.format("%.2f", data?.value)
        unit?.text = "${data?.unit}"
        normal?.text = "${data?.normal}"
        header?.text = getString(R.string.my_summary, titleText)
        //header?.text = "My $titleText"

        data?.iconRes?.let {
            image?.setImageResource(it)
        }
        try {
            bgView?.background = Utils.getColorFilterDrawable(context, R.drawable.bg_body_measure_summary_details, data!!.imageColor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isCancelable = true
        //setupChart(chart)
//        tabs?.addTab(TabLayout.Tab().setText("Today"))
//        tabs?.addTab(TabLayout.Tab().setText("Week"))
//        tabs?.addTab(TabLayout.Tab().setText("Month"))

        val pref = Prefs.get(context)
        val member = Prefs.get(context).member
        var gndr = getString(R.string.gender_male)
        if ("female" == member?.gender?.trim()?.toLowerCase())
            gndr = getString(R.string.gender_female)
        gender?.text = "$gndr"
        weight?.text = "${pref[Prefs.WEIGHT]}"
        height?.text = "${pref[Prefs.HEIGHT]}"

        try {

            //"${pref["user_date"]}"
            //tvDate?.text = "${pref["user_date"]}"
            Utils.loadImage(profilePic, member?.profileImg, member?.isMale() ?: true)
            tvName?.text = member?.firstName + " " + member?.lastName
            tvDate?.text = SimpleDateFormat.getDateInstance()
                .format(SimpleDateFormat("yyyy-MM-dd").parse(pref[Prefs.DATE]))
        } catch (e: java.lang.Exception) {
            MiboEvent.log(e)
            tvDate?.text = "${pref[Prefs.DATE]}"
        }

        data?.title?.let {
            getChartPrefs(it.toLowerCase(), chart)
        }
        back?.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    private fun getTitle(title: String?): String? {
        if (title == null)
            return title
        when {
            title.contains("bmi") -> {
                data?.normal = getString(R.string.bmi_normal)
                return getString(R.string.bmi_title)
            }
            title.contains("bsa") -> {
                data?.normal = getString(R.string.bsa_normal)
                return getString(R.string.bsa_title)
            }
            title.contains("ibw") -> {
                data?.normal = getString(R.string.ibw_normal)
                return getString(R.string.ibw_title)
            }
            title.contains("bmr") -> {
                data?.normal = getString(R.string.bmr_normal)
                return getString(R.string.bmr_title)
            }
            title.contains("weight loss") -> {
                data?.normal = getString(R.string.weight_loss_normal)
                return getString(R.string.weight_loss_title)
            }
            title.contains("body fat") -> {
                data?.normal = getString(R.string.body_fat_normal)
                return getString(R.string.body_fat_title)
            }
            title.contains("fat free") -> {
                data?.normal = getString(R.string.ffmi_normal)
                return getString(R.string.ffmi_title)
            }
            title.contains("energy") -> {
                data?.normal = getString(R.string.energy_normal)
                return getString(R.string.energy_title)
            }
            title.contains("body water") -> {
                data?.normal = getString(R.string.body_water_normal)
                return getString(R.string.body_water_title)
            }
            title.contains("waist height") -> {
                data?.normal = getString(R.string.waist_height_normal)
                return getString(R.string.waist_height_title)
            }
            title.contains("waist hip") -> {
                data?.normal = getString(R.string.waist_hip_normal)
                return getString(R.string.waist_hip_title)
            }

            title.contains("body mass") -> {
                data?.normal = getString(R.string.body_mass_normal)
                return getString(R.string.body_mass_title)
            }
        }

        return title
    }

//    private fun setupChart(chart: BarChart?) {
//        if (chart != null) {
//
//            val list = ArrayList<BarEntry>()
//            list.add(BarEntry(1f, Random.nextInt(100).toFloat()))
//            list.add(BarEntry(2f, Random.nextInt(100).toFloat()))
//            list.add(BarEntry(3f, Random.nextInt(100).toFloat()))
//            list.add(BarEntry(4f, Random.nextInt(100).toFloat()))
//            list.add(BarEntry(5f, Random.nextInt(100).toFloat()))
//            list.add(BarEntry(6f, Random.nextInt(100).toFloat()))
//            list.add(BarEntry(7f, Random.nextInt(100).toFloat()))
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
//                val xFormat: ValueFormatter = MyXFormatter(ArrayList())
//                val xAxis = chart.xAxis
//                xAxis.position = XAxisPosition.BOTTOM
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
//                leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
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
//                l.form = LegendForm.CIRCLE
//                l.formSize = 20f
//                l.textSize = 18f
//                l.textColor = Color.WHITE
//                l.xEntrySpace = 4f
//
////                val mv = XYMarkerView(this, xAxisFormatter)
////                mv.setChartView(chart) // For bounds control
////                chart.marker = mv // Set the marker to the chart
//
//                set1 = BarDataSet(list, data?.title)
//                set1.setDrawIcons(false)
//                set1.setColor(data!!.imageColor, 150)
//                val dataSets = ArrayList<IBarDataSet>()
//                dataSets.add(set1)
//                val data = BarData(dataSets)
//                //data.setValueTextSize(10f)
//                // data.setValueTextColor(Color.WHITE)
//                data.barWidth = 0.5f
//                data.setDrawValues(false)
//                chart.data = data
//                chart.animateXY(500, 500)
//            }
//
//        }
//    }

    private fun setupChart(chart: BarChart?, list: ArrayList<BarEntry>, dates: ArrayList<String>) {
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


//                val l = chart.legend
//                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//                l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//                l.orientation = Legend.LegendOrientation.HORIZONTAL
//                l.setDrawInside(false)
//                l.form = LegendForm.CIRCLE
//                l.formSize = 20f
//                l.textSize = 18f
//                l.textColor = Color.WHITE
//                l.xEntrySpace = 4f
                chart.legend?.isEnabled = false

//                val mv = XYMarkerView(this, xAxisFormatter)
//                mv.setChartView(chart) // For bounds control
//                chart.marker = mv // Set the marker to the chart

                set1 = BarDataSet(list, "")
                set1.setDrawIcons(false)
                set1.setColor(data!!.imageColor, 200)
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
            return it.toFloatOrNull() ?: 0.0f
        }
        return 0f
    }

    var chartType = ""
    private fun getChartPrefs(
        title: String,
        chart: BarChart?
    ) {
        val list: List<Biometric.Data?>? = Prefs.get(requireContext())
            .getJsonList(Prefs.BIOMETRIC, Biometric.Data::class.java)
        Logger.e("getChartPrefs $title ${list?.size}")
        //Logger.e("getChartPrefs $list")

        list?.let {
            val entries = ArrayList<BarEntry>()
            val dates = ArrayList<String>()
            val parser = SimpleDateFormat("yyyy-mm-dd")
            val formater = SimpleDateFormat("dd/mm")
            var count = 1.0f
            for (i in list) {
                if (i == null)
                    break
                val data = Biometric.Decrypted.from(i)
                try {
                    dates.add(formater.format(parser.parse(data?.createdAt?.date)))
                } catch (e: Exception) {

                }
                when {
                    title.contains("bmi") -> {
                        entries.add(BarEntry(count, getFloat(data?.bMI)))
                    }
                    title.contains("bsa") -> {
                        entries.add(BarEntry(count, getFloat(data?.bSA)))
                    }
                    title.contains("ibw") -> {
                        entries.add(BarEntry(count, getFloat(data?.iBW)))
                    }
                    title.contains("bmr") -> {
                        entries.add(BarEntry(count, getFloat(data?.bMR)))
                    }
                    title.contains("weight loss") -> {
                        entries.add(BarEntry(count, getFloat(data?.weightLoss)))
                    }
                    title.contains("body fat") -> {
                        entries.add(BarEntry(count, getFloat(data?.bodyFat)))
                    }
                    title.contains("fat free") -> {
                        entries.add(BarEntry(count, getFloat(data?.fatFreeWeight)))
                    }
                    title.contains("energy") -> {
                        entries.add(BarEntry(count, getFloat(data?.energy)))
                    }
                    title.contains("body water") -> {
                        entries.add(BarEntry(count, getFloat(data?.bodyWater)))
                    }
                    title.contains("waist height") -> {
                        entries.add(BarEntry(count, getFloat(data?.waistHeightRatio)))
                    }
                    title.contains("waist hip") -> {
                        entries.add(BarEntry(count, getFloat(data?.waistHipRatio)))
                    }

                    title.contains("body mass") -> {
                        entries.add(BarEntry(count, getFloat(data?.leanBodyMass)))
                    }
                }
                count++
            }
            Logger.e("getChartPrefs $count list.size ${entries.size} ${dates.size}")
            Single.just("").delay(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
                    setupChart(chart, entries, dates)
                }.doOnError {
                    activity?.runOnUiThread {
                        setupChart(chart, entries, dates)
                    }
                }.subscribe()

        }

    }

    fun getIndicators(): String {

        return ""
    }

    private fun parseEntry(title: String?): String? {
        if (title == null)
            return title

        return title
    }

    class MyXFormatter(var list: ArrayList<String>) : ValueFormatter() {

        val days = arrayOf(
            "Sun", "Mon", "Tue", "Wed", "Thus", "Fri", "Sat"
        )

        val dates = arrayOf(
            "25/4", "26/4", "27/4", "28/4", "29/4", "30/4", "01/05"
        )

        val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        override fun getFormattedValue(value: Float): String {
            val i = (value % 7).toInt()
            if (i < dates.size)
                return dates[i]
            return super.getFormattedValue(value)
        }

        fun dummy() {
            list.clear()
            list.add("")
        }

    }

    class MyYFormatter() : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return String.format("%.2f", value)
            //return super.getFormattedValue(value)
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

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog?.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}