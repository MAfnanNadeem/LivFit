package life.mibo.android.ui.login

//import com.twilio.verification.TwilioVerification
//import com.twilio.verification.external.Via
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.phone.SmsRetriever
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register_page1.*
import kotlinx.android.synthetic.main.activity_register_page2.*
import kotlinx.android.synthetic.main.activity_register_page3.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.dialog.MyDialog
import life.mibo.android.ui.login.RegisterController.Companion.NUMBER_VIEW
import life.mibo.android.ui.login.RegisterController.Companion.OTP_VIEW
import life.mibo.android.ui.login.RegisterController.Companion.REGISTER_VIEW
import life.mibo.android.ui.main.MessageDialog
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

    //SID SK1a9a10e57385098b542fa54f4c6d3a3b
    //SECRET ekHtNKxLLF1yPeqWROHTw1CuCfPHHWUM
    interface Listener {
        fun onRegisterClicked(
            firstName: String?,
            lastName: String?,
            email: String?,
            password: String?,
            cPassword: String?,
            city: String?,
            country: String?,
            dob: String?,
            checkBox: Boolean,
            phoneNumber: String?,
            socialType: String,
            socialKey: String,
            socialPhoto: String,
            areaCode: String
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
                // termsDialog(getString(R.string.terms_of_agreement))
                TermsDialog(this, object : ItemClickListener<Int> {
                    override fun onItemClicked(item: Int?, position: Int) {
                        if (position == 2) {
                            isDialog = true
                            checkbox_terms?.isChecked = true
                        }
                    }

                }).show(supportFragmentManager, "TermsDialog")
                checkbox_terms?.isChecked = false
            }
        }

        val update = intent?.getBooleanExtra("is_update_profile", false) ?: false
        if (update) {
            updateProfile()
            return
        }

        val social: Bundle? = intent?.getBundleExtra("social_data")
        if (social != null) {

            val code = intent?.getIntExtra("social_data_code", 100) ?: 100
            if (code == 100) {
                socialType = "facebook"
                socialPhoto = social.getString("profile", "")
                socialKey = social.getString("id", "")
                et_first_name?.setText(social.getString("first_name", ""))
                et_last_name?.setText(social.getString("last_name", ""))
                et_email?.setText(social.getString("email", ""))

            } else if (code == 200) {
                socialType = "google"
                socialPhoto = social.getString("photoUrl", "")
                socialKey = social.getString("id", "")
                val name = social.getString("displayName", "").split(" ")
                if (name.size > 0)
                    et_first_name?.setText(name[0])
                if (name.size > 1)
                    et_last_name?.setText(name[1])
                et_email?.setText(social.getString("email", ""))

            }
            //Toasty.info(this, socialType).show()
        }

        et_password?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                pwd_view?.visibility = View.VISIBLE
                // Utils.show(pwd_view)
            } else {
//                Utils.hide(pwd_view)
//                Single.just("").delay(700, TimeUnit.MILLISECONDS)
//                    .observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
//                    pwd_view?.visibility = View.GONE
//                }.subscribe()
                pwd_view?.visibility = View.GONE
            }
        }
        et_password?.doOnTextChanged { text, start, before, count ->
            try {
                validatePwd(text?.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        et_confirm_password?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (et_confirm_password?.text?.isEmpty() == true)
                    tv_confirm_password?.visibility = View.GONE
            } else {

            }
        }
        et_confirm_password?.doOnTextChanged { text, start, before, count ->
            try {
                checkPwd(et_password?.text?.toString() ?: "", text.toString())
            } catch (e: Exception) {

            }
        }
        colorGreen = ContextCompat.getColor(this, R.color.textColorGreen)
        colorRed = ContextCompat.getColor(this, R.color.colorAccent)

        otp_edit_number?.setOnClickListener {
            //otp_ccp_?.isEnabled = true
            // otp_et_phone_number?.isEnabled = true
        }
    }

    var colorRed = 0
    var colorGreen = 0

    @Synchronized
    private fun validatePwd(input: String?) {
        if (input != null) {
            if (colorGreen == 0) {
                colorGreen = ContextCompat.getColor(this, R.color.textColorGreen)
            }
            if (colorRed == 0) {
                colorRed = ContextCompat.getColor(this, R.color.error_message_color)
            }

            val specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?"
            var currentCharacter: Char
            var result = 0
            var numberPresent = false
            var upperCasePresent = false
            var lowerCasePresent = false
            var lengthPresent = false
            var specialCharacterPresent = false

            if (input.length >= 8)
                lengthPresent = true

            for (element in input) {
                currentCharacter = element
                when {
                    specialChars.contains(currentCharacter.toString()) -> specialCharacterPresent =
                        true
                    Character.isDigit(currentCharacter) -> numberPresent = true
                    Character.isUpperCase(currentCharacter) -> upperCasePresent = true
                    Character.isLowerCase(currentCharacter) -> lowerCasePresent = true
                }
            }

            if (numberPresent) {
                ++result
                tv_digit?.setTextColor(colorGreen)
            } else {
                tv_digit?.setTextColor(colorRed)
            }

            if (upperCasePresent && lowerCasePresent) {
                ++result
                tv_upper?.setTextColor(colorGreen)
            } else {
                tv_upper?.setTextColor(colorRed)
            }

            if (lengthPresent) {
                ++result
                tv_8_char?.setTextColor(colorGreen)
            } else {
                tv_8_char?.setTextColor(colorRed)
            }

            if (specialCharacterPresent) {
                ++result
                tv_special?.setTextColor(colorGreen)
            } else {
                tv_special?.setTextColor(colorRed)
            }
            pwdIndicator = result

            tv_confirm_password?.visibility = View.GONE

        }
    }

    fun checkPwd(old: String, new: String) {
        if (old.isNotEmpty() && old == new)
            tv_confirm_password?.visibility = View.GONE
        else tv_confirm_password?.visibility = View.VISIBLE
    }

    var pwdIndicator = 0

    private var isUpdateProfile = false
    private var updateMemberId = ""

    fun updateProfile() {
        val member = Prefs.get(this@RegisterActivity).member
        isUpdateProfile = true
        tv_register_info?.setText(R.string.update_your_profile)
        updateMemberId = member?.id() ?: ""
        val fName = member?.firstName ?: ""
        val lName = member?.lastName ?: ""
        val email = intent?.getStringExtra("member_email") ?: ""
        //val pwd = intent?.getStringExtra("member_pwd") ?: ""

        if (fName.isNotEmpty()) {
            et_first_name?.setText(fName)
            //et_first_name?.isEnabled = false
        }
        if (lName.isNotEmpty()) {
            et_last_name?.setText(lName)
            //et_last_name?.isEnabled = false
        }
        if (email.isNotEmpty()) {
            et_email?.setText(email)
            et_email?.isEnabled = false
        }
//        if (pwd.isNotEmpty()) {
//            et_password?.setText(fName)
//            et_confirm_password?.setText(fName)
//            // et_first_name?.isEnabled = false
//        }

        member?.city?.let {
            if (it.isNotEmpty()) {
                et_city?.setText(it)
            }
        }

        member?.gender?.let {
            if (it.isNotEmpty()) {
                if (it.equals("male", true) || it.equals("female", true)) {
                    controller.updateGender(it)
                }
            }
        }
    }

    var socialType = "";
    var socialPhoto = "";
    var socialKey = "";

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

//        controller.sendOtp(object : SMSBroadcastReceiver.OTPReceiveListener {
//            override fun onOTPReceived(otp: String?) {
//                log("OTPReceiveListener onOTPReceived $otp")
//            }
//
//            override fun onOTPTimeOut() {
//                log("OTPReceiveListener timeout")
//            }
//
//        })
    }

    private var resendFormatter = ""
    fun getResendFormat(): String {
        if (resendFormatter.isEmpty()) {
            resendFormatter = getString(R.string.resend_otp_timer)
        }
        return resendFormatter
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
                tv_resend?.text = getString(R.string.resend_otp)
                disposable?.dispose()
            } else {
                tv_resend?.text = String.format(getResendFormat(), time / 1000)
            }
        }

        override fun otpReceived(otp: Intent?) {
            if (otp != null)
                startActivityForResult(otp, controller.OTP_HINT)
            // et_otp?.setText(otp)
        }


        override fun updateView(id: Int) {
            updateNumberView(id)
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

        if (isUpdateProfile && updateMemberId.isNotEmpty()) {
            controller.validateInvitedMember(
                et_first_name.text?.toString(),
                et_last_name.text?.toString(),
                et_email.text?.toString(),
                et_password.text?.toString(),
                et_confirm_password.text?.toString(),
                et_city.text?.toString(),
                ccp?.defaultCountryNameCode,
                tv_dob.text?.toString(),
                checkbox_terms.isChecked,
                et_phone_number.text?.toString(), socialType, socialKey, updateMemberId, "0"
            )
            return
        }
        controller.onRegisterClicked(
            et_first_name.text?.toString(),
            et_last_name.text?.toString(),
            et_email.text?.toString(),
            et_password.text?.toString(),
            et_confirm_password.text?.toString(),
            et_city.text?.toString(),
            ccp?.defaultCountryNameCode,
            tv_dob.text?.toString(),
            checkbox_terms.isChecked,
            et_phone_number.text?.toString(), socialType, socialKey, socialPhoto, "0"
        )

    }

    fun otpReceived(otp: String?) {
        et_otp?.setText(otp)
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
            REGISTER_VIEW -> {

            }
            NUMBER_VIEW -> {
                viewAnimator.showNext()
                et_number.text = et_phone_number.text
                et_number.isEnabled = false
                ccp_otp.fullNumber = ccp.fullNumber
                ccp_otp.isEnabled = false
                ccp_otp.isClickable = false
            }
            OTP_VIEW -> {
                // registerOtpListener()

                viewAnimator.showNext()
                otp_ccp_?.setCountryForNameCode(ccp_otp?.defaultCountryNameCode)
                var num = et_phone_number.text?.toString()
                if (num != null && num.startsWith("0"))
                    num = num.substring(1)
                otp_et_phone_number?.setText(num)
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

    override fun onStop() {
        super.onStop()
        controller.onStop()
        disposable?.dispose()
        //cancelTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
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
        if (requestCode == controller.OTP_HINT && resultCode == Activity.RESULT_OK) {
            try {
                val message = data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE) ?: ""
                val pattern = Pattern.compile("(|^)\\d{4}")
                val matcher: Matcher = pattern.matcher(message)
                if (matcher.find()) {
                    otpReceived(matcher.group(0))
                    //otpText.setText(matcher.group(0))
                }
            } catch (e: Exception) {

            }
        } else
            controller.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (controller.onBackPressed())
            super.onBackPressed()
    }

    var mDialog: MyDialog? = null

    fun getDialog(): MyDialog? {
        if (mDialog == null)
            mDialog = MyDialog.get(this)
        return mDialog
    }

    fun log(msg : String?){
        Logger.e("RegisterActivity", "$msg")
    }

}