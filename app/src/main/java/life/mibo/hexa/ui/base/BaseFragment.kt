package life.mibo.hexa.ui.base

import android.view.View
import androidx.fragment.app.Fragment
import life.mibo.hardware.core.Logger
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

    override fun onStart() {
        super.onStart()
//        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
     //   EventBus.getDefault().unregister(this)
    }

    interface BaseListener {
        fun onCreate(view: View? = null, data: Any? = null)
        fun onResume()
        fun onStop()
    }
}
