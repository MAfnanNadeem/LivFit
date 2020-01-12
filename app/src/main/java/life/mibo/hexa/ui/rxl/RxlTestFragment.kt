/*
 *  Created by Sumeet Kumar on 1/12/20 8:07 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 8:07 AM
 *  Mibo Hexa - app 
 */

package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_rxl_test.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.views.ColorSeekBar

class RxlTestFragment : BaseFragment() {

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View? {
        super.onCreateView(i, c, b)

        val view: View? = i.inflate(R.layout.fragment_rxl_test, c, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorSelectListener(color: Int) {
                tv_device_color?.setTextColor(color)
            }

            override fun onColorChangeListener(color: Int) {

            }

        })
    }

}