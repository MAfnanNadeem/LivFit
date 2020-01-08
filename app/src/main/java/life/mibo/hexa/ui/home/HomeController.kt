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
                intArrayOf(Color.parseColor("#FF0000"), Color.parseColor("#393939")),
                HomeItem.Type.HEART, R.drawable.ic_rxl_heart_unselect
            )
        )
        list.add(
            HomeItem(
                "Weight " + report.report?.weight,
                intArrayOf(Color.parseColor("#2F2FF3"), Color.parseColor("#121260"), Color.DKGRAY),
                HomeItem.Type.WEIGHT
            )
        )
        list.add(
            HomeItem(
                "Calendar",
                intArrayOf(Color.parseColor("#2A72D1"), Color.parseColor("#00FFF2")),
                HomeItem.Type.CALENDAR, R.drawable.ic_dashboard_calendar
            )
        )
        list.add(
            HomeItem(
                "Calories " + data.caloriesBurnt,
                intArrayOf(Color.parseColor("#065A2A"), Color.parseColor("#CCF9AE")),
                HomeItem.Type.CALORIES
            )
        )
        list.add(
            HomeItem(
                "Schedule",
                intArrayOf(Color.parseColor("#C21F2A"), Color.parseColor("#FE9001")),
                HomeItem.Type.SCHEDULE, R.drawable.ic_dashboard_schedule
            )
        )
        list.add(
            HomeItem(
                "Programs",
                intArrayOf(Color.parseColor("#0084E9"), Color.parseColor("#0C1E51")),
                HomeItem.Type.PROGRAMS
            )
        )
        list.add(
            HomeItem(
                "Booster",
                intArrayOf(Color.parseColor("#2F2FF3"), Color.parseColor("#121260"), Color.DKGRAY),
                HomeItem.Type.BOOSTER, R.drawable.ic_dashboard_booster
            )
        )
        list.add(
            HomeItem(
                "Add Product",
                intArrayOf(Color.parseColor("#C9C8C8"), Color.parseColor("#393939")),
                HomeItem.Type.ADD, R.drawable.ic_plus_main
            )
        )
        observer.onDataRecieved(list)
    }

}