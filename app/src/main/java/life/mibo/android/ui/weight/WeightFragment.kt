/*
 *  Created by Sumeet Kumar on 1/8/20 5:09 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 10:10 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.weight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_weight.*
import life.mibo.android.R
import life.mibo.android.models.weight.Data
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.home.HomeItem


class WeightFragment : BaseFragment(), WeightObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: WeightController
    var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_weight, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = WeightController(this@WeightFragment, this)
        //controller.setRecycler(recyclerView!!)
        controller.getUserDetails()
        controller.getAllWeight()
        tv_bmi_value?.text = ""
    }

    override fun onChartDataReceived(list: List<Data?>?) {
        if (list != null) {
            life.mibo.android.ui.heart_rate.chart.ChartData().loadWeightChart(list, lineChart)
        }
    }

    override fun onItemClicked(item: HomeItem?) {

    }

    override fun onUserDetailsReceived(data: life.mibo.android.models.user_details.Data?) {
        val medical = data?.medicalHistory

        if (medical != null) {
            no_data_layout?.visibility = View.GONE
            constraintLayout1?.visibility = View.VISIBLE
            if (medical.weight.isNullOrEmpty())
                weight_value.text = "0"
            else
                weight_value.text = "${medical.weight} ${medical.weightUnit}"
            try {
                val weight: Double? = medical.weight?.toDoubleOrNull()
                var bmi: Double? = medical.height?.toDouble()?.div(100)
                bmi = bmi!!.times(bmi)
                //tv_start_bmi.text = String.format("%.2f", weight?.div(bmi!!))
                tv_bmi_value.text = String.format("BMI: %.2f", weight?.div(bmi))
            } catch (e: Exception) {
               // tv_start_bmi.text = "0.0"
                tv_bmi_value.text = "0.0"

            }
        } else {
            weight_value.text = "0"
            tv_bmi_value.text = "0.0"
            no_data_layout?.visibility = View.VISIBLE

        }
    }

    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}