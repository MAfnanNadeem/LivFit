/*
 *  Created by Sumeet Kumar on 1/25/20 5:36 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/25/20 5:36 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_rxl_initial.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.Navigator

class ReflexSelectFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rxl_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_quickplay?.setOnClickListener {
            navigate(Navigator.RXL_EXERCISE, null)
        }

        btn_create.setOnClickListener {
            navigate(Navigator.RXL_COURSE_SELECT, null)
        }

    }
}
