package life.mibo.hexa.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register_page1.*
import kotlinx.android.synthetic.main.activity_register_page2.*
import kotlinx.android.synthetic.main.activity_register_page3.*
import life.mibo.hexa.libs.datepicker.SpinnerDatePickerDialogBuilder
import life.mibo.hexa.MainActivity
import life.mibo.hexa.R
import life.mibo.hexa.utils.Toasty
import android.content.DialogInterface
import android.widget.Toast


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
            //validateOtp()
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
        ccp.setOnCountryChangeListener { selectedCountry ->
            tv_country.text = selectedCountry.name
            isCountry = true
        }
        ccp_otp.registerPhoneNumberTextView(et_number)
        ccp_otp.setOnCountryChangeListener { selectedCountry ->
            //tv_country.text = selectedCountry.name
        }
        et_otp.setOnCompleteListener {
            Toasty.success(this, "OTP Verified").show()
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }

    }


    fun validate() {
        if (et_first_name.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, "Please enter First Name").show()
            return
        }
        if (et_last_name.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, "Please enter Last Name").show()
            return
        }
        if (et_email.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, "Please enter Email").show()
            return
        }
        if (et_password.text?.isEmpty()!!) {
            Toasty.error(this, "Please enter Password").show()
            return
        }
        if (et_confirm_password.text?.toString()?.isEmpty()!!) {
            Toasty.error(this, "Please enter Confirm Password").show()
            return
        }
        if (et_city.text?.isEmpty()!!) {
            Toasty.error(this, "Please enter City").show()
            return
        }
        if (!checkbox_terms.isChecked) {
            Toasty.error(this, "Please accept terms and conditions").show()
            return
        }
        if (!isDob) {
            Toasty.error(this, "Please enter your Date of Birth").show()
            return
        }
        if (!isGender) {
            Toasty.error(this, "Please select your Gender").show()
            return
        }
        if (!isCountry) {
            Toasty.error(this, "Please select your Country of residence").show()
            return
        }

        if ((et_password.text?.trim()!! == et_confirm_password.text?.trim()!!)) {
            Toasty.success(this, "Successfully registered").show()
            viewAnimator.showNext()
            //startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            return
        } else {
            Toasty.error(this, "Confirm Password not matched").show()
        }

    }

    private fun sendOtp() {
        if (et_number.text?.trim()?.isEmpty()!!) {
            Toasty.error(this, "Please enter Number").show()
            return
        }

        viewAnimator.showNext()
    }

    private fun validateOtp() {
        if (et_otp.text?.trim()?.isEmpty()!! || et_otp.text?.trim()!!.length < 3) {
            Toasty.error(this, "Please enter OTP Code").show()
            return
        }
        viewAnimator.showNext()
        //startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
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
            .callback { view, year, monthOfYear, dayOfMonth ->
                //tv_dob.setText("$dayOfMonth/$monthOfYear/$year")
                tv_dob.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear.plus(1), year))
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