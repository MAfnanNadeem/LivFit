/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.login

//import android.net.wifi.hotspot2.pps.Credential

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.GoogleApiClient
import com.rilixtech.widget.countrycodepicker.Country
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import life.mibo.hardware.core.Logger
import life.mibo.hexa.MainActivity
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.libs.datepicker.SpinnerDatePickerDialogBuilder
import life.mibo.hexa.models.register.Data
import life.mibo.hexa.models.register.RegisterGuestMember
import life.mibo.hexa.models.register.RegisterResponse
import life.mibo.hexa.models.send_otp.SendOTP
import life.mibo.hexa.models.send_otp.SendOtpResponse
import life.mibo.hexa.models.verify_otp.VerifyOTP
import life.mibo.hexa.models.verify_otp.VerifyOtpResponse
import life.mibo.hexa.receiver.AppSignatureHelper
import life.mibo.hexa.receiver.SMSBroadcastReceiver
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import kotlin.math.log

//import kotlinx.android.synthetic.main.activity_register.*
//import kotlinx.android.synthetic.main.activity_register_page1.*
//import kotlinx.android.synthetic.main.activity_register_page2.*
//import kotlinx.android.synthetic.main.activity_register_page3.*

class RegisterController(val context: RegisterActivity, val observer: RegisterObserver) :
    RegisterActivity.Listener {


    interface RegisterObserver {
        fun onDobSelect(dob: String?)
        fun onGenderSelect(gender: String)
        fun onCountrySelect(country: String)
        fun updateNumber(id: Int)
        fun otpReceived(otp: String?)
    }

    //lateinit var observer: RegisterObserver

    override fun onCreate(view: View) {

//        ccp_otp.registerPhoneNumberTextView(et_number)
//        ccp_otp.registerPhoneNumberTextView(et_number)
//        ccp_otp.setOnCountryChangeListener { selectedCountry ->
//            //tv_country.text = selectedCountry.name
//        }
    }

    override fun onStop() {
        if (::smsBroadcast.isInitialized)
            context.unregisterReceiver(smsBroadcast)
    }

    override fun onRegisterClicked() {
        //validate()
    }

    override fun onSendOtpClicked(number: String?) {
        sendOtp(number)
    }

    override fun onVerifyOtpClicked(code: String?) {
        validateOtp(code)
    }

    private lateinit var smsBroadcast: SMSBroadcastReceiver
    var mCredentialsApiClient: GoogleApiClient? = null
    val RC_HINT = 1012


    fun sendOtp(listener: SMSBroadcastReceiver.OTPReceiveListener) {
        smsBroadcast = SMSBroadcastReceiver()
        mCredentialsApiClient = GoogleApiClient.Builder(context)
            .addApi(Auth.CREDENTIALS_API)
            .build()

        requestHint()

        startSMSListener()

        smsBroadcast.init(listener)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

        context.registerReceiver(smsBroadcast, intentFilter)

        //Used to generate hash signature
        //O2KCiTrZWLq
        Logger.e("RegisterController appSignatures ${AppSignatureHelper(context).appSignatures?.toArray()?.contentToString()}")
    }

    private fun startOtpListener() {
        smsBroadcast = SMSBroadcastReceiver()

        val client: SmsRetrieverClient = SmsRetriever.getClient(context)

        client.startSmsRetriever()
            .addOnSuccessListener {
                // otpTxtView.text = "Waiting for OTP"
                Toasty.info(context, "SMS Retriever starts ").show()
            }.addOnFailureListener {
                //  otpTxtView.text = "Cannot Start SMS Retriever"
                Toasty.error(context, "Error " + it?.message, Toast.LENGTH_LONG).show()
            }


        smsBroadcast.init(object : SMSBroadcastReceiver.OTPReceiveListener{
            override fun onOTPReceived(otp: String?) {
                Toasty.info(context, "SMS OTP Received $otp").show()
                observer.otpReceived(otp)
            }

            override fun onOTPTimeOut() {
                Toasty.info(context, "SMS OTP TimeOut").show()
            }

        })

        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

        context.registerReceiver(smsBroadcast, intentFilter)

        //Used to generate hash signature
        //O2KCiTrZWLq
        Logger.e("RegisterController appSignatures ${AppSignatureHelper(context).appSignatures?.toArray()?.contentToString()}")
    }

    private fun startSMSListener() {

        val client: SmsRetrieverClient = SmsRetriever.getClient(context)

        client.startSmsRetriever()
            .addOnSuccessListener {
                // otpTxtView.text = "Waiting for OTP"
                Toasty.info(context, "SMS Retriever starts ").show()
            }.addOnFailureListener {
                //  otpTxtView.text = "Cannot Start SMS Retriever"
                Toasty.error(context, "Error " + it?.message, Toast.LENGTH_LONG).show()
            }


    }

    private fun requestHint() {

        val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
        val intent = Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest)

        try {
            context.startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0)
        } catch (e: Exception) {
            Logger.e("RegisterController Error In getting Msg", e.message)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.e("RegisterController onActivityResult $data")
        if (requestCode == RC_HINT && resultCode == Activity.RESULT_OK) {
            val credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)
            Logger.e("RegisterController credential : $credential")
        }
    }

    fun twilioJwt() {
        //TwilioVerification.getVerificationStatus()
    }


    var selectedCountry: Country? = null

    fun validate(
        firstName: String?,
        lastName: String?,
        email: String?,
        password: String?,
        cPassword: String?,
        city: String?,
        country: String?,
        dob: String?,
        checkBox: Boolean,
        phoneNumber: String?
    ) {
        if (firstName.isNullOrEmpty()) {
            Toasty.error(context, getString(R.string.enter_fname)).show()
            return
        }

        if (lastName.isNullOrEmpty()) {
            Toasty.error(context, getString(R.string.enter_lname)).show()
            return
        }
        if (email.isNullOrEmpty()) {
            Toasty.error(context, getString(R.string.enter_email)).show()
            return
        }
        if (password.isNullOrEmpty()) {
            Toasty.error(context, getString(R.string.enter_password)).show()
            return
        }
        if (cPassword.isNullOrEmpty()) {
            Toasty.error(context, getString(R.string.enter_confirm_password)).show()
            return
        }
        if (city.isNullOrEmpty()) {
            Toasty.error(context, getString(R.string.enter_city)).show()
            return
        }
        if (!checkBox) {
            Toasty.error(context, getString(R.string.accept_term_conditions)).show()
            return
        }
        if (!isDob) {
            Toasty.error(context, getString(R.string.enter_dob)).show()
            return
        }
        if (!isGender) {
            Toasty.error(context, getString(R.string.select_gender)).show()
            return
        }
        if (!isCountry) {
            Toasty.error(context, getString(R.string.select_your_country)).show()
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            Toasty.error(context, getString(R.string.enter_number)).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toasty.warning(context, getString(R.string.email_not_valid)).show()
            return
        }



        if ((password.trim() == cPassword.trim())) {
            if (!isValidPassword(password)) {
                Toasty.warning(context, getString(R.string.password_requirement), Toast.LENGTH_LONG)
                    .show()
                return
            }

            val data = Data(
                firstName, lastName, cPassword, email, gender,
                city, country, dob, ccp!!.selectedCountryCode, ccp!!.number
            )
            register(data = RegisterGuestMember(data))
            //Toasty.success(context, "Successfully registered").show()
            //viewAnimator.showNext()
            //updateNumber()
            //startActivity(Intent(context, MainActivity::class.java))
            return
        } else {
            Logger.e("Password not matched $password == $cPassword")
            Toasty.error(context, getString(R.string.confirm_password_not_matched)).show()
        }
    }

    private fun getString(resId: Int): String {
        return context.getString(resId)
    }

    private var userId: String = ""

    private fun register(data: RegisterGuestMember) {
        context.getDialog()?.show()
        API.request.getApi().register(data).enqueue(object : Callback<RegisterResponse> {

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
                Toasty.error(context, "Failed " + t.message).show()
                t.printStackTrace()
                Logger.e("RegisterActivity : register API ", t)

            }

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                context.getDialog()?.dismiss()
                val data = response.body()
                if (data == null) {
                    Toasty.error(context, getString(R.string.error_occurred)).show()
                    return
                }
                when {
                    data.status == "success" -> {
                        userId = data.member?.userId!!
                        Toasty.success(context, "Successfully Registered $userId")
                            .show()
                        updateNumber(1)
                        isOtp = true
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
                            context,
                            "Error: ${data.errors?.get(0)?.message}"
                        ).show()

                    }
                    else -> Toasty.warning(
                        context,
                        "Register: " + response.body()
                    ).show()
                }
            }

        })

    }

    private fun isValidPassword(s: String): Boolean {
        //val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        val pattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$&+=])(?=\\S+\$).{4,}$")
        //val pattern2 = Pattern.compile("[a-zA-Z0-9!@#$]{8,24}")

        return pattern.matcher(s).matches()
    }

    var isOtpVerified = false
    var isOtp = false
    //private val ringCaptchaController = RingcaptchaAPIController("i2ucy2y2y3any8u5i8uz")

    private fun updateNumber(id: Int) {
        observer.updateNumber(id)
        //viewAnimator.showNext()
        //otpNumber?.text = phoneNumber?.text
        //ccp_otp.setSelectedCountry(selectedCountry)
        //ccpOtp.fullNumber = ccp.fullNumber
        if (id == 1)
            isOtp = true
        else if (id == 2)
            isOtpVerified = true
    }

    private fun sendOtp(number: String?) {
        startOtpListener()
        context.getDialog()?.show()
        val data = SendOTP(userId)
        API.request.getApi().sendOtp(data).enqueue(object : Callback<SendOtpResponse> {

            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
                Toasty.error(context, "Failed " + t.message).show()
                t.printStackTrace()
                Logger.e("RegisterActivity : register API ", t)
            }

            override fun onResponse(
                call: Call<SendOtpResponse>, response: Response<SendOtpResponse>
            ) {
                context.getDialog()?.dismiss()
                val data = response.body()
                if (data == null) {
                    Toasty.error(context, getString(R.string.error_occurred)).show()
                    return
                }
                when {
                    data.status == "success" -> {
                        Toasty.success(
                            context, "OTP Code has been sent successfully", Toast.LENGTH_LONG
                        ).show()
                        updateNumber(2)
                    }
                    data.status == "error" -> {
                        Toasty.error(
                            context, "Error: ${data.errors?.get(0)?.message}"
                        ).show()

                    }
                    else -> Toasty.warning(
                        context,
                        "Register: " + response.body()
                    ).show()
                }
            }
        })

    }

    private fun validateOtp(otp: String?) {
        if (otp == null)
            return
        context.log("validateOtp $otp")
        context.getDialog()?.show()
        val data = VerifyOTP(userId, otp)
        API.request.getApi().verifyOtp(data).enqueue(object : Callback<VerifyOtpResponse> {

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
                Toasty.error(context, "Failed " + t.message).show()
                t.printStackTrace()
                Logger.e("RegisterActivity : register API ", t)
            }

            override fun onResponse(
                call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>
            ) {
                context.getDialog()?.dismiss()
                val data = response.body()
                if (data == null) {
                    Toasty.error(context, getString(R.string.error_occurred)).show()
                    return
                }
                when {
                    data.status == "success" -> {
                        Toasty.success(context, "OTP Verified").show()
                        updateNumber(3)
                    }
                    data.status == "error" -> {
                        Toasty.error(
                            context, "Error: ${data.errors?.get(0)?.message}"
                        ).show()

                    }
                    else -> Toasty.warning(
                        context,
                        "Register: " + response.body()
                    ).show()
                }
            }

        })
    }

    fun loginToHome() {
        // Toasty.success(context, "Successfully registered").show()
        //TODO
        if (isOtpVerified)
            context.startActivity(Intent(context, MainActivity::class.java))
    }

    var isDob = false
    var isGender = false
    var isCountry = false
    var gender = ""
    var ccp: CountryCodePicker? = null
    var otpCcp: CountryCodePicker? = null
    fun showCountry() {
        //ccp.showFullName(true)
        // ccp.showCountryCodePickerDialog
        ccp?.showCountryCodePickerDialog()
    }

    fun showGender() {
        val animals = arrayOf("Male", "Female")
        AlertDialog.Builder(context).setTitle("Select Gender")
            .setItems(animals, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0)
                    gender = "MALE"
                else if (which == 1)
                    gender = "FEMALE"
                observer.onGenderSelect(gender)
                //gender?.text = gender
                isGender = true
            }).create().show()
    }

    fun showDobPicker() {
        SpinnerDatePickerDialogBuilder()
            .context(context)
            .callback { _, year, monthOfYear, dayOfMonth ->
                //tv_dob.setText("$dayOfMonth/$monthOfYear/$year")
                observer.onDobSelect(
                    String.format(
                        "%02d/%02d/%d",
                        dayOfMonth,
                        monthOfYear.plus(1),
                        year
                    )
                )
                //dob?.text = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear.plus(1), year)
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