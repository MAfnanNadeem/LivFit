/*
 *  Created by Sumeet Kumar on 4/27/20 8:42 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/26/20 12:52 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.BodyShapeAdapter
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.hardware.core.Logger
import life.mibo.views.body.picker.RulerValuePicker
import life.mibo.views.body.picker.RulerValuePickerListener


class MeasureBodyDialog(
    var data: BodyShapeAdapter.Item,
    var listner: ItemClickListener<BodyShapeAdapter.Item>,
    var unitType: String = ""
) :
    DialogFragment() {

    //var unit: String = "cm"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_body_shape_dialog, container, false)
    }

    var toggleButton: ToggleButton? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var title: TextView? = view?.findViewById(R.id.tv_title)
        var weight: TextView? = view?.findViewById(R.id.tv_weight)
        var image: ImageView? = view?.findViewById(R.id.imageView)
        var done: View? = view?.findViewById(R.id.btn_done)
        toggleButton = view?.findViewById(R.id.toggleButton)
        title?.text = data.title
        image?.setImageResource(data?.imageRes)
        isCancelable = false
        var rulerValuePicker: RulerValuePicker? = view?.findViewById(R.id.rulerValuePicker)

        if (unitType.toLowerCase().contains("cm")) {
            toggleButton?.isChecked = false
            rulerValuePicker?.setMinMaxValue(data.minValue, data.maxValue)
            data.unit = "cm"
        } else {
            toggleButton?.isChecked = true
            rulerValuePicker?.setMinMaxValue(
                Calculate.cmToInch(data.minValue)
                    .toInt(),
                Calculate.cmToInch(data.maxValue)
                    .toInt()
            )
            data.unit = "inches"
        }

        weight?.text = "${Calculate.cmToInch(data.defValue).toInt()} ${data.unit}"
        //rulerValuePicker?.setMinMaxValue(data.minValue, data.maxValue)
        rulerValuePicker?.selectValue(
            Calculate.cmToInch(
                data.defValue
            ).toInt())
        rulerValuePicker?.setValuePickerListener(object : RulerValuePickerListener {
            override fun onValueChange(feetValue: String?) {

            }

            override fun onValueChange(selectedValue: Int) {
                weight?.text = "$selectedValue ${data.unit}"
                data.value = "$selectedValue"
//                if (data.unit.contains("cm"))
//                    data.value = "$selectedValue"
//                else data.value = "${Calculate.inchToCm(selectedValue.toDouble()).toInt()}"
            }

            override fun onIntermediateValueChange(feetValue: String?) {

            }

            override fun onIntermediateValueChange(selectedValue: Int) {
                weight?.text = "$selectedValue ${data.unit}"
            }

        })

        toggleButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            Logger.e("toggleButton clicked $isChecked")
            if (isChecked) {
                rulerValuePicker?.setMinMaxValue(
                    Calculate.cmToInch(
                        data.minValue
                    ).toInt(),
                    Calculate.cmToInch(
                        data.maxValue
                    ).toInt()
                )
                data.unit = "inches"
            } else {
                rulerValuePicker?.setMinMaxValue(data.minValue, data.maxValue)
                // rulerValuePicker?.refreshInchesToCm()
                data.unit = "cm"
            }
        }

        done?.setOnClickListener {
            listner?.onItemClicked(data, rulerValuePicker?.currentValue ?: 0)
            dismiss()
        }

        Logger.e("MeasureBodyDialog onCreate $data")

    }


    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog?.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)

    }
}