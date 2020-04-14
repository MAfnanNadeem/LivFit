/*
 *  Created by Sumeet Kumar on 1/12/20 3:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 3:16 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calories

import life.mibo.android.models.calories.CaloriesData

interface CaloriesObserver {
    fun onDataReceived(list: ArrayList<CaloriesData>)
}