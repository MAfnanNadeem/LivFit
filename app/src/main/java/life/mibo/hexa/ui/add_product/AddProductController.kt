/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.add_product

import android.view.View
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

class AddProductController(val fragment: AddProductFragment, val observer: ProductObserver) :
    AddProductFragment.Listener {
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

    fun parseData(report: SessionReport?) {
        val list = ArrayList<ProductItem>()
        if (report == null) {
            list.add(ProductItem(1, "10 Channel Booster", R.drawable.ic_dashboard_booster, true))
            list.add(ProductItem(1, "6 Channel Booster", R.drawable.ic_dashboard_booster))
            list.add(ProductItem(1, "4 Channel Booster", R.drawable.ic_dashboard_booster))
            list.add(ProductItem(1, "Heart Rate Sensor", R.drawable.ic_dashboard_booster))
            list.add(ProductItem(1, "Reaction Lights (RXL)", R.drawable.ic_dashboard_booster, true))
            list.add(ProductItem(1, "RXl Rope", R.drawable.ic_dashboard_booster))
            list.add(ProductItem(1, "Weight Scale", R.drawable.ic_dashboard_booster))
            list.add(ProductItem(1, "Weight Scale", R.drawable.ic_dashboard_booster))
        }

        //val adapter = AddProductAdapter(list)
        //recyclerView?.layoutManager = LinearLayoutManager(fragment.context)
        //recyclerView?.adapter = adapter

        observer.onDataReceived(list)
    }
}