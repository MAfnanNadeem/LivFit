/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.add_product

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.catalog.Products
import life.mibo.android.utils.Toasty
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

    var isApi = true

    fun getProduct() {
        if (!isApi) {
            parseData(null)
            return
        }
        val member =
            Prefs.get(this.fragment.context).member ?: return
        fragment.getDialog()?.show()
        API.request.getApi()
            .getProductList(MemberPost("${member.id}", "${member.accessToken}", "ProductList"))
            .enqueue(object : Callback<Products> {
                override fun onFailure(call: Call<Products>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                }

                override fun onResponse(call: Call<Products>, response: Response<Products>) {

                    val data = response.body()
                    if (data != null && data.isSuccess()) {
                        parseData(data)
                    } else {
                        val err = data?.errors?.get(0)?.message
                        if (err.isNullOrEmpty())
                            Toasty.error(fragment.context!!, R.string.error_occurred).show()
                        else Toasty.error(fragment.context!!, err, Toasty.LENGTH_LONG).show()
                    }
                    fragment.getDialog()?.dismiss()
                }
            })
    }

    fun parseData(products: Products?) {
        fragment.getDialog()?.dismiss()
        val list = ArrayList<ProductItem>()
        if (products?.data != null) {
            for (data in products.data!!) {
                data?.let {
                    list.add(
                        ProductItem(
                            it.id!!,
                            "${it.name}",
                            R.drawable.ic_dashboard_booster,
                            false
                        )
                    )
                }
            }
        }
        if (products == null) {
//            list.add(ProductItem(1, "10 Channel Booster", R.drawable.ic_dashboard_booster, false))
//            list.add(ProductItem(1, "6 Channel Booster", R.drawable.ic_dashboard_booster, true))
//            list.add(ProductItem(1, "4 Channel Booster", R.drawable.ic_dashboard_booster))
//            list.add(ProductItem(1, "Heart Rate Sensor", R.drawable.ic_dashboard_booster))
//            list.add(ProductItem(1, "Reaction Lights (RXL)", R.drawable.ic_dashboard_booster, true))
//            list.add(ProductItem(1, "RXl Rope", R.drawable.ic_dashboard_booster))
//            list.add(ProductItem(1, "Weight Scale", R.drawable.ic_dashboard_booster))
//            list.add(ProductItem(1, "Weight Scale", R.drawable.ic_dashboard_booster))
        }

        //val adapter = AddProductAdapter(list)
        //recyclerView?.layoutManager = LinearLayoutManager(fragment.context)
        //recyclerView?.adapter = adapter

        observer.onDataReceived(list)
    }
}