package life.mibo.android.ui.base

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import life.mibo.android.models.base.BaseResponse
import life.mibo.android.ui.dialog.MyDialog
import life.mibo.android.ui.login.LoginActivity
import life.mibo.android.ui.main.MainActivity
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
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
            mDialog = MyDialog.get(this@BaseFragment.requireContext())
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

    fun <V : BaseResponse<*>> checkSession(data: V?): Boolean {
        try {
            data?.errors?.get(0)?.code?.let { code ->
                if (code == 401) {
                    context?.let {
                        Toasty.error(it, data.errors?.get(0)?.message!!).show()
                    }
                    MiboEvent.log("Session Expired - $data")
                    logout()
                    return false
                }
            }
        } catch (e: java.lang.Exception) {

        }
        MiboEvent.log("error $data")
        return true
    }

    fun logout() {
        try {
            startActivity(Intent(this@BaseFragment.context, LoginActivity::class.java))
            activity?.finish()
        } catch (e: Exception) {
            MiboEvent.log(e)
        }
    }

}
