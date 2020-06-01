/*
 *  Created by Sumeet Kumar on 5/31/20 2:31 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/31/20 2:31 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.product


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class Catalog(@SerializedName("products") var products: List<Product?>?, data: Data?) :
    BaseResponse<Catalog.Data?>(data) {
    data class Data(
        @SerializedName("products")
        var products: List<Product?>?
    )

//    data class Product(
//        @SerializedName("CategoryName")
//        var categoryName: String?,
//        @SerializedName("Description")
//        var description: String?,
//        @SerializedName("HSCode")
//        var hSCode: String?,
//        @SerializedName("Id")
//        var id: Int?,
//        @SerializedName("Images")
//        var images: Images?,
//        @SerializedName("LongForm")
//        var longForm: String?,
//        @SerializedName("ManufacturerName")
//        var manufacturerName: Any?,
//        @SerializedName("ProductName")
//        var productName: String?,
//        @SerializedName("ShortForm")
//        var shortForm: String?,
//        @SerializedName("Stock")
//        var stock: Int?,
//        @SerializedName("SubCategoryName")
//        var subCategoryName: String?,
//        @SerializedName("UnitPrice")
//        var unitPrice: String?
//    )
//
//    data class Images(
//        @SerializedName("Photo1")
//        var photo1: Any?,
//        @SerializedName("Photo2")
//        var photo2: Any?
//    )
}