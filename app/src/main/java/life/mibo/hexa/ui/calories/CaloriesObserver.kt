/*
 *  Created by Sumeet Kumar on 1/12/20 3:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 3:16 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.calories

import life.mibo.hexa.models.calories.CaloriesData
import life.mibo.hexa.ui.base.BaseListener

interface CaloriesObserver {
    fun onDataReceived(list: ArrayList<CaloriesData>)
}