/*
 *  Created by Sumeet Kumar on 1/8/20 5:09 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 3:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.weight

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.login.Member
import life.mibo.hexa.models.session.SessionDetails
import life.mibo.hexa.models.session.SessionReport
import life.mibo.hexa.ui.home.HomeItem
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeightController(val fragment: WeightFragment, val observer: WeightObserver) :
    WeightFragment.Listener {

    override fun onCreate(view: View?, data: Any?) {

    }

    override fun onResume() {

    }

    override fun onStop() {

    }

    override fun onHomeItemClicked(position: Int) {

    }


    fun getDashboard() {
        val member =
            Prefs.get(this.fragment.context).getMember<Member?>(Member::class.java)
                ?: return
        fragment.getDialog()?.show()
        val session = SessionDetails("${member.id}", member.accessToken)
        API.request.getApi().getSessionDetails(session).enqueue(object : Callback<SessionReport> {
            override fun onFailure(call: Call<SessionReport>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, "Unable to connect").show()
            }

            override fun onResponse(call: Call<SessionReport>, response: Response<SessionReport>) {

                val data = response.body()
                if (data != null && data.status.equals("success")) {
                    Prefs.get(fragment.context).settJson(Prefs.SESSION, data.report)
                    parseData(data)
                } else {

                    val err = data?.error?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.context!!, R.string.error_occurred).show()
                    else Toasty.error(fragment.context!!, err, Toasty.LENGTH_LONG).show()
                }
                fragment.getDialog()?.dismiss()
            }
        })
    }

    fun parseData(report: SessionReport) {
        val list = ArrayList<HomeItem>()
        val data = report.report?.sessionMemberReports!!
        list.add(
            HomeItem(
                "Heart Rate " + data.peakHr,
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.HEART, R.drawable.ic_rxl_heart_selected
            )
        )
        list.add(
            HomeItem(
                "Weight " + report.report?.weight,
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.WEIGHT
            )
        )
        list.add(
            HomeItem(
                "Calendar",
                intArrayOf(Color.DKGRAY, Color.LTGRAY),
                HomeItem.Type.CALENDAR, R.drawable.ic_dashboard_calendar
            )
        )
        list.add(
            HomeItem(
                "Calories " + data.caloriesBurnt,
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.CALORIES
            )
        )
        list.add(
            HomeItem(
                "Schedule",
                intArrayOf(Color.GRAY, Color.LTGRAY),
                HomeItem.Type.SCHEDULE, R.drawable.ic_dashboard_schedule
            )
        )
        list.add(
            HomeItem(
                "Programs",
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.PROGRAMS
            )
        )
        list.add(
            HomeItem(
                "Booster",
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.BOOSTER, R.drawable.ic_dashboard_booster
            )
        )
        list.add(
            HomeItem(
                "Add Product",
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.ADD, R.drawable.ic_plus_main
            )
        )
        observer.onDataRecieved(list)
    }

}