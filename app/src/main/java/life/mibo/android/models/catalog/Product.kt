/*
 *  Created by Sumeet Kumar on 6/1/20 10:06 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/1/20 10:05 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog

/*
 *  Created by Sumeet Kumar on 6/1/20 10:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/1/20 10:05 AM
 *  Mibo Hexa - app
 */


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("CategoryName")
    var categoryName: String?,
    @SerializedName("Description")
    var description: String?,
    @SerializedName("HSCode")
    var hSCode: Any?,
    @SerializedName("Id")
    var id: Int?,
    @SerializedName("Image")
    var image: String?,
    @SerializedName("LongForm")
    var longForm: String?,
    @SerializedName("ManufacturerName")
    var manufacturerName: Any?,
    @SerializedName("ProductName")
    var productName: String?,
    @SerializedName("ShortForm")
    var shortForm: String?,
    @SerializedName("Stock")
    var stock: Int?,
    @SerializedName("SubCategoryName")
    var subCategoryName: String?,
    @SerializedName("SubImages")
    var subImages: List<String?>?,
    @SerializedName("UnitPrice")
    var unitPrice: String?,
    @SerializedName("Currency")
    var currency: String?
) : Serializable {

    fun match(query: String): Boolean {
        if (productName?.toLowerCase()?.contains(query) == true)
            return true
        //if (shortForm?.toLowerCase()?.contains(query) == true)
         //   return true
        return false
    }
}