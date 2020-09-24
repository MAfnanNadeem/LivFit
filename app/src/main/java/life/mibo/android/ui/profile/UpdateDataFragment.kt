/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rilixtech.widget.countrycodepicker.CountryUtils
import kotlinx.android.synthetic.main.fragment_update_data.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.core.security.Encrypt
import life.mibo.android.libs.datepicker.SpinnerDatePickerDialogBuilder
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.utils.Toasty
import life.mibo.hardware.encryption.MCrypt
import java.text.SimpleDateFormat
import java.util.*

class UpdateDataFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_update_data, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setProfile()

        // setHasOptionsMenu(true)
    }

    fun onNextClicked(): Boolean {
        log("onNextClicked")
        if (et_fname?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_fname))
            return false
        }

        if (et_lname?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_lname))
            return false
        }
        if (et_email?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_email))
            return false
        }
        if (!isGender) {
            error(getString(R.string.select_gender))
            return false
        }

        if (tv_dob2?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_dob))
            return false
        }

        if (!isDob) {
            error(getString(R.string.enter_dob))
            return false
        }

        if (et_city?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_city))
            return false
        }

        if (!isCountry) {
            error(getString(R.string.select_your_country))
            return false
        }
        if (countryCode.isEmpty()) {
            error(getString(R.string.select_your_country))
            return false
        }


        val member = UpdateMember(
            et_fname?.text.toString(),
            et_lname?.text.toString(),
            dobUpdate,
            et_city?.text.toString(),
            countryCode, gender
        )
        Prefs.get(context).setJson("update_member", member)

        return true
    }


    private fun setProfile() {
        val member = Prefs.get(context).member ?: return
        et_fname?.setText(member.firstName)
        et_lname?.setText(member.lastName)
        et_email?.setText(Prefs.get(context).get("user_email"))

        try {
            val crypt = Encrypt()
            log("setProfile : name "+member.firstName)
            log("setProfile : crypt2 "+ String(crypt.decrypt(member.firstName)))
            log("setProfile : name "+member.lastName)
            log("setProfile : crypt2 "+ String(crypt.decrypt(member.lastName)))
            log("setProfile : name "+member.dob)
            log("setProfile : crypt2 "+ String(crypt.decrypt(member.dob)))
            log("setProfile : name "+member.numberVerify)
            log("setProfile : crypt2 "+ String(crypt.decrypt(member.numberVerify)))
        }
        catch (e: Exception){
         e.printStackTrace()
        }

        try {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val date = Calendar.getInstance()
            date.time = format?.parse(member.dob)

            dobUpdate = String.format(
                "%02d/%02d/%d",
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH).plus(1),
                date.get(Calendar.YEAR)
            )
            if (member.dob?.length ?: 0 > 1 && dobUpdate.isNotEmpty()) {
                isDob = true
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //dobUpdate = member.dob ?: ""
            //dobUpdate = ""
        }

        tv_dob2?.setText(dobUpdate)
        //et_number.setText(member.contact)
        et_city.setText(member.city)
        tv_country2.setText(member.country)

//        if (member.country?.length ?: 0 > 1 &&  member.countryCode?.length > 1) {
//            isCountry = true
//            //countryCode = member.countryCode
//        }

        //et_email?.keyListener = null
        et_email?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                toastNotEditable()
            }
        }
        gender = member.gender ?: ""

        if(gender.length > 1){
            tv_gender2?.text = gender
            isGender = true
        }
        // et_dob?.isClickable = false
        //et_country?.isClickable = false


        tv_dob1?.setOnClickListener {
            showDobPicker()
        }
        tv_dob2?.setOnClickListener {
            showDobPicker()
        }

        tv_gender2?.setOnClickListener {
            showGender()
        }

        tv_country1?.setOnClickListener {
            ccp_country?.showCountryCodePickerDialog()
        }
        tv_country2?.setOnClickListener {
            ccp_country?.showCountryCodePickerDialog()
        }

        ccp_country?.setOnCountryChangeListener {
            tv_country2?.setText(it.name)
            countryCode = it.iso
            isCountry = true
        }

        //isMale = member.isMale()
        profileUrl = member.profileImg ?: ""
        //Utils.loadImage(userImage, member?.profileImg, member.isMale())

    }

    fun decrypt(){
        var crypt = MCrypt()


    }

    var isGender = false
    var isCountry = false
    var isDob = false
    var gender = ""

    fun showGender() {
        val genders = arrayOf(
            context?.getString(R.string.gender_male),
            context?.getString(R.string.gender_female)
        )
        android.app.AlertDialog.Builder(context)
            .setTitle(context?.getString(R.string.gender_select))
            .setItems(genders) { _, which ->
                if (which == 0)
                    gender = "Male"
                else if (which == 1)
                    gender = "Female"
                tv_gender2?.text = gender
                isGender = true
            }.create().show()
    }

    var countryCode = ""

    var dobUpdate = ""

    // private var isMale = true
    private var profileUrl = ""
    fun showDobPicker() {
        var year = 2000
        var day = 1
        var month = 2
        try {
            val sp = dobUpdate.split("/")
            day = sp.get(0).toIntOrNull() ?: 1
            month = sp.get(1).toIntOrNull() ?: 2
            year = sp.get(2).toIntOrNull() ?: 2000
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        SpinnerDatePickerDialogBuilder()
            .context(context)
            .callback { _, year, monthOfYear, dayOfMonth ->
                //tv_dob.setText("$dayOfMonth/$monthOfYear/$year")
//                String.format(
//                    "%02d/%02d/%d",
//                    dayOfMonth,
//                    monthOfYear.plus(1),
//                    year
//                )
                dobUpdate = String.format(
                    "%02d/%02d/%d",
                    dayOfMonth,
                    monthOfYear.plus(1),
                    year
                )
                tv_dob2?.setText(dobUpdate)
                isDob = true
                //dob?.text = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear.plus(1), year)
            }
            .spinnerTheme(R.style.DatePickerSpinner)
            .showTitle(true)
            .showDaySpinner(true)
            .defaultDate(year, month.minus(1), day)
            .maxDate(2015, 0, 1)
            .minDate(1950, 0, 1)
            .build()
            .show()
    }


    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )


    fun toastNotEditable() {
        Toasty.snackbar(tv_dob2, getString(R.string.not_editable))
    }

    fun error(msg: String) {
        Toasty.snackbar(tv_dob2, msg)
    }


}