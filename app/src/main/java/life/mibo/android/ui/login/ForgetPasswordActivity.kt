/*
 *  Created by Sumeet Kumar on 5/27/20 3:20 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/27/20 3:20 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import kotlinx.android.synthetic.main.activity_forget_page1.*
import kotlinx.android.synthetic.main.activity_forget_page2.*
import kotlinx.android.synthetic.main.activity_forget_page3.*
import kotlinx.android.synthetic.main.activity_forget_page4.*
import kotlinx.android.synthetic.main.activity_forget_password.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.models.base.ResponseStatus
import life.mibo.android.models.password.CreatePassword
import life.mibo.android.models.password.ForgetPasswordPost
import life.mibo.android.models.password.ForgetPasswordVerifyOtp
import life.mibo.android.models.password.PasswordVerifyOTPResponse
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.regex.Pattern

class ForgetPasswordActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)


        btn_send_otp?.setOnClickListener {
            if (isValidEmail(et_email?.text?.toString())) {
                sendOtp(et_email!!.text!!.toString())
            }
        }
        btn_verify_otp?.setOnClickListener {
            if (!et_otp?.text.isNullOrEmpty()) {
                verifyOtp(et_otp.text!!.toString(), userEmail)
            }
        }
        btn_change_password?.setOnClickListener {
            validateChangePassword()
            //changePage()
        }
        btn_login_now?.setOnClickListener {
            gotoLogin()
        }

        et_otp?.setOnCompleteListener {
            verifyOtp(it, userEmail)
        }
    }

    private var canBack = true
    private var currentPage = 0;
    private var userId = "";
    private var userEmail = "";
    fun changePage(page: Int) {
        viewAnimator?.displayedChild = page
        currentPage = page
    }

    fun changePage() {
        viewAnimator?.showNext()
    }

    fun gotoLogin() {
        val intent = Intent(this@ForgetPasswordActivity, LoginActivity::class.java)
        intent.putExtra("user_email", et_email?.text?.toString())
        startActivity(intent)
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
                changePassword(userId, tv_pwd_new!!.text.toString())
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

    private fun isValidEmail(email: String?): Boolean {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toasty.snackbar(et_email, getString(R.string.email_not_valid))
            return false
        }
        return true
    }

    private fun updateHint(res: Int) {
        pwd_hint?.text = "* " + this.getString(res)
        pwd_hint?.visibility = View.VISIBLE
    }

    private fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.trim().isEmpty()
    }


    fun sendOtp(email: String) {
        getDialog()?.show()
        API.request.getApi().forgotPasswordOTP(ForgetPasswordPost(ForgetPasswordPost.Data(email)))
            .enqueue(object : Callback<ResponseStatus> {
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.error(this@ForgetPasswordActivity, R.string.unable_to_connect).show()

                }

                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    getDialog()?.dismiss()
                    val data = response.body()
                    if (data != null && data.isSuccess()) {
                        canBack = false
                        userEmail = email
                        changePage(1)
                    }
                    else {
                        try {
                            val msg = data?.errors?.get(0)?.message
                            if (msg != null && msg.isNotEmpty()) {
                                Toasty.error(this@ForgetPasswordActivity, msg).show()
                            } else {
                                Toasty.error(this@ForgetPasswordActivity, R.string.error_occurred).show()
                            }

                        } catch (e: Exception){
                            Toasty.error(this@ForgetPasswordActivity, R.string.error_occurred).show()
                        }
                    }
                }

            })
    }

    fun verifyOtp(otp: String, email: String) {
        getDialog()?.show()
        API.request.getApi().forgotPasswordVerifyOTP(
            ForgetPasswordVerifyOtp(
                ForgetPasswordVerifyOtp.Data(
                    email,
                    otp
                )
            )
        )
            .enqueue(object : Callback<PasswordVerifyOTPResponse> {
                override fun onFailure(call: Call<PasswordVerifyOTPResponse>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.error(this@ForgetPasswordActivity, R.string.unable_to_connect).show()
                }

                override fun onResponse(
                    call: Call<PasswordVerifyOTPResponse>,
                    response: Response<PasswordVerifyOTPResponse>
                ) {
                    getDialog()?.dismiss()
                    val data = response.body()
                    if (data != null && data.isSuccess()) {
                        canBack = false
                        userId = data.data?.userID ?: ""
                        changePage(2)
                    }
                    else {
                        try {
                            val msg = data?.errors?.get(0)?.message
                            if (msg != null && msg.isNotEmpty()) {
                                Toasty.error(this@ForgetPasswordActivity, msg).show()
                            } else {
                                Toasty.error(this@ForgetPasswordActivity, R.string.error_occurred).show()
                            }

                        } catch (e: Exception){
                            Toasty.error(this@ForgetPasswordActivity, R.string.error_occurred).show()
                        }
                    }
                }

            })
    }

    fun changePassword(userId: String, password: String) {
        getDialog()?.show()
        API.request.getApi().createPassword(
            CreatePassword(CreatePassword.Data(password, userId))
        )
            .enqueue(object : Callback<ResponseStatus> {
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.error(this@ForgetPasswordActivity, R.string.unable_to_connect).show()
                }

                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    getDialog()?.dismiss()
                    val data = response.body()
                    if (data != null && data.isSuccess()) {
                        changePage(3)
                    }
                    else {
                        try {
                            val msg = data?.errors?.get(0)?.message
                            if (msg != null && msg.isNotEmpty()) {
                                Toasty.error(this@ForgetPasswordActivity, msg).show()
                            } else {
                                Toasty.error(this@ForgetPasswordActivity, R.string.error_occurred).show()
                            }

                        } catch (e: Exception){
                            Toasty.error(this@ForgetPasswordActivity, R.string.error_occurred).show()
                        }
                    }
                }

            })
    }


    override fun onBackPressed() {
        if (canBack)
            super.onBackPressed()
    }
}