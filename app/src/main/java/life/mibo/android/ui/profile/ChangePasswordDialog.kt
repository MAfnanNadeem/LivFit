/*
 *  Created by Sumeet Kumar on 4/11/20 3:54 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 3:54 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.member.ChangePassword
import life.mibo.android.models.member.ChangePasswordResponse
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ChangePasswordDialog(c: Context) : AlertDialog(c) {

    var listener: ItemClickListener<Any>? = null
    var tvHint: TextView? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_password)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window?.attributes?.windowAnimations = R.style.DialogBounceAnimation;
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        val btnCancel: View? = findViewById(R.id.button_cancel)
        val btnChange: View? = findViewById(R.id.button_change)
        tvHint = findViewById(R.id.pwd_hint)
        progressBar = findViewById(R.id.progressBar)
        val currentPwd: EditText? = findViewById(R.id.tv_pwd_current)
        val newPwd: EditText? = findViewById(R.id.tv_pwd_new)
        val confirmPwd: EditText? = findViewById(R.id.tv_pwd_confirm)
        //val program: TextView? = findViewById(R.id.programName)

        //val completed: ImageView? = findViewById(R.id.iv_completed)
        setCancelable(false)

        btnCancel?.setOnClickListener {
            dismiss()
            //listener?.onItemClicked(null, 0)
        }

        confirmPwd?.addTextChangedListener {
            tvHint?.visibility = View.GONE
        }

        btnChange?.setOnClickListener {
            if (isEmpty(currentPwd?.text)) {
                updateHint(R.string.enter_current_password)
                return@setOnClickListener
            }
            if (isEmpty(newPwd?.text)) {
                updateHint(R.string.enter_new_password)
                return@setOnClickListener
            }
            if (isEmpty(confirmPwd?.text)) {
                updateHint(R.string.enter_confirm_password)
                return@setOnClickListener
            }

            if (newPwd?.text?.toString().equals(confirmPwd?.text?.toString())) {
                if (isValidPassword(newPwd!!.text.toString()))
                    changePasswordApi(currentPwd?.text?.toString(), confirmPwd?.text?.toString())
                else {
                    updateHint(R.string.pwd_req_not_match)
                }
            } else {
                updateHint(R.string.pwd_not_match)
            }
        }
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

    }

    private fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.trim().isEmpty()
    }

    private fun updateHint(res: Int) {
        tvHint?.text = "* " + context.getString(res)
        tvHint?.visibility = View.VISIBLE
    }

//    private var mDialog: MyDialog? = null
//
//    private fun getDialog(): MyDialog? {
//        if (mDialog == null)
//            mDialog = MyDialog.get(context)
//        return mDialog
//    }

    private fun changePasswordApi(current: String?, newPwd: String?) {
        val member = Prefs.get(context).member ?: return
        progressBar?.visibility = View.VISIBLE
        API.request.getApi()
            .changePassword(
                ChangePassword(
                    ChangePassword.Data(current, newPwd, member.id()),
                    member.accessToken
                )
            )
            .enqueue(object : Callback<ChangePasswordResponse> {
                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    //getDialog()?.dismiss()
                    Toasty.error(context, R.string.unable_to_connect).show()
                    progressBar?.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<ChangePasswordResponse>,
                    response: Response<ChangePasswordResponse>
                ) {
                    // getDialog()?.dismiss()
                    progressBar?.visibility = View.GONE
                    val data = response?.body()?.data
                    Logger.e("onResponse response $response?.body()")
                    if ("success" == response.body()?.status?.toLowerCase()) {
                        var msg = "Your password has been changed successfully"
                        if (data != null && data.isNotEmpty()) {
                            msg = data[0]?.message ?: "Your password has been changed successfully"
                        }
                        Toasty.success(context, msg).show()
                        dismiss()
                    } else if ("error" == response.body()?.status?.toLowerCase()) {
                        Logger.e("onResponse isError....")
                        Logger.e("onResponse errors...." + response.body()?.errors)
                        response.body()?.errors?.get(0)?.message?.let {
                            //Logger.e("onResponse message $it")
                            Toasty.error(context, it).show()
                            Logger.e("onResponse isError message....")
                            //Toasty.snackbar(view, it).show()
                        }
                    }

                    Logger.e("onResponse end....")
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
}