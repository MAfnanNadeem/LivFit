package life.mibo.hexa.ui.base

import androidx.fragment.app.Fragment
import life.mibo.hardware.core.Logger

class BaseFragment : Fragment() {

    fun log(msg: String) {
        Logger.e(this.javaClass.name + " : " + msg)
    }
}