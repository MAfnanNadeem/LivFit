/*
 *  Created by Sumeet Kumar on 4/5/20 4:02 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 4:02 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.muscle

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Suit(
    @SerializedName("abdominal")
    var abdominal: Int?,
    @SerializedName("arm")
    var arm: Int?,
    @SerializedName("calf")
    var calf: Int?,
    @SerializedName("chest")
    var chest: Int?,
    @SerializedName("gluteal")
    var gluteal: Int?,
    @SerializedName("hamstring")
    var hamstring: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("inventory_item_id")
    var inventoryItemId: Int?,
    @SerializedName("lower_back")
    var lowerBack: Int?,
    @SerializedName("qr_code")
    var qrCode: Any?,
    @SerializedName("quadriceps")
    var quadriceps: Int?,
    @SerializedName("shoulders")
    var shoulders: Int?,
    @SerializedName("suit_description")
    var suitDescription: String?,
    @SerializedName("suit_model")
    var suitModel: Any?,
    @SerializedName("suit_name")
    var suitName: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("upper_back")
    var upperBack: Int?
) : Serializable