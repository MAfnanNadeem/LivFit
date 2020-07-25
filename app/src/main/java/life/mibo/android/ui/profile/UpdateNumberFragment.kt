/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.rilixtech.widget.countrycodepicker.Country
import kotlinx.android.synthetic.main.fragment_update_number.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.send_otp.SendOTP
import life.mibo.android.models.send_otp.SendOtpResponse
import life.mibo.android.models.user_details.UpdateMemberDetails
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateNumberFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_update_number, container, false)
        return root
    }

    var country_: Country? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ccp_otp?.registerPhoneNumberTextView(et_number)
        ccp_otp?.setOnCountryChangeListener {
            country_ = it
        }

        val update =
            Prefs.get(requireContext())
                .getJson<UpdateMember>("update_member", UpdateMember::class.java)

        ccp_otp?.setCountryForNameCode(update.countryIso)
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
        if (isVisible) {
            log("onResume  isVisible")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            openKeyboard()
//                et_number?.requestFocus()
//                //et_number?.focusable = View.FOCUSABLE
//            } else {
//                et_number?.requestFocus()
//            }
        }
    }

    private fun openKeyboard() {
        try {
            et_number?.requestFocus()
            val imm: InputMethodManager? =
                activity?.getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager?

            imm?.showSoftInput(et_number, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {

        }
        try {
            Handler().postDelayed({
                et_number?.dispatchTouchEvent(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN,
                        0f,
                        0f,
                        0
                    )
                )
                et_number?.dispatchTouchEvent(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP,
                        0f,
                        0f,
                        0
                    )
                )
            }, 200)
        } catch (e: Exception) {

        }


    }

    var isOtpSent = false
    fun onNextClicked(): Boolean {
        if (isOtpSent)
            return true
        val phone = et_number?.text?.toString()?.trim() ?: ""
        if (phone.isNotEmpty())
            saveDateApi()
        else {
            Toasty.snackbar(et_number, R.string.enter_number)
        }
        return false
    }

    fun saveDateApi() {
        val member = Prefs.get(context).member

        val update =
            Prefs.get(requireContext())
                .getJson<UpdateMember>("update_member", UpdateMember::class.java) ?: return

        log("saveDateApi update $update")

        var date = ""
        try {
            val sp = update.dob.split("/")
            val day = sp.get(0).toIntOrNull() ?: 0
            val month = sp.get(1).toIntOrNull() ?: 0
            val year = sp.get(2).toIntOrNull() ?: 0
            date = String.format("%d-%02d-%02d", year, month, day)
            //date = "$year-$month-$day"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        log("date :::::::  $date")

        if (date.isNullOrEmpty()) {
            error(getString(R.string.enter_dob))
            return
        }

        val num = ccp_otp.phoneNumber?.nationalNumber
        //var number = et_number?.text?.toString()?.trim() ?: ""
        if (num == null) {
            error(getString(R.string.enter_your_mobile_number))
            return
        }
        val number = "$num"
        if (number.length < 7) {
            error(getString(R.string.number_not_valid))
            return
        }
        // number = number.substring(1)


        val data = UpdateMemberDetails.Data(
            update.city,
            update.countryIso?.toUpperCase(),
            date,
            update.fName,
            update.lName,
            update.gender, "+${ccp_otp.phoneNumber?.countryCode}", number,
            member?.id, null
        )

        getDialog()?.show()
        API.request.getApi().updateMemberDetails(UpdateMemberDetails(data, member?.accessToken))
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {

                    val body = response?.body()
                    if (body != null && body.isSuccess()) {
                        Prefs.get(context).set("profile_update", "true")
                        //Toasty.info(requireContext(), getString(R.string.profile_updated)).show()
                        sendOtp(member?.id()!!)
                        return
                    } else {
                        Toasty.error(requireContext(), "${body?.errors?.get(0)?.message}").show()
                    }

                    getDialog()?.dismiss()
                }

            })
    }

    var test = false

    private fun sendOtp(userId: String) {
//        if (test) {
//            getDialog()?.dismiss()
//            Toasty.success(requireContext(), R.string.otp_sent).show()
//            isOtpSent = true
//            updateNextButton()
//            return
//        }
        startOtpReceiver()
        getDialog()?.show()
        // val data = SendOTP(userId)
        API.request.getApi().sendOtp(SendOTP(userId)).enqueue(object : Callback<SendOtpResponse> {

            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                getDialog()?.dismiss()
                Toasty.error(requireContext(), R.string.unable_to_connect).show()
                //Toasty.error(context, "Failed " + t.message).show()
                t.printStackTrace()
                //Logger.e("RegisterActivity : register API ", t)
            }

            override fun onResponse(
                call: Call<SendOtpResponse>, response: Response<SendOtpResponse>
            ) {
                getDialog()?.dismiss()
                val data = response.body()
                if (data == null) {
                    Toasty.snackbar(et_number, getString(R.string.otp_not_sent))
                    return
                }
                when {
                    data.status?.toLowerCase() == "success" -> {
                        Toasty.success(requireContext(), R.string.otp_sent).show()
                        isOtpSent = true
                        updateNextButton()
                    }
                    data.status?.toLowerCase() == "error" -> {
                        Toasty.error(requireContext(), "${data.errors?.get(0)?.message}").show()
                    }
                    else -> Toasty.warning(requireContext(), "Register: " + response.body()).show()
                }
            }
        })

    }

    fun toastNotEditable() {
        Toasty.snackbar(et_number, getString(R.string.not_editable))
    }

    fun error(msg: String) {
        Toasty.snackbar(et_number, msg)
    }

    fun updateNextButton() {
        try {
            log("BodyBaseFragment updateNextButton ")
            val frg = parentFragment
            log("BodyBaseFragment updateNextButton $frg")
            if (frg is ProfileUpdateFragment) {
                frg.nextClick()
                return
            }


            val frg2 = parentFragmentManager?.fragments
            log("BodyBaseFragment updateNextButton list $frg2")
            if (frg2.size > 0) {
                for (frg3 in frg2) {
                    log("BodyBaseFragment updateNextButton2 $frg3")
                    if (frg3 is ProfileUpdateFragment) {
                        frg3.nextClick()
                        return
                    }
                }
            }
        } catch (e: Exception) {

        }

    }


    private fun startOtpReceiver() {
        try {
            log("BodyBaseFragment startOtpReceiver ")
            val frg = parentFragment
            log("BodyBaseFragment startOtpReceiver $frg")
            if (frg is ProfileUpdateFragment) {
                frg.startOtpReceiver()
                return
            }


            val frg2 = parentFragmentManager?.fragments
            log("BodyBaseFragment startOtpReceiver list $frg2")
            if (frg2.size > 0) {
                for (frg3 in frg2) {
                    log("BodyBaseFragment startOtpReceiver $frg3")
                    if (frg3 is ProfileUpdateFragment) {
                        frg3.startOtpReceiver()
                        return
                    }
                }
            }
        } catch (e: Exception) {

        }

    }
}