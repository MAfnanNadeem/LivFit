/*
 *  Created by Sumeet Kumar on 2/25/20 4:43 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/25/20 4:43 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.Navigator


class RxlQuickPlayDetailsPlayFragment : BaseFragment() {


    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_quickplay_detail_2, c, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        //controller = ReactionLightController(this, this)
        navigate(Navigator.HOME_VIEW, true)
        setHasOptionsMenu(true)
        //controller.onStart()
        //controller.getPrograms()

//        btn_next?.setOnClickListener {
//            navigate(Navigator.RXL_QUICKPLAY_DETAILS_PLAY, null)
//        }

    }

}
