/*
 *  Created by Sumeet Kumar on 1/8/20 5:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 9:28 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.programs

data class Program(
    val id: Int, var title: String = "",
    var iconRes: Int = 0, var isPurchased: Boolean = false
) {
    private var colorArray: IntArray? = null


}