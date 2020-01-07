/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.home

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
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeController(val fragment: HomeFragment, val observer: HomeObserver) :
    HomeFragment.Listener {

    override fun onCreate(view: View?, data: Any?) {

    }

    override fun onResume() {

    }

    override fun onStop() {

    }

    override fun onHomeItemClicked(position: Int) {

    }

    fun setRecycler(view: RecyclerView) {
        val list = ArrayList<HomeItem>();
        for (i in 1..20
        ) {
            list.add(HomeItem(0, "$i"))
        }
        val adapter = HomeAdapter(list)
        //val manager = LinearLayoutManager(this@HomeFragment.activity)
        val grid = GridLayoutManager(fragment.activity, 1)
//        grid.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                var size = 1
//                if ((position + 1) % 5 == 0) {
//                    size = 2
//                }
//                return size
//            }
//        }


        view.layoutManager = LinearLayoutManager(fragment.context)

        view.adapter = adapter
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
                HomeItem.Type.HEART
            )
        )
        list.add(
            HomeItem(
                "Calendar ",
                intArrayOf(Color.DKGRAY, Color.LTGRAY),
                HomeItem.Type.HEART, R.drawable.ic_dashboard_calendar
            )
        )
        list.add(
            HomeItem(
                "Calories " + data.caloriesBurnt,
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.HEART
            )
        )
        list.add(
            HomeItem(
                "Schedule " + data.peakHr,
                intArrayOf(Color.GRAY, Color.LTGRAY),
                HomeItem.Type.HEART, R.drawable.ic_dashboard_schedule
            )
        )
        list.add(
            HomeItem(
                "Programs " + data.peakHr,
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.HEART
            )
        )
        list.add(
            HomeItem(
                "Booster " + data.peakHr,
                intArrayOf(Color.RED, Color.BLUE, Color.GREEN),
                HomeItem.Type.HEART, R.drawable.ic_dashboard_booster
            )
        )
        observer.onDataRecieved(list)
    }

}