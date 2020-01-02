package life.mibo.hexa.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.rilixtech.widget.countrycodepicker.Country
import com.thrivecom.ringcaptcha.RingcaptchaAPIController
import com.thrivecom.ringcaptcha.RingcaptchaService
import com.thrivecom.ringcaptcha.lib.handlers.RingcaptchaHandler
import com.thrivecom.ringcaptcha.lib.models.RingcaptchaResponse
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register_page1.*
import kotlinx.android.synthetic.main.activity_register_page2.*
import kotlinx.android.synthetic.main.activity_register_page3.*
import life.mibo.hardware.core.Logger
import life.mibo.hexa.MainActivity
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.libs.datepicker.SpinnerDatePickerDialogBuilder
import life.mibo.hexa.models.register.Data
import life.mibo.hexa.models.register.RegisterGuestMember
import life.mibo.hexa.models.register.RegisterResponse
import life.mibo.hexa.models.verify_number.VerifyNumber
import life.mibo.hexa.models.verify_number.VerifyResponse
import life.mibo.hexa.ui.base.BaseActivity
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class RegisterActivity : BaseActivity() {

    interface Listener {
        fun onRegisterClicked()
        fun onSendOtpClicked(number: String)
        fun onVerifyOtpClicked(code: String)
        fun onStop()
        fun onCreate(view: View)
    }

    private lateinit var controller: RegisterController
    private var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        controller = RegisterController(this, null)

        btn_register?.setOnClickListener {
            //viewAnimator?.showNext()
            validate()
            // startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
        btn_send_otp?.setOnClickListener {
            sendOtp()
            // startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
        btn_otp_confirmed?.setOnClickListener {
            validateOtp()
            // startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
        tv_gender?.setOnClickListener {
            showGender()
        }
        tv_dob?.setOnClickListener {
            showDobPicker()
        }
        tv_country?.setOnClickListener {
            showCountry()
        }
        //ccp.registerPhoneNumberTextView(tv_country)
        ccp.setDefaultCountryUsingPhoneCodeAndApply(971)
        ccp.registerPhoneNumberTextView(et_phone_number)
        ccp.setOnCountryChangeListener { country ->
            tv_country.text = country.name
            this.selectedCountry = country
            isCountry = true

        }
        ccp_otp.registerPhoneNumberTextView(et_number)
        ccp_otp.registerPhoneNumberTextView(et_number)
        ccp_otp.setOnCountryChangeListener { selectedCountry ->
            //tv_country.text = selectedCountry.name
        }
        et_otp.setOnCompleteListener {
            validateOtp()
            //Toasty.success(this, "OTP Verified").show()
            //loginToHome()
        }

    }

    var selectedCountry: Country? = null
    private fun validate() {
        if (et_first_name.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, getString(R.string.enter_fname)).show()
            return
        }
        if (et_last_name.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, getString(R.string.enter_lname)).show()
            return
        }
        if (et_email.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, getString(R.string.enter_email)).show()
            return
        }
        if (et_password.text?.isEmpty()!!) {
            Toasty.error(this, getString(R.string.enter_password)).show()
            return
        }
        if (et_confirm_password.text?.toString()?.isEmpty()!!) {
            Toasty.error(this, getString(R.string.enter_confirm_password)).show()
            return
        }
        if (et_city.text?.isEmpty()!!) {
            Toasty.error(this, getString(R.string.enter_city)).show()
            return
        }
        if (!checkbox_terms.isChecked) {
            Toasty.error(this, getString(R.string.accept_term_conditions)).show()
            return
        }
        if (!isDob) {
            Toasty.error(this, getString(R.string.enter_dob)).show()
            return
        }
        if (!isGender) {
            Toasty.error(this, getString(R.string.select_gender)).show()
            return
        }
        if (!isCountry) {
            Toasty.error(this, getString(R.string.select_your_country)).show()
            return
        }
        if (et_phone_number.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, getString(R.string.enter_number)).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(et_email.text).matches()) {
            Toasty.warning(this, getString(R.string.email_not_valid)).show()
            return
        }



        if ((et_password.text?.trim()!! == et_confirm_password.text?.trim()!!)) {
            if (!isValidPassword(et_password.text.toString())) {
                Toasty.warning(this, getString(R.string.password_requirement)).show()
                return
            }

            val data = Data(
                firstName = et_first_name?.text.toString(),
                lastName = et_last_name?.text.toString(),
                email = et_email?.text.toString(),
                password = et_confirm_password.text.toString(),
                city = et_city?.text.toString(),
                phone = ccp.fullNumberWithPlus,
                country = tv_country?.text.toString(),
                gender = tv_gender.text.toString(),
                dOB = tv_dob.text.toString(),
                countryCode = ccp.selectedCountryCode
            )
            register(data = RegisterGuestMember(data))
            //Toasty.success(this, "Successfully registered").show()
            //viewAnimator.showNext()
            //updateNumber()
            //startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            return
        } else {
            Logger.e("Password not matched ${et_password.text?.trim()} == ${et_confirm_password.text?.trim()}")
            Toasty.error(this, getString(R.string.confirm_password_not_matched)).show()
        }
    }

    fun register(data: RegisterGuestMember) {
        getDialog()?.show()
        API.request.getApi().register(data).enqueue(object : Callback<RegisterResponse> {
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                getDialog()?.dismiss()
                Toasty.error(this@RegisterActivity, "Failed " + t.message).show()
                t.printStackTrace()
                Logger.e("RegisterActivity : register API ", t)

            }

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                getDialog()?.dismiss()
                val data = response.body()
                if (data == null) {
                    Toasty.error(this@RegisterActivity, getString(R.string.error_occurred)).show()
                    return
                }
                when {
                    data.status == "success" -> {
                        userId = data.member?.userId!!
                        Toasty.success(this@RegisterActivity, "Successfully Registered $userId")
                            .show()
                        updateNumber()
                    }
                    data.status == "error" -> {
                        //val er = data.errors;
                        //var msg = data?.status
                        //val e = data.errors
                        //log("RegisterGuestMember $e")
                        // log("RegisterGuestMember ${e.toString()}")
                        //val el = JsonParser.parseString(data.errors.toString())
                        // val rr: Error? = Gson().fromJson(el, Error::class.java)
                        // log("RegisterGuestMember rr ${rr.toString()}")
                        Toasty.error(
                            this@RegisterActivity,
                            "Error: ${data.errors?.get(0)?.message}"
                        ).show()

                    }
                    else -> Toasty.warning(
                        this@RegisterActivity,
                        "Register: " + response.body()
                    ).show()
                }
            }

        })

    }

    //    fun <T> stringToArray(s: String, clazz: Class<Array<T>>): List<T> {
//        val arr = Gson().fromJson(s, clazz)
//        return Arrays.asList(arr) //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
//    }
    private fun isValidPassword(s: String): Boolean {
        //val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        val pattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$&+=])(?=\\S+\$).{4,}$")
        //val pattern2 = Pattern.compile("[a-zA-Z0-9!@#$]{8,24}")

        return pattern.matcher(s).matches()
    }

    var isOtpVerified = false
    var isOtp = false
    private val ringCaptchaController = RingcaptchaAPIController("i2ucy2y2y3any8u5i8uz")

    private fun updateNumber() {
        viewAnimator.showNext()
        et_number.text = et_phone_number.text
        //ccp_otp.setSelectedCountry(selectedCountry)
        ccp_otp.fullNumber = ccp.fullNumber
        isOtp = true
    }

    private fun sendOtp() {
        if (et_number.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, "Please enter Number").show()
            return
        }
        log("sendOtp ${ccp_otp.fullNumber}")
        getDialog()?.show()
        ringCaptchaController.sendCaptchaCodeToNumber(
            applicationContext,
            ccp_otp.fullNumber,
            RingcaptchaService.SMS,
            object : RingcaptchaHandler {

                //Called when the response is successful
                override fun onSuccess(response: RingcaptchaResponse) {
                    getDialog()?.dismiss()
                    //Handle SMS reception automatically (only valid for verification)

                    log("OTP Response $response")
                    if (response.status.equals("SUCCESS", true)) {
                        Toasty.error(this@RegisterActivity, "OTP Sent").show()
                        viewAnimator.showNext()
                    } else {
                        Toasty.error(this@RegisterActivity, "Unable to send OTP").show()
                    }
                    //RingcaptchaSMSHandler
                    RingcaptchaAPIController.setSMSHandler { s, s1 ->
                        //Only called when SMS reception was detected
                        //Automatically verify PIN code
                        true
                    }
                }

                //Called when the response is unsuccessful
                override fun onError(e: Exception) {
                    //Display an error to the user
                    getDialog()?.dismiss()
                    e.printStackTrace()
                    Toasty.error(this@RegisterActivity, "OTP Error: " + e.message).show()
                }
            },
            "fd50d709e866af8ce33a9eacee42db1a8cee8f75"
        )


    }

    private fun validateOtp() {
        if (et_otp.text?.trim()?.isEmpty()!! || et_otp.text?.trim()!!.length < 3) {
            Toasty.error(this, "Please enter OTP Code").show()
            return
        }

        getDialog()?.show()
        ringCaptchaController.verifyCaptchaWithCode(
            applicationContext,
            et_otp.text?.trim().toString(),
            object : RingcaptchaHandler {
                override fun onSuccess(response: RingcaptchaResponse?) {
                    log("verifyCaptchaWithCode $response")
                    Toasty.error(this@RegisterActivity, "OTP Response $response").show()
                    getDialog()?.dismiss()
                    //isOtpVerified = true
                    API.request.getApi().verifyNumber(VerifyNumber(userId))
                        .enqueue(object : Callback<VerifyResponse> {
                            override fun onFailure(call: Call<VerifyResponse>, t: Throwable?) {
                                getDialog()?.dismiss()
                                Toasty.error(this@RegisterActivity, "Error: " + t?.message).show()
                            }

                            override fun onResponse(
                                call: Call<VerifyResponse>,
                                response: Response<VerifyResponse>
                            ) {
                                getDialog()?.dismiss()
                                response?.body()?.let {
                                    if (it.status!!.contains("success")) {
                                        isOtpVerified = true
                                        loginToHome()
                                    }
                                }

                            }

                        })
                    RingcaptchaAPIController.setSMSHandler(null);
                }

                override fun onError(e: java.lang.Exception?) {
                    getDialog()?.dismiss()
                    e?.printStackTrace()
                    val m = e?.message
                    Toasty.error(this@RegisterActivity, "Error: $m").show()
                }

            },
            "fd50d709e866af8ce33a9eacee42db1a8cee8f75"
        )

    }

    private fun loginToHome() {
        Toasty.success(this@RegisterActivity, "Successfully registered").show()
        //TODO
        //if (isOtpVerified)
        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
    }

    var isDob = false
    var isGender = false
    var isCountry = false
    var gender = ""
    fun showCountry() {
        //ccp.showFullName(true)
        // ccp.showCountryCodePickerDialog
        ccp.showCountryCodePickerDialog()
    }

    fun showGender() {
        val animals = arrayOf("Male", "Female")
        AlertDialog.Builder(this).setTitle("Select Gender")
            .setItems(animals, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0)
                    gender = "MALE"
                else if (which == 1)
                    gender = "FEMALE"
                tv_gender.text = gender
                isGender = true
            }).create().show()
    }

    fun showDobPicker() {
        SpinnerDatePickerDialogBuilder()
            .context(this@RegisterActivity)
            .callback { _, year, monthOfYear, dayOfMonth ->
                //tv_dob.setText("$dayOfMonth/$monthOfYear/$year")
                tv_dob.text = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear.plus(1), year)
                isDob = true
            }
            .spinnerTheme(R.style.DatePickerSpinner)
            .showTitle(true)
            .showDaySpinner(true)
            .defaultDate(2000, 1, 1)
            .maxDate(2010, 0, 1)
            .minDate(1980, 0, 1)
            .build()
            .show()
    }
}