/*
 *  Created by Sumeet Kumar on 1/8/20 8:20 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 8:20 AM
 */

package life.mibo.hexa.ui.add_product

data class ProductItem(
    val id: Int, var title: String = "",
    var iconRes: Int = 0, var isPurchased: Boolean = false
) {
    private var colorArray: IntArray? = null


}