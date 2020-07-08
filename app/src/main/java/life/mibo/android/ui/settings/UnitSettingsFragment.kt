/*
 *  Created by Sumeet Kumar on 5/12/20 12:26 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/12/20 12:26 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment

class UnitSettingsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}