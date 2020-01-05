package life.mibo.hexa.ui.login

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseActivity


class LoginActivity : BaseActivity() {

    interface Listener {
        fun onCreate()
        fun onLogin(user: String, password: String)
        fun onRegister()
        fun onForgetPassword()
        fun onStop()
    }

    private lateinit var controller: LoginController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        controller = LoginController(this)

        btn_login?.setOnClickListener {
            controller.onLogin(et_username?.text.toString(), et_password?.text.toString())
        }

        btn_register?.setOnClickListener {
            controller.onRegister()
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

}