/*
 *  Created by Sumeet Kumar on 4/22/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/16/20 10:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import life.mibo.android.ui.body_measure.MeasurementFragment
import life.mibo.android.ui.body_measure.MeasurementFragmentDialog

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
        try {
            log("BodyBaseFragment updateNextButton ")
            val frg = parentFragment
            log("BodyBaseFragment updateNextButton $frg")
            if (frg is MeasurementFragment) {
                frg.updateNext(enable, title)
                return
            }

            if (frg is MeasurementFragmentDialog) {
                log("BodyBaseFragment MeasurementFragmentDialog updateNext")
                frg.updateNext(enable, title)
                return
            }



            val frg2 = parentFragmentManager?.fragments
            log("BodyBaseFragment updateNextButton list $frg2")
            if (frg2 != null && frg2.size > 0) {
                for (frg3 in frg2) {
                    log("BodyBaseFragment updateNextButton2 $frg3")
                    if (frg3 is MeasurementFragment) {
                        frg3.updateNext(enable, title)
                        return
                    }
                }
            }
        } catch (e: Exception) {

        }

    }

    fun updateSkipButton(enable: Boolean, text: String = "") {
        try {
            log("BodyBaseFragment updateSkipButton")
            val frg = parentFragment

            if (frg is MeasurementFragment) {
                frg.updateSkip(enable, text)
                return
            }

            if (frg is MeasurementFragmentDialog) {
                frg.updateSkip(enable)
                return
            }

            val frg2 = parentFragmentManager?.fragments
            if (frg2 != null && frg2.size > 0) {
                for (frg3 in frg2) {
                    if (frg3 is MeasurementFragment) {
                        frg3.updateSkip(enable, text)
                        return
                    }
                }
            }
        } catch (e: java.lang.Exception) {

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