/*
 *  Created by Sumeet Kumar on 1/8/20 11:23 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 10:12 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.heart_rate

import android.view.View
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.session.Report
import life.mibo.android.models.session.SessionDetails
import life.mibo.android.models.session.SessionReport
import life.mibo.android.ui.heart_rate.chart.ChartData
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class HearRateController(val fragment: HeartRateFragment, val observer: HeartRateObserver) :
    HeartRateFragment.Listener {

    override fun onHomeItemClicked(position: Int) {
    }

    override fun onCreate(view: View?, data: Any?) {
    }

    override fun onResume() {
    }

    override fun onStop() {
    }

    fun loadChart(chart: LineChart?) {
        val r = Random(999)
        val list = ArrayList<Entry>()
        for (i in 1..20) {
            list.add(Entry(i.toFloat(), r.nextInt().toFloat()))
        }
        ChartData().loadChart(list, chart)
    }

    var isApi = false

    fun getHeartRate(chart: LineChart?) {
        if (!isApi) {
            parseData(null)
            return
        }
        val report: Report? = Prefs.get(fragment.context).getJson(Prefs.SESSION, Report::class.java)
        if (report != null) {
            val list = report?.heartRate
            val entries = ArrayList<Entry>()

            list?.forEachIndexed { index, s ->
                entries.add(Entry(index.toFloat(), getFloar(s)))
            }
            if (entries.isEmpty()) {
                //fetchHeartRateData()
            } else {
                ChartData().loadChart(entries, chart)
            }

        } else {
            fetchHeartRateData()
        }
    }

    fun getFloar(s: String?): Float {
        try {
            if (s != null)
                return s.toFloat()
        } catch (e: Exception) {

        }

        return 0f
    }

    private fun fetchHeartRateData() {
        val member =
            Prefs.get(this.fragment.context).member
                ?: return
        fragment.getDialog()?.show()
        val session = SessionDetails("${member.id}", member.accessToken)
        API.request.getApi().getSessionDetails(session).enqueue(object : Callback<SessionReport> {
            override fun onFailure(call: Call<SessionReport>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
            }

            override fun onResponse(call: Call<SessionReport>, response: Response<SessionReport>) {

                val data = response.body()
                if (data != null && data.status.equals("success", true)) {
                    Prefs.get(fragment.context).settJson(Prefs.SESSION, data.report)
                    parseData(data)
                } else {

                    val err = data?.error?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.context!!, R.string.error_occurred).show()
                    else Toasty.error(fragment.context!!, err, Toasty.LENGTH_SHORT).show()
                }
                fragment.getDialog()?.dismiss()
            }
        })
    }

    fun parseData(report: SessionReport?) {
        val list = ArrayList<HeartRateItem>()
        if (report == null) {
            list.add(HeartRateItem(1, "10 Channel Booster", R.drawable.ic_dashboard_booster, true))
            list.add(HeartRateItem(1, "6 Channel Booster", R.drawable.ic_dashboard_booster))
            list.add(HeartRateItem(1, "4 Channel Booster", R.drawable.ic_dashboard_booster))
            list.add(HeartRateItem(1, "Heart Rate Sensor", R.drawable.ic_dashboard_booster))
            list.add(
                HeartRateItem(
                    1,
                    "Reaction Lights (RXL)",
                    R.drawable.ic_dashboard_booster,
                    true
                )
            )
            list.add(HeartRateItem(1, "RXl Rope", R.drawable.ic_dashboard_booster))
            list.add(HeartRateItem(1, "Weight Scale", R.drawable.ic_dashboard_booster))
            list.add(HeartRateItem(1, "Weight Scale", R.drawable.ic_dashboard_booster))
        }

        //val adapter = AddProductAdapter(list)
        //recyclerView?.layoutManager = LinearLayoutManager(fragment.context)
        //recyclerView?.adapter = adapter

        observer.onDataReceived(list)
    }


}