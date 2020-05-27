/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.database.Database
import life.mibo.android.models.login.LoginResponse
import life.mibo.android.models.login.LoginUser
import life.mibo.android.models.login.SocialLoginUser
import life.mibo.android.ui.main.MainActivity
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginController(val context: LoginActivity) : LoginActivity.Listener {

    override fun onCreate() {

    }

    override fun onStop() {

    }

    override fun onRegister() {
        val intent = Intent(context, RegisterActivity::class.java)
        //intent.putExtra("social_data", Bundle())
        context.startActivity(intent)
    }

    fun onRegister(bundle: Bundle, code: Int) {
        val intent = Intent(context, RegisterActivity::class.java)
        intent.putExtra("social_data_code", code)
        intent.putExtra("social_data", bundle)
        context.startActivity(intent)
    }

    override fun onForgetPassword() {

    }

    private var isLogin = false;

    // SocialHelper 200 :: {"id":"100833147217416230443","displayName":"Sumit Raj","email":"raj8xm@gmail.com","photoUrl":"https:\/\/lh3.googleusercontent.com\/DialogListener-\/AOh14GhC3rsNfjspSF2wztpOX5y2TUChj8k5Hb2XxGzKLg","familyName":"Raj","givenName":"Sumit"}
    //SocialHelper 100 :: {"id":"3174109019295426","first_name":"Sumeet","last_name":"Gehi","email":"sumeetgehi@gmail.com"} -- false
    fun onSocialLogin(code: Int, bundle: Bundle) {
        val email = bundle.getString("email")
        val pwd = bundle.getString("id")
        if (email == null || pwd == null) {
            Toasty.info(context, R.string.auth_error).show()
            return
        }
        val socialType = when (code) {
            100 -> {
                "facebook"
            }
            200 -> {
                "google"
            }
            else -> {
                ""
            }
        }
        context.getDialog()?.show()
        API.request.getApi().socialLoginUser(SocialLoginUser(email, pwd, socialType))
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    context.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(context, R.string.unable_to_connect).show()
                    // if(MiboApplication.DEBUG)
                    //     loginSucceed()
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    context.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            save(email, pwd, "$code", bundle)
                            Observable.just("").observeOn(Schedulers.newThread()).doOnComplete {
                                Database.getInstance(context).memberDao()
                                    .add(life.mibo.android.database.Member.from(data.data!!))
                            }.subscribe()
                            //Toasty.success(context, context.getString(R.string.logged_succes)).show()
                            isLogin = true
                            Prefs.get(this@LoginController.context).member = data.data
                            Prefs.get(this@LoginController.context).set("user_email", email)
                            MiboEvent.loginSuccess(
                                "${data.data?.firstName} - ${data.data?.lastName}", "$email"
                            )



                            loginSucceed()
                        } else if (data.status.equals("error", true)) {
                            onRegister(bundle, code)
                            // Toasty.error(context, "${data.errors?.get(0)?.message}").show()
                        }
                    } else {
                        Toasty.error(context, R.string.error_occurred).show()
                    }
                }
            })
    }

    override fun onLogin(user: String, password: String) {
        //var usr = user;
       // var pwd = password;
//        if (BuildConfig.DEBUG && usr.isEmpty() && pwd.isEmpty()) {
//            //usr = "test@mibo.life"
//            //usr = "christie.ffrench@gmail.com"
//            //usr = "sumeetgehi@gmail.com"
//            usr = "diana@gmail.com"
//            //usr = "sameerk@gmail.com"
//           // usr = "alisher@mibo.life"
//            //pwd = "Qwe123@@"
//            pwd = "123456"
//        }

        if(user.isEmpty())
        {
            Toasty.info(context, context.getString(R.string.enter_username)).show()
            return
        }
        if(password.isEmpty())
        {
            Toasty.info(context, R.string.enter_your_password).show()
            return
        }

        context.getDialog()?.show()
        API.request.getApi().login(LoginUser(user, password)).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                context.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(context, R.string.unable_to_connect).show()
               // if(MiboApplication.DEBUG)
               //     loginSucceed()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                context.getDialog()?.dismiss()

                val data = response.body()
                if (data != null) {
                    if (data.status.equals("success", true)) {
                        if (data.data?.firstLogin == 1) {
                            Prefs.get(this@LoginController.context).member = data.data
                            Prefs.get(this@LoginController.context).set("user_email", user)
                            val intent = Intent(context, RegisterActivity::class.java)
                            intent.putExtra("is_update_profile", true)
                            intent.putExtra("member_email", user)
                            intent.putExtra("member_pwd", password)
                            intent.putExtra("member_id", data.data?.id ?: 0)
                            //intent.putExtra("member_profile", data)
                            context.startActivity(intent)
                            return
                        }
                        save(user, password, "1", null)
                        Observable.just("").observeOn(Schedulers.newThread()).doOnComplete {
                            Database.getInstance(context).memberDao()
                                .add(life.mibo.android.database.Member.from(data.data!!))
                        }.subscribe()
                        //Toasty.success(context, context.getString(R.string.logged_succes)).show()
                        isLogin = true
                        Prefs.get(this@LoginController.context).member = data.data
                        Prefs.get(this@LoginController.context).set("user_email",user)
                        MiboEvent.loginSuccess(
                            "${data.data?.firstName} - ${data.data?.lastName}", "$user"
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

    private fun save(user: String, password: String, social: String, bundle: Bundle? = null) {
        try {
            val prefs = Prefs.getEncrypted(context)
            prefs.create()
            prefs.set("user_email", user, true)
            prefs.set("user_password", password, true)
            prefs.set("user_social", social, true)
            prefs.set("login_enable", "true", true)
            // if (bundle != null)
            //     prefs.set("user_social", bundle.toString(), true)
        } catch (e: Exception) {

        }
    }

    fun autoLogin(): Boolean {
        try {
            val prefs = Prefs.getEncrypted(context)
            prefs.create()
            val isLogin = prefs.get("login_enable", "false", true)
            val user = prefs.get("user_email", "", true)
            val pwd = prefs.get("user_password", "", true)
            val social = prefs.get("user_social", "2", true)
            if (java.lang.Boolean.parseBoolean(isLogin) && user != null && pwd != null) {
                val i = social!!.toInt()
                if (i == 100 || i == 200) {
                    // onSocialLogin(i, Bundle())
                    return true;
                }
                onLogin(user, pwd)
                return true
            }
        } catch (e: Exception) {
            MiboEvent.log(e)
        }
        return false
    }

    private fun loginSucceed() {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("from_user_int", 7)
        context.startActivity(intent)
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