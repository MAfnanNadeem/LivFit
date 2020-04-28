/*
 *  Created by Sumeet Kumar on 4/20/20 10:59 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/20/20 10:59 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_body_type_image.*
import life.mibo.android.R
import life.mibo.android.ui.body_measure.adapter.BodyBaseFragment

class BodyTypeImageFragment() : BodyBaseFragment() {

    companion object {
        fun create(
            gender: Int,
            bodyImage: Int,
            titleRes: Int,
            textRes: Int
        ): BodyTypeImageFragment {
            val frg = BodyTypeImageFragment()
            val arg = Bundle()
            // male 1, female = 2
            arg.putInt("type_gender", gender)
            arg.putInt("type_shape", bodyImage)
            arg.putInt("type_title", titleRes)
            arg.putInt("type_text", textRes)
            frg.arguments = arg
            return frg
        }

        fun create(isMale: Boolean, shapeType: Int): BodyTypeImageFragment {
            val frg = BodyTypeImageFragment()
            val arg = Bundle()
            // male 1, female = 2
            arg.putInt("type_gender", if (isMale) 1 else 2)
            arg.putInt("type_shape_type", shapeType)
            frg.arguments = arg
            return frg
        }

        const val TYPE_X = 1
        const val TYPE_O = 2
        const val TYPE_A = 3
        const val TYPE_V = 4
        const val TYPE_I = 5
    }

    private var genderType = 0
    private var imageRes = 0
    private var titleTextRes = 0
    private var textRes = 0
    private var shapeType = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_type_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            genderType = it.getInt("type_gender", 0)
            imageRes = it.getInt("type_shape", 0)
            titleTextRes = it.getInt("type_title", 0)
            textRes = it.getInt("type_text", 0)
            shapeType = it.getInt("type_shape_type", 0)
        }

        if (shapeType > 0) {
            when (shapeType) {
                TYPE_X -> {
                    imageRes =
                        if (genderType == 1) R.drawable.ic_body_male_x else R.drawable.ic_body_female_x
                    textRes = R.string.body_type_x
                }
                TYPE_O -> {
                    imageRes =
                        if (genderType == 1) R.drawable.ic_body_male_o else R.drawable.ic_body_female_o
                    textRes = R.string.body_type_o
                }
                TYPE_A -> {
                    imageRes =
                        if (genderType == 1) R.drawable.ic_body_male_a else R.drawable.ic_body_female_a
                    textRes = R.string.body_type_a
                }
                TYPE_V -> {
                    imageRes =
                        if (genderType == 1) R.drawable.ic_body_male_v else R.drawable.ic_body_female_v
                    textRes = R.string.body_type_v
                }
                TYPE_I -> {
                    imageRes =
                        if (genderType == 1) R.drawable.ic_body_male_i else R.drawable.ic_body_female_i
                    textRes = R.string.body_type_i
                }
            }
        }

        if (genderType != 0) {
//            if (genderType == 1) {
//                header?.setImageResource(R.drawable.bg_body_header_male)
//                header_bottom?.setImageResource(R.drawable.bg_body_header_male_bottom)
//            } else {
//                header?.setImageResource(R.drawable.bg_body_header_female)
//                header_bottom?.setImageResource(R.drawable.bg_body_header_female_bottom)
//            }
            imageView?.setImageResource(imageRes)
            //title_text?.setText(titleTextRes)
            tv_type?.setText(textRes)
        }
    }

}