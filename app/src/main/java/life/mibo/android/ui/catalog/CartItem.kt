/*
 *  Created by Sumeet Kumar on 6/2/20 2:30 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/2/20 2:30 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.os.Parcel
import android.os.Parcelable
import life.mibo.android.models.catalog.PromoResponse
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
    val isPackage: Boolean = false,
    var locationId: Int = 0,
    var serviceLocationId: String = "0",
    var adviceNumber: String = "",
    var promoAmount: Double = 0.0,
    var isFlat: Boolean = true,
    var isPromo: Boolean = false,
    var startDate: String = "",
    var endDate: String = "",
    var promoCode: String = "",
    var validity: Int = 0,
    var promoService: Int = 0
) : Parcelable {

    var quantityDisable = false

    // var locationId = 0
    // var serviceLocationId = "0"
    var transactionId = 0
    var encAmount = "0.0"
    var orgAmount = 0.0
    //var promoAmount = 0.0
    //var isFlat = true
    //var adviceNumber = ""

    fun havePromo(): Boolean {
        return isPromo && promoAmount > 0
    }

    fun calculatePrice(): Double {
        return Calculate.round(price.times(quantity))
    }

    fun getTotalAmount(): Double {
        return checkPromo()
    }


    fun getVat(): Double {
        if (vat_ ?: 0.0 > 0.0) {
            val am = getTotalAmount()
            val v = vat_?.div(100)
            return Calculate.round(am.times(v ?: 0.0))
        }
        return 0.0
    }

    fun getTotal(): Double {
        if (vat_ ?: 0.0 > 0.0) {
            val am = getTotalAmount()
            val v = vat_?.div(100)
            val tv = am.times(v ?: 0.0)
            return Calculate.round(am.plus(tv))
        }
        return getTotalAmount()
    }

    fun getBillable(): Double {
        if (vat_ ?: 0.0 > 0.0) {
            val am = getTotalAmount()
            val v = vat_?.div(100)
            val tv = am.times(v ?: 0.0)
            return Calculate.round(am.plus(tv))
        }
        return getTotalAmount()
    }

    fun getPromo(): Double {
        if (isPromo && promoAmount > 0) {
            if (isFlat) {
                return promoAmount
            } else {
                val amount = price.times(quantity)
                val p = promoAmount.div(100)
                return amount.times(p)
            }
        }
        return 0.0
    }

    private fun checkPromo(): Double {
        if (isPromo && promoAmount > 0) {
            val am = price.times(quantity)
            if (isFlat) {
                return am.minus(promoAmount)
            } else {
                val p = promoAmount.div(100)
                val pr = am.times(p)
                return am.minus(pr)
            }
        }
        return calculatePrice()
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
        parcel.writeInt(locationId)
        parcel.writeString(serviceLocationId)
        parcel.writeString(adviceNumber)
        parcel.writeDouble(promoAmount)
        parcel.writeInt(if (isFlat) 1 else 0)
        parcel.writeInt(if (isPromo) 1 else 0)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(promoCode)
        parcel.writeInt(validity)
        parcel.writeInt(promoService)
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
        parcel.readInt() == 1,
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt() == 1,
        parcel.readInt() == 1,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )


    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "CartItem(id=$id, name=$name, price=$price, currencyType=$currencyType, image=$image, quantity=$quantity, vat_=$vat_, location=$location, isService=$isService, isPackage=$isPackage, quantityDisable=$quantityDisable, locationId=$locationId, serviceLocationId='$serviceLocationId', transactionId=$transactionId, encAmount='$encAmount', adviceNumber='$adviceNumber')"
    }

    private var promo: PromoResponse.Promo? = null
    fun promo(data: PromoResponse.Promo?, code: String) {
        this.promo = data
        if (promo != null) {
            if (promo!!.isFLat() || promo!!.isPercent()) {
                promoAmount = promo!!.getValue()
                isFlat = promo!!.isFLat()
                isPromo = true;
                promoCode = code;
            } else {
                isPromo = false;
                promoAmount = 0.0
                promo = null
                promoCode = ""
            }
        } else {
            isPromo = false;
            promoAmount = 0.0
            promo = null
            promoCode = ""
        }
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
