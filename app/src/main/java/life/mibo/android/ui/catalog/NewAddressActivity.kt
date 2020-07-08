/*
 *  Created by Sumeet Kumar on 6/3/20 12:37 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/3/20 12:37 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_add_new_address.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.catalog.SaveShippingAddress
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewAddressActivity : BaseActivity() {

    companion object {

        fun launch(context: Context?, item: BuyActivity.AddressItem?) {
            val i = Intent(context, NewAddressActivity::class.java)
            item?.let {
                i.putExtra("product_address", it.addres)
                i.putExtra("product_name", it.name)
                i.putExtra("product_city", it.city)
                i.putExtra("product_country", it.country)
                i.putExtra("product_id", it.id)
                i.putExtra("product_phone", it.phone)
            }
            context?.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_new_address)

        btn_save?.setOnClickListener {
            onSaveClicked()
        }

        tv_country?.isClickable = false

        tv_country?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                ccp?.showCountryCodePickerDialog()
            }
            return@setOnTouchListener true
        }

        ccp?.setOnCountryChangeListener {
            tv_country?.setText(it.name)
        }
        loadData()
    }

    private var updateId = -1
    private fun loadData() {
        try {
            var name = intent?.getStringExtra("product_name") ?: ""
            if (name.length > 2) {
                updateId = intent?.getIntExtra("product_id", 0) ?: 0
                val first = name?.split(" ")[0]
                et_first_name?.setText(name?.split(" ")[0])
                et_last_name?.setText(name.substring(first.length))
                et_phone?.setText(intent?.getStringExtra("product_phone"))
                et_address1?.setText(intent?.getStringExtra("product_address"))
                et_city?.setText(intent?.getStringExtra("product_city"))
                tv_country?.setText(intent?.getStringExtra("product_country"))
                tv_add_new?.setText(R.string.update_shipping_address)
                btn_save?.setText(R.string.update_address)
            }
        } catch (e: Exception) {

        }

    }


    fun onSaveClicked() {
        if (isEmpty(et_first_name?.text)) {
            message(R.string.enter_fname)
            return
        }
        if (isEmpty(et_last_name?.text)) {
            message(R.string.enter_fname)
            return
        }
        if (isEmpty(et_phone?.text)) {
            message(R.string.enter_number)
            return
        }
        if (isEmpty(et_address1?.text)) {
            message(R.string.enter_address)
            return
        }
        if (isEmpty(et_city?.text)) {
            message(R.string.enter_city)
            return
        }
        if (isEmpty(tv_country?.text)) {
            message(R.string.select_country)
            return
        }

        val memberId = Prefs.get(this).member
        val data =
            SaveShippingAddress.Data(
                et_address1?.text?.toString() + " - " + et_address2?.text?.toString(),
                et_city?.text?.toString(),
                tv_country?.text?.toString(),
                memberId?.id(),
                et_first_name?.text?.toString() + " " + et_last_name.text?.toString(),
                et_phone?.text?.toString(), updateId
            )

        if (updateId > 1) {
            updateAddresses(
                SaveShippingAddress(
                    data,
                    memberId?.accessToken,
                    "UpdateMemberShippingAddress"
                )
            )
        } else {
            saveAddresses(
                SaveShippingAddress(
                    data,
                    memberId?.accessToken,
                    "SaveMemberShippingAddress"
                )
            )
        }
    }

    fun message(msg: String) {
        Toasty.snackbar(btn_save, msg)
    }

    fun message(msg: Int) {
        message(getString(msg))
    }


    fun isEmpty(s: CharSequence?): Boolean {
        return s == null || s.isEmpty()
    }

    private fun saveAddresses(data: SaveShippingAddress) {

        getDialog()?.show()

        API.request.getApi().saveMemberShippingAddress(data)
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.info(this@NewAddressActivity, R.string.unable_to_connect).show()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    getDialog()?.dismiss()
                    val body = response?.body()
                    if (body?.isSuccess() == true) {
                        val msg = body?.data?.message
                        msg?.let {
                            Toasty.info(this@NewAddressActivity, it).show()
                        }
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toasty.info(this@NewAddressActivity, R.string.error_occurred).show()
                    }

                }

            })
    }


    private fun updateAddresses(data: SaveShippingAddress) {

        getDialog()?.show()
        data.requestType = "UpdateMemberShippingAddress"

        API.request.getApi().updateMemberShippingAddress(data)
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.info(this@NewAddressActivity, R.string.unable_to_connect).show()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    getDialog()?.dismiss()
                    val body = response?.body()
                    if (body?.isSuccess() == true) {
                        val msg = body?.data?.message
                        msg?.let {
                            Toasty.info(this@NewAddressActivity, it).show()
                        }
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toasty.info(this@NewAddressActivity, R.string.error_occurred).show()
                    }

                }

            })
    }
}