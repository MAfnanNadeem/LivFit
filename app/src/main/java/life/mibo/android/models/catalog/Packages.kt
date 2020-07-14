/*
 *  Created by Sumeet Kumar on 6/2/20 3:27 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/2/20 3:27 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse
import life.mibo.android.models.biometric.CreatedAt
import java.io.Serializable

class Packages(data: List<Data?>?) : BaseResponse<List<Packages.Data?>>(data) {

    data class Data(
        @SerializedName("created_at")
        var createdAt: CreatedAt?,
        @SerializedName("created_by")
        var createdBy: String?,
        @SerializedName("currency_type")
        var currencyType: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("location")
        var location: String?,
        @SerializedName("locationID")
        var locationID: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("price")
        var price: Double?,
        @SerializedName("products")
        var products: Products?,
        @SerializedName("services")
        var services: Services?,
        @SerializedName("vat")
        var vat: Double?,
        @SerializedName("validity")
        var validity: Int?
    ) : Serializable {
        fun match(query: String): Boolean {
            if (name?.toLowerCase()?.contains(query) == true)
                return true
            return false
        }

    }


    data class Package(
        @SerializedName("created_at")
        var createdAt: CreatedAt?,
        @SerializedName("created_by")
        var createdBy: String?,
        @SerializedName("currency_type")
        var currencyType: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("location")
        var location: String?,
        @SerializedName("locationID")
        var locationID: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("price")
        var price: Int?,
        @SerializedName("products")
        var products: Products?,
        @SerializedName("services")
        var services: Services?,
        @SerializedName("vat")
        var vat: Int?
    ) : Serializable {
        fun match(query: String): Boolean {
            if (name?.toLowerCase()?.contains(query) == true)
                return true
            return false
        }
    }


    data class Products(
        @SerializedName("product_category")
        var productCategory: String?,
        @SerializedName("product_description")
        var productDescription: String?,
        @SerializedName("product_model")
        var productModel: String?,
        @SerializedName("product_name")
        var productName: String?
    ) : Serializable

    data class Services(
        @SerializedName("location_type")
        var locationType: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("no_of_session")
        var noOfSession: Int?,
        @SerializedName("service_description")
        var serviceDescription: String?,
        @SerializedName("service_type")
        var serviceType: String?,
        @SerializedName("validity")
        var validity: Int?
    ) : Serializable

}