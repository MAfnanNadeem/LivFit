/*
 *  Created by Sumeet Kumar on 5/27/20 3:20 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/27/20 3:20 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.login

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_forget_page1.*
import kotlinx.android.synthetic.main.activity_forget_page2.*
import kotlinx.android.synthetic.main.activity_forget_page3.*
import kotlinx.android.synthetic.main.activity_forget_page4.*
import kotlinx.android.synthetic.main.activity_forget_password.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseActivity
import java.util.regex.Pattern

class ForgetPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)


        btn_send_otp?.setOnClickListener {
            changePage()
        }
        btn_verify_otp?.setOnClickListener {
            changePage()
        }
        btn_change_password?.setOnClickListener {
            validateChangePassword()
            //changePage()
        }
        btn_login_now?.setOnClickListener {
            changePage()
        }

        et_otp?.setOnCompleteListener {

        }
    }


    fun changePage(page: Int) {
        viewAnimator?.displayedChild = page
    }

    fun changePage() {
        viewAnimator?.showNext()
    }

    fun validateChangePassword() {
        if (isEmpty(tv_pwd_new?.text)) {
            updateHint(R.string.enter_new_password)
            return
        }
        if (isEmpty(tv_pwd_confirm?.text)) {
            updateHint(R.string.enter_confirm_password)
            return
        }

        if (tv_pwd_new?.text?.toString().equals(tv_pwd_confirm?.text?.toString())) {
            if (isValidPassword(tv_pwd_new!!.text.toString())) {
                changePage()
                //    changePasswordApi(currentPwd?.text?.toString(), confirmPwd?.text?.toString())
            } else {
                updateHint(R.string.pwd_req_not_match)
            }
        } else {
            updateHint(R.string.pwd_not_match)
        }
    }

    private fun isValidPassword(s: String): Boolean {
        //val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        val pattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[DialogListener-z])(?=.*[A-Z])(?=.*[@#\$&+=])(?=\\S+\$).{4,}$")
        //val pattern2 = Pattern.compile("[DialogListener-zA-Z0-9!@#$]{8,24}")

        return pattern.matcher(s).matches()
    }

    private fun updateHint(res: Int) {
        pwd_hint?.text = "* " + this.getString(res)
        pwd_hint?.visibility = View.VISIBLE
    }

    private fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.trim().isEmpty()
    }

    var canBack = true
    override fun onBackPressed() {
        if (canBack)
            super.onBackPressed()
    }
}