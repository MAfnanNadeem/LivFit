package life.mibo.hexa.ui.base

import androidx.fragment.app.Fragment
import life.mibo.hardware.core.Logger

abstract class BaseFragment : Fragment() {

    fun log(msg: String) {
        Logger.e(this.javaClass.name + " : " + msg)
    }

    fun log(msg: String, t: Throwable) {
        Logger.e(this.javaClass.name + " : " + msg, t)
    }
}