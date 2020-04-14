/*
 *  Created by Sumeet Kumar on 2/2/20 8:34 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/2/20 8:17 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.login

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import life.mibo.android.R

class OTPDialog(
    c: Context, var country: Int, var number: String, val listener: Listener?
) : AlertDialog(c) {

    public interface Listener {
        fun onClick(button: Int)
    }

   // var textView: TextView? = null
    //var messageView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_number_dialog)
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
       // textView = findViewById(R.id.tv_title)
      //  messageView = findViewById(R.id.tv_message)
        val ccp: CountryCodePicker? = findViewById(R.id.ccp_otp)
        val et_number: EditText? = findViewById(R.id.et_number)
        val yes: TextView? = findViewById(R.id.tv_yes)
        val no: TextView? = findViewById(R.id.tv_no)

       ccp?.setDefaultCountryUsingPhoneCodeAndApply(country)
       et_number?.setText(number)

        yes?.setOnClickListener {
            listener?.onClick(2)
            dismiss()
        }

        no?.setOnClickListener {
            listener?.onClick(1)
            dismiss()
        }
        super.setCancelable(false)
    }


}