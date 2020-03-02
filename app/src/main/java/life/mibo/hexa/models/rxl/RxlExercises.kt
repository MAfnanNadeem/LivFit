/*
 *  Created by Sumeet Kumar on 2/27/20 11:16 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/27/20 11:16 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.rxl

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.hexa.models.base.BaseModel
import life.mibo.hexa.pods.rxl.RxlLight
import java.io.Serializable

class RxlExercises(
    @SerializedName("data")
    var `data`: List<RxlProgram>?,
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    @SerializedName("error")
    var error: List<life.mibo.hexa.models.base.Error?>?,
    @SerializedName("status")
    var status: String?
) : BaseModel {

    companion object {
        fun getType(type: Int?): String {
            when (type) {
                1 -> {
                    return "Sequence"
                }
                2 -> {
                    return "Random"
                }
                3 -> {
                    return "Focus"
                }
                4 -> {
                    return "All at once - Tap one"
                }
                5 -> {
                    return "All at once - Tap All"
                }
            }

            return ""
        }

        fun getType(type: String?): Int {
            when (type?.toLowerCase()) {
                "sequence" -> {
                    return 1
                }
                "random" -> {
                    return 2
                }
                "focus" -> {
                    return 3
                }
                "all at once" -> {
                    return 4
                }

                "all at once - tap one" -> {
                    return 4
                }
                "tap one" -> {
                    return 4
                }
                "all at once - tap all" -> {
                    return 5
                }
                "tap all" -> {
                    return 5
                }
                "tap at once" -> {
                    return 5
                }
            }

            return 0
        }
    }

}