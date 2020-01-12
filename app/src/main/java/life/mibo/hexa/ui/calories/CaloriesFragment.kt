/*
 *  Created by Sumeet Kumar on 1/9/20 9:58 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 9:58 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.calories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.Entry
import kotlinx.android.synthetic.main.fragment_calories.*
import life.mibo.hexa.R
import life.mibo.hexa.models.calories.CaloriesData
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener


class CaloriesFragment : BaseFragment(), CaloriesObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: CaloriesController
    //var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_calories, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = CaloriesController(this@CaloriesFragment, this)
        controller.getCalories()
    }

    override fun onDataReceived(list: ArrayList<CaloriesData>) {
        val data = life.mibo.hexa.ui.heart_rate.chart.ChartData()
        var total = 0
        val entries = java.util.ArrayList<Entry>()
        list.forEachIndexed { index, caloriesData ->
            total += caloriesData.caloriesBurnt ?: 0
        }

        hr_value.text = "$total"
        hr_value_symbol?.visibility = View.VISIBLE
        controller.loadChart(lineChart, list, "")
    }


    override fun onStop() {
        super.onStop()
        //controller.onStop()
    }

}