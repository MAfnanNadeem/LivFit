/*
 *  Created by Sumeet Kumar on 1/8/20 5:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 10:12 AM
 *  Mibo Hexa - app
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.programs

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.session.SessionDetails
import life.mibo.android.models.session.SessionReport
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgramController(val fragment: BaseFragment, val observer: ProgramObserver) :
    ProgramFragment.Listener {
    override fun onHomeItemClicked(position: Int) {
    }

    override fun onCreate(view: View?, data: Any?) {
    }

    override fun onResume() {
    }

    override fun onStop() {
    }

    var recyclerView: RecyclerView? = null

    fun setRecycler(recycler: RecyclerView?) {
        recyclerView = recycler
    }

    var isApi = false

    fun getProduct() {
        if (!isApi) {
            parseData(null)
            return
        }
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

    fun parseData(report: SessionReport?) {
        val list = ArrayList<Program>()
        if (report == null) {
            list.add(Program(1, "10 Channel Booster", R.drawable.ic_dashboard_booster, true))
            list.add(Program(1, "6 Channel Booster", R.drawable.ic_dashboard_booster))
            list.add(Program(1, "4 Channel Booster", R.drawable.ic_dashboard_booster))
            list.add(Program(1, "Heart Rate Sensor", R.drawable.ic_dashboard_booster))
            list.add(Program(1, "Reaction Lights (RXL)", R.drawable.ic_dashboard_booster, true))
            list.add(Program(1, "RXl Rope", R.drawable.ic_dashboard_booster))
            list.add(Program(1, "Weight Scale", R.drawable.ic_dashboard_booster))
            list.add(Program(1, "Weight Scale", R.drawable.ic_dashboard_booster))
        }

        //val adapter = AddProductAdapter(list)
        //recyclerView?.layoutManager = LinearLayoutManager(fragment.context)
        //recyclerView?.adapter = adapter

        observer.onDataReceived(list)
    }
}