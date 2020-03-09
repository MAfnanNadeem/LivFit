/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.login

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import life.mibo.hexa.BuildConfig
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.login.LoginResponse
import life.mibo.hexa.models.login.LoginUser
import life.mibo.hexa.database.Database
import life.mibo.hexa.ui.main.MainActivity
import life.mibo.hexa.ui.main.MiboEvent
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
            //usr = "diana@gmail.com"
            usr = "sameerk@gmail.com"
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
            Toasty.info(context, R.string.enter_your_password).show()
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
                                .add(life.mibo.hexa.database.Member.from(data.data!!))
                        }.subscribe()
                        //Toasty.success(context, context.getString(R.string.logged_succes)).show()
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

    fun showError(error: String, editText: EditText?) {
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
}