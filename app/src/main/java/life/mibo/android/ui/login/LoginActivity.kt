package life.mibo.android.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import com.bumptech.glide.Glide
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.database.Database
import life.mibo.android.social.SocialHelper
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import java.util.concurrent.TimeUnit


class LoginActivity : BaseActivity() {

    interface Listener {
        fun onCreate()
        fun onLogin(user: String, password: String, autoLogin: Boolean)
        fun onRegister()
        fun onForgetPassword()
        fun onStop()
    }

    private lateinit var controller: LoginController
    private var isPwd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        log("onCreate")
        setSplash()
        controller = LoginController(this)

        btn_login?.setOnClickListener {
            login()
        }

        btn_register?.setOnClickListener {
            controller.onRegister()
        }

        tv_forget?.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        clear()

        et_password?.setOnKeyListener { v, keyCode, event ->
            //showToast("setOnKeyListener $keyCode")
            log("et_password EditorInfo.IME_ACTION_DONE $keyCode $event")
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                login()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        et_password?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
                return@OnEditorActionListener true
            }
            false
        })
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

        if (Utils.isConnected(this)) {
            isAutologin = controller.autoLogin()
            log("autoLogin")
        } else {
            Toasty.snackbar(btn_register, R.string.unable_to_connect)
        }
        if (DEBUG) {
            log("DEBUG MODE")
            btn_login?.setOnLongClickListener {
               //controller.onLogin("sumeetgehi@gmail.com", "Qwe123@@")
                controller.onLogin("sumeet.kumar@mibo.life", "123Qwe@@", false)
                //controller.onLogin("alisher@mibo.life", "123456")
                return@setOnLongClickListener true
            }
        }

        if (Prefs.getTemp(this).get("body_measure")?.toLowerCase() == "skip") {
            Prefs.getTemp(this).set("body_measure", "")
        }

        socialHelper = SocialHelper(SocialHelper.Listener { type, response, error ->
            log("SocialHelper $type :: $response -- $error")
            if (error) {
                try {
                    val msg = response?.getString("message") ?: getString(R.string.auth_error)
                    Toasty.snackbar(btn_register, msg)
                } catch (e: Exception) {
                    Toasty.snackbar(btn_register, getString(R.string.auth_error))
                }
            } else {

                controller?.onSocialLogin(type, response, false,
                    View.OnClickListener {
                        if (type == SocialHelper.FACEBOOK) {
                           // socialHelper?.facebookLogout()
                        } else if (type == SocialHelper.GOOGLE) {
                            socialHelper?.googleLogout()
                        }
                    })
            }
            //log("Social toast")
        })

        btn_facebook?.setOnClickListener {
            socialHelper?.facebookLogin(this)
        }
        btn_google?.setOnClickListener {
            socialHelper?.googleLogin(this)

        }
        btn_twitter?.setOnClickListener {
            // socialHelper?.withTwitter(this)

        }

        var em = intent?.getStringExtra("user_email") ?: ""
        if (em.isNotEmpty())
            et_username?.setText(em)

        //Toasty.info(this, "SDK " + Build.VERSION.SDK_INT).show()
       // videoBg()
    }

    var isAutologin = false
    private fun setSplash() {
        //Glide.with(this).asGif().load(R.drawable.mibo_livfit_giff).into(splashImage3)

        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_image_from_right)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                stopAnim()
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
        splashLayout?.visibility = View.VISIBLE
        splashImage.animation = AnimationUtils.loadAnimation(this, R.anim.slide_image_from_left)
        splashImage2.animation = anim
    }

    fun stopAnim() {
        Single.just("0").delay(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (!isAutologin)
                    splashLayout?.visibility = View.GONE
            }.doOnError {
                if (!isAutologin)
                    splashLayout?.visibility = View.GONE
            }.subscribe()

    }

    fun hideSplashView() {
        runOnUiThread {
            splashImage?.clearAnimation()
            splashImage2?.clearAnimation()
            splashLayout?.visibility = View.GONE
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        try {
            val em = intent?.getStringExtra("user_email") ?: ""
            if (em.isNotEmpty())
                et_username?.setText(em)
        } catch (e: java.lang.Exception) {

        }

    }


    var socialHelper: SocialHelper? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        socialHelper?.onActivityResult(requestCode, resultCode, data)

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

        controller.onLogin(et_username?.text.toString(), et_password?.text.toString(), false)
    }

    fun debug() {
        // usr = "alisher@mibo.life"
        et_username?.setText("diana@gmail.com")

        et_password?.setText("123456")
    }

    fun clear(){
        log("clear db")
        Prefs.get(this).clear()
        Database.getInstance(this).clearAll()
    }

    override fun onPause() {
        socialHelper?.onPause(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        socialHelper?.onResume(this)
    }

    override fun onStop() {
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