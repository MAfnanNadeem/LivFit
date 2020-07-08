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
    }


    private fun showWeightDialog() {
        var options = arrayOf(getString(R.string.kilograms), getString(R.string.pounds))

        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setSingleChoiceItems(options, 0) { dialog, which ->
            val text =
                if (which == 0) getString(R.string.kilograms) else getString(R.string.pounds)
            tv_weight_unit?.text = text
        }.setPositiveButton(R.string.ok_button) { dialog, which ->
            // save
        }
        builder.show()
    }

    private fun showHeightDialog() {
        var options = arrayOf(getString(R.string.centimeters), getString(R.string.feet_inches))

        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setSingleChoiceItems(options, 0) { dialog, which ->
            val text =
                if (which == 0) getString(R.string.centimeters) else getString(R.string.feet_inches)
            tv_measure_unit?.text = text
        }.setPositiveButton(R.string.ok_button) { dialog, which ->
            // save
        }
        builder.show()
    }

}