/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.login

import android.content.Intent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import life.mibo.hexa.BuildConfig
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.login.LoginResponse
import life.mibo.hexa.models.login.LoginUser
import life.mibo.hexa.room.Database
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.ui.main.MainActivity
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginController(val context: LoginActivity) : LoginActivity.Listener {

    override fun onCreate() {

    }

    override fun onStop() {

    }

    override fun onRegister() {
        context.startActivity(Intent(context, RegisterActivity::class.java))
    }

    override fun onForgetPassword() {

    }

    private var isLogin = false;
    override fun onLogin(user: String, password: String) {
        var usr = user;
        var pwd = password;
        if (BuildConfig.DEBUG && usr.isEmpty() && pwd.isEmpty()) {
            //usr = "test@mibo.life"
            //usr = "christie.ffrench@gmail.com"
            usr = "diana@gmail.com"
           // usr = "alisher@mibo.life"
            pwd = "123456"
        }

        if(usr.isEmpty())
        {
            Toasty.info(context, context.getString(R.string.enter_username)).show()
            return
        }
        if(pwd.isEmpty())
        {
            Toasty.info(context, R.string.enter_password).show()
            return
        }

        context.getDialog()?.show()
        API.request.getApi().login(LoginUser(usr, pwd)).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(context, R.string.unable_to_connect).show()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                context.getDialog()?.dismiss()

                val data = response.body()
                if (data != null) {
                    if (data.status.equals("success", true)) {
                        Observable.just("").observeOn(Schedulers.newThread()).doOnComplete {
                            Database.getInstance(context).memberDao()
                                .add(life.mibo.hexa.room.Member.from(data.data!!))
                        }.subscribe()
                        Toasty.success(context, context.getString(R.string.logged_succes)).show()
                        isLogin = true
                        Prefs.get(this@LoginController.context).member = data.data
                        Prefs.get(this@LoginController.context).set("user_email",usr)
                        MiboEvent.loginSuccess(
                            "${data.data?.firstName} - ${data.data?.lastName}", "$usr"
                        )



                        loginSucceed()
                    } else if (data.status.equals("error", true)) {
                        Toasty.error(context, "${data.errors?.get(0)?.message}").show()
                    }
                } else {
                    Toasty.error(context, R.string.error_occurred).show()
                }
            }
        })
    }

    private fun loginSucceed() {
        context.startActivity(Intent(context, MainActivity::class.java))
        context.finish()
    }
}