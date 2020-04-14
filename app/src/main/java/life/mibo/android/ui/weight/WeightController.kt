/*
 *  Created by Sumeet Kumar on 1/8/20 5:09 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 3:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.weight

import android.view.View
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.user_details.UserDetails
import life.mibo.android.models.user_details.UserDetailsPost
import life.mibo.android.models.weight.WeightAll
import life.mibo.android.models.weight.WeightAllResponse
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
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


    fun getUserDetails() {
        val member =
            Prefs.get(this.fragment.context).member
                ?: return
        fragment.getDialog()?.show()
        val session = UserDetailsPost("${member.id}", member.accessToken)
        API.request.getApi().userDetails(session).enqueue(object : Callback<UserDetails> {
            override fun onFailure(call: Call<UserDetails>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                fragment?.context?.let {
                    Toasty.error(it, R.string.unable_to_connect).show()
                }
            }

            override fun onResponse(call: Call<UserDetails>, response: Response<UserDetails>) {

                val data = response.body()
                if (data != null && data.status.equals("success" , true)) {
                    parseUserData(data)
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

    fun getAllWeight() {
        val member =
            Prefs.get(this.fragment.context).member
                ?: return
        fragment.getDialog()?.show()
        val session = WeightAll("${member.id}", member.accessToken)
        API.request.getApi().getAllWeight(session).enqueue(object : Callback<WeightAllResponse> {
            override fun onFailure(call: Call<WeightAllResponse>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                MiboEvent.log(t)
            }

            override fun onResponse(
                call: Call<WeightAllResponse>,
                response: Response<WeightAllResponse>
            ) {

                val data = response.body()
                if (data != null && data.status.equals("success", true)) {
                    parseData(data)
                } else {

                   // val err = data?.errors?.get(0)?.message
//                    if (err.isNullOrEmpty())
//                        Toasty.error(fragment.context!!, R.string.error_occurred).show()
//                    else Toasty.error(fragment.context!!, err, Toasty.LENGTH_LONG).show()
                }
                fragment.getDialog()?.dismiss()
            }
        })
    }

    fun parseData(report: WeightAllResponse) {
        observer.onChartDataReceived(report.data)
    }

    fun parseUserData(report: UserDetails) {
        observer.onUserDetailsReceived(report.data)
    }

}