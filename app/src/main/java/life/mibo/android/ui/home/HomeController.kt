/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.home

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.base.PostData
import life.mibo.android.models.biometric.Biometric
import life.mibo.android.models.calories.Calories
import life.mibo.android.models.session.Report
import life.mibo.android.models.session.SessionDetails
import life.mibo.android.models.session.SessionReport
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeController(val fragment: BaseFragment, val observer: HomeObserver) :
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
        //val adapter = HomeAdapter(list)
        //val manager = LinearLayoutManager(this@HomeFragment.activity)
        //  val grid = GridLayoutManager(fragment.activity, 1)
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

        //view.adapter = adapter
    }

    private var isMember = false
    var isDashboard = true

    fun getDashboard(trainer: Boolean = false) {
        if (isDashboard) {
            var weight = ""
            var weather = "0 "
            var cal = 0
            var today = ""
            try {
                weight = Prefs.get(fragment.context)["user_weight"]
                val date = SimpleDateFormat("yymmddhh").format(Date())
                weather = Prefs.getTemp(fragment.context)["weather_$date"]
                cal = Prefs.get(this.fragment.context).get("calories_burnt", 0)
                val format = SimpleDateFormat("EEE, dd MMM")
                today = format.format(Date())
            } catch (e: java.lang.Exception) {

            }
            if (Utils.isEmpty(weather))
                weather = "0 "
            if (trainer) {
                getTrainerMenu(weight, weather, today)
            } else {
                getBioMetric(weight)
                getMemberMenu(weight, weather, cal, today)
            }
            return
        }

        val s: Report? = Prefs.get(fragment.context).getJson(Prefs.SESSION, Report::class.java)
        if (s?.sessionMemberReports != null) {
            parseData(s)
            return
        }
        s?.weight?.let {
            if (it == "0") {
                parseData(s)
                return
            }
        }

        val member = Prefs.get(this.fragment.context).member ?: return
        fragment.getDialog()?.show()
        val session = SessionDetails("${member.id}", member.accessToken)
        API.request.getApi().getSessionDetails(session).enqueue(object : Callback<SessionReport> {
            override fun onFailure(call: Call<SessionReport>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
            }

            override fun onResponse(call: Call<SessionReport>, response: Response<SessionReport>) {

                val data = response.body()
                if (data != null && data.status.equals("success", true)) {
                    if (data.report != null) {
                        fragment.log("getDashboard onResponse data.report not null")
                        Prefs.get(fragment.context).setJson(Prefs.SESSION, data.report)
                        parseData(data.report!!)
                    }
                    fragment.log("getDashboard onResponse data.report")
                } else {
                    val err = data?.error?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                    else {
                        if (err.contains(
                                "No Data Found",
                                true
                            ) || 404 == data.error?.get(0)?.code
                        ) {
                            Prefs.get(fragment.context)
                                .setJson(Prefs.SESSION, Report(null, null, null, null, "0"))
                            parseDefaults()
                        } else {
                            Toasty.error(fragment.requireContext(), err, Toasty.LENGTH_SHORT).show()
                        }
                    }
                }
                fragment.getDialog()?.dismiss()
            }
        })
    }

    fun parseData(report: Report) {
        fragment.log("parseData onDataReceived $report")
        val list = ArrayList<HomeItem>()
        val data = report?.sessionMemberReports
        list.add(
            HomeItem(
                fragment.getString(R.string.weight), "" + (report.weight ?: 0) + " Kg",
                HomeItem.Type.WEIGHT, 0, R.drawable.dashboard_item_bg_4
            )
        )
        list.add(
            HomeItem(
                "" + (data?.peakHr ?: 0) + "  " + fragment.getString(R.string.bmp_unit), "",
                HomeItem.Type.HEART,
                R.drawable.ic_rxl_heart_unselect,
                R.drawable.dashboard_item_bg_5
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.calendar_title),
                "",
                HomeItem.Type.CALENDAR,
                R.drawable.ic_dashboard_calendar,
                R.drawable.dashboard_item_bg_2
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.calories), "" + (data?.caloriesBurnt ?: 0),
                HomeItem.Type.CALORIES, 0, R.drawable.dashboard_item_bg_12
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.schedule),
                "",
                HomeItem.Type.SCHEDULE,
                R.drawable.ic_dashboard_schedule,
                R.drawable.dashboard_item_bg_1
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.measurement), "",
                HomeItem.Type.MEASURE, R.drawable.ic_nfc_black_24dp, R.drawable.dashboard_item_bg_6
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.myobooster_6ch),
                "",
                HomeItem.Type.BOOSTER_SCAN,
                R.drawable.ic_dashboard_booster,
                R.drawable.dashboard_item_bg_3
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.add_product), "",
                HomeItem.Type.ADD, R.drawable.ic_add_circle_24dp, R.drawable.dashboard_item_bg_8
            )
        )

        list.add(
            HomeItem(
                fragment.getString(R.string.rxl), "",
                HomeItem.Type.RXL_SCAN, R.drawable.ic_rxl_pods_icon, R.drawable.dashboard_item_bg_9
            )
        )
        isMember = true
        observer.onDataReceived(list)
    }

    fun parseDefaults() {
        val list = ArrayList<HomeItem>()
        list.add(
            HomeItem(
                fragment.getString(R.string.weight), "0 " + fragment.getString(R.string.kg_unit),
                HomeItem.Type.WEIGHT, 0, R.drawable.dashboard_item_bg_4
            )
        )
        list.add(
            HomeItem(
                "0 " + fragment.getString(R.string.bmp_unit), "",
                HomeItem.Type.HEART,
                R.drawable.ic_rxl_heart_unselect,
                R.drawable.dashboard_item_bg_5
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.calendar_title),
                "",
                HomeItem.Type.CALENDAR,
                R.drawable.ic_dashboard_calendar,
                R.drawable.dashboard_item_bg_2
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.calories), "0",
                HomeItem.Type.CALORIES, 0, R.drawable.dashboard_item_bg_12
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.schedule),
                "",
                HomeItem.Type.SCHEDULE,
                R.drawable.ic_dashboard_schedule,
                R.drawable.dashboard_item_bg_1
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.measurement), "",
                HomeItem.Type.MEASURE, R.drawable.ic_nfc_black_24dp, R.drawable.dashboard_item_bg_6
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.myobooster_6ch),
                "",
                HomeItem.Type.BOOSTER_SCAN,
                R.drawable.ic_dashboard_booster,
                R.drawable.dashboard_item_bg_3
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.add_product), "",
                HomeItem.Type.ADD, R.drawable.ic_add_circle_24dp, R.drawable.dashboard_item_bg_8
            )
        )

        list.add(
            HomeItem(
                fragment.getString(R.string.rxl), "",
                HomeItem.Type.RXL_SCAN, R.drawable.ic_rxl_pods_icon, R.drawable.dashboard_item_bg_9
            )
        )

        isMember = true
        observer.onDataReceived(list)
    }


    fun getMemberMenu(weight: String, weather: String, cal: Int, date: String) {
        val list = ArrayList<HomeItem>()


        list.add(
            HomeItem(
                "",
                if (weight.isNullOrEmpty()) "0 " + fragment.getString(R.string.kg_unit) else weight,
                HomeItem.Type.WEIGHT,
                R.drawable.ic_dashboard_weight,
                R.drawable.dashboard_item_bg_10
            )
        )
        list.add(
            HomeItem(
                "0 " + fragment.getString(R.string.bmp_unit), "",
                HomeItem.Type.HEART,
                R.drawable.ic_rxl_heart_unselect,
                R.drawable.dashboard_item_bg_5
            )
        )

        list.add(
            HomeItem(
                date,
                "",
                HomeItem.Type.CALENDAR,
                R.drawable.ic_dashboard_calendar,
                R.drawable.dashboard_item_bg_2
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.steps),
                "0",
                HomeItem.Type.STEPS,
                R.drawable.ic_dashboard_steps,
                R.drawable.dashboard_item_bg_11
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.calories),
                "$cal",
                HomeItem.Type.CALORIES,
                R.drawable.ic_body_summary_bsa,
                R.drawable.dashboard_item_bg_1
            )
        )
//        list.add(
//            HomeItem(
//                fragment.getString(R.string.schedule),
//                "",
//                HomeItem.Type.SCHEDULE,
//                R.drawable.ic_dashboard_schedule,
//                R.drawable.dashboard_item_bg_1
//            )
//        )


        list.add(
            HomeItem(
                fragment.getString(R.string.my_body),
                "",
                HomeItem.Type.MEASURE,
                R.drawable.ic_dashboard_my_body,
                R.drawable.dashboard_item_bg_9
            )
        )

        list.add(
            HomeItem(
                "",
                weather + " " + 0x00B0.toChar(),
                HomeItem.Type.WEATHER,
                R.drawable.ic_dashboard_weather,
                R.drawable.dashboard_item_bg_3
            )
        )
//
//        list.add(
//            HomeItem(
//                fragment.getString(R.string.myobooster_6ch),
//                "",
//                HomeItem.Type.BOOSTER_SCAN,
//                R.drawable.ic_dashboard_booster,
//                R.drawable.dashboard_item_bg_3
//            )
//        )
//        list.add(
//            HomeItem(
//                fragment.getString(R.string.add_product), "",
//                HomeItem.Type.ADD, R.drawable.ic_add_circle_24dp, R.drawable.dashboard_item_bg_8
//            )
//        )
//
//        list.add(
//            HomeItem(
//                fragment.getString(R.string.rxl), "",
//                HomeItem.Type.RXL_SCAN, R.drawable.ic_rxl_pods_icon, R.drawable.dashboard_item_bg_9
//            )
//        )

        isMember = true
        observer.onDataReceived(list)
    }

    fun getMemberMenu(
        weight: String,
        calories: String,
        hr: String,
        steps: String,
        weather: String
    ) {
        val list = ArrayList<HomeItem>()


        list.add(
            HomeItem(
                "",
                weight,
                HomeItem.Type.WEIGHT,
                R.drawable.ic_dashboard_weight,
                R.drawable.dashboard_item_bg_10
            )
        )
        list.add(
            HomeItem(
                "$hr " + fragment.getString(R.string.bmp_unit), "",
                HomeItem.Type.HEART,
                R.drawable.ic_rxl_heart_unselect,
                R.drawable.dashboard_item_bg_5
            )
        )

        list.add(
            HomeItem(
                fragment.getString(R.string.calendar_title),
                "",
                HomeItem.Type.CALENDAR,
                R.drawable.ic_dashboard_calendar,
                R.drawable.dashboard_item_bg_2
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.steps),
                steps,
                HomeItem.Type.STEPS,
                R.drawable.ic_dashboard_steps,
                R.drawable.dashboard_item_bg_11
            )
        )
        list.add(
            HomeItem(
                fragment.getString(R.string.calories),
                calories,
                HomeItem.Type.CALORIES,
                R.drawable.ic_body_summary_bsa,
                R.drawable.dashboard_item_bg_1
            )
        )
//        list.add(
//            HomeItem(
//                fragment.getString(R.string.schedule),
//                "",
//                HomeItem.Type.SCHEDULE,
//                R.drawable.ic_dashboard_schedule,
//                R.drawable.dashboard_item_bg_1
//            )
//        )


        list.add(
            HomeItem(
                fragment.getString(R.string.my_body),
                "",
                HomeItem.Type.MEASURE,
                R.drawable.ic_dashboard_my_body,
                R.drawable.dashboard_item_bg_9
            )
        )

        list.add(
            HomeItem(
                "",
                "$weather " + 0x00B0.toChar(),
                HomeItem.Type.WEATHER,
                R.drawable.ic_dashboard_weather,
                R.drawable.dashboard_item_bg_3
            )
        )
        isMember = true
        observer.onDataReceived(list)
    }

    private fun getTrainerMenu(weight: String, weather: String, date: String) {
        val list = ArrayList<HomeItem>()
        list.add(
            HomeItem(
                "My Services", "",
                HomeItem.Type.MY_SERVICES,
                R.drawable.ic_nfc_black_24dp,
                R.drawable.dashboard_item_bg_4
            )
        )

        list.add(
            HomeItem(
                "My Account",
                "",
                HomeItem.Type.MY_ACCOUNT,
                R.drawable.ic_account_box_24dp,
                R.drawable.dashboard_item_bg_9
            )
        )
        list.add(
            HomeItem(
                date,
                "",
                HomeItem.Type.CALENDAR,
                R.drawable.ic_dashboard_calendar,
                R.drawable.dashboard_item_bg_12
            )
        )
//        list.add(
//            HomeItem(
//                "0 " + fragment.getString(R.string.bmp_unit), "",
//                HomeItem.Type.HEART,
//                R.drawable.ic_rxl_heart_unselect,
//                R.drawable.dashboard_item_bg_5
//            )
//        )
//        list.add(
//            HomeItem(
//                fragment.getString(R.string.calories), "0",
//                HomeItem.Type.CALORIES, 0, R.drawable.dashboard_item_bg_12
//            )
//        )
        list.add(
            HomeItem(
                fragment.getString(R.string.schedule),
                "",
                HomeItem.Type.SCHEDULE,
                R.drawable.ic_dashboard_schedule,
                R.drawable.dashboard_item_bg_1
            )
        )

        list.add(
            HomeItem(
                fragment.getString(R.string.ems_tens_sessions),
                "",
                HomeItem.Type.BOOSTER_SCAN,
                R.drawable.ic_dashboard_booster,
                R.drawable.dashboard_item_bg_3
            )
        )

        list.add(
            HomeItem(
                "",
                weather + " " + 0x00B0.toChar(),
                HomeItem.Type.WEATHER,
                R.drawable.ic_dashboard_weather,
                R.drawable.dashboard_item_bg_10
            )
        )

        list.add(
            HomeItem(
                fragment.getString(R.string.create_service), "",
                HomeItem.Type.ADD_SERVICE, R.drawable.ic_add_circle_24dp, R.drawable.dashboard_item_bg_8
            )
        )
//
//        list.add(
//            HomeItem(
//                fragment.getString(R.string.rxl), "",
//                HomeItem.Type.RXL_SCAN, R.drawable.ic_rxl_pods_icon, R.drawable.dashboard_item_bg_9
//            )
//        )



        isMember = false
        observer.onDataReceived(list)
    }

    fun yahooWeather() {
        var appId = "v6lUty30"
        var client =
            "dj0yJmk9dWFNOHp0Rk9iOTZxJmQ9WVdrOWRqWnNWWFI1TXpBbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTE0"
        var secret = "45fa89ae56212b17d9d4df99bb475fa99b418ee9"
    }

//    fun parseData2(report: SessionReport) {
//        val list = ArrayList<HomeItem>()
//        val data = report.report?.sessionMemberReports!!
//        list.add(
//            HomeItem(
//                "Heart Rate " + data.peakHr,
//                intArrayOf(Color.parseColor("#FF0000"), Color.parseColor("#393939")),
//                HomeItem.Type.HEART, R.drawable.ic_rxl_heart_unselect
//            )
//        )
//        list.add(
//            HomeItem(
//                "Weight " + report.report?.weight,
//                intArrayOf(Color.parseColor("#2F2FF3"), Color.parseColor("#121260"), Color.DKGRAY),
//                HomeItem.Type.WEIGHT
//            )
//        )
//        list.add(
//            HomeItem(
//                "Calendar",
//                intArrayOf(Color.parseColor("#2A72D1"), Color.parseColor("#00FFF2")),
//                HomeItem.Type.CALENDAR, R.drawable.ic_dashboard_calendar
//            )
//        )
//        list.add(
//            HomeItem(
//                "Calories " + data.caloriesBurnt,
//                intArrayOf(Color.parseColor("#065A2A"), Color.parseColor("#CCF9AE")),
//                HomeItem.Type.CALORIES
//            )
//        )
//        list.add(
//            HomeItem(
//                "Schedule",
//                intArrayOf(Color.parseColor("#C21F2A"), Color.parseColor("#FE9001")),
//                HomeItem.Type.SCHEDULE, R.drawable.ic_dashboard_schedule
//            )
//        )
//        list.add(
//            HomeItem(
//                "Programs",
//                intArrayOf(Color.parseColor("#0084E9"), Color.parseColor("#0C1E51")),
//                HomeItem.Type.PROGRAMS, R.drawable.ic_dashboard_booster
//            )
//        )
//        list.add(
//            HomeItem(
//                "Booster",
//                intArrayOf(Color.parseColor("#2F2FF3"), Color.parseColor("#121260"), Color.DKGRAY),
//                HomeItem.Type.BOOSTER_SCAN, R.drawable.ic_dashboard_booster
//            )
//        )
//        list.add(
//            HomeItem(
//                "Add life.mibo.android.models.product.Product",
//                intArrayOf(Color.parseColor("#C9C8C8"), Color.parseColor("#393939")),
//                HomeItem.Type.ADD, R.drawable.ic_add_circle_24dp
//            )
//        )
//        observer.onDataReceived(list)
//    }

    private fun getBioMetric(weight: String?) {
        if (weight != null && weight.length > 1)
            return
        val member = Prefs.get(fragment.context).member
        val memberId = member?.id() ?: ""
        val token = member?.accessToken ?: ""
        API.request.getApi().getMemberBiometrics(MemberPost(memberId, token, "GetMemberBiometrics"))
            .enqueue(object : retrofit2.Callback<Biometric> {
                override fun onFailure(call: Call<Biometric>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<Biometric>, response: Response<Biometric>
                ) {
                    try {
                        val body = response?.body()
                        if (body != null && body.isSuccess()) {
                            val list = body.data
                            list.let {
                                parseBiometric(it)
                            }
                        }
                    } catch (e: Exception) {
                        MiboEvent.log(e)
                    }

                }

            })
    }

    fun parseBiometric(bio: List<Biometric.Data?>?) {
        if (bio != null) {
            try {
                val data = bio[bio.size - 1]
                var kg = fragment.getString(R.string.kg_unit)?.toUpperCase()
                var cm = fragment.getString(R.string.cm_unit)?.toUpperCase()
                Prefs.get(fragment.context)["user_weight"] = "${Calculate.round(data!!.weight)} $kg"
                Prefs.get(fragment.context)["user_height"] = "${data!!.height} $cm"
                Prefs.get(fragment.context)["user_date"] =
                    "${data.createdAt?.date?.split(" ")?.get(0)}"
                observer.onNotify(30, "${Calculate.round(data.weight)} $kg")
            } catch (e: Exception) {
                // Prefs.get(fragment.context)["user_date"] = "${data.createdAt?.date}"
            }

        }
    }

    fun getCalories() {

        val cal = Prefs.get(this.fragment.context).get("calories_burnt", -1)
        if (cal >= 0) {
            observer?.onNotify(21, cal)
            return
        }

        val member = Prefs.get(this.fragment.context).member
            ?: return
        //fragment.getDialog()?.show()
        val post = PostData("${member.id}", member.accessToken, "CaloriesBurnt")
        API.request.getApi().getAllCaloriesBurnt(post).enqueue(object : Callback<Calories> {
            override fun onFailure(call: Call<Calories>, t: Throwable) {
                // fragment.getDialog()?.dismiss()
                t.printStackTrace()
            }

            override fun onResponse(call: Call<Calories>, response: Response<Calories>) {

                // fragment.getDialog()?.dismiss()
                val data = response.body()
                parseCalories(data)
            }
        })
    }

    fun parseCalories(calories: Calories?) {
        try {
            var cal = 0
            if (calories != null) {
                calories.data?.forEach {
                    // list.add(it!!)
                    cal += it?.caloriesBurnt ?: 0
                }
            }
            Prefs.get(this.fragment.context).set("calories_burnt", cal)
            observer?.onNotify(20, cal)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}