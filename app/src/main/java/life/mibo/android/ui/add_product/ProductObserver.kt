/*
 *  Created by Sumeet Kumar on 1/8/20 8:18 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 8:18 AM
 */

package life.mibo.android.ui.add_product

interface ProductObserver {
    fun onDataReceived(list: ArrayList<ProductItem>)
    fun onItemClicked(item: ProductItem?)
}