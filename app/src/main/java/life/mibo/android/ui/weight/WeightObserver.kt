/*
 *  Created by Sumeet Kumar on 1/8/20 5:10 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 8:09 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.weight

import life.mibo.android.models.weight.Data
import life.mibo.android.ui.home.HomeItem

interface WeightObserver {
    fun onChartDataReceived(list: List<Data?>?)
    fun onUserDetailsReceived(list: life.mibo.android.models.user_details.Data?)
    fun onItemClicked(item: HomeItem?)
}