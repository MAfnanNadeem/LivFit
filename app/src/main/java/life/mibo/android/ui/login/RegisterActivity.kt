package life.mibo.android.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
//import com.twilio.verification.TwilioVerification
//import com.twilio.verification.external.Via
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register_page1.*
import kotlinx.android.synthetic.main.activity_register_page2.*
import kotlinx.android.synthetic.main.activity_register_page3.*
import life.mibo.android.R
import life.mibo.android.receiver.SMSBroadcastReceiver
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.main.MessageDialog
import life.mibo.android.utils.Toasty


class RegisterActivity : BaseActivity() {

    //SID SK1a9a10e57385098b542fa54f4c6d3a3b
    //SECRET ekHtNKxLLF1yPeqWROHTw1CuCfPHHWUM
    interface Listener {
        fun onRegisterClicked(
            firstName: String?, lastName: String?, email: String?, password: String?,
            cPassword: String?, city: String?, country: String?, dob: String?,
            checkBox: Boolean, phoneNumber: String?
        )
        fun onSendOtpClicked(number: String?)
        fun onResendOtpClicked(number: String?)
        fun onVerifyOtpClicked(code: String?)
        fun onStop()
        fun onCreate(view: View)
    }

    private lateinit var controller: RegisterController
    //private lateinit var twilio: TwilioVerification
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        controller = RegisterController(this, observer)

        btn_register?.setOnClickListener {
            //viewAnimator?.showNext()
            validate()
            // startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
        btn_send_otp?.setOnClickListener {
            controller.onSendOtpClicked(et_number?.text?.toString())
            // startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
        tv_resend?.setOnClickListener {
            if (isResend)
                controller.onResendOtpClicked(et_number?.text?.toString())
            // startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
        btn_otp_confirmed?.setOnClickListener {
            if (et_otp?.text.isNullOrEmpty()) {
               // error(getString(R.string.enter_otp))
                Toasty.error(this, getString(R.string.enter_otp)).show()
                return@setOnClickListener
            }
            controller.onVerifyOtpClicked(et_otp?.text?.toString())
            // startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
        tv_gender?.setOnClickListener {
            controller.showGender()
        }
        tv_dob?.setOnClickListener {
            controller.showDobPicker()
        }
        tv_country?.setOnClickListener {
            controller.showCountry()
        }
        //ccp.registerPhoneNumberTextView(tv_country)
        controller.ccp = ccp
        controller.otpCcp = ccp_otp

        ccp.setDefaultCountryUsingPhoneCodeAndApply(971)
        ccp.registerPhoneNumberTextView(et_phone_number)
        ccp.setOnCountryChangeListener { country ->
            tv_country.text = country.name
            controller.selectedCountry = country
            controller.isCountry = true

        }
        ccp_otp.registerPhoneNumberTextView(et_number)
        ccp_otp.registerPhoneNumberTextView(et_number)
        ccp_otp.setOnCountryChangeListener { selectedCountry ->
            //tv_country.text = selectedCountry.name
        }
        et_otp.setOnCompleteListener {
            controller.onVerifyOtpClicked(et_otp?.text?.toString())
            //Toasty.success(this, "OTP Verified").show()
            //loginToHome()
        }
        checkbox_terms?.setOnCheckedChangeListener { _, isChecked ->
            if (isDialog) {
                isDialog = false
                return@setOnCheckedChangeListener
            }
            if (isChecked) {
                termsDialog(getString(R.string.terms_of_agreement))
                checkbox_terms?.isChecked = false
            }
        }
    }

    var isDialog = false
    fun termsDialog(terms: String) {
        MessageDialog(this,
            getString(R.string.terms_of_agreement),
            terms,
            getString(R.string.cancel),
            getString(R.string.accept),
            object : MessageDialog.Listener {
                override fun onClick(button: Int) {
                    if (button == MessageDialog.POSITIVE) {
                        isDialog = true
                    }
                    checkbox_terms?.isChecked = button == MessageDialog.POSITIVE

                }

            }).show()
    }

    fun otpDialog() {
        MessageDialog(this,
            "",
            "",
            getString(R.string.cancel),
            getString(R.string.send),
            object : MessageDialog.Listener {
                override fun onClick(button: Int) {
                    if (button == MessageDialog.POSITIVE) {
                        isDialog = true
                    }
                    checkbox_terms?.isChecked = button == MessageDialog.POSITIVE

                }

            }).show()
    }

    fun registerOtpListener() {

        controller.sendOtp(object : SMSBroadcastReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                log("OTPReceiveListener onOTPReceived $otp")
            }

            override fun onOTPTimeOut() {
                log("OTPReceiveListener timeout")
            }

        })
    }

    val observer = object : RegisterController.RegisterObserver {
        override fun onInvalidOtp() {
            et_otp?.clear()
        }

        override fun onValidationError(type: Int) {
            showError(type)
        }

        override fun onTimerUpdate(time: Long) {
            log("startTimer $time")
            if (time == 0L) {
                isResend = true
                tv_resend?.text = "Resend OTP"
                disposable?.dispose()
            } else {
                tv_resend?.text = String.format("Resend OTP in %02d", time / 1000)
            }
        }

        override fun otpReceived(otp: String?) {
            et_otp.setText(otp)
        }

        override fun updateNumber(id: Int) {
            this@RegisterActivity.updateNumberView(id)
        }

        override fun onDobSelect(dob: String?) {
            tv_dob.text = dob
        }

        override fun onGenderSelect(gender: String) {
            tv_gender.text = gender
        }

        override fun onCountrySelect(country: String) {
            tv_country.text = country
        }

    }

    private fun validate() {

        if (et_first_name.text.isNullOrEmpty()) {
            controller.showError(R.string.enter_fname, et_first_name)
            return
        }

        if (et_last_name.text.isNullOrEmpty()) {
            controller.showError(getString(R.string.enter_lname), et_last_name)
            return
        }
        if (et_email.text.isNullOrEmpty()) {
            controller.showError(getString(R.string.enter_email), et_email)
            return
        }
        if (et_password.text.isNullOrEmpty()) {
            controller.showError(getString(R.string.enter_password), et_password)
            return
        }
        if (et_confirm_password.text.isNullOrEmpty()) {
            controller.showError(getString(R.string.enter_confirm_password), et_confirm_password)
            return
        }
        if (!controller.isGender) {
            controller.showError(getString(R.string.select_gender), tv_gender)
            return
        }
        if (!controller.isDob) {
            controller.showError(getString(R.string.enter_dob), tv_dob)
            return
        }
        if (et_city.text.isNullOrEmpty()) {
            controller.showError(getString(R.string.enter_city), et_city)
            return
        }

        if (!controller.isCountry) {
            controller.showError(getString(R.string.select_your_country), tv_country)
            return
        }
        if (et_phone_number.text.isNullOrEmpty()) {
            controller.showError(getString(R.string.enter_number), et_phone_number)
            return
        }

        controller.onRegisterClicked(
            et_first_name.text?.toString(),
            et_last_name.text?.toString(),
            et_email.text?.toString(),
            et_password.text?.toString(),
            et_confirm_password.text?.toString(),
            et_city.text?.toString(),
            tv_country.text?.toString(),
            tv_dob.text?.toString(),
            checkbox_terms.isChecked,
            et_phone_number.text?.toString()
        )

    }

    private fun showError(type: Int) {
//        controller.onRegisterClicked(
//            et_first_name.text?.toString(),
//            et_last_name.text?.toString(),
//            et_email.text?.toString(),
//            et_password.text?.toString(),
//            et_confirm_password.text?.toString(),
//            et_city.text?.toString(),
//            tv_country.text?.toString(),
//            tv_dob.text?.toString(),
//            checkbox_terms.isChecked,
//            et_phone_number.text?.toString()
//        )

        when (type) {
            1 -> {
                controller.showError(R.string.enter_fname, et_first_name)
            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
            5 -> {

            }
            6 -> {

            }
            7 -> {

            }
            8 -> {

            }
        }
    }

    private fun updateNumberView(id: Int) {
        when (id) {
            0 -> {

            }
            1 -> {
                viewAnimator.showNext()
                et_number.text = et_phone_number.text
                et_number.isEnabled = false
                ccp_otp.fullNumber = ccp.fullNumber
                ccp_otp.isEnabled = false
                ccp_otp.isClickable = false
            }
            2 -> {
                viewAnimator.showNext()
                //tv_resend?.visibility = View.INVISIBLE
                //startResend(60)
                controller.startCountDown(60)
//                GlobalScope.async {
//                    startTicker(60)
//                }
            }
            3 -> {
                getDialog()?.dismiss()
                controller.loginToHome()
            }
        }

        //isOtp = true
    }


    var disposable: Disposable? = null
    private var isResend = false


    //var isOtp = false
    // private val ringCaptchaController = RingcaptchaAPIController("i2ucy2y2y3any8u5i8uz")


//    fun sendOtpTwilio(jwt: String?) {
//        if (jwt != null) {
//            if (!::twilio.isInitialized)
//                twilio = TwilioVerification(this);
//            twilio.startVerification(jwt, Via.SMS)
//        }
//    }
//
//    fun checkOtpTwilio(otp: String?) {
//        if (otp != null) {
//            if (!::twilio.isInitialized)
//                twilio = TwilioVerification(this);
//            twilio.checkVerificationPin(otp)
//        }
//    }

    override fun onStop() {
        super.onStop()
        controller.onStop()
        disposable?.dispose()
        //cancelTimer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        controller.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        controller.onRestoreInstanceState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        controller.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (controller.onBackPressed())
            super.onBackPressed()
    }

}