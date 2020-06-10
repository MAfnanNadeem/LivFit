/*
 *  Created by Sumeet Kumar on 6/4/20 10:53 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/3/20 11:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_invoice_details.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.catalog.GetInvoiceDetail
import life.mibo.android.models.catalog.GetInvoices
import life.mibo.android.models.catalog.InvoiceDetails
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.dialog.MyDialog
import life.mibo.android.ui.payments.PaymentActivity
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.util.*


class InvoiceDetailsDialog(var data: GetInvoices.Invoice, var listener: ItemClickListener<String>) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invoice_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_pay_now?.setOnClickListener {
            if (isLoaded)
                payNow(
                    currency,
                    address,
                    city,
                    getCountryIsoCode(),
                    getString(R.string.app_version)
                )

        }

        val pref = Prefs.get(context).member

        getDetails(pref?.id, data.invoiceNumber, pref?.accessToken)
        //userImage?.attach(requireActivity(), list)
    }

    fun getCountryIsoCode(): String {

        try {
            return Locale.getISOCountries().find { Locale("", it).displayCountry == country }!!
        } catch (e: java.lang.Exception) {

        }

        try {
            return Locale.getISOCountries().find { Locale("", it).country == country }!!
        } catch (e: java.lang.Exception) {

        }

        return country
    }

    fun getAppId(): String {
        try {
            return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: java.lang.Exception) {

        }
        return BigInteger(128, Random()).toString()
    }

    fun payNow(
        currency: String,
        address: String,
        city: String,
        countryCode: String,
        appVersion: String
    ) {
        //listener?.onItemClicked("", 101)
        //dismiss()
        try {
            if (totalAmount > 0.0) {
                val prefs = Prefs.get(context).member ?: return
                var email = Prefs.get(context).get("user_email")

                val data = PaymentActivity.PaymentData(
                    prefs.id(), "", prefs.firstName, prefs.lastName, email, currency, totalAmount,
                    address, city, city, countryCode, appVersion, getAppId(), "Test Item"
                )
                PaymentActivity.payNow(this@InvoiceDetailsDialog.activity, data)
            }
//            val uid = SimpleDateFormat("yyMMddHHmmss").format(Date())
//            Logger.e("PayNow 1: " + BigInteger(128, Random()).toString())
//            Logger.e("PayNow 2: $uid")
            // PaymentActivity.payNow(this@InvoiceDetailsDialog.activity, "200", "")
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

    var isLoaded = false
    private var totalAmount: Double = 0.0
    private var city: String = ""
    private var country: String = ""

    //private var countryCode: String = ""
    private var currency: String = ""
    private var address: String = ""
    private fun getDetails(userId: Int?, booking: String?, token: String?) {
        if (userId == null || booking == null) {
            dismiss()
            return
        }
        nestedScrollView?.visibility = View.GONE
        showProgress()
        API.request.getApi()
            .getInvoiceDetails(GetInvoiceDetail(GetInvoiceDetail.Data(booking, userId), token))
            .enqueue(object : Callback<InvoiceDetails> {
                override fun onFailure(call: Call<InvoiceDetails>, t: Throwable) {
                    hideProgress()
                    Toasty.info(requireContext(), getString(R.string.unable_to_connect)).show()

                }

                override fun onResponse(
                    call: Call<InvoiceDetails>,
                    response: Response<InvoiceDetails>
                ) {

                    try {
                        val data = response?.body();
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            parseData(list)
                            isLoaded = true

                        } else {
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.message?.let {
                                    Toasty.snackbar(view, it)
                                }
                        }
                    } catch (e: Exception) {
                        Toasty.info(requireContext(), getString(R.string.unable_to_process)).show()
                    }
                    //getmDialog()?.dismiss()
                    hideProgress()
                }

            })
    }

    private fun parseData(data: InvoiceDetails.Data?) {
        val invoice = data?.invoice
        if (invoice != null) {
            nestedScrollView?.visibility = View.VISIBLE
            val user = invoice.user
            if (user != null) {
                tv_customerName?.text = user.name
                address = user.address1 ?: ""
                country = user.country ?: ""
                //city = user.city ?: ""
                tv_customerAddress?.text = address
                tv_customerPhone?.text = user.countryCode + " " + user.areaCode + " " + user.phone
            }

            tv_tv_bookingNumber?.text = invoice.bookingAdviceNo
            tv_tv_bookingDate?.text = invoice.bookingAdviceDate

            val pkg = invoice.packages?.get(0)
            if (pkg != null) {
                tv_packageName?.text = pkg.name
                currency = pkg.currency ?: ""
                val amount = pkg.price ?: 0.0
                val vat = pkg.vat ?: 0.0
                val conv = invoice.conFee ?: 0.0

                tv_tv_packageAmount?.text = "$currency $amount"
                tv_tv_convAmount?.text = "$currency $conv"
                val grand = amount.plus(conv)
                tv_tv_subAmount?.text = "$currency $grand"

                val v = vat.div(100)
                val vatAmount = Calculate.round(grand.times(v))
                totalAmount = grand.plus(vatAmount);

                tv_tv_vatAmount?.text = "$currency $vatAmount"

                tv_tv_totalAmount?.text = "$currency $totalAmount"
            }
            isLoaded = true

        } else {
            Toasty.info(requireContext(), getString(R.string.unable_to_process)).show()
        }
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