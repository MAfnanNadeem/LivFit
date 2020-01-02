package life.mibo.hexa.ui.login

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import life.mibo.hexa.BuildConfig
import life.mibo.hexa.MainActivity
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.models.login.LoginResponse
import life.mibo.hexa.models.login.LoginUser
import life.mibo.hexa.ui.base.BaseActivity
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login?.setOnClickListener {
            login(et_username?.text.toString(), et_password?.text.toString())
        }

        btn_register?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    //    {
//        "status": "error",
//        "error": {
//        "code": 404,
//        "message": "Email or password incorrect"
//    }
//    }
    // private var dialog : KProgressHUD?
    private var isLogin = false;
    fun login(user: String, password: String){
        var usr = user;
        var pwd = password;
        if(BuildConfig.DEBUG && usr.isEmpty() && pwd.isEmpty()){
            usr = "dinesh.kan@gmail.com"
            pwd = "123456"
        }

        getDialog()?.show()
        API.request.getApi().login(LoginUser(usr, pwd)).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                getDialog()?.dismiss()
                Toasty.error(this@LoginActivity, "Unable to connect").show()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                getDialog()?.dismiss()

                val data = response.body()
                if (data != null) {
                    if (data.status.equals("success", true)) {
                        Toasty.success(this@LoginActivity, "Successfully logged").show()
                        isLogin = true
                        loginSucceed()
                    } else if (data.status.equals("error", true)) {
                        Toasty.success(this@LoginActivity, "${data.error?.get(0)}").show()
                    }
                } else {
                    Toasty.success(this@LoginActivity, R.string.error_occurred).show()
                }

            }

        })


    }

    private fun loginSucceed() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }
}