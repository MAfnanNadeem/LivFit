/*
 *  Created by Sumeet Kumar on 1/8/20 11:28 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 11:25 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.heart_rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import life.mibo.hexa.R
import life.mibo.hexa.ui.heart_rate.chart.ChartData


class HeartRateTabFragment : Fragment() {

    companion object {
        fun create(type: Int): HeartRateTabFragment {
            val fragment = HeartRateTabFragment()
            val args = Bundle()
            args.putInt("type_", type)
            fragment.arguments = args
            return fragment
        }
    }


    var type: Int = 0
    var chart: LineChart? = null
    var noDataText: TextView? = null
    lateinit var chartData: ChartData
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(life.mibo.hexa.R.layout.fragment_hr_tab1, container, false)
        chart = root.findViewById(R.id.lineChart)
        noDataText = root.findViewById(R.id.lineChart_no_data)
        type = arguments?.getInt("type_") ?: 0
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chartData = ChartData()
        //chartData.loadChart(chart)
        chartData.getHeartRate(chart, noDataText, type)
    }


    override fun onStop() {
        super.onStop()
    }

}