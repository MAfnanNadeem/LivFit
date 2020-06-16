/*
 *  Created by Sumeet Kumar on 4/15/20 10:15 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 10:15 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_body_bmi.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.body_measure.adapter.BodyBaseFragment
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.utils.Toasty
import life.mibo.views.body.picker.RulerValuePickerListener
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import java.math.BigDecimal
import java.math.RoundingMode

class BMIFragment : BodyBaseFragment() {

    companion object {
        fun create(type: Int): BMIFragment {
            val frg = BMIFragment()
            val arg = Bundle()
            arg.putInt("gender_type", type)
            frg.arguments = arg
            return frg
        }

        const val MAX_WEIGHT_KG = 190
        const val MIN_WEIGHT_KG = 30

        const val MAX_HEIGHT_CM = 200
        const val MIN_HEIGHT_CM = 100

    }

    //private var viewModel: MeasureViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_bmi, container, false)
    }

    var gender = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel = ViewModelProvider(this@ProfilePicFragment).get(MeasureViewModel::class.java)
        gender = arguments?.getInt("gender_type", 1) ?: 1


        if (gender == 1) {
            imageView?.setImageResource(R.drawable.ic_intro_gender_male)
            header?.setImageResource(R.drawable.bg_body_header_male)
            header_bottom?.setImageResource(R.drawable.bg_body_header_male_bottom)
        } else {
            imageView?.setImageResource(R.drawable.ic_intro_gender_female)
            header?.setImageResource(R.drawable.bg_body_header_female)
            header_bottom?.setImageResource(R.drawable.bg_body_header_female_bottom)
        }
        // updateNextButton(false)
        setSpinners()
        setListeners()
    }


    override fun isNextClickable(): Boolean {
        log("BMIFragment isNextClickable called")
        if (!isWeight) {
            Toasty.snackbar(tv_weight, getString(R.string.select_weight))
            return false
        }
        if (!isHeight) {
            Toasty.snackbar(tv_weight, getString(R.string.select_height))
            return false
        }
        return true
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        log("setUserVisibleHint $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            updateNextButton(false)
            updateSkipButton(true, getString(R.string.skip))
            setDefaults()
        }
    }

    override fun onResume() {
        super.onResume()
        updateSkipButton(true, getString(R.string.skip))
    }

    var isHeight = false
    var isWeight = false

    fun updateNextButton() {
        if (isHeight && isWeight)
            updateNextButton(true)
    }

    override fun onStart() {
        super.onStart()
        // updateNextButton(false)
    }

    private fun setListeners() {

        if (gender == 1) {
            rulerValuePicker?.selectValue(65)
        } else {
            rulerValuePicker?.selectValue(50)
        }


        rulerValuePicker?.setValuePickerListener(object : RulerValuePickerListener {
            override fun onValueChange(feetValue: String?) {

            }

            override fun onValueChange(value: Int) {
                selectedWeight = value.toDouble()
                tv_weight?.text = "$value $weightUnit"
                resizeImage(selectedWeight, false)
                // Calculate.addValue("value_weight", selectedWeight)
                isWeight = true
            }

            override fun onIntermediateValueChange(feetValue: String?) {

            }

            override fun onIntermediateValueChange(value: Int) {
                tv_weight?.text = "$value $weightUnit"
            }

        })

        rulerValuePickerHeight?.setValuePickerListener(object : RulerValuePickerListener {

            override fun onValueChange(feetValue: String?) {
                log("rulerValuePickerHeight onValueChange $feetValue :: ${rulerValuePickerHeight?.currentValue}")
                selectedHeight =
                    round(Calculate.inchToCm((rulerValuePickerHeight?.currentValue?.plus(24.0))))
                tv_height?.text = "$feetValue $heightUnit"
                // Calculate.addValue("value_height", selectedHeight)
                log("selectedHeight $selectedHeight")
                resizeImage(selectedHeight, true)
                isHeight = true
                log("rulerValuePickerHeight selectedHeight $selectedHeight")
            }

            override fun onValueChange(value: Int) {
                selectedHeight = value.toDouble()
                tv_height?.text = "$value $heightUnit"
                resizeImage(selectedHeight, true)
                //Calculate.addValue("value_height", selectedHeight)
                isHeight = true
                log("rulerValuePickerHeight selectedHeight $selectedHeight")
            }

            override fun onIntermediateValueChange(feetValue: String?) {
                tv_height?.text = "$feetValue $heightUnit"
            }

            override fun onIntermediateValueChange(value: Int) {
                tv_height?.text = "$value $heightUnit"
            }

        })
    }

    fun round(value: Double): Double {
        return try {
            BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
        } catch (e: java.lang.Exception) {
            value
        }
    }

    private var maxHeight = 190.0;
    private var minHeight = 120.0;
    private var maxWeight = 180.0;
    private var minWeight = 30.0;
    private fun resizeImage(value: Double, height: Boolean = true) {
        if (height) {
//            log("resizeImage: width ${imageView.width} : height ${imageView.height} :: value $value")
//            var percent = (value.minus(minHeight)).div((maxHeight.minus(minHeight)))
//            log("resizeImage: percent $percent")
//            if (percent < 0.3)
//                percent = 0.3
//            imageView?.scaleX = percent.toFloat()
//            imageView?.scaleY = percent.toFloat()
        } else {
//            log("resizeImage: width ${imageView.width} : height ${imageView.height} :: value $value")
//            var percent = (value.minus(minWeight)).div((maxWeight.minus(minWeight)))
//            log("resizeImage: percent $percent")
//            if (percent < 0.3)
//                percent = 0.3
//            imageView?.scaleX = 1 + percent.toFloat()
//            imageView?.scaleY = 1 + percent.toFloat()
            // imageView?.scaleY = percent.toFloat()
        }
        updateBmi()
        updateNextButton()
    }

    private fun setSpinners() {
        setAgeSpinner()
        setUnitSpinner()
        setWeightSpinner()

        // log("calculateBmi " + Calculate.calculateBmi(68.0, 175.0))
        // log("calculateBmiNew " + Calculate.calculateBmiNew(68.0, 175.0))
    }

    private fun setAgeSpinner() {
        val member = Prefs.get(context).member
        val dob = member?.dob
        if (!dob.isNullOrEmpty()) {
            val dobs = dob.split("-")
            if (dobs.size > 2) {
                age_text_value?.text =
                    "" + Period.between(
                        LocalDate.of(dobs[0].toInt(), dobs[1].toInt(), dobs[2].toInt()),
                        LocalDate.now()
                    ).years;
                Calculate.getMeasureData().age =
                    Calculate.getDouble(age_text_value?.text?.toString())
                //Calculate.addValue("user_age", "${age_text_value?.text}")
                return
            }
        }
        val list = ArrayList<String>()
        for (i in 18..90) {
            list.add("$i")
        }
        val adapter =
            ArrayAdapter<String>(requireContext(), R.layout.list_item_spinner, list)
        spinner_age.adapter = adapter
        adapter.notifyDataSetChanged()
        spinner_age?.visibility = View.VISIBLE
        age_text_value?.visibility = View.GONE
        spinner_age?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerChanged(1, position)
                Calculate.getMeasureData().age =
                    Calculate.getDouble(spinner_age?.selectedItem?.toString())
                //Calculate.addValue("user_age", "${spinner_age?.selectedItem?.toString()}")
            }
        }
    }

    private fun setUnitSpinner() {
        val list = ArrayList<String>()
        list.add(getString(R.string.cm_unit))
        //list.add("Inch")
        list.add(getString(R.string.feet_in_unit))
        val adapter =
            ArrayAdapter<String>(requireContext(), R.layout.list_item_spinner, list)
        spinner_height.adapter = adapter
        adapter.notifyDataSetChanged()
        spinner_height?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerChanged(2, position)
            }
        }
    }

    private fun setWeightSpinner() {
        val list = ArrayList<String>()
        list.add("KG")
        list.add("LBS")
        val adapter =
            ArrayAdapter<String>(requireContext(), R.layout.list_item_spinner, list)
        spinner_weight.adapter = adapter
        adapter.notifyDataSetChanged()
        spinner_weight?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerChanged(3, position)
            }
        }
    }

    fun setDefaults() {
        rulerValuePickerHeight?.postDelayed({
//            rulerValuePickerHeight?.setFeetMode(true)
//            rulerValuePickerHeight?.setMinMaxValue(30, 78)
//            rulerValuePickerHeight?.invalidate()
//            heightUnit = "Ft"
//            heightType = 3
            spinner_height?.setSelection(1)
        }, 500)

        tv_text?.text = ""

    }

    var heightUnit = "cm";
    var weightUnit = "kg";
    var userAge = "cm";
    var weightType = 1;
    var heightType = 1
    fun spinnerChanged(type: Int, position: Int) {
        log("spinnerChanged type: $type : position: $position")
        when (type) {
            1 -> {
                // Age
                when (position) {

                }

            }
            2 -> {
                // Height
                when (position) {
                    0 -> {
                        rulerValuePickerHeight?.setFeetMode(false)
                        rulerValuePickerHeight?.setMinMaxValue(MIN_HEIGHT_CM, MAX_HEIGHT_CM)
                        rulerValuePickerHeight?.invalidate()
                        heightUnit = "cm"
                        heightType = 1
                        tv_height?.text = "$MIN_HEIGHT_CM $heightUnit"
                    }
                    2 -> {
                        rulerValuePickerHeight?.setFeetMode(false)
                        rulerValuePickerHeight?.setMinMaxValue(
                            MIN_HEIGHT_CM.div(2.54).toInt(),
                            MAX_HEIGHT_CM.div(2.54).toInt()
                        )
                        rulerValuePickerHeight?.invalidate()

                        heightUnit = "inches"
                        heightType = 2
                    }
                    1 -> {
                        rulerValuePickerHeight?.setFeetMode(true)
                        rulerValuePickerHeight?.setMinMaxValue(24, 62)
                        rulerValuePickerHeight?.invalidate()
                        heightUnit = "ft/in"
                        heightType = 3
                        tv_height?.text = "4'0 $heightUnit"

                    }
                }
            }
            3 -> {
                if (position == 0) {
                    rulerValuePicker?.post {
                        rulerValuePicker?.setMinMaxValue(MIN_WEIGHT_KG, MAX_WEIGHT_KG)

                    }
                    //rulerValuePicker?.invalidate()
                    weightType = 1
                    weightUnit = "kgs"
                } else if (position == 1) {
                    //(MIN_WEIGHT_KG * 2.205).toInt() + 1
                    rulerValuePicker?.post {
                        rulerValuePicker?.setMinMaxValue(
                            65,
                            (MAX_WEIGHT_KG * 2.205).toInt()
                        )
                    }
                    weightType = 2
                    weightUnit = "lbs"
                }
                // Weight

            }
        }

    }

    var selectedHeight: Double = 0.0
    var selectedWeight: Double = 0.0
    var selectedAge = 0

    private fun getWeight(): Double {
        //log("getWeight weightType $weightType selectedWeight $selectedWeight")
//        try {
//            return tv_weight?.toString()?.replace("[^0-9]".toRegex(), "")?.toDoubleOrNull() ?: 0.0
//        } catch (e: Exception) {
//
//        }
        if (weightType == 2 || weightUnit == "lbs")
            return selectedWeight.div(2.205)
        return selectedWeight.toDouble()
    }

    private fun getHeight(): Double {
        //log("getHeight heightType $heightType selectedHeight $selectedHeight")

//        try {
//            return tv_height?.toString()?.replace("[^0-9]".toRegex(), "")?.toDoubleOrNull() ?: 0.0
//        } catch (e: Exception) {
//
//        }
//        if (heightType == 2)
//            return selectedHeight.times(2.54)
//        if (heightType == 3)
//            return selectedHeight.div(2.205)
        return selectedHeight.toDouble()
    }

    private fun updateBmi() {
       // log("updateBmi ${getWeight()} ${getHeight()} ")
        val bmi = Calculate.calculateBmi(getWeight(), getHeight())
        val bmi2 = Calculate.calculateBmi2(getWeight(), getHeight())
        //log("updateBmi $bmi $bmi2")
       // log("updateBmi ${round(bmi)} ${round(bmi2)}")
//        if (!bmi.isInfinite())
//            tv_text?.text = String.format("BMI %.2f", bmi)
//        else tv_text?.text = String.format("BMI %.2f", 0.0)
        Calculate.getMeasureData().bmi = round(bmi)
        Calculate.getMeasureData().height = getHeight()
        Calculate.getMeasureData().weight = getWeight()
//        Calculate.addValue("user_bmi", String.format("%.2f", bmi))
//        Calculate.addValue("user_weight", "$selectedWeight")
//        Calculate.addValue("user_height", "$selectedHeight")
    }
}