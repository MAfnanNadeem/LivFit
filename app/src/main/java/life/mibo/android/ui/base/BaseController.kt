/*
 *  Created by Sumeet Kumar on 2/4/20 8:23 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/2/20 5:24 PM
 *  Mibo Hexa - app
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import life.mibo.android.models.base.BaseResponse
import life.mibo.android.ui.login.LoginActivity
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty

abstract class BaseController(val context: Activity) {
    abstract fun onStart()
    abstract fun onStop()

    fun <V : BaseResponse<*>> checkError(data: V?) {

        if (data?.status.equals("error", true)) {
            data?.errors?.get(0)?.message?.let {
                Toasty.error(getContext(), it).show()
            }
            MiboEvent.log("saveSessionReport :: error $data")
            if (data?.errors?.get(0)?.code == 401) {
                try {
                    logoutAndLogin()
                } catch (e: Exception) {
                    MiboEvent.log(e)
                }
            }
        }

    }

    fun checkSessionExpired(data: BaseResponse<Any>) {
        if (data.errors?.get(0)?.code == 401) {
            try {
                logoutAndLogin()
            } catch (e: Exception) {
                MiboEvent.log(e)
            }
        }
    }

    private fun logoutAndLogin() {
        context.startActivity(Intent(context, LoginActivity::class.java))
        context.finish()
    }

    fun error(msg: String? = "", resId: Int = 0, length: Int = Toast.LENGTH_SHORT) {
        if (!msg.isNullOrEmpty())
            Toasty.error(context, msg, length, false).show()
        else if (resId != 0)
            Toasty.error(context, context.getString(resId), length, false).show()
    }

    fun success(resId: Int = 0, length: Int = Toast.LENGTH_SHORT) {
        success(context.getString(resId), length)
    }

    fun success(msg: String? = "", length: Int = Toast.LENGTH_SHORT) {
        if (!msg.isNullOrEmpty())
            Toasty.success(context, msg, length, true).show()
    }

    fun getContext(): Context {
        return context
    }
}