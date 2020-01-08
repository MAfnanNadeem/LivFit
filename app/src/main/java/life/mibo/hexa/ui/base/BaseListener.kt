/*
 *  Created by Sumeet Kumar on 1/8/20 9:46 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 9:46 AM
 */

package life.mibo.hexa.ui.base

import android.view.View

interface BaseListener {
    fun onCreate(view: View? = null, data: Any? = null)
    fun onResume()
    fun onStop()
}