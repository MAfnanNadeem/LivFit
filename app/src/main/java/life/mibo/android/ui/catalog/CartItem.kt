/*
 *  Created by Sumeet Kumar on 6/2/20 2:30 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/2/20 2:30 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.os.Parcel
import android.os.Parcelable
import life.mibo.android.ui.body_measure.adapter.Calculate

data class CartItem(
    var id: Int,
    var name: String?,
    var price: Double,
    var currencyType: String?,
    var image: String?,
    var quantity: Int = 1
) : Parcelable {

    fun getAmount(): Double {
        return Calculate.round(price.times(quantity))
    }

    // Parcel Impl
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(currencyType)
        parcel.writeString(image)
        parcel.writeInt(quantity)
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}
