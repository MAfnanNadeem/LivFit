package life.mibo.hexa.ui.base

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import life.mibo.hardware.core.Logger
import life.mibo.hexa.models.base.BaseResponse
import life.mibo.hexa.ui.dialog.MyDialog
import life.mibo.hexa.ui.login.LoginActivity
import life.mibo.hexa.ui.main.MainActivity
import life.mibo.hexa.ui.main.MiboApplication
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.utils.Toasty
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment : Fragment() {

    val DEBUG = MiboApplication.DEBUG

    fun log(msg: String) {
        Logger.e(this.javaClass.name + " : " + msg)
    }

    fun log(msg: String, t: Throwable) {
        Logger.e(this.javaClass.name + " : " + msg, t)
    }

    var mDialog: MyDialog? = null

    fun getDialog(): MyDialog? {
        if (mDialog == null)
            mDialog = MyDialog.get(this@BaseFragment.activity!!)
        return mDialog
    }

    var navigator: Navigator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //if(context is Navigator)
        navigator = context as MainActivity
    }

    open fun navigate(type: Int, data: Any?) {
        navigator?.navigateTo(type, data)
    }

    override fun onStart() {
        super.onStart()
//        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        //   EventBus.getDefault().unregister(this)
    }

    open fun onBackPressed(): Boolean {
        return true
    }

    open fun onNavBackPressed(): Boolean {
        return true
    }

    fun register(any: Any) {
        if (!EventBus.getDefault().isRegistered(any))
            EventBus.getDefault().register(any)
    }

    fun unregister(any: Any) {
        EventBus.getDefault().unregister(any)
    }

    fun <V : BaseResponse<*>> checkError(data: V?) {

        if (data?.status.equals("error", true)) {
            data?.errors?.get(0)?.message?.let {
                context?.let {
                    Toasty.error(it, data.errors?.get(0)?.message!!).show()
                }
            }
            MiboEvent.log("error $data")
            if (data?.errors?.get(0)?.code == 401) {
                MiboEvent.log("Session Expired - $data")
                logout()
            }
        }
    }

    fun <V : BaseResponse<*>> checkSession(data: V?) {

        data?.errors?.get(0)?.code?.let { code ->
            if (code == 401) {
                context?.let {
                    Toasty.error(it, data.errors?.get(0)?.message!!).show()
                }
                MiboEvent.log("Session Expired - $data")
                logout()
            }
        }
        MiboEvent.log("error $data")
    }

    private fun logout() {
        try {
            startActivity(Intent(this@BaseFragment.context, LoginActivity::class.java))
        } catch (e: Exception) {
            MiboEvent.log(e)
        }
    }

}
