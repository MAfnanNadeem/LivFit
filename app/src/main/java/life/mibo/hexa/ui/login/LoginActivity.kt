package life.mibo.hexa.ui.login

import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_login.*
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.room.Database
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
    private var isPwd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        controller = LoginController(this)

        btn_login?.setOnClickListener {
            login()
        }

        btn_register?.setOnClickListener {
            controller.onRegister()
        }

        clear()

        et_password?.setOnKeyListener { v, keyCode, event ->
            //showToast("setOnKeyListener $keyCode")
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                login()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        iv_pwd_visible?.setOnClickListener {
            if (isPwd) {
                iv_pwd_visible?.setImageResource(R.drawable.ic_visibility_off)
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
                et_password?.setSelection(et_password?.length() ?: 0)
                isPwd = false
            } else {
                iv_pwd_visible?.setImageResource(R.drawable.ic_visibility_on)
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                et_password?.setSelection(et_password?.length() ?: 0)
                isPwd = true

            }

        }
//        iv_pwd_visible?.setOnTouchListener { v, event ->
//
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            }
//            if (event.action == MotionEvent.ACTION_UP) {
//                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
//                et_password?.setSelection(et_password?.length() ?: 0)
//            }
//
//            true
//
//        }
        //Database.getInstance(this).clearAllTables()
        // debug()
        if (DEBUG) {
            btn_login?.setOnLongClickListener {
                controller.onLogin(et_username?.text.toString(), et_password?.text.toString())
                return@setOnLongClickListener true
            }
        }
        videoBg()
    }

    private fun videoBg() {
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.login_video)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView?.setOnPreparedListener {
            it.isLooping = true
        }

    }

    private fun login() {
        if (et_username?.text.toString().isEmpty()) {
            controller.showError(getString(R.string.enter_username), et_username)
            et_username?.requestFocus()
            return
        }

        if (et_password?.text.toString().isEmpty()) {
            controller.showError(getString(R.string.enter_your_password), et_password)
            et_password?.requestFocus()
            return
        }

        controller.onLogin(et_username?.text.toString(), et_password?.text.toString())
    }

    fun debug() {
        // usr = "alisher@mibo.life"
        et_username?.setText("diana@gmail.com")

        et_password?.setText("123456")
    }

    fun clear(){
        Prefs.get(this).clear()
        Database.getInstance(this).clearAll()
    }

    override fun onPause() {
        videoView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        videoView?.start()
    }

    override fun onStop() {
        videoView?.stopPlayback()
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
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