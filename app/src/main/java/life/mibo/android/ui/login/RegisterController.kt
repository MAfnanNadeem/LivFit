/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.login

//import android.net.wifi.hotspot2.pps.Credential

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.rilixtech.widget.countrycodepicker.Country
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.libs.datepicker.SpinnerDatePickerDialogBuilder
import life.mibo.android.models.login.LoginResponse
import life.mibo.android.models.login.LoginUser
import life.mibo.android.models.register.Data
import life.mibo.android.models.register.RegisterMember
import life.mibo.android.models.register.RegisterResponse
import life.mibo.android.models.send_otp.SendOTP
import life.mibo.android.models.send_otp.SendOtpResponse
import life.mibo.android.models.verify_otp.VerifyOTP
import life.mibo.android.models.verify_otp.VerifyOtpResponse
import life.mibo.android.receiver.AppSignatureHelper
import life.mibo.android.receiver.SMSBroadcastReceiver
import life.mibo.android.ui.main.MainActivity
import life.mibo.android.ui.main.MessageDialog
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

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
        fun updateView(id: Int)
        fun otpReceived(otp: Intent?)
        fun onTimerUpdate(time: Long)
        fun onValidationError(type: Int)
        fun onInvalidOtp()
    }

    companion object {
        const val REGISTER_VIEW = 1
        const val NUMBER_VIEW = 2
        const val OTP_VIEW = 3
    }

    //lateinit var observer: RegisterObserver
    private var testDebug = false

    override fun onCreate(view: View) {

//        ccp_otp.registerPhoneNumberTextView(et_number)
//        ccp_otp.registerPhoneNumberTextView(et_number)
//        ccp_otp.setOnCountryChangeListener { selectedCountry ->
//            //tv_country.text = selectedCountry.name
//        }
    }
    override fun onStop() {
        try {
            if (smsBroadcast != null)
                context.unregisterReceiver(smsBroadcast)
        } catch (e: java.lang.Exception) {
            MiboEvent.log(e)
        }
        cancelTimer()
    }

    override fun onRegisterClicked(
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
    ) {
        //context.log("onRegisterClicked $firstName, $lastName, $email, $password, $cPassword, $city, $dob, $gender, $phoneNumber")
        //updateNumber(1)
        validate(
            firstName, lastName, email,
            password, cPassword, city,
            country, dob, checkBox, phoneNumber, socialType, socialKey, socialPhoto, areaCode
        )
    }


    var resend = false
    override fun onResendOtpClicked(number: String?) {
        resend = true
        sendOtp(number)
    }

    override fun onSendOtpClicked(number: String?) {
        //updateNumber(2)
        resend = false
        sendOtp(number)
    }

    override fun onVerifyOtpClicked(code: String?) {
        validateOtp(code)
    }

    private var smsBroadcast: SMSBroadcastReceiver? = null

    //var mCredentialsApiClient: GoogleApiClient? = null
    val RC_HINT = 1012
    val OTP_HINT = 2345


//    fun sendOtp(listener: SMSBroadcastReceiver.OTPReceiveListener) {
//        smsBroadcast = SMSBroadcastReceiver()
//        mCredentialsApiClient = GoogleApiClient.Builder(context)
//            .addApi(Auth.CREDENTIALS_API)
//            .build()
//
//        requestHint()
//
//        startSMSListener()
//
//        smsBroadcast.init(listener)
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//
//        context.registerReceiver(smsBroadcast, intentFilter)
//
//        //Used to generate hash signature
//        //O2KCiTrZWLq
//        Logger.e("RegisterController appSignatures ${AppSignatureHelper(context).appSignatures?.toArray()?.contentToString()}")
//    }

//    private fun startSMSListener() {
//
//        val client: SmsRetrieverClient = SmsRetriever.getClient(context)
//
//        client.startSmsRetriever()
//            .addOnSuccessListener {
//                // otpTxtView.text = "Waiting for OTP"
//                Toasty.info(context, "SMS Retriever starts ").show()
//            }.addOnFailureListener {
//                //  otpTxtView.text = "Cannot Start SMS Retriever"
//                Toasty.error(context, "Error " + it?.message, Toast.LENGTH_LONG).show()
//            }
//    }

//    private fun requestHint() {
//
//        val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
//        val intent = Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest)
//
//        try {
//            context.startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0)
//        } catch (e: Exception) {
//            Logger.e("RegisterController Error In getting Msg", e.message)
//        }
//    }

    private fun startOtpListener() {
        context?.log("startOtpListener ::")
        try {
            smsBroadcast = SMSBroadcastReceiver()
            val client: SmsRetrieverClient = SmsRetriever.getClient(context)

            smsBroadcast?.init(object : SMSBroadcastReceiver.OTPReceiveListener {
                override fun onOTPReceived(otp: Intent?) {
                    //Toasty.info(context, "SMS OTP Received $otp").show()
                    observer.otpReceived(otp)
                    context?.log("startOtpListener : onOTPReceived $otp ")
                }

                override fun onOTPTimeOut() {
                    //Toasty.info(context, "SMS OTP TimeOut").show()
                    context?.log("startOtpListener : onOTPTimeOut ")
                }

            })

            client.startSmsUserConsent("MIBO")
                .addOnSuccessListener {
                    // otpTxtView.text = "Waiting for OTP"
                    // Toasty.info(context, "SMS Retriever starts ").show()
                }.addOnFailureListener {
                    //  otpTxtView.text = "Cannot Start SMS Retriever"
//                    Toasty.info(
//                        context,
//                        "SMSBroadcastReceiver Error " + it?.message,
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
                }




            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

            context.registerReceiver(smsBroadcast, intentFilter)

            //Used to generate hash signature
            //O2KCiTrZWLq
            context?.log(
                "RegisterController appSignatures ${AppSignatureHelper(context).appSignatures?.toArray()
                    ?.contentToString()}"
            )
        } catch (e: java.lang.Exception) {
            context?.log("startOtpListener : error > $e")
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.e("RegisterController onActivityResult $data")
        if (requestCode == RC_HINT && resultCode == Activity.RESULT_OK) {
            val credential: Credential? = data!!.getParcelableExtra(Credential.EXTRA_KEY)
            Logger.e("RegisterController credential : $credential")
        }
    }

    fun twilioJwt() {
        //TwilioVerification.getVerificationStatus()
    }


    var selectedCountry: Country? = null

    private fun validate(
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
    ) {
        if (firstName.isNullOrEmpty()) {
            error(getString(R.string.enter_fname))
            return
        }

        if (lastName.isNullOrEmpty()) {
            error(getString(R.string.enter_lname))
            return
        }
        if (email.isNullOrEmpty()) {
            error(getString(R.string.enter_email))
            return
        }
        if (password.isNullOrEmpty()) {
            error(getString(R.string.enter_password))
            return
        }
        if (cPassword.isNullOrEmpty()) {
            error(getString(R.string.enter_confirm_password))
            return
        }
        if (!isGender) {
            error(getString(R.string.select_gender))
            return
        }
        if (!isDob) {
            error(getString(R.string.enter_dob))
            return
        }
        if (city.isNullOrEmpty()) {
            error(getString(R.string.enter_city))
            return
        }

        if (!isCountry) {
            error(getString(R.string.select_your_country))
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            error(getString(R.string.enter_number))
            return
        }
//        if (areaCode.isNullOrEmpty()) {
//            error(getString(R.string.enter_area_number))
//            return
//        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toasty.warning(context, getString(R.string.email_not_valid)).show()
            return
        }

        if (!checkBox) {
            error(getString(R.string.accept_term_conditions))
            return
        }

        if ((password.trim() == cPassword.trim())) {
            if (!isValidPassword(password)) {
                MessageDialog.info(
                    context,
                    context.getString(R.string.pwd_requirement),
                    getString(R.string.password_requirement)
                )
                //Toasty.warning(context, getString(R.string.password_requirement), Toast.LENGTH_LONG, false).show()
                return
            }

            val data = Data(
                firstName, lastName, cPassword, email, gender,
                city,
                country,
                dob,
                ccp!!.selectedCountryCodeWithPlus,
                phoneNumber,
                socialType,
                socialKey, areaCode
            )
            register(RegisterMember(data))

            //Toasty.success(context, "Successfully registered").show()
            //viewAnimator.showNext()
            //updateNumber()
            //startActivity(Intent(context, MainActivity::class.java))
            return
        } else {
            Logger.e("Password not matched $password == $cPassword")
            error(getString(R.string.confirm_password_not_matched))
        }
    }

    private fun showOtpDialog(code: Int) {
        //OTPDialog(context,code, "", null)
    }

    private fun getString(resId: Int): String {
        return context.getString(resId)
    }

    private var userId: String = ""
    private var memberData: RegisterMember? = null

    private fun register(registerData: RegisterMember) {
        MiboEvent.registerEvent(
            "${registerData.data?.firstName} - ${registerData.data?.lastName}",
            "${registerData.data?.email}"
        )
        if (testDebug) {
            error("Test Mode Registered")
            userId = "1236"
            sendOtp(registerData.data!!.phone)
            isOtp = true
            isBack = false
            return
        }
        context.getDialog()?.show()
        API.request.getApi().register(registerData).enqueue(object : Callback<RegisterResponse> {

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
                Toasty.error(context, R.string.unable_to_connect).show()
                t.printStackTrace()
                Logger.e("RegisterActivity : register API ", t)
                MiboEvent.registerFailed("${t?.message}")

            }

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val data = response.body()
                if (data == null) {
                    context.getDialog()?.dismiss()
                    error(getString(R.string.error_occurred))
                    return
                }
                when {
                    data.status?.toLowerCase() == "success" -> {
                        memberData = registerData
                        userId = data.data?.userId!!
                        //success(R.string.regestered)
                        //updateNumber(1)
                        sendOtp(registerData.data!!.phone)
                        isOtp = true
                        //Prefs.get(this@RegisterController.context).member = Member(data)
                        Prefs.get(this@RegisterController.context)
                            .set("user_idd", data.data?.userId)
                        Prefs.get(this@RegisterController.context)
                            .set("user_email", registerData.data?.email)
                        MiboEvent.registerSuccess("${data.data?.userId}")
                        isBack = false
                        return
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
                        error("${data.errors?.get(0)?.message}")
                        MiboEvent.registerError("${data.data?.userId}", "$data")
                    }
                    else -> Toasty.warning(
                        context,
                        "Register: " + response.body()
                    ).show()
                }
                context.getDialog()?.dismiss()
            }

        })

    }

    fun validateInvitedMember(
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
        memberId: String,
        areaCode: String
    ) {
        if (firstName.isNullOrEmpty()) {
            error(getString(R.string.enter_fname))
            return
        }

        if (lastName.isNullOrEmpty()) {
            error(getString(R.string.enter_lname))
            return
        }
        if (email.isNullOrEmpty()) {
            error(getString(R.string.enter_email))
            return
        }
        if (password.isNullOrEmpty()) {
            error(getString(R.string.enter_password))
            return
        }
        if (cPassword.isNullOrEmpty()) {
            error(getString(R.string.enter_confirm_password))
            return
        }
        if (!isGender) {
            error(getString(R.string.select_gender))
            return
        }
        if (!isDob) {
            error(getString(R.string.enter_dob))
            return
        }
        if (city.isNullOrEmpty()) {
            error(getString(R.string.enter_city))
            return
        }

        if (!isCountry) {
            error(getString(R.string.select_your_country))
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            error(getString(R.string.enter_number))
            return
        }
//        if (areaCode.isNullOrEmpty()) {
//            error(getString(R.string.enter_area_number))
//            return
//        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toasty.warning(context, getString(R.string.email_not_valid)).show()
            return
        }

        if (!checkBox) {
            error(getString(R.string.accept_term_conditions))
            return
        }

        if ((password.trim() == cPassword.trim())) {
            if (!isValidPassword(password)) {
                MessageDialog.info(
                    context,
                    context.getString(R.string.pwd_requirement),
                    getString(R.string.password_requirement)
                )
                //Toasty.warning(context, getString(R.string.password_requirement), Toast.LENGTH_LONG, false).show()
                return
            }

            val data = Data(
                firstName, lastName, cPassword, email, gender,
                city,
                country,
                dob,
                ccp!!.selectedCountryCodeWithPlus,
                phoneNumber,
                socialType,
                socialKey, areaCode
            )
            data.memberID = memberId
            context.log("registerInvitedMember memberId $memberId")
            context.log("registerInvitedMember $data")
            registerInvitedMember(RegisterMember(data, "RegisterInvitedMember"), memberId)

            //Toasty.success(context, "Successfully registered").show()
            //viewAnimator.showNext()
            //updateNumber()
            //startActivity(Intent(context, MainActivity::class.java))
            return
        } else {
            Logger.e("Password not matched $password == $cPassword")
            error(getString(R.string.confirm_password_not_matched))
        }
    }

    private fun registerInvitedMember(registerData: RegisterMember, memberId: String) {
        MiboEvent.registerEvent(
            "${registerData.data?.firstName} - ${registerData.data?.lastName}",
            "${registerData.data?.email}"
        )

        //return

        context.getDialog()?.show()
        API.request.getApi().registerInvitedMember(registerData)
            .enqueue(object : Callback<RegisterResponse> {

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    context.getDialog()?.dismiss()
                    Toasty.error(context, R.string.unable_to_connect).show()
                    t.printStackTrace()
                    Logger.e("RegisterActivity : register API ", t)

                }

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val data = response.body()
                    if (data == null) {
                        context.getDialog()?.dismiss()
                        error(getString(R.string.error_occurred))
                        return
                    }
                    when {
                        data.status?.toLowerCase() == "success" -> {
                            memberData = registerData
                            userId = memberId
                            //success(R.string.regestered)
                            //updateNumber(1)
                            sendOtp(registerData.data!!.phone)
                            isOtp = true
                            //Prefs.get(this@RegisterController.context).member = Member(data)
                            Prefs.get(this@RegisterController.context)
                                .set("user_idd", data.data?.userId)
                            Prefs.get(this@RegisterController.context)
                                .set("user_email", registerData.data?.email)
                            MiboEvent.registerSuccess("${data.data?.userId}")
                            isBack = false
                            return
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
                            error("${data.errors?.get(0)?.message}")
                            MiboEvent.registerError("${data.data?.userId}", "$data")
                        }
                        else -> Toasty.warning(
                            context,
                            "Register: " + response.body()
                        ).show()
                    }
                    context.getDialog()?.dismiss()
                }

            })

    }

    private fun isValidPassword(s: String): Boolean {
        //val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        val pattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[DialogListener-z])(?=.*[A-Z])(?=.*[@#\$&+=])(?=\\S+\$).{4,}$")
        //val pattern2 = Pattern.compile("[DialogListener-zA-Z0-9!@#$]{8,24}")

        return pattern.matcher(s).matches()
    }

    var isOtpVerified = false
    var isOtp = false
    //private val ringCaptchaController = RingcaptchaAPIController("i2ucy2y2y3any8u5i8uz")

    private fun updateNumber(id: Int) {
        observer.updateView(id)
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
        if (testDebug) {
            success(R.string.otp_sent, Toast.LENGTH_LONG)
            if (resend) {
                resend = false
                return
            }
            updateNumber(OTP_VIEW)
            return
        }

        startOtpListener()
        context.getDialog()?.show()
       // val data = SendOTP(userId)
        API.request.getApi().sendOtp(SendOTP(userId)).enqueue(object : Callback<SendOtpResponse> {

            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
                Toasty.error(context, R.string.unable_to_connect).show()
                //Toasty.error(context, "Failed " + t.message).show()
                t.printStackTrace()
                Logger.e("RegisterActivity : register API ", t)
            }

            override fun onResponse(
                call: Call<SendOtpResponse>, response: Response<SendOtpResponse>
            ) {
                context.getDialog()?.dismiss()
                val data = response.body()
                if (data == null) {
                    error(getString(R.string.otp_not_sent))
                    return
                }
                when {
                    data.status?.toLowerCase() == "success" -> {
                        success(R.string.otp_sent, Toast.LENGTH_LONG)
                        if (resend) {
                            resend = false
                            return
                        }
                        updateNumber(OTP_VIEW)
                    }
                    data.status?.toLowerCase() == "error" -> {
                        error("${data.errors?.get(0)?.message}")

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
        if (otp.isNullOrEmpty()) {
            error(context.getString(R.string.ent_otp))
            return
        }
        if (otp.length < 4) {
            //error(context.getString(R.string.ent_otp))
            return
        }
        //userId = "139"
        context.log("validateOtp $otp")
        context.getDialog()?.show()
        if (testDebug && otp == "1234") {
            success("Success!")
            //success("OTP Verified")
            //updateNumber(3)
            MiboEvent.otpSuccess("$userId", "$otp")
            loginUser(memberData?.data?.email, memberData?.data?.password)
            return
        }
        val otpData = VerifyOTP(userId, otp)
        API.request.getApi().verifyOtp(otpData).enqueue(object : Callback<VerifyOtpResponse> {

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
               // error("Failed " + t.message)
                error(R.string.unable_to_connect)
                t.printStackTrace()
                Logger.e("RegisterActivity : register API ", t)
            }

            override fun onResponse(
                call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>
            ) {
                val data = response.body()
                if (data == null) {
                    error(getString(R.string.error_occurred))
                    return
                }
                when {
                    data.status?.toLowerCase() == "success" -> {
                        //success("${data.data?.message}")
                        success(context.getString(R.string.number_verified))
                        //updateNumber(3)
                        MiboEvent.otpSuccess("$userId", "$otp")
                        loginUser(memberData?.data?.email, memberData?.data?.password)
                        return
                    }
                    data.status == "error" -> {
                        error("${data.errors?.get(0)?.message}")
                        observer?.onInvalidOtp()
                        try {
                            if (MiboApplication.DEBUG) {
                                if (otp == "1728") {
                                    success(context.getString(R.string.number_verified))
                                    //updateNumber(3)
                                    MiboEvent.otpSuccess("$userId", "$otp")
                                    loginUser(memberData?.data?.email, memberData?.data?.password)
                                }
                            }
                        } catch (e: Exception) {

                        }

                    }
                    else -> Toasty.warning(
                        context,
                        "Register: " + response.body()
                    ).show()

                }
                context.getDialog()?.dismiss()
            }

        })
    }


    private fun loginUser(email: String?, password: String?) {

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            // TODO this should not happen
            context.getDialog()?.dismiss()
            loginToLogin()
            return
        }

        context.getDialog()?.show()
        API.request.getApi().login(LoginUser(email, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    context.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(context, R.string.unable_to_connect).show()
                    loginToLogin()
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    context.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {

                        if (data.status.equals("success", true)) {
                            Toasty.success(context, R.string.registered).show()
                            Prefs.get(this@RegisterController.context).member = data.data
                            Prefs.get(this@RegisterController.context).set("user_email", email)
                            MiboEvent.loginSuccess(
                                "${data.data?.firstName} - ${data.data?.lastName}", "$email"
                            )
                            isOtpVerified = true
                            //updateNumber(3)
                            loginToHome()
                            return
                        } else if (data.status.equals("error", true)) {
                            Toasty.error(context, "${data.errors?.get(0)?.message}").show()
                        }
                    } else {
                        Toasty.error(context, R.string.error_occurred).show()
                    }
                    loginToLogin()
                }
            })
    }

    private fun loginToLogin() {
        context.startActivity(Intent(context, LoginActivity::class.java))
        context.finish()
    }

    fun loginToHome() {
        // Toasty.success(context, "Successfully registered").show()
        //TODO
        if (isOtpVerified) {
            //context.startActivity(Intent(context, MainActivity::class.java))
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("from_user_int", 7)
            context.startActivity(intent)
            context.finish()
        }
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
        val genders = arrayOf(
            context.getString(R.string.gender_male),
            context.getString(R.string.gender_female)
        )
        AlertDialog.Builder(context).setTitle(context.getString(R.string.gender_select))
            .setItems(genders) { _, which ->
                if (which == 0)
                    gender = "Male"
                else if (which == 1)
                    gender = "Female"
                observer.onGenderSelect(gender)
                //gender?.text = gender
                isGender = true
            }.create().show()
    }

    fun updateGender(g: String) {
        gender = g
        observer.onGenderSelect(gender)
        isGender = true
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
            .minDate(1950, 0, 1)
            .build()
            .show()
    }

    private var countTimer: CountDownTimer? = null

    fun startCountDown(seconds: Int) {
        cancelTimer()
        countTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(it: Long) {
                context.log("startTimer $it")
                observer?.onTimerUpdate(it)
            }

            override fun onFinish() {
                observer?.onTimerUpdate(0L)
                cancelTimer()
            }
        }
        countTimer?.start()
    }

    internal fun cancelTimer() {
        countTimer?.cancel()
    }

    fun error(type: Int) {
        observer?.onValidationError(type)
    }

    fun error(msg: String? = "", resId: Int = 0, length: Int = Toast.LENGTH_SHORT) {
        if (!msg.isNullOrEmpty())
            Toasty.error(context, msg, length, false).show()
        else if (resId != 0)
            Toasty.error(context, getString(resId), length, false).show()
    }

    fun success(resId: Int = 0, length: Int = Toast.LENGTH_SHORT) {
        success(getString(resId), length)
    }

    fun success(msg: String? = "", length: Int = Toast.LENGTH_SHORT) {
        if (!msg.isNullOrEmpty())
            Toasty.success(context, msg, length, true).show()
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        userId = savedInstanceState.getString("userId") ?: userId
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString("userId", userId)
    }

    fun showError(error: Int, editText: EditText?) {
        showError(context?.getString(error), editText)
    }

    fun setHint(hint: String?, editText: EditText?) {
        //editText?.hint = Html.fromHtml("<small><small><small>$hint</small></small></small>");
        editText?.hint = Html.fromHtml("<small>$hint</small>");
    }

    fun showError(error: String?, editText: TextView?) {
        editText?.error = error
        editText?.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (editText?.error != null)
                    editText?.error = null
            }
        })
    }

    private var isBack = true
    fun onBackPressed(): Boolean {
        return isBack
    }
}