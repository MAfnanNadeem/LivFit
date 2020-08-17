/*
 *  Created by Sumeet Kumar on 2/27/20 11:16 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/27/20 11:16 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxl

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.android.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.android.models.base.BaseModel
import life.mibo.android.pods.rxl.program.RxlLight

class RxlExercises(
    @SerializedName("data")
    var `data`: List<RxlProgram>?,
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    @SerializedName("error")
    var error: List<life.mibo.android.models.base.BaseError?>?,
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
                    return "Tap All"
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

        fun lightLogic(type: Int?): RxlLight {
            when (type) {
                1 -> {
                    return RxlLight.SEQUENCE
                }
                2 -> {
                    return RxlLight.RANDOM
                }
                3 -> {
                    return RxlLight.FOCUS
                }
                4 -> {
                    return RxlLight.ALL_AT_ONCE
                }
                5 -> {
                    return RxlLight.TAP_AT_ALL
                }
                6 -> {
                    return RxlLight.ALL_AT_ALL
                }
            }

            return RxlLight.UNKNOWN
        }

        fun lightLogic2(type: Int?): life.mibo.hardware.rxl.program.RxlLight {
            when (type) {
                1 -> {
                    return life.mibo.hardware.rxl.program.RxlLight.SEQUENCE
                }
                2 -> {
                    return life.mibo.hardware.rxl.program.RxlLight.RANDOM
                }
                3 -> {
                    return life.mibo.hardware.rxl.program.RxlLight.FOCUS
                }
                4 -> {
                    return life.mibo.hardware.rxl.program.RxlLight.ALL_AT_ONCE
                }
                5 -> {
                    return life.mibo.hardware.rxl.program.RxlLight.TAP_AT_ALL
                }
                6 -> {
                    return life.mibo.hardware.rxl.program.RxlLight.ALL_AT_ALL
                }
            }

            return life.mibo.hardware.rxl.program.RxlLight.UNKNOWN
        }
    }

}