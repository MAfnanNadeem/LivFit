/*
 *  Created by Sumeet Kumar on 5/18/20 4:03 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/17/20 9:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_catalog_details.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.UserID
import life.mibo.android.models.trainer.ProfessionalDetails
import life.mibo.android.ui.dialog.MyDialog
import life.mibo.android.ui.payments.PaymentActivity
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CatalogDetailsDialog(var data: CatalogFragment.CartItem?) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (data?.type == 2)
            tv_product_location?.text = "add your address"
        tv_product_quantity_value?.text = "$quantity"
        tv_product_price_value?.text = "${data?.price}"

        btn_plus?.setOnClickListener {
            if (quantity < 10) {
                quantity++
                tv_product_quantity_value?.text = "$quantity"
            }
        }
        btn_minus?.setOnClickListener {
            if (quantity > 0) {
                quantity--
                tv_product_quantity_value?.text = "$quantity"
            }

        }

        val list = ArrayList<Int>()
        // list.add("https://live.staticflickr.com/4561/38054606355_26429c884f_b.jpg")
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)

        userImage?.setImages(list)

        btn_buy_now?.setOnClickListener {
            try {
                //PaymentActivity.payNow(activity, "200")
                val i = Intent(context, BuyActivity::class.java)
                i.putExtra("type_type", data?.type)
                startActivity(i)
                dismiss()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        //userImage?.attach(requireActivity(), list)
    }

    fun payNow() {
        try {
            PaymentActivity.payNow(this@CatalogDetailsDialog.activity, "200")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    var quantity = 1


    var mDialog: MyDialog? = null

    fun getmDialog(): MyDialog? {
        if (mDialog == null)
            mDialog = MyDialog.get(requireContext())
        return mDialog
    }

    fun showProgress() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressBar?.visibility = View.GONE
    }

    var isTrainer = false
    private fun getDetails(userId: Int?) {
        if (userId == null) {
            return
        }
        // Prefs.get(context).member

        //getmDialog()?.show()
        showProgress()
        API.request.getApi()
            .getProfessionalDetails(
                UserID(
                    UserID.Data("$userId"),
                    "ServicesIndependentProfessionals",
                    Prefs.get(context).member?.accessToken
                )
            )
            .enqueue(object : Callback<ProfessionalDetails> {
                override fun onFailure(call: Call<ProfessionalDetails>, t: Throwable) {
                    // getmDialog()?.dismiss()
                    hideProgress()

                }

                override fun onResponse(
                    call: Call<ProfessionalDetails>,
                    response: Response<ProfessionalDetails>
                ) {

                    try {
                        val data = response?.body();
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            parseData(list)
                            isTrainer = true

                        } else {
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.message?.let {
                                    Toasty.snackbar(view, it)
                                }
                        }
                    } catch (e: Exception) {

                    }
                    //getmDialog()?.dismiss()
                    hideProgress()
                }

            })
    }

    private fun parseData(list: List<ProfessionalDetails.Data?>?) {

    }


    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog?.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


}