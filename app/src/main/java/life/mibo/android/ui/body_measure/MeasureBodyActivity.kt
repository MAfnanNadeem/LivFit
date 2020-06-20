/*
 *  Created by Sumeet Kumar on 4/27/20 8:42 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/26/20 12:52 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.fragment_body_shape_activity.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.body_measure.adapter.BodyShapeAdapter
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.hardware.core.Logger
import life.mibo.views.body.picker.RulerValuePickerListener


class MeasureBodyActivity() : BaseActivity() {


    companion object {
        var CODE = 56321
        fun launch(
            data: BodyShapeAdapter.Item,
            context: androidx.fragment.app.Fragment,
            unitType: String = "",
            selected: Int = 0,
            image: View?,
            title: View?,
            value: View?,
            unit: View?
        ) {
            val intent = Intent(context.context, MeasureBodyActivity::class.java)
            intent.putExtra("data_data", data)
            intent.putExtra("data_unit", unitType)
            intent.putExtra("data_selected", selected)
            try {
                val p1: androidx.core.util.Pair<View, String> =
                    androidx.core.util.Pair.create(image, ViewCompat.getTransitionName(image!!))
                val p2: androidx.core.util.Pair<View, String> =
                    androidx.core.util.Pair.create(title, ViewCompat.getTransitionName(title!!))

                val p3: androidx.core.util.Pair<View, String> =
                    androidx.core.util.Pair.create(value, ViewCompat.getTransitionName(value!!))
                val p4: androidx.core.util.Pair<View, String> =
                    androidx.core.util.Pair.create(unit, ViewCompat.getTransitionName(unit!!))
//                val p1: androidx.core.util.Pair<View, String> =
//                    androidx.core.util.Pair.create(image, "profile_image")
//                val p2: androidx.core.util.Pair<View, String> =
//                    androidx.core.util.Pair.create(title, "profile_title")
//
//                val p3: androidx.core.util.Pair<View, String> =
//                    androidx.core.util.Pair.create(value, "profile_value")
//                val p4: androidx.core.util.Pair<View, String> =
//                    androidx.core.util.Pair.create(unit, "profile_unit")

                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context.requireActivity(), p1, p2, p3, p4
                    )
                context.startActivityForResult(intent, CODE, options.toBundle())

            } catch (e: Exception) {
                context.startActivityForResult(intent, CODE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        //window?.enterTransition = Explode()
        //window?.exitTransition = Explode()
        //window?.exitTransition = TransitionSet().addTransition(Slide())
        //window?.setExitTransition(TransitionSet().addTransition(Slide()))

        setContentView(R.layout.fragment_body_shape_activity)
        data = intent?.getSerializableExtra("data_data") as BodyShapeAdapter.Item?
        unitType = intent?.getStringExtra("data_unit") ?: ""
        selectedValue = intent?.getIntExtra("data_selected", 0) ?: 0
        if (data != null) {
            initViews(data!!)
        } else {
            finish()
        }
    }

    //var unit: String = "cm"


    var unitType: String = ""
    var selectedValue: Int = 0
    var data: BodyShapeAdapter.Item? = null

    //var toggleButton: ToggleButton? = null
    fun initViews(data: BodyShapeAdapter.Item) {

        ViewCompat.setTransitionName(iv_body_image!!, data.transitionImage())
        ViewCompat.setTransitionName(tv_title, data.transitionTitle())
        ViewCompat.setTransitionName(tv_value, data.transitionValue())
        ViewCompat.setTransitionName(tv_value_unit, data.transitionUnit())

//        var title: TextView? = view?.findViewById(R.id.tv_title)
//        var weight: TextView? = view?.findViewById(R.id.tv_weight)
//        var image: ImageView? = view?.findViewById(R.id.imageView)
//        var done: View? = view?.findViewById(R.id.btn_done)
        //toggleButton = view?.findViewById(R.id.toggleButton)
        tv_title?.text = data?.title
        iv_body_image?.setImageResource(data!!.imageRes)
        //isCancelable = false
        // var rulerValuePicker: RulerValuePicker? = view?.findViewById(R.id.rulerValuePicker)

        val cmUnit = getString(R.string.cm_unit)
        if (unitType.toLowerCase().contains("cm") || unitType.toLowerCase().contains(cmUnit)) {
            toggleButton?.isChecked = false
            rulerValuePicker?.setMinMaxValue(data?.minValue, data?.maxValue)
            data?.unit = cmUnit

            if (selectedValue > 1) {
                rulerValuePicker?.selectValue(selectedValue, true)
                tv_value?.text = "$selectedValue"
                tv_value_unit?.text = "${data.unit}"
            } else {
                tv_value?.text = "${data?.defValue}"
                tv_value_unit?.text = "${data.unit}"
                rulerValuePicker?.selectValue(data?.defValue)
            }

        } else {
            toggleButton?.isChecked = true
            rulerValuePicker?.setMinMaxValue(
                Calculate.cmToInch(data?.minValue)
                    .toInt(),
                Calculate.cmToInch(data.maxValue)
                    .toInt()
            )
            data?.unit = getString(R.string.inch_unit)
            //tv_value?.text = "${Calculate.cmToInch(data?.defValue).toInt()} ${data?.unit}"
            if (selectedValue > 1) {
                rulerValuePicker?.selectValue(selectedValue, true)
                tv_value?.text = "$selectedValue"
                tv_value_unit?.text = "${data.unit}"
            } else {
                tv_value?.text = "${Calculate.cmToInch(data?.defValue).toInt()}"
                tv_value_unit?.text = "${data.unit}"
                rulerValuePicker?.selectValue(Calculate.cmToInch(data?.defValue).toInt())
            }
        }


        //rulerValuePicker?.setMinMaxValue(data.minValue, data.maxValue)

        rulerValuePicker?.setValuePickerListener(object : RulerValuePickerListener {
            override fun onValueChange(feetValue: String?) {

            }

            override fun onValueChange(selectedValue: Int) {
                tv_value?.text = "$selectedValue"
                tv_value_unit?.text = "${data.unit}"
                data.value = "$selectedValue"
//                if (data.unit.contains("cm"))
//                    data.value = "$selectedValue"
//                else data.value = "${Calculate.inchToCm(selectedValue.toDouble()).toInt()}"
            }

            override fun onIntermediateValueChange(feetValue: String?) {

            }

            override fun onIntermediateValueChange(selectedValue: Int) {
                tv_value?.text = "$selectedValue"
            }

        })

        toggleButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            Logger.e("toggleButton clicked $isChecked")
            if (isChecked) {
                rulerValuePicker?.setMinMaxValue(
                    Calculate.cmToInch(
                        data?.minValue
                    ).toInt(),
                    Calculate.cmToInch(
                        data?.maxValue
                    ).toInt()
                )
                data?.unit = getString(R.string.inch_unit)
                tv_value?.text = "${Calculate.cmToInch(data?.minValue).toInt()}"
                tv_value_unit?.text = "${data.unit}"
            } else {
                rulerValuePicker?.setMinMaxValue(data.minValue, data.maxValue)
                // rulerValuePicker?.refreshInchesToCm()
                data?.unit = getString(R.string.cm_unit)
                tv_value?.text = "${data.minValue}"
                tv_value_unit?.text = "${data.unit}"
            }
        }

        btn_done?.setOnClickListener {
            //listner?.onItemClicked(data, rulerValuePicker?.currentValue ?: 0)
            val result = Intent()
            result.putExtra("data_result", data)
            result.putExtra("data_result_unit", unitType)
            setResult(Activity.RESULT_OK, result)
            onBackPressed()
            //finish()
        }

        btn_back?.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            //finish()
            onBackPressed()
        }

        Logger.e("MeasureBodyDialog onCreate $data")

    }

    override fun finish() {
        super.finish()
    }

    override fun onStart() {
        super.onStart()
    }
}