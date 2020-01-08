/*
 *  Created by Sumeet Kumar on 1/8/20 11:24 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 9:28 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.heart_rate

data class HeartRateItem(
    val id: Int, var title: String = "",
    var iconRes: Int = 0, var isPurchased: Boolean = false
) {
    private var colorArray: IntArray? = null


}