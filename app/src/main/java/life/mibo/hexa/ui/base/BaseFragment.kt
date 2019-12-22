package life.mibo.hexa.ui.base

import androidx.fragment.app.Fragment
import life.mibo.hardware.core.Logger
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment : Fragment() {

    fun log(msg: String) {
        Logger.e(this.javaClass.name + " : " + msg)
    }

    fun log(msg: String, t: Throwable) {
        Logger.e(this.javaClass.name + " : " + msg, t)
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