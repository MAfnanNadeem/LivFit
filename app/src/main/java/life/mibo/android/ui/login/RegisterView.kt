/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.login

import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.ViewAnimator
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import life.mibo.android.R

class RegisterView {


    lateinit var viewAnimator: ViewAnimator
    lateinit var ccp: CountryCodePicker
    lateinit var ccpOtp: CountryCodePicker
    lateinit var otpCode: EditText
    lateinit var firstName: EditText
    var lastName: EditText? = null
    var password: EditText? = null
    var cPassword: EditText? = null
    var city: EditText? = null
    var email: EditText? = null
    var phoneNumber: EditText? = null
    var otpNumber: EditText? = null
    var country: TextView? = null
    var gender: TextView? = null
    var dob: TextView? = null
    lateinit var checkBox: CheckBox

    fun init(view: View?): RegisterView? {
        if (view == null)
            return null
        firstName = view.findViewById(R.id.et_first_name)
        lastName = view.findViewById(R.id.et_last_name)

        return this
    }
}