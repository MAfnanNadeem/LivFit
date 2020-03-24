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
import life.mibo.hexa.ui.heart_rate.chart.ChartData
import life.mibo.hexa.utils.Constants
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CaloriesController(val fragment: CaloriesFragment, val observer: CaloriesObserver) {

    fun getCalories() {
        val member =
            Prefs.get(this.fragment.context).member
                ?: return
        fragment.getDialog()?.show()
        val post = PostData("${member.id}", member.accessToken, "CaloriesBurnt")
        API.request.getApi().getAllCaloriesBurnt(post).enqueue(object : Callback<Calories> {
            override fun onFailure(call: Call<Calories>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
            }

            override fun onResponse(call: Call<Calories>, response: Response<Calories>) {

                fragment.getDialog()?.dismiss()
                val data = response.body()
                if (data != null && data.status.equals("success", true)) {
                    parseData(data)
                } else {
                    val err = data?.errors?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.context!!, R.string.error_occurred).show()
                    else Toasty.error(fragment.context!!, err, Toasty.LENGTH_LONG).show()
                    parseData(null)
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

    fun loadChart(chart: LineChart?, data: ArrayList<CaloriesData>?, legendText: String) {
        ChartData().loadCaloriesChart(chart, data, legendText)
    }
}