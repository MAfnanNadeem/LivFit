/*
 *  Created by Sumeet Kumar on 1/8/20 11:24 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 9:12 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.heart_rate

interface HeartRateObserver {
    fun onDataReceived(list: ArrayList<HeartRateItem>)
    fun onItemClicked(item: HeartRateItem?)
}