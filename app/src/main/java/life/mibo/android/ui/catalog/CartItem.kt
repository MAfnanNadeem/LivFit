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
    var quantity: Int = 1,
    var vat_: Double?,
    var location: String?,
    val isService: Boolean = false,
    val isPackage: Boolean = false
) : Parcelable {

    var locationId = 0
    var transactionId = 0
    var encAmount = "0.0"

    fun getAmount(): Double {
        return Calculate.round(price.times(quantity))
    }

    fun getVat(): Double {
        if (vat_ ?: 0.0 > 0.0) {
            val am = price.times(quantity)
            val v = vat_?.div(100)
            return Calculate.round(am.times(v ?: 0.0))
        }
        return 0.0
    }

    fun getTotal(): Double {
        if (vat_ ?: 0.0 > 0.0) {
            val am = price.times(quantity)
            val v = vat_?.div(100)
            val tv = am.times(v ?: 0.0)
            return Calculate.round(am.plus(tv))
        }
        return Calculate.round(price.times(quantity))
    }

    fun getBillable(): Double {
        if (vat_ ?: 0.0 > 0.0) {
            val am = price.times(quantity)
            val v = vat_?.div(100)
            val tv = am.times(v ?: 0.0)
            return Calculate.round(am.plus(tv))
        }
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
        parcel.writeDouble(vat_ ?: 0.0)
        parcel.writeString(location)
        parcel.writeInt(if (isService) 1 else 0)
        parcel.writeInt(if (isPackage) 1 else 0)
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readInt() == 1,
        parcel.readInt() == 1
    ) {

    }

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
