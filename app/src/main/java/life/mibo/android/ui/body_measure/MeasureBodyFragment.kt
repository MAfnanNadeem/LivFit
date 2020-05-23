/*
 *  Created by Sumeet Kumar on 4/16/20 11:19 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 9:05 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

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
                    ItemClickListener<BodyShapeAdapter.Item> {
                    override fun onItemClicked(
                        item: BodyShapeAdapter.Item?,
                        position: Int
                    ) {
                        adapter?.select(item)
                        selected = position
                        showPicker(item)
                    }

                })
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)
        //recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        updateNextButton(false)
        updateSkipButton(false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        log("setUserVisibleHint $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            updateNextButton(false)
            updateSkipButton(false)
        }
    }

    private fun getShapes(male: Boolean): ArrayList<BodyShapeAdapter.Item> {
        val list = ArrayList<BodyShapeAdapter.Item>()
        if (male) {
            list.add(
                BodyShapeAdapter.Item(
                    1, R.drawable.ic_intro_male_chest, "Chest", "", "cm", 36, 170, 36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    2, R.drawable.ic_intro_male_waist, "Waist", "", "cm", 36, 146, 36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    3, R.drawable.ic_intro_male_thigh, "Hips", "", "cm", 50, 150, 50
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    4, R.drawable.ic_intro_male_hip, "High Hip", "", "cm", 50, 150, 50
                )
            )
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
                    1, R.drawable.ic_intro_female_chest, "Breast", "", "cm", 36, 170, 36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    2, R.drawable.ic_intro_female_waist, "Waist", "", "cm", 36, 146, 36
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    3, R.drawable.ic_intro_female_thigh, "Hips", "", "cm", 50, 150, 50
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    4, R.drawable.ic_intro_female_hip, "High Hip", "", "cm", 50, 150, 50
                )
            )

            list.add(
                BodyShapeAdapter.Item(
                    5, R.drawable.ic_intro_female_wrist, "Wrist", "", "cm", 5, 25, 5
                )
            )
            list.add(
                BodyShapeAdapter.Item(
                    6, R.drawable.ic_intro_female_forearm, "Forearm", "", "cm", 5, 25, 5
                )
            )
        }

        return list
    }

    var lastUnit = ""
    fun showPicker(data: BodyShapeAdapter.Item?) {
        data?.let {
            MeasureBodyDialog(
                it,
                object : ItemClickListener<BodyShapeAdapter.Item> {
                    override fun onItemClicked(item: BodyShapeAdapter.Item?, position: Int) {
                        if (item != null) {
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

                            item.value = item.value
                            item.unit = item.unit

                            adapter?.update(item)
                        }
                        //Calculate.getMeasureData().addMeasurement(item)
                        //Calculate.addValue("value_${item?.id}", item?.value)
                        calculateType()
                    }

                }, lastUnit
            ).show(childFragmentManager, "BodyShapeDialog")
        }
    }

    fun calculateType() {
        try {
            val data = Calculate.getMeasureData()
            val chest = data.getMeasurement(1)
            val waist = data.getMeasurement(2)
            val hips = data.getMeasurement(3)
            val highHips = data.getMeasurement(4)

            log("chest $chest, waist $waist, hips: $hips, highHips $highHips")
//            Snackbar.make(
//                view!!,
//                "chest $chest, waist $waist, hips: $hips, highHips $highHips",
//                Snackbar.LENGTH_LONG
//            ).show()


            if (chest > 0 && waist > 0 && hips > 0 && highHips > 0) {
                if (isMale) {
                    updateNextButton(true)
                } else {
                    if (data.getMeasurement(5) > 0 && data.getMeasurement(6) > 0) {
                        updateNextButton(true)
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            log("calculateType $e")
        }
    }

    fun getInt(str: String?): Int {
        return try {
            str?.toDouble()?.toInt() ?: 0
        } catch (e: Exception) {
            0;
        }
    }
}