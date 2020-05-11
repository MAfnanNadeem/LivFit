/*
 *  Created by Sumeet Kumar on 4/22/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/16/20 10:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import life.mibo.android.ui.body_measure.MeasurementFragment

open class BodyBaseFragment : life.mibo.android.ui.base.BaseFragment() {

    override fun onStart() {
        log("onStart")
        super.onStart()
    }

    override fun onStop() {
        log("onStop")
        super.onStop()
    }
    override fun onResume() {
        log("onResume")
        super.onResume()
        if (isVisible)
            resumed()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        log("setUserVisibleHint $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            resumed()
        }
    }

    open fun resumed() {

    }

    fun updateNextButton(enable: Boolean, title: String = "Continue") {
        log("BodyBaseFragment updateNextButton")
        val frg = parentFragment
        if (frg is MeasurementFragment) {
            frg.updateNext(enable, title)
            return
        }
        val frg2 = parentFragmentManager?.fragments
        if (frg2 != null && frg2.size > 0) {
            for (frg3 in frg2) {
                if (frg3 is MeasurementFragment) {
                    frg3.updateNext(enable, title)
                    return
                }
            }
        }
    }

    fun updateSkipButton(enable: Boolean) {
        log("BodyBaseFragment updateSkipButton")
        val frg = parentFragment
        if (frg is MeasurementFragment) {
            frg.updateSkip(enable)
            return
        }
        val frg2 = parentFragmentManager?.fragments
        if (frg2 != null && frg2.size > 0) {
            for (frg3 in frg2) {
                if (frg3 is MeasurementFragment) {
                    frg3.updateSkip(enable)
                    return
                }
            }
        }
    }

    var test = true
    fun testMode() {
        if (test) {
            updateNextButton(true)
            updateSkipButton(true)
        }
    }
}