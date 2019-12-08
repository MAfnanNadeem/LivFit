package life.mibo.hexa.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import life.mibo.hexa.MainActivity
import life.mibo.hexa.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }

    }
}