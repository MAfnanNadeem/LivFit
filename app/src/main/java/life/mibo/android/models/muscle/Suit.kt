/*
 *  Created by Sumeet Kumar on 4/5/20 4:02 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 4:02 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.muscle

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Suit(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("abdominal")
    var abdominal: Int?,
    @SerializedName("arm")
    var arm: Int?,
    @SerializedName("calf")
    var calf: Int?,
    @SerializedName("category")
    var category: String?,
    @SerializedName("chest")
    var chest: Int?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("gluteal")
    var gluteal: Int?,
    @SerializedName("hamstring")
    var hamstring: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("lower_back")
    var lowerBack: Int?,
    @SerializedName("Muscles")
    var muscles: List<Muscle?>?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("qr_code")
    var qrCode: Any?,
    @SerializedName("quadriceps ")
    var quadriceps: Int?,
    @SerializedName("shoulders")
    var shoulders: Int?,
    @SerializedName("suit_model")
    var suitModel: String?,
    @SerializedName("upper_back")
    var upperBack: Int?
) : Serializable