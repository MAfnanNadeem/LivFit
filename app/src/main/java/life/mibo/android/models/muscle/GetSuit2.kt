/*
 *  Created by Sumeet Kumar on 4/8/20 4:53 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/8/20 4:53 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.muscle


import com.google.gson.annotations.SerializedName

data class GetSuit2(
    @SerializedName("data")
    var `data`: List<Data?>?,
    @SerializedName("error")
    var error: List<Any?>?,
    @SerializedName("status")
    var status: String?
) {
    data class Data(
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
    ) {
        data class Muscle(
            @SerializedName("id")
            var id: Int?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("muscle_name")
            var muscleName: String?,
            @SerializedName("position")
            var position: Int?
        )
    }
}