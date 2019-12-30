package life.mibo.hexa.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import life.mibo.hexa.BuildConfig
import life.mibo.hexa.R


class LoginActivity : AppCompatActivity() {

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

   // private var dialog : KProgressHUD?
    fun login(user: String, password: String){
        var usr = user;
        var pwd = password;
        if(BuildConfig.DEBUG && usr.isEmpty() && pwd.isEmpty()){
            usr = "dinesh.kan@gmail.com"
            pwd = "123456"
        }





    }
}