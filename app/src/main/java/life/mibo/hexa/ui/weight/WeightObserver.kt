/*
 *  Created by Sumeet Kumar on 1/8/20 5:10 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 8:09 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.weight

import life.mibo.hexa.ui.home.HomeItem

interface WeightObserver {
    fun onDataRecieved(list: ArrayList<HomeItem>)
    fun onItemClicked(item: HomeItem?)
}