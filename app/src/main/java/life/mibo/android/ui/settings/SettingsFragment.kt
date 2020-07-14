/*
 *  Created by Sumeet Kumar on 5/12/20 12:26 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/12/20 12:26 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.fragment_settings.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment

class SettingsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_measure?.setOnClickListener {
            showHeightDialog()
        }
        tv_weight?.setOnClickListener {
            showWeightDialog()
        }

        weight = getPref()?.weightPrefs ?: 0
        height = getPref()?.heightPrefs ?: 0

        if (height > 1)
            height = 0
        if (weight > 1)
            weight = 0

    }

    var prefs: Prefs? = null

    fun getPref(): Prefs? {
        if (prefs == null)
            prefs = Prefs.getTemp(context)
        return prefs
    }


    var weight = -1
    var height = -1

    private fun showWeightDialog() {
        val options = arrayOf(getString(R.string.kilograms), getString(R.string.pounds))
        if (weight > 1)
            weight = 0
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setSingleChoiceItems(options, weight) { dialog, which ->
            weight = which

        }.setPositiveButton(R.string.ok_button) { dialog, which ->
            // save
            val text =
                if (weight == 0) getString(R.string.kilograms) else getString(R.string.pounds)
            tv_weight_unit?.text = text
            getPref()?.weightPrefs = weight
        }
        builder.show()
    }

    private fun showHeightDialog() {
        var options = arrayOf(getString(R.string.centimeters), getString(R.string.feet_inches))
        if (height > 1)
            height = 0
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setSingleChoiceItems(options, height) { dialog, which ->
            height = which
        }.setPositiveButton(R.string.ok_button) { dialog, which ->
            // save
            val text =
                if (height == 0) getString(R.string.centimeters) else getString(R.string.feet_inches)
            tv_measure_unit?.text = text
            getPref()?.heightPrefs = height
        }
        builder.show()
    }

}