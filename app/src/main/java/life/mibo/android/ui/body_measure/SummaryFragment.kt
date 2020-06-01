/*
 *  Created by Sumeet Kumar on 4/22/20 3:13 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/21/20 6:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_body_measure.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.biometric.Biometric
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.body_measure.adapter.SummaryAdapter
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt


class SummaryFragment : BaseFragment() {
    companion object {
        fun create(genderMale: Boolean): SummaryFragment {
            val frg = SummaryFragment()
            val arg = Bundle()
            arg.putBoolean("profile_gender", genderMale)
            frg.arguments = arg
            return frg
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_summary, container, false)
    }

    var selected = -1
    var adapter: SummaryAdapter? = null
    var isMale = false;
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isMale = arguments?.getBoolean("profile_gender", false) ?: false

        val fab = view?.findViewById<View?>(R.id.fab_add)
        fab?.setOnClickListener {
            //Toasty.snackbar(it, "clicked")
            navigate(life.mibo.android.ui.main.Navigator.BODY_MEASURE, null)
        }
    }

    override fun onPlusClicked(): Boolean {
        navigate(life.mibo.android.ui.main.Navigator.BODY_MEASURE, null)
       // showMeasureDialog()
        return false
    }

//    fun setAdapters() {
//        adapter =
//            SummaryAdapter(
//                getSummary(), object : ItemClickListener<SummaryAdapter.Item> {
//                    override fun onItemClicked(
//                        item: SummaryAdapter.Item?, position: Int
//                    ) {
//                        SummaryDetailsDialog(
//                            item,
//                            null
//                        ).show(
//                            childFragmentManager,
//                            "SummaryDetailsDialog"
//                        )
//                        // adapter?.select(item)
//                        // selected = position
//                        //showPicker(item)
//                    }
//
//                })
//        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 3)
//        recyclerView?.layoutAnimation =
//            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down);
//        //recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        recyclerView?.adapter = adapter
//    }



//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        log("setUserVisibleHint $isVisibleToUser")
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            setAdapters()
//            updateNextButton(true, "Finish")
//            Prefs.getTemp(context).set("body_measure", "done")
//            //updateSkipButton(false)
//
//        }
//    }

//    private fun getSummary(): ArrayList<SummaryAdapter.Item> {
//        arguments?.getInt("data_from")
////        val list = ArrayList<SummaryAdapter.Item>()
////        val MyWebViewClient = Calculate.getValue("user_bmi", "0.00")
////        val w = Calculate.getValue("user_weight", "0.00")
////        val h = Calculate.getValue("user_height", "0.00")
////        val DialogListener = Calculate.getValue("user_age", "0.00")
////
////        val ch = Calculate.getValue("value_1", "0")
////        val wst = Calculate.getValue("value_2", "0")
////        val hp = Calculate.getValue("value_3", "0")
////        val hhp = Calculate.getValue("value_4", "0")
////        val elbw = Calculate.getValue("value_5", "0")
////        val wrst = Calculate.getValue("value_6", "0")
//
//        val data = Calculate.getMeasureData()
//        val list = ArrayList<SummaryAdapter.Item>()
//        log("getSummary getMeasureData $data")
//        val chest = data.getMeasurement(1)
//        val waist = data.getMeasurement(2)
//        val hip = data.getMeasurement(3)
//        val highHips = data.getMeasurement(4)
//        //val elbow = data.getMeasurement(5)
//        val wrist = data.getMeasurement(5)
//        val forearm = data.getMeasurement(6)
//        var hr = 0
//        var timeInHours = 0
//
//        val member = Prefs.get(activity).member
//        val male = member?.gender.equals("male", true) ?: false
//
//
//        //val waist = getInt(wst)
//        //val hip = getInt(hp)
//        //val wrist = getInt(wrst)
//
//
//        val age = data.age
//        val height = data.height
//        val weight = data.weight
//        val bmi = data.bmi
//
//        Prefs.get(context)["user_age"] = "$age"
//        Prefs.get(context)["user_gender"] = "${member?.gender}"
//        Prefs.get(context)["user_weight"] = "$weight KG"
//        Prefs.get(context)["user_height"] = "$height CM"
//
//        log("getSummary height $height")
//        log("getSummary weight $weight")
//        log("getSummary age $age")
//        log("getSummary bmi $bmi ")
//        log("getSummary chest $chest")
//        log("getSummary waist $waist")
//        log("getSummary hip $hip")
//        log("getSummary high-hip $highHips")
//        log("getSummary wrist $wrist")
//
//
//        val bmr = getBasalMetabolicRate(male, weight, height, age)
//
//        val ibw = getIdealBodyWeight(bmi, height)
//
//
//        val bsa = getBSA(male, weight, height)
//
//        // val caloriesBurnt = getCaloriesBurntHR(hr, weight, age, timeInHours, male)
//        //val weightLoss = getWeightLoss(caloriesBurnt)
//        val weightLoss = 0.0
//
//        val bodyMass = getBodyMass(weight, height, male)
//
//
//        val bodyFat = getBodyFatPercentage(
//            Calculate.kgToPounds(weight),
//            Calculate.cmToInch(waist),
//            Calculate.cmToInch(wrist),
//            Calculate.cmToInch(hip),
//            Calculate.cmToInch(forearm),
//            male
//        )
//
//
//        val waistHipRatio = waist.div(hip.toDouble())
//
//        val waistHeightRatio = waist.div(height)
//
//        var waterRatio = 0.6
//        if (age > 59)
//            waterRatio = if (male) 0.5 else 0.45
//        val bodyWater = ibw.times(waterRatio)
//        //val bodyWater2 = age.times(waterRatio)
//
//        val fatFreeMass = weight * (1 - (bodyFat.div(100.0)))
//        val heightMeter = height.div(100.0)
//        //  log("Height  $height")
//        //log("Height heightMeter $heightMeter")
//        //log("Height sqrt " + heightMeter.pow(2))
//        val fatFree = fatFreeMass.div(heightMeter.pow(2))
//
//        val physicalActivity = data.getActivityScale()
//        val energy = bmr.times(physicalActivity)
//        saveData(
//            member?.id()!!,
//            member?.accessToken!!,
//            data,
//            round(bmr),
//            round(ibw),
//            round(bsa),
//            round(bodyWater),
//            round(energy),
//            round(fatFree),
//            round(bodyFat.times(100)),
//            round(bodyMass),
//            round(waistHeightRatio),
//            round(waistHipRatio),
//            round(weightLoss)
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1, 0, R.drawable.ic_body_summary_bmi, 0xFF5DCEED.toInt(), "BMI", bmi, ""
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1, 0, R.drawable.ic_body_summary_bmr, 0xFF8BC53F.toInt(), "BMR", bmr, "\nkcal/day"
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1, 0, R.drawable.ic_body_summary_ibw, 0xFF29A3DA.toInt(), "IBW", ibw, "kg"
//            )
//        )
//
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_bsa,
//                ContextCompat.getColor(requireContext(), R.color.bsa_color),
//                "BSA",
//                bsa,
//                getString(R.string.meter_square)
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1, 0,
//                R.drawable.ic_body_summary_wl,
//                0xFF42C3A4.toInt(),
//                "Weight Loss",
//                weightLoss,
//                "kg"
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_mass,
//                0xFFECB581.toInt(),
//                "Body Mass",
//                bodyMass,
//                "kg"
//            )
//        )
//
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_fat,
//                0xFFD99700.toInt(),
//                "Body Fat",
//                bodyFat.times(100),
//                "%"
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_wh_ratio,
//                0xFFFF1D25.toInt(),
//                "Waist Hip Ratio",
//                waistHipRatio
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_height_ratio,
//                0xFF09B189.toInt(),
//                "Waist Height Ratio",
//                waistHeightRatio
//            )
//        )
//
//
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_water,
//                0xFF0071B4.toInt(),
//                "Body Water",
//                bodyWater,
//                "kg"
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_fat_free,
//                0xFFFFB174.toInt(),
//                "Fat Free (FFMI)",
//                fatFree,
//                "\n" + getString(R.string.ffmi_unit)
//            )
//        )
//        list.add(
//            SummaryAdapter.Item(
//                1,
//                0,
//                R.drawable.ic_body_summary_energy,
//                0xFF333333.toInt(),
//                "Energy",
//                energy,
//                "\ncal"
//            )
//        )
//
//        return list
//    }
//
//    private fun getIdealBodyWeight(bmi: Double, height: Double) =
//        2.2.times(bmi) + 3.5.times(bmi) * ((height.div(100.0)).minus(1.5))
//
//    private fun getBasalMetabolicRate(
//        male: Boolean,
//        weight: Double,
//        height: Double,
//        age: Double
//    ): Double {
//        ////BMR (kcal / day)
//        val bmrS = if (male) 5 else -161
//        val bmr = 10.times(weight) + 6.25.times(height) - 5.times(age) + bmrS
//        return bmr
//    }
//
//    private fun getBSA(
//        male: Boolean,
//        weight: Double,
//        height: Double
//    ): Double {
//        ////BMR (kcal / day)
//        if (male)
//            return 0.000579479.times(weight.times(0.38)) * height.times(1.08)
//        return 0.000975482.times(weight.times(0.46)) * height.times(1.24)
//    }
//
//    private fun getCaloriesBurntHR(
//        hr: Int,
//        weight: Double,
//        age: Double,
//        timeInHours: Double,
//        male: Boolean
//    ): Double {
//        //HR = Heart rate (in beats/minute)
//        var calMale =
//            ((-55.0969 + (0.6309.times(hr)) + (0.1988.times(weight)) + (0.2017.times(age))) / 4.184) * 60.times(
//                timeInHours
//            )
//        var calFemale =
//            ((-20.4022 + (0.4472.times(hr)) + (0.1263.times(weight)) + (0.074.times(age))) / 4.184) * 60.times(
//                timeInHours
//            )
//
//        val bsa = if (male) calMale else calFemale
//
//        // Calories burned /7700 = Weight Loss from this exercise
//        return bsa
//    }
//
//    private fun getWeightLoss(calories: Double): Double {
//
//        // Calories burned /7700 = Weight Loss from this exercise
//        return calories.div(7700)
//    }
//
//    private fun getBodyMass(
//        weight: Double,
//        heightCm: Double,
//        male: Boolean
//    ): Double {
//        val leanBodyMassMen = 0.407 * weight + 0.267 * heightCm - 19.2
//        val leanBodyMassWomen = 0.252 * weight + 0.473 * heightCm - 48.3
//
//        return if (male) leanBodyMassMen else leanBodyMassWomen
//    }
//
//    private fun getBodyFatPercentage(
//        weight: Double,
//        waist: Double,
//        wrist: Double,
//        hip: Double,
//        forearm: Double,
//        male: Boolean
//    ): Double {
//        val fatLeanBodyMassMen = (weight * 1.082) + 94.42 - waist * 4.15
//        val fatLeanBodyMassWomen =
//            (weight * 0.732) + 8.987 + wrist / 3.140 - waist * 0.157 - hip * 0.249 + forearm * 0.434
//        val bodyFatWeight =
//            if (male) weight.minus(fatLeanBodyMassMen) else weight.minus(fatLeanBodyMassWomen)
//        log("getBodyFatPercentage $fatLeanBodyMassMen :: $fatLeanBodyMassWomen  -- $bodyFatWeight")
//        //body fat percentage(BFP) = body fat weight / weight
//        //        •	Essential fat: 10–13% (women), 2–5% (men)
//        //        •	Athletes: 14–20% (women), 6–13% (men)
//        //        •	Fitness: 21–24% (women), 14-17% (men)
//        //        •	Average: 25–31% (women), 18–24% (men)
//        //        •	Obese: 32%+ (women), 25%+ (men)
//        val bodyFat = bodyFatWeight.div(weight)
//        log("getBodyFatPercentage $weight :: $waist  -- $male : $wrist")
//        log("getBodyFatPercentage $fatLeanBodyMassMen :: $fatLeanBodyMassWomen  -- $bodyFatWeight : $bodyFat")
//        return bodyFat
//    }


    fun getInt(str: String?): Int {
        return try {
            str?.toInt() ?: 0
        } catch (e: Exception) {
            0;
        }
    }

    fun getInt(str: Double?): Int {
        return try {
            str?.roundToInt() ?: 0
        } catch (e: Exception) {
            0;
        }
    }

    fun doubleToInt(str: String?): Int {
        return try {
            val double = str?.toDouble()
            double?.roundToInt() ?: 0
        } catch (e: Exception) {
            0;
        }
    }

    fun getDouble(str: String?): Double {
        return try {
            str?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0;
        }
    }

    fun getDouble(str: Double?): Double {
        return str ?: 0.0
    }

//    private fun saveData(
//        memberId: String, token: String, data: Calculate.MeasureData,
//        bmr: Double, ibw: Double, bsa: Double, bodyWater: Double,
//        energy: Double, ffmi: Double, bodyFat: Double, leanBodyMass: Double,
//        wHeightRatio: Double, wHipRatio: Double, weightLoss: Double
//    ) {
//
//        getDialog()?.show()
//        //val data = Calculate.getMeasureData();
//        // Math.round()
//        val post = PostBiometric.Data(
//            data.bmi, bmr, bsa, bodyFat, "cm", bodyWater, data.chest,
//            data.elbow, energy, ffmi, data.forearm, data.height, "cm",
//            data.highHips, data.hips, data.waist, data.wrist, data.weight,
//            wHeightRatio, wHipRatio, weightLoss, "cm", ibw,
//            leanBodyMass, "${data.activityType}", "${data.goalType}", "${data.shapeType}", memberId
//        )
//
//        API.request.getApi().saveMemberBiometrics(PostBiometric(post, token))
//            .enqueue(object : retrofit2.Callback<ResponseData> {
//                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
//                    getDialog()?.dismiss()
//                    Toasty.info(requireContext(), R.string.unable_to_connect).show()
//                }
//
//                override fun onResponse(
//                    call: Call<ResponseData>, response: Response<ResponseData>
//                ) {
//                    // getDialog()?.dismiss()
//                    try {
//                        val body = response?.body()
//                        log("saveData response $body")
//                        if (body != null && body.isSuccess()) {
//                            val msg = body.data?.message
//                            msg?.let {
//                                Toasty.info(requireContext(), msg).show()
//                            }
//                        } else if (body != null && body.isError()) {
//                            val msg = body.errors?.get(0)?.message
//                            msg?.let {
//                                Toasty.info(requireContext(), msg).show()
//                            }
//
//                        } else {
//                            Toasty.info(requireContext(), R.string.unable_to_connect).show()
//                        }
//                    } catch (e: Exception) {
//                        MiboEvent.log(e)
//                    }
//
//                    getDialog()?.dismiss()
//
//                }
//
//            })
//
//        //getBioMetric(memberId, token)
//    }

    fun round(value: Double): Double {
        return try {
            BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
        } catch (e: java.lang.Exception) {
            value
        }
    }

    private fun getBioMetric() {
        val member = Prefs.get(activity).member
        Prefs.get(context)["user_gender"] = "${member?.gender}"
        val memberId = member?.id() ?: ""
        val token = member?.accessToken ?: ""
        getDialog()?.show()
        API.request.getApi().getMemberBiometrics(MemberPost(memberId, token, "GetMemberBiometrics"))
            .enqueue(object : retrofit2.Callback<Biometric> {
                override fun onFailure(call: Call<Biometric>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.info(requireContext(), R.string.unable_to_connect).show()
                }

                override fun onResponse(
                    call: Call<Biometric>, response: Response<Biometric>
                ) {
                    getDialog()?.dismiss()
                    try {
                        val body = response?.body()
                        log("getBioMetric response $body")
                        if (body != null && body.isSuccess()) {
                            val list = body.data
                            list.let {
                                Prefs.get(requireContext()).setJson("user_biometric", it)
                                parseBiometric(it)
                            }

                        } else if (body != null && body.isError()) {

                            val msg = body.errors?.get(0)?.message
                            if (msg != null)
                                Toasty.info(requireContext(), msg).show()
                            if (msg?.toLowerCase()?.contains("session expire") == true) {
                                navigate(life.mibo.android.ui.main.Navigator.LOGOUT, null)
                                return
                            }
                            isFromDialog = true
                            //showMeasureDialog()
                            navigate(life.mibo.android.ui.main.Navigator.BODY_MEASURE, null)
                            return

                        } else {
                            Toasty.info(requireContext(), R.string.unable_to_connect).show()
                        }
                    } catch (e: Exception) {
                        MiboEvent.log(e)
                    }


                    getDialog()?.dismiss()

                }

            })
    }

    fun parseBiometric(bio: List<Biometric.Data?>?) {
        if (bio != null) {

            val list = ArrayList<SummaryAdapter.Item>()
            val data = bio[bio.size - 1]
            if (data == null) {
                Toasty.info(requireContext(), R.string.error_occurred).show()
                navigate(life.mibo.android.ui.main.Navigator.CLEAR_HOME, null)
                return
            }

            //Prefs.get(context)["user_age"] = "$data"
            //Prefs.get(context)["user_gender"] = "${data?.gender}"
            Prefs.get(context)["user_weight"] = "${Calculate.round(data.weight)} KG"
            Prefs.get(context)["user_height"] = "${data.height} CM"

            try {
                Prefs.get(context)["user_date"] = "${data.createdAt?.date?.split(" ")?.get(0)}"
            } catch (e: Exception) {
                Prefs.get(context)["user_date"] = "${data.createdAt?.date}"
            }

            //weight?.text = "${pref["user_weight"]}"
            // height?.text = "${pref["user_height"]}"
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_bmi,
                    0xFF5DCEED.toInt(),
                    "BMI",
                    getDouble(data.bMI),
                    ""
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_bmr,
                    0xFF8BC53F.toInt(),
                    "BMR",
                    getDouble(data.bMR),
                    "\nkcal/day"
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_ibw,
                    0xFF29A3DA.toInt(),
                    "IBW",
                    getDouble(data.iBW),
                    "kg"
                )
            )

            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_bsa,
                    ContextCompat.getColor(requireContext(), R.color.bsa_color),
                    "BSA",
                    getDouble(data.bSA),
                    getString(R.string.meter_square)
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1, 0,
                    R.drawable.ic_body_summary_wl,
                    0xFF42C3A4.toInt(),
                    "Weight Loss",
                    getDouble(data.weightLoss),
                    "kg"
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_mass,
                    0xFFECB581.toInt(),
                    "Body Mass",
                    getDouble(data.leanBodyMass),
                    "kg"
                )
            )

            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_fat,
                    0xFFD99700.toInt(),
                    "Body Fat",
                    getDouble(data.bodyFat),
                    "%"
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_wh_ratio,
                    0xFFFF1D25.toInt(),
                    "Waist Hip Ratio",
                    getDouble(data.waistHipRatio)
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_height_ratio,
                    0xFF09B189.toInt(),
                    "Waist Height Ratio",
                    getDouble(data.waistHeightRatio)
                )
            )


            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_water,
                    0xFF0071B4.toInt(),
                    "Body Water",
                    getDouble(data.bodyWater),
                    "kg"
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_fat_free,
                    0xFFFFB174.toInt(),
                    "Fat Free (FFMI)",
                    getDouble(data.fatFreeWeight),
                    "\n" + getString(R.string.ffmi_unit)
                )
            )
            list.add(
                SummaryAdapter.Item(
                    1,
                    0,
                    R.drawable.ic_body_summary_energy,
                    0xFF333333.toInt(),
                    "Energy",
                    getDouble(data.energy),
                    "\ncal"
                )
            )

            adapter =
                SummaryAdapter(
                    list, object : ItemClickListener<SummaryAdapter.Item> {
                        override fun onItemClicked(
                            item: SummaryAdapter.Item?, position: Int
                        ) {
                            SummaryDetailsDialog(
                                item,
                                null
                            ).show(
                                childFragmentManager,
                                "SummaryDetailsDialog"
                            )
                            // adapter?.select(item)
                            // selected = position
                            //showPicker(item)
                        }

                    })
            recyclerView?.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView?.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(
                    requireContext(),
                    R.anim.layout_animation_fall_down
                );
            //recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerView?.adapter = adapter

        }
    }

    var isFromDialog = false
//    fun showMeasureDialog() {
//        // val dialog = MeasurementFragmentDialog()
//        MeasurementFragmentDialog(object : ItemClickListener<Any?> {
//            override fun onItemClicked(item: Any?, position: Int) {
//                //navigate(position, item)
//                getBioMetric()
//            }
//
//        }).show(childFragmentManager, "MeasurementFragmentDialog")
//    }

    override fun onResume() {
        super.onResume()
        getBioMetric()
        navigate(Navigator.FAB_UPDATE, 101)
        //setAdapters()
        //updateNextButton(true, "Finish")
        //Prefs.getTemp(context).set("body_measure", "done")
    }

    override fun onStop() {
        //val MyWebViewClient = Bundle()
        //MyWebViewClient.putBoolean("fab_visible", false)
        //MyWebViewClient.putInt("fab_type", false)
        navigate(Navigator.FAB_UPDATE, 100)
        super.onStop()
    }
}