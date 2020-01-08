package life.mibo.hexa.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import life.mibo.hardware.core.Logger
import life.mibo.hexa.Navigator
import life.mibo.hexa.MainActivity
import life.mibo.hexa.ui.dialog.MyDialog

abstract class BaseFragment : Fragment() {

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
}
