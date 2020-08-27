/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_measurement.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.biometric.Biometric
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode

class ViewMeasureFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_view_measurement, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProfile()
        button_update?.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("measure_new", 2)
            navigate(Navigator.BODY_MEASURE, bundle)
            //navigate(Navigator.BODY_MEASURE, null)
        }

        setup()
    }


    private fun setProfile() {

        var biometric = Calculate.getBioData()
        if (biometric == null) {
            val biometrics: List<Biometric.Data?>? = Prefs.get(requireContext())
                .getJsonList(Prefs.BIOMETRIC, Biometric.Data::class.java)

            if (biometrics != null && biometrics.isNotEmpty()) {
                val bmc = biometrics[biometrics.size - 1]
                if (bmc != null) {
                    Calculate.addBioData(bmc)
                    biometric = Biometric.Decrypted.from(bmc)
                    update(biometric)
                    return
                }
            }
        }

        if (biometric == null) {
            getBioMetric()
        } else {
            update(biometric)
        }


    }


    fun setup() {
        val member = Prefs.get(context).member ?: return
        et_weight?.keyListener = null
        et_height?.keyListener = null
        et_chest?.keyListener = null
        et_waist?.keyListener = null
        et_hips?.keyListener = null
        et_hhips?.keyListener = null

        if (member.isMale()) {
            et_wrist?.visibility = View.GONE
            et_forearm?.visibility = View.GONE
            view_wrist?.visibility = View.GONE
            view_forearm?.visibility = View.GONE

        }
        else {
            et_wrist?.keyListener = null
            et_forearm?.keyListener = null

            et_wrist?.visibility = View.VISIBLE
            et_forearm?.visibility = View.VISIBLE
            view_wrist?.visibility = View.VISIBLE
            view_forearm?.visibility = View.VISIBLE

        }

    }


    fun update(data: Biometric.Decrypted?) {
        if (data == null)
            return
        val member = Prefs.get(context).member ?: return
        var unit = getString(R.string.cm_unit)
        var kg = getString(R.string.kg_unit)


        et_weight?.setText("${round(data.weight)} $kg")
        et_height?.setText("${round(data.height)} $unit")

        et_chest?.setText("${round(data.chest)} $unit")
        et_waist?.setText("${round(data.waist)} $unit")
        et_hips?.setText("${round(data.hips)} $unit")
        et_hhips?.setText("${round(data.highHips)} $unit")

        if (!member.isMale()) {
           // et_wrist?.keyListener = null
            //et_forearm?.keyListener = null

            et_wrist?.setText("${round(data.wrist)} $unit")
            et_forearm?.setText("${round(data.forearm)} $unit")
        }

    }

    fun round(value: Double?): Double {
        if (value == null)
            return 0.0
        return try {
            BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
        } catch (e: java.lang.Exception) {
            value
        }
    }


    private fun getBioMetric() {
        val member = Prefs.get(activity).member
        Prefs.get(context)["user_gender"] = "${member?.gender}"
        val memberId = member?.id() ?: ""
        val token = member?.accessToken ?: ""
        getDialog()?.show()
        API.request.getApi().getMemberBiometrics(MemberPost(memberId, token, "GetMemberBiometrics"))
            .enqueue(object : retrofit2.Callback<Biometric> {
                override fun onFailure(call: Call<Biometric>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.info(requireContext(), R.string.unable_to_connect).show()
                }

                override fun onResponse(call: Call<Biometric>, response: Response<Biometric>) {
                    getDialog()?.dismiss()

                    try {
                        val body = response?.body()
                        log("getBioMetric response $body")
                        if (body != null && body.isSuccess()) {
                            val list = body.data
                            list?.let {
                                Prefs.get(requireContext()).setJson(Prefs.BIOMETRIC, it)
                                if (it.isNotEmpty()) {
                                    val data = it[0]
                                    if (data != null)
                                        update(Biometric.Decrypted.from(data))
                                    else update(null)
                                }
                            }

                        } else if (body != null && body.isError()) {
                            button_update?.setText(R.string.add)
                            val msg = body.errors?.get(0)?.message
                            if (msg != null)
                                Toasty.info(requireContext(), msg).show()
                            return

                        } else {
                            Toasty.info(requireContext(), R.string.unable_to_connect).show()
                        }
                    } catch (e: Exception) {
                        MiboEvent.log(e)
                    }
                    getDialog()?.dismiss()
                }

            })
    }
}