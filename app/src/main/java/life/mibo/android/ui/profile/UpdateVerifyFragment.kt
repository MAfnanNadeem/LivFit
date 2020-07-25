/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_update_verify.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.send_otp.SendOTP
import life.mibo.android.models.send_otp.SendOtpResponse
import life.mibo.android.models.verify_otp.VerifyOTP
import life.mibo.android.models.verify_otp.VerifyOtpResponse
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateVerifyFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_update_verify, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_otp?.setOnCompleteListener {
            validateOtp(Prefs.get(context).member?.id(), it)
        }

        tv_resend?.setOnClickListener {
            if (canSendOtp)
                resendOtp()
        }


        //tv_resend?.isSoundEffectsEnabled

    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            startCountDown(60)
        }
    }

    fun onNextClicked(): Boolean {
        validateOtp(Prefs.get(context).member?.id(), et_otp?.text?.toString())
        return true
    }

    fun otpReceived(otp: String?) {
        log("startOtpListener : sent to UpdateVerifyFragment ")
        et_otp?.setText(otp)
        validateOtp(Prefs.get(context).member?.id(), otp)
    }


    private fun validateOtp(userId: String?, otp: String?) {
        if (otp.isNullOrEmpty()) {
            Toasty.snackbar(et_otp, context?.getString(R.string.ent_otp))
            return
        }
        if (otp.length < 4) {
            //error(context.getString(R.string.ent_otp))
            return
        }
        //userId = "139"
        log("validateOtp $otp")
        getDialog()?.show()

        val otpData = VerifyOTP(userId ?: "", otp)
        API.request.getApi().verifyOtp(otpData).enqueue(object : Callback<VerifyOtpResponse> {

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                getDialog()?.dismiss()
                Toasty.snackbar(et_otp, context?.getString(R.string.unable_to_connect))
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>
            ) {
                getDialog()?.dismiss()
                val data = response.body()
                if (data == null) {
                    Toasty.snackbar(et_otp, context?.getString(R.string.error_occurred))
                    return
                }
                when {
                    data.status?.toLowerCase() == "success" -> {
                        try {
                            if (isAdded)
                                Toasty.success(requireContext(), R.string.number_verified).show()
                            //success(context.getString(R.string.number_verified))
                            //updateNumber(3)
                            MiboEvent.otpSuccess("$userId", "$otp")
                            //loginUser(memberData?.data?.email, memberData?.data?.password)
                            otpVerified()

                        } catch (e: java.lang.Exception) {

                        }
                        return
                    }
                    data.status == "error" -> {
                        //error("${data.errors?.get(0)?.message}")
                        Toasty.snackbar(et_otp, "${data.errors?.get(0)?.message}")
                        if (MiboApplication.DEBUG) {
                            if (otp == "1728") {
                                Toasty.success(requireContext(), R.string.number_verified).show()
                                MiboEvent.otpSuccess("$userId", "$otp")
                                otpVerified()
                                return
                            }
                            // return
                        }

                    }
                    else -> Toasty.warning(
                        requireContext(),
                        "OTP: " + response.body()
                    ).show()

                }
                getDialog()?.dismiss()
            }

        })
    }


    private var test = false

    //var isOtpSent = false
    var canSendOtp = false

    private var resendFormatter = ""
    private fun getResendFormat(): String {
        if (resendFormatter.isEmpty()) {
            resendFormatter = getString(R.string.resend_otp_in)
        }
        return resendFormatter
    }

    private var countTimer: CountDownTimer? = null

    var isTimerStarted = false
    private fun startCountDown(seconds: Int) {
        if (isTimerStarted && countTimer != null)
            return
        cancelTimer()
        countTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(it: Long) {
                log("startTimer $it")

                onTimerUpdate(it)
            }

            override fun onFinish() {
                onTimerUpdate(0L)
                cancelTimer()
            }
        }
        countTimer?.start()
        isTimerStarted = true
        // isTimerStarted = true
    }

    internal fun cancelTimer() {
        countTimer?.cancel()
        isTimerStarted = false
    }

    fun onTimerUpdate(time: Long) {
        log("startTimer $time")
        if (time == 0L) {
            canSendOtp = true
            //tv_resend?.text = getString(R.string.resend_otp)
            tv_resend_timer?.text = ""
        } else {
            // tv_resend?.text = String.format(getResendFormat(), time / 1000)
            tv_resend_timer?.text = String.format(getResendFormat(), time / 1000)
        }
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }

    override fun onDetach() {
        cancelTimer()
        super.onDetach()
    }


    private fun resendOtp() {

//        if (test) {
//            getDialog()?.dismiss()
//            Toasty.success(requireContext(), R.string.otp_sent).show()
//            canSendOtp = false
//            // updateNextButton()
//            return
//        }

        val userId = Prefs.get(context).member?.id() ?: return
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
                    //error(getString(R.string.otp_not_sent))
                    Toasty.snackbar(et_otp, context?.getString(R.string.otp_not_sent))
                    return
                }
                when {
                    data.status?.toLowerCase() == "success" -> {
                        Toasty.success(requireContext(), R.string.otp_sent).show()
                        canSendOtp = false
                        //updateNextButton()
                    }
                    data.status?.toLowerCase() == "error" -> {
                        Toasty.error(requireContext(), "${data.errors?.get(0)?.message}").show()
                    }
                    else -> Toasty.warning(requireContext(), "Register: " + response.body()).show()
                }
            }
        })

    }


    private fun otpVerified() {
        try {
            log("BodyBaseFragment otpVerified ")
            val frg = parentFragment
            log("BodyBaseFragment otpVerified $frg")
            if (frg is ProfileUpdateFragment) {
                frg.otpVerified()
                return
            }


            val frg2 = parentFragmentManager?.fragments
            log("BodyBaseFragment otpVerified list $frg2")
            if (frg2.size > 0) {
                for (frg3 in frg2) {
                    log("BodyBaseFragment otpVerified $frg3")
                    if (frg3 is ProfileUpdateFragment) {
                        frg3.otpVerified()
                        return
                    }
                }
            }
        } catch (e: Exception) {

        }

    }

}