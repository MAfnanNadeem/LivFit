package life.mibo.hexa.ui.base

import life.mibo.hexa.ui.ch6.Channel6Fragment
import life.mibo.hexa.ui.devices.DeviceScanFragment

public class ScreenNavigator(fragmentHelper: FragmentHelper) {

    private var helper: FragmentHelper? = fragmentHelper

    fun toChannel6() {
        helper?.replaceFragmentAndClearBackstack(Channel6Fragment())
    }

    fun toScanDevices() {
        helper?.replaceFragmentAndClearBackstack(DeviceScanFragment())
    }

    fun navigateUp() {
        helper?.navigateUp()
    }
}