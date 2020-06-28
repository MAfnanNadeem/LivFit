/*
 *  Created by Sumeet Kumar on 4/22/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/22/20 10:00 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import android.os.Bundle
import android.util.SparseIntArray
import life.mibo.android.core.toIntOrZero
import life.mibo.android.models.biometric.Biometric
import life.mibo.hardware.core.Logger
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

object Calculate {

    private val bundle = Bundle()
    private val measureData = MeasureData(1)

    fun addValue(key: String, value: String?) {
        bundle.putString(key, value)
    }

    fun addValue(key: String, value: Double) {
        bundle.putDouble(key, value)
    }

    fun addValue(key: String, value: Int) {
        bundle.putInt(key, value)
    }

    fun getValue(key: String, defValue: String): String {
        return bundle.getString(key, defValue)
    }

    fun getData() = bundle
    fun getMeasureData() = measureData

    fun clear() {
        bundle.clear()
        measureData.reset()
        bioData = null
    }

    fun getBioData() = bioData

    private var bioData: Biometric.Data? = null

    fun addBioData(data: Biometric.Data) {
        bioData = data
        //bundle.putParcelable("bio_data", data)
    }

    fun calculateBmi(weight: Double?, heightCm: Double?): Double {
        //Logger.e("calculateBmi weight $weight : height $heightCm")
        //val weight: Double? = weight?.toDoubleOrNull()
        // var bmi: Double = heightCm?.div(100) ?: 1.0
        // bmi = bmi.times(bmi)
        //tv_start_bmi.text = String.format("%.2f", weight?.div(bmi!!))
        // tv_bmi_value.text = String.format("BMI: %.2f", weight?.div(bmi))
        val heightMeter = heightCm?.div(100) ?: 1.0
        val bmi = weight?.div(heightMeter)
        //return weight?.div(bmi) ?: 1.0
        return bmi?.div(heightMeter) ?: 1.0
    }


    fun calculateBmi2(weight: Double?, heightCm: Double?): Double {
        //Logger.e("calculateBmi weight $weight : height $heightCm")
        //val weight: Double? = weight?.toDoubleOrNull()
        var bmi: Double = heightCm?.div(100) ?: 1.0
        bmi = bmi.times(bmi)
        //tv_start_bmi.text = String.format("%.2f", weight?.div(bmi!!))
        // tv_bmi_value.text = String.format("BMI: %.2f", weight?.div(bmi))

        return weight?.div(bmi) ?: 1.0

    }


    fun calculateBmiNew(weight: Double?, height: Double?): Double {
        //val weight: Double? = weight?.toDoubleOrNull()
        var w: Double = weight?.div(
            cmToMeter(
                height
            )
        ) ?: 1.0
        w = w.div(
            cmToMeter(
                height
            )
        )
        //tv_start_bmi.text = String.format("%.2f", weight?.div(bmi!!))
        // tv_bmi_value.text = String.format("BMI: %.2f", weight?.div(bmi))
        return w
    }

    fun cmToInch(cm: Double?): Double {
        return cm?.times(0.3937) ?: 1.0
    }

    fun cmToInch(cm: Int?): Double {
        return cm?.times(0.3937) ?: 1.0
    }

    fun getInt(string: String?): Int {
        return try {
            string!!.toIntOrZero()
        } catch (e: Exception) {
            0
        }
    }

    fun getDouble(string: String?): Double {
        return try {
            string?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun inchToCm(inch: String?): Double {
        return getDouble(inch).times(2.54)
    }

    fun cmToInch(cm: String?): Double {
        return getInt(cm).times(0.3937)
    }

    fun inchToCm(inch: Double?): Double {
        return inch?.times(2.54) ?: 1.0
    }


    fun cmToMeter(cm: Double?): Double {
        return cm?.div(100) ?: 1.0
    }

    fun format2f(value: Double?): String {
        return String.format("%.2f", value)
    }

    fun calculateShape() {
        val data = getData()
        val chest = data.getString("value_1", "0")
        val waist = data.getString("value_2", "0")
        val hips = data.getString("value_3", "0")
        val highHips = data.getString("value_4", "0")

    }

    //value must be in cm
    fun calculateShape(ch: Int, wt: Int, hp: Int, hhp: Int): ArrayList<Int> {
        val type = ArrayList<Int>()
        //var message: String = ""
        log("calculateShape $ch : $wt : $hp : $hhp")
        val bustIn =
            cmToInch(ch)
        val waistIn =
            cmToInch(wt)
        val hipsIn =
            cmToInch(hp)
        val highHipsIn =
            cmToInch(hhp)
        log("calculateShape $bustIn : $waistIn : $hipsIn : $highHipsIn")
//        XType - Hourglass
//        If (bust - hips) ≤ 1" AND (hips - bust) < 3.6" AND (bust - waist) ≥ 9" OR (hips - waist) ≥ 10"
//        Xtype - Bottom hourglass
//                If (hips - bust) ≥ 3.6" AND (hips - bust) < 10" AND (hips - waist) ≥ 9" AND (high hip/waist) < 1.193
//        Xtype  - Top hourglass
//                If (bust - hips) > 1" AND (bust - hips) < 10" AND (bust - waist) ≥ 9"

//        OType - Apple
//        If (hips - bust) > 2" AND (hips - waist) ≥ 7" AND (high hip/waist) ≥ 1.193
//        A Type – Triangle / pear
//                If (hips - bust) ≥ 3.6" AND (hips - waist) < 9"
//        V Type - Inverted triangle
//                If (bust - hips) ≥ 3.6" AND (bust - waist) < 9"
//        I Type - Rectangle
//        If (hips - bust) < 3.6" AND (bust - hips) < 3.6" AND (bust - waist) < 9" AND (hips - waist) < 10"

        // X
        if (bustIn.minus(hipsIn) <= 1 && hipsIn.minus(bustIn) < 3.6 &&
            bustIn.minus(waistIn) >= 9 || hipsIn.minus(bustIn) >= 10
        ) {
            log(" XType - Hourglass")
            type.add(1)
            return type
            //message += "XType Hourglass, "
        }

        if (hipsIn.minus(bustIn) >= 3.6 && hipsIn.minus(bustIn) < 10 &&
            hipsIn.minus(waistIn) >= 9 && highHipsIn.div(waistIn) < 1.193
        ) {
            log(" Bottom hourglass")
            type.add(2)
            //message += "Bottom hourglass, "
            return type
        }

        if (bustIn.minus(hipsIn) > 1 && bustIn.minus(hipsIn) < 10 && (bustIn.minus(waistIn) >= 9)) {
            log(" Top hourglass")
            type.add(3)
            // message += "Top hourglass, "
            return type
        }

        if (hipsIn.minus(bustIn) > 2 && hipsIn.minus(waistIn) >= 7 && (highHipsIn.div(waistIn) >= 1.193)) {
            log(" OType - Apple...")
            type.add(4)
            // message += "OType Apple, "
            return type
        }

        if (hipsIn.minus(bustIn) >= 3.6 && hipsIn.minus(waistIn) < 9) {
            log(" A Type – Triangle / pear...")
            type.add(5)
            //  message += "A Type Triangle / pear,  "
            return type
        }

        if (bustIn.minus(hipsIn) >= 3.6 && bustIn.minus(waistIn) < 9) {
            log(" V Type - Inverted triangle...")
            type.add(6)
            // message += "V Type Inverted triangle, "
            return type
        }

        //        If (hips - bust) < 3.6" AND (bust - hips) < 3.6" AND (bust - waist) < 9" AND (hips - waist) < 10"
        if (hipsIn.minus(bustIn) < 3.6 && bustIn.minus(hipsIn) < 3.6 &&
            bustIn.minus(waistIn) < 9 && hipsIn.div(waistIn) < 10
        ) {
            log("I Type - Rectangle....")
            type.add(7)
            // message += "I Type Rectangle "
            return type
        }
//        addValue(
//            "shape_msg",
//            message
//        )
        log("calculateShape finished...........")
        return type
    }

    var bodyShapePage = 0
    fun getShapeType(shape: Int): String {

        return when (shape) {
            1 -> {
                "X Type Hourglass"
            }
            2 -> {
                "X Type Bottom Hourglass"
            }
            3 -> {
                "X Type Top Hourglass"
            }
            4 -> {
                "O Type - Apple"
            }
            5 -> {
                "A Type - Triangle / pear"
            }
            6 -> {
                "V Type - Inverted triangle"
            }
            7 -> {
                "I Type - Rectangle"
            }
            else -> {
                "Type - Unknown"
            }
        }


    }

    fun log(string: String) {
        Logger.e("Calculate: $string")
    }

    fun format(value: Any?): String {
        return String.format("%.2f", value)
    }

    fun bmiType(bmi: Double): String {
        if (bmi < 18.5)
            return "Underweight "
        if (bmi <= 24.9)
            return "Normal weight  "
        if (bmi <= 29.9)
            return "Overweight  "
        if (bmi <= 35)
            return "Obesity  "
        if (bmi > 35)
            return "Severe obesity  "
        return ""
    }

    fun kgToPounds(weight: Double): Double {
        return weight.times(2.205)
    }

    fun poundToKg(weight: Int?): Int {
        return try {
            (weight?.div(2.205) ?: 0.0).roundToInt()
        } catch (e: Exception) {
            0
        }
    }

    fun round(value: Double?): Double {
        if (value == null)
            return 0.0
        return try {
            BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
        } catch (e: java.lang.Exception) {
            value
        }
    }


    data class MeasureData(var type: Int) {
        var age = 0.0
        var gender = 1
        var weight = 0.0
        var height = 0.0
        var bmi = 0.0

        var chest = 0.0
        var waist = 0.0
        var hips = 0.0
        var highHips = 0.0
        var wrist = 0.0
        var elbow = 0.0
        var forearm = 0.0

        var shapeType = ""
        var goalType = 0
        var activityType = 0

        var hr = 0.0
        var restingHr = 0.0
        var peakHr = 0.0

        fun gender(male: Boolean) {
            gender = if (male) 1 else 2
        }

        fun isMale(): Boolean = gender == 1

        fun heightMeter() = height.div(100)
        private val shapes = SparseIntArray()

        fun question(type: Int, value: Int) {
            if (type == 1)
                goalType = value
            if (type == 2)
                activityType = value
        }

        fun addMeasurement(type: Int, value: Int) {
            shapes.put(type, value)
        }

        fun getMeasurement() = shapes
        fun getMeasurement(type: Int) = shapes.get(type)
        fun addMeasurement(item: BodyShapeAdapter.Item?) {
            item?.let {
                if (item.unit.toLowerCase().contains("cm"))
                    addMeasurement(item.id, getInt(item.value))
                else
                    addMeasurement(item.id, inchToCm(item.value).toInt())
                //data.value = "${Calculate.inchToCm(selectedValue.toDouble()).toInt()}"
            }
        }

        fun getActivityScale(): Double {
            return when (activityType) {
                1 -> 1.2
                2 -> 1.4
                3 -> 1.6
                4 -> 1.75
                5 -> 2.0
                6 -> 2.3
                else -> 1.2
            }
        }

        fun reset() {
            age = 0.0
            weight = 0.0
            height = 0.0
            bmi = 0.0
            chest = 0.0
            waist = 0.0
            hips = 0.0
            highHips = 0.0
            wrist = 0.0
            elbow = 0.0
            forearm = 0.0
            gender = 1
            shapes.clear()
        }


    }

}