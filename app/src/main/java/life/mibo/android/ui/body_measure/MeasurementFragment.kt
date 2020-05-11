/*
 *  Created by Sumeet Kumar on 4/14/20 10:16 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 10:16 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_body_measurement.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.biometric.PostBiometric
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.body_measure.adapter.BodyAdapter
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.roundToInt

class MeasurementFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_measurement, container, false)
    }

    var adapter: BodyAdapter? = null
    private var viewModel: MeasureViewModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //adapter = BodyAdapter(ArrayList<BodyAdapter.Item>(), 0)
        //adapter = BodyAdapter(ArrayList<BodyAdapter.Item>(), 0)
//        viewModel = activity?.run {
//            ViewModelProviders.of(this@MeasurementFragment)[MeasureViewModel::class.java]
//        }

        viewModel = ViewModelProvider(this@MeasurementFragment).get(MeasureViewModel::class.java)

        //viewPager.adapter = PageAdapter(getPages(), activity!!.supportFragmentManager, lifecycle)
        viewPager?.setPagingEnabled(false)
        viewPager?.offscreenPageLimit = 1
        viewPager.adapter = PagerAdapter(getPages(), childFragmentManager)
        // viewPager.isUserInputEnabled = false;
        //viewPager?.setPageTransformer(PageTransformation())
        //tabLayout.setupWithViewPager(viewPager2)
        //controller.setRecycler(recyclerView)

        tv_skip?.setOnClickListener {
            viewPager?.currentItem = --frg
        }

        imageNext?.setOnClickListener {
            nextClicked()
        }
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,

                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                frg = position.plus(1)
            }

        })
//        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//                frg = position.plus(1)
//                // updateButton()
//            }
//
//        })
        setHasOptionsMenu(false)
//        viewModel!!.nextButton.observe(this@MeasurementFragment, Observer { select ->
//            log("viewModel nextButton $select")
//            if (select) {
//                imageView3?.visibility = View.VISIBLE
//                tv_continue?.visibility = View.VISIBLE
//            } else {
//                tv_continue?.visibility = View.INVISIBLE
//                imageView3?.visibility = View.INVISIBLE
//            }
//        })

        Prefs.getTemp(context).set("body_measure", "skip")

    }


    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        log("onAttachFragment $childFragment")
    }

    @Synchronized
    fun updateNext(enable: Boolean, title: String) {
        log("parentFragment updateNext $enable")
        if (enable) {
            imageNext.visibility = View.VISIBLE
            tv_continue.visibility = View.VISIBLE
            tv_continue.text = title
        } else {
            tv_continue.visibility = View.INVISIBLE
            imageNext.visibility = View.INVISIBLE
        }
    }

    @Synchronized
    fun updateSkip(enable: Boolean) {
        log("parentFragment updateNext $enable")
        if (enable) {
            tv_skip.visibility = View.VISIBLE
        } else {
            tv_skip.visibility = View.INVISIBLE
        }
    }

    private fun getPages(): ArrayList<Fragment> {
        val member = Prefs(context).member
        val male = member?.gender.equals("male", true) ?: false
        Calculate.clear()
        Calculate.getMeasureData().gender(male)
        val list = ArrayList<Fragment>()
        //list.add(SummaryFragment())
        //list.add(QuestionFragment.create(1))
        //list.add(QuestionFragment.create(2))
        // if (member?.imageThumbnail == null)
        //     list.add(ProfilePicFragment.create(1))
        list.add(BMIFragment.create(if (male) 1 else 2))
        //list.add(BMIFragment.create(if (!male) 1 else 2))
        list.add(MeasureBodyFragment.create(male))
        //list.add(MeasureFragment.create(!male))
        list.add(BodyTypeFragment())
        list.add(QuestionFragment.create(1))
        list.add(QuestionFragment.create(2))
        //log("")
       // list.add(SummaryFragment())
//        list.add(QuestionFragment())
//        list.add(QuestionFragment())
//        list.add(QuestionFragment())
//        list.add(QuestionFragment())
        return list
    }

    var frg = 1;
    var lastFrg = 0;
    private fun nextClicked() {
        // startActivity(Intent(activity, TestActivity::class.java))
        //log("nextClicked $frg")
        if ("finish" == tv_continue?.text?.toString()?.toLowerCase()) {
            saveBiometric()
            //navigate(life.mibo.android.ui.main.Navigator.BODY_MEASURE_SUMMARY, null)
            return
        }
        viewPager?.currentItem = frg++
        //log("nextClicked2 $frg")

    }

    // for ViewPager2
    class PageAdapter(val list: ArrayList<Fragment>, fa: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fa, lifecycle) {
        //constructorfm: FragmentManager) : this(fm)
        override fun getItemCount(): Int = list.size

        override fun createFragment(position: Int): Fragment {
            if (position < itemCount)
                return list[position]
            return MeasurementFragment()
        }
    }

    class PagerAdapter(val list: ArrayList<Fragment>, fa: FragmentManager) :
        FragmentPagerAdapter(fa, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        //constructorfm: FragmentManager) : this(fm)
        fun getItemCount(): Int = list.size

        fun createFragment(position: Int): Fragment {
            if (position < getItemCount())
                return list[position]
            return MeasurementFragment()
        }

        override fun getItem(position: Int): Fragment {
            if (position < count)
                return list[position]
            return MeasurementFragment()
        }

        override fun getCount(): Int {
            return list.size
        }
    }

    class PageFragment : BaseFragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
                View? {
            return inflater.inflate(R.layout.fragment_body_measure, container, false)
        }
    }

    class PageTransformation : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val absPos = Math.abs(position)
            page.apply {
                translationY = absPos * 500f
                translationX = absPos * 500f
                scaleX = 1f
                scaleY = 1f
            }
            when {
                position < -1 ->
                    page.alpha = 0.1f
                position <= 1 -> {
                    page.alpha = Math.max(0.2f, 1 - Math.abs(position))
                }
                else -> page.alpha = 0.1f
            }
        }
    }


    private fun saveBiometric() {
        //arguments?.getInt("data_from")
//        val list = ArrayList<SummaryAdapter.Item>()
//        val b = Calculate.getValue("user_bmi", "0.00")
//        val w = Calculate.getValue("user_weight", "0.00")
//        val h = Calculate.getValue("user_height", "0.00")
//        val a = Calculate.getValue("user_age", "0.00")
//
//        val ch = Calculate.getValue("value_1", "0")
//        val wst = Calculate.getValue("value_2", "0")
//        val hp = Calculate.getValue("value_3", "0")
//        val hhp = Calculate.getValue("value_4", "0")
//        val elbw = Calculate.getValue("value_5", "0")
//        val wrst = Calculate.getValue("value_6", "0")

        val data = Calculate.getMeasureData()
        log("getSummary getMeasureData $data")
        val chest = data.getMeasurement(1)
        val waist = data.getMeasurement(2)
        val hip = data.getMeasurement(3)
        val highHips = data.getMeasurement(4)
        //val elbow = data.getMeasurement(5)
        val wrist = data.getMeasurement(5)
        val forearm = data.getMeasurement(6)
        var hr = 0
        var timeInHours = 0

        val member = Prefs.get(activity).member
        val male = member?.gender.equals("male", true) ?: false


        //val waist = getInt(wst)
        //val hip = getInt(hp)
        //val wrist = getInt(wrst)


        val age = data.age
        val height = data.height
        val weight = data.weight
        val bmi = data.bmi

        Prefs.get(context)["user_age"] = "$age"
        Prefs.get(context)["user_gender"] = "${member?.gender}"
        Prefs.get(context)["user_weight"] = "$weight KG"
        Prefs.get(context)["user_height"] = "$height CM"

        log("getSummary height $height")
        log("getSummary weight $weight")
        log("getSummary age $age")
        log("getSummary bmi $bmi ")
        log("getSummary chest $chest")
        log("getSummary waist $waist")
        log("getSummary hip $hip")
        log("getSummary high-hip $highHips")
        log("getSummary wrist $wrist")


        val bmr = getBasalMetabolicRate(male, weight, height, age)

        val ibw = getIdealBodyWeight(bmi, height)


        val bsa = getBSA(male, weight, height)

        // val caloriesBurnt = getCaloriesBurntHR(hr, weight, age, timeInHours, male)
        //val weightLoss = getWeightLoss(caloriesBurnt)
        val weightLoss = 0.0

        val bodyMass = getBodyMass(weight, height, male)


        val bodyFat = getBodyFatPercentage(
            Calculate.kgToPounds(weight),
            Calculate.cmToInch(waist),
            Calculate.cmToInch(wrist),
            Calculate.cmToInch(hip),
            Calculate.cmToInch(forearm),
            male
        )


        val waistHipRatio = waist.div(hip.toDouble())

        val waistHeightRatio = waist.div(height)

        var waterRatio = 0.6
        if (age > 59)
            waterRatio = if (male) 0.5 else 0.45
        val bodyWater = ibw.times(waterRatio)
        //val bodyWater2 = age.times(waterRatio)

        val fatFreeMass = weight * (1 - (bodyFat.div(100.0)))
        val heightMeter = height.div(100.0)
        //  log("Height  $height")
        //log("Height heightMeter $heightMeter")
        //log("Height sqrt " + heightMeter.pow(2))
        val fatFree = fatFreeMass.div(heightMeter.pow(2))

        val physicalActivity = data.getActivityScale()
        val energy = bmr.times(physicalActivity)
        saveData(
            member?.id()!!,
            member?.accessToken!!,
            data,
            round(bmr),
            round(ibw),
            round(bsa),
            round(bodyWater),
            round(energy),
            round(fatFree),
            round(bodyFat.times(100)),
            round(bodyMass),
            round(waistHeightRatio),
            round(waistHipRatio),
            round(weightLoss)
        )
    }

    private fun getIdealBodyWeight(bmi: Double, height: Double) =
        2.2.times(bmi) + 3.5.times(bmi) * ((height.div(100.0)).minus(1.5))

    private fun getBasalMetabolicRate(
        male: Boolean,
        weight: Double,
        height: Double,
        age: Double
    ): Double {
        ////BMR (kcal / day)
        val bmrS = if (male) 5 else -161
        val bmr = 10.times(weight) + 6.25.times(height) - 5.times(age) + bmrS
        return bmr
    }

    private fun getBSA(
        male: Boolean,
        weight: Double,
        height: Double
    ): Double {
        ////BMR (kcal / day)
        if (male)
            return 0.000579479.times(weight.times(0.38)) * height.times(1.08)
        return 0.000975482.times(weight.times(0.46)) * height.times(1.24)
    }

    private fun getCaloriesBurntHR(
        hr: Int,
        weight: Double,
        age: Double,
        timeInHours: Double,
        male: Boolean
    ): Double {
        //HR = Heart rate (in beats/minute)
        var calMale =
            ((-55.0969 + (0.6309.times(hr)) + (0.1988.times(weight)) + (0.2017.times(age))) / 4.184) * 60.times(
                timeInHours
            )
        var calFemale =
            ((-20.4022 + (0.4472.times(hr)) + (0.1263.times(weight)) + (0.074.times(age))) / 4.184) * 60.times(
                timeInHours
            )

        val bsa = if (male) calMale else calFemale

        // Calories burned /7700 = Weight Loss from this exercise
        return bsa
    }

    private fun getWeightLoss(calories: Double): Double {

        // Calories burned /7700 = Weight Loss from this exercise
        return calories.div(7700)
    }

    private fun getBodyMass(
        weight: Double,
        heightCm: Double,
        male: Boolean
    ): Double {
        val leanBodyMassMen = 0.407 * weight + 0.267 * heightCm - 19.2
        val leanBodyMassWomen = 0.252 * weight + 0.473 * heightCm - 48.3

        return if (male) leanBodyMassMen else leanBodyMassWomen
    }

    private fun getBodyFatPercentage(
        weight: Double,
        waist: Double,
        wrist: Double,
        hip: Double,
        forearm: Double,
        male: Boolean
    ): Double {
        val fatLeanBodyMassMen = (weight * 1.082) + 94.42 - waist * 4.15
        val fatLeanBodyMassWomen =
            (weight * 0.732) + 8.987 + wrist / 3.140 - waist * 0.157 - hip * 0.249 + forearm * 0.434
        val bodyFatWeight =
            if (male) weight.minus(fatLeanBodyMassMen) else weight.minus(fatLeanBodyMassWomen)
        log("getBodyFatPercentage $fatLeanBodyMassMen :: $fatLeanBodyMassWomen  -- $bodyFatWeight")
        //body fat percentage(BFP) = body fat weight / weight
        //        •	Essential fat: 10–13% (women), 2–5% (men)
        //        •	Athletes: 14–20% (women), 6–13% (men)
        //        •	Fitness: 21–24% (women), 14-17% (men)
        //        •	Average: 25–31% (women), 18–24% (men)
        //        •	Obese: 32%+ (women), 25%+ (men)
        val bodyFat = bodyFatWeight.div(weight)
        log("getBodyFatPercentage $weight :: $waist  -- $male : $wrist")
        log("getBodyFatPercentage $fatLeanBodyMassMen :: $fatLeanBodyMassWomen  -- $bodyFatWeight : $bodyFat")
        return bodyFat
    }


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

    private fun saveData(
        memberId: String, token: String, data: Calculate.MeasureData,
        bmr: Double, ibw: Double, bsa: Double, bodyWater: Double,
        energy: Double, ffmi: Double, bodyFat: Double, leanBodyMass: Double,
        wHeightRatio: Double, wHipRatio: Double, weightLoss: Double
    ) {

        getDialog()?.show()
        //val data = Calculate.getMeasureData();
        // Math.round()
        val post = PostBiometric.Data(
            data.bmi, bmr, bsa, bodyFat, "cm", bodyWater, data.chest,
            data.elbow, energy, ffmi, data.forearm, data.height, "cm",
            data.highHips, data.hips, data.waist, data.wrist, data.weight,
            wHeightRatio, wHipRatio, weightLoss, "cm", ibw,
            leanBodyMass, "${data.activityType}", "${data.goalType}", "${data.shapeType}", memberId
        )

        API.request.getApi().saveMemberBiometrics(PostBiometric(post, token))
            .enqueue(object : retrofit2.Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.snackbar(view, R.string.unable_to_connect)
                }

                override fun onResponse(
                    call: Call<ResponseData>, response: Response<ResponseData>
                ) {
                    // getDialog()?.dismiss()
                    try {
                        val body = response?.body()
                        log("saveData response $body")
                        if (body != null && body.isSuccess()) {
                            val msg = body.data?.message
                            msg?.let {
                                Toasty.snackbar(view, msg)
                            }
                            getDialog()?.dismiss()
                            navigate(life.mibo.android.ui.main.Navigator.BODY_MEASURE_SUMMARY, null)
                            return
                        } else if (body != null && body.isError()) {
                            val msg = body.errors?.get(0)?.message
                            msg?.let {
                                Toasty.snackbar(view, msg)
                            }

                        } else {
                            Toasty.snackbar(view, R.string.unable_to_connect)
                        }
                    } catch (e: Exception) {
                        MiboEvent.log(e)
                    }

                    getDialog()?.dismiss()


                }

            })

        //getBioMetric(memberId, token)
    }

    fun round(value: Double): Double {
        return try {
            BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
        } catch (e: java.lang.Exception) {
            value
        }
    }


    override fun onStart() {
        super.onStart()
        val a = activity
        if(a is AppCompatActivity)
            a.supportActionBar?.hide()
    }

    override fun onStop() {
        val a = activity
        if(a is AppCompatActivity)
            a.supportActionBar?.show()
        super.onStop()
    }
}