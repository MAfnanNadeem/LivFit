/*
 *  Created by Sumeet Kumar on 1/12/20 3:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 3:15 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calories

import com.github.mikephil.charting.charts.LineChart
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.PostData
import life.mibo.android.models.calories.Calories
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.ui.heart_rate.chart.ChartData
import life.mibo.android.utils.Toasty
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