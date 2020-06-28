/*
 *  Created by Sumeet Kumar on 4/16/20 11:19 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 9:05 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_body_measure.*
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.BodyBaseFragment
import life.mibo.android.ui.body_measure.adapter.BodyShapeAdapter
import life.mibo.android.ui.body_measure.adapter.Calculate


class MeasureBodyFragment : BodyBaseFragment() {
    companion object {
        fun create(genderMale: Boolean): MeasureBodyFragment {
            val frg = MeasureBodyFragment()
            val arg = Bundle()
            arg.putBoolean("profile_gender", genderMale)
            frg.arguments = arg
            return frg
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_measure, container, false)
    }

    var selected = -1
    var adapter: BodyShapeAdapter? = null
    var isMale = false;
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isMale = arguments?.getBoolean("profile_gender", false) ?: false
        adapter =
            BodyShapeAdapter(
                getShapes(
                    isMale
                ),
                object :
                    BodyShapeAdapter.ItemClickListener {
                    override fun onItemClicked(
                        item: BodyShapeAdapter.Item?,
                        holder: BodyShapeAdapter.Holder,
                        position: Int
                    ) {
                        log("onItemClicked ${item?.id}  ${item?.title} : position $position")
                        adapter?.select(item)
                        selected = position
                        //showPicker2(item)
                        showPicker(item, holder)
                    }

                })
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)
        //recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.adapter = adapter

        //updateCalculateData()

    }

    override fun isNextClickable(): Boolean {
        //log("MeasureBodyFragment isNextClickable called")
        return calculateType()
    }

    override fun onResume() {
        super.onResume()
        log("onResume onResume")
        updateButtons()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        log("setUserVisibleHint $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            updateButtons()
        }
    }

    var isUpdate = false
    fun updateButtons() {
        if (isUpdate)
            return
        log(" updateButtons()")
        updateNextButton(false)
        updateSkipButton(false)
        isUpdate = true
        updateCalculateData()
    }

    private var isNextEnable = false

    private fun getShapes(male: Boolean): ArrayList<BodyShapeAdapter.Item> {
        var value1 = ""
        var value2 = ""
        var value3 = ""
        var value4 = ""
        var value5 = ""
        var value6 = ""
        val data = Calculate.getBioData()
        if (data != null) {
            // log("Calculate.getBioData() $data")

            value1 = "${data.chest}"
            value2 = "${data.waist}"
            value3 = "${data.hips}"
            value4 = "${data.highHips}"
            value5 = "${data.wrist}"
            value6 = "${data.forearm}"
            if (value1.trim().length > 1 && value3.trim().length > 1) {
                isNextEnable = true
                isUpdateMode = true
            }
        }

        val list = ArrayList<BodyShapeAdapter.Item>()
        if (male) {
            list.add(
                BodyShapeAdapter.Item(
                    1,
                    R.drawable.ic_intro_male_chest,
                    getString(R.string.chest),
                    value1,
                    "cm",
                    36,
                    170,
                    36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    2,
                    R.drawable.ic_intro_male_waist,
                    getString(R.string.waist),
                    value2,
                    "cm",
                    36,
                    146,
                    36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    3,
                    R.drawable.ic_intro_male_thigh,
                    getString(R.string.hips),
                    value3,
                    "cm",
                    50,
                    150,
                    50
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    4,
                    R.drawable.ic_intro_male_hip,
                    getString(R.string.high_hips),
                    value4,
                    "cm",
                    50,
                    150,
                    50
                )
            )
            /*list.add(
                BodyShapeAdapter.Item(
                    5,
                    R.drawable.ic_intro_male_hip,
                    getString(R.string.high_hips),
                    "",
                    "cm",
                    50,
                    150,
                    50
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    6,
                    R.drawable.ic_intro_male_hip,
                    getString(R.string.high_hips),
                    "",
                    "cm",
                    50,
                    150,
                    50
                )
            )

            list.add(
                BodyShapeAdapter.Item(
                    7,
                    R.drawable.ic_intro_male_hip,
                    getString(R.string.high_hips),
                    "",
                    "cm",
                    50,
                    150,
                    50
                )
            )*/
//            list.add(
//                BodyShapeAdapter.Item(
//                    5, R.drawable.ic_intro_male_wrist, "Wrist", "", "cm", 5, 25, 5
//                )
//            )
//            list.add(
//                BodyShapeAdapter.Item(
//                    6, R.drawable.ic_intro_male_elbow, "Elbow", "", "cm", 5, 25, 5
//                )
//            )
        } else {
            list.add(
                BodyShapeAdapter.Item(
                    1,
                    R.drawable.ic_intro_female_chest,
                    getString(R.string.breast),
                    value1,
                    "cm",
                    36,
                    170,
                    36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    2,
                    R.drawable.ic_intro_female_waist,
                    getString(R.string.waist),
                    value2,
                    "cm",
                    36,
                    146,
                    36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    3,
                    R.drawable.ic_intro_female_thigh,
                    getString(R.string.hips),
                    value3,
                    "cm",
                    50,
                    150,
                    50
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    4,
                    R.drawable.ic_intro_female_hip,
                    getString(R.string.high_hips),
                    value4,
                    "cm",
                    50,
                    150,
                    50
                )
            )

            list.add(
                BodyShapeAdapter.Item(
                    5,
                    R.drawable.ic_intro_female_wrist,
                    getString(R.string.wrist),
                    value5,
                    "cm",
                    5,
                    35,
                    5
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    6,
                    R.drawable.ic_intro_female_forearm,
                    getString(R.string.forearm),
                    value6,
                    "cm",
                    5,
                    35,
                    5
                )
            )
        }

        return list
    }

    var lastUnit = ""
    fun showPicker(
        data: BodyShapeAdapter.Item?,
        holder: BodyShapeAdapter.Holder
    ) {
        data?.let {
            MeasureBodyActivity.launch(
                it,
                this,
                data.unit,
                getInt(data.value),
                holder.image,
                holder.text,
                holder.value,
                holder.unit
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MeasureBodyActivity.CODE && resultCode == Activity.RESULT_OK) {
            val i: BodyShapeAdapter.Item? =
                data?.getSerializableExtra("data_result") as BodyShapeAdapter.Item?
            i?.let {
                onPickerItemClicked(it)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun showPicker2(data: BodyShapeAdapter.Item?) {
        data?.let {
            MeasureBodyDialog(
                it,
                object : ItemClickListener<BodyShapeAdapter.Item> {
                    override fun onItemClicked(item: BodyShapeAdapter.Item?, position: Int) {
                        if (item != null) {
                            onPickerItemClicked(item)
                        }
                        //Calculate.getMeasureData().addMeasurement(item)
                        //Calculate.addValue("value_${item?.id}", item?.value)

                    }

                }, lastUnit
            ).show(childFragmentManager, "BodyShapeDialog")
        }
    }

    @Synchronized
    fun onPickerItemClicked(item: BodyShapeAdapter.Item) {
        log("onPickerItemClicked $item")
        if (item.value.isNullOrEmpty())
            return
        if (item.unit.toLowerCase().contains("cm")) {
            lastUnit = "cm"
            Calculate.getMeasureData()
                .addMeasurement(item.id, getInt(item.value))
            // item.value = String.format("%.2f", Calculate.cmToInch(item.value))
            // item.unit = "inches"

        } else {
            lastUnit = "inches"
            Calculate.getMeasureData()
                .addMeasurement(item.id, Calculate.inchToCm(item.value).toInt())
        }

        //item.value = item.value
        //item.unit = item.unit

        adapter?.update(item)

        calculateType()
    }

    fun refreshAdapter() {
        val data = Calculate.getMeasureData()
        val chest = data.getMeasurement(1)
        val waist = data.getMeasurement(2)
        val hips = data.getMeasurement(3)
        val highHips = data.getMeasurement(4)

        if (chest > 0 && waist > 0 && hips > 0 && highHips > 0) {
            if (isMale) {

            } else {
                if (data.getMeasurement(5) > 0 && data.getMeasurement(6) > 0) {

                }
            }

        }
    }

    fun updateCalculateData() {
        log("updateCalculateData $isNextEnable")
        if (isNextEnable) {
            val list = adapter?.list
            if (list != null) {
                for (item in list) {
                    Calculate.getMeasureData()
                        .addMeasurement(item.id, getInt(item.value))
                    log("updateCalculateData item ${item.id} ${getInt(item.value)}")
                }

                calculateType()
            }
        }
    }

    var isUpdateMode = false

    @Synchronized
    private fun calculateType(): Boolean {
        log("calculateType started")
        try {
            val data = Calculate.getMeasureData()
            val chest = data.getMeasurement(1)
            val waist = data.getMeasurement(2)
            val hips = data.getMeasurement(3)
            val highHips = data.getMeasurement(4)

            log("calculateType chest $chest, waist $waist, hips: $hips, highHips $highHips")
//            Snackbar.make(
//                view!!,
//                "chest $chest, waist $waist, hips: $hips, highHips $highHips",
//                Snackbar.LENGTH_LONG
//            ).show()


            if (chest > 0 && waist > 0 && hips > 0 && highHips > 0) {
                if (isMale) {
                    if (isUpdateMode)
                        updateNextButton(true, getString(R.string.update))
                    else updateNextButton(true, getString(R.string.continue_action))

                    log("calculateType updateNextButton Male")
                    return true
                } else {
                    if (data.getMeasurement(5) > 0 && data.getMeasurement(6) > 0) {
                        if (isUpdateMode)
                            updateNextButton(true, getString(R.string.update))
                        else updateNextButton(true, getString(R.string.continue_action))
                        log("calculateType updateNextButton Female")
                        return true
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            log("calculateType $e")
        }
        return false
    }

    fun getInt(str: String?): Int {
        return try {
            str?.toDouble()?.toInt() ?: 0
        } catch (e: Exception) {
            0;
        }
    }
}