/*
 *  Created by Sumeet Kumar on 9/9/20 5:26 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/9/20 5:26 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl

import life.mibo.android.models.rxl.RxlProgram
import life.mibo.android.models.workout.RXL
//import life.mibo.hardware.rxl.core.RxlBlock
//import life.mibo.hardware.rxl.core.RxlProgram

object RxlUtils {
//    fun convert(rxt: RXL): RxlProgram {
////        val list = ArrayList<RXL.RXLBlock>()
////        val blocks = rxt.blocks
////        if (blocks != null && blocks.isNotEmpty()) {
////            for (i in blocks) {
////                if (i != null) {
////                    //i.pattern = "1,2-3,4,5-6,7,8,9-10,8,7,6-5,4,3-2"
////                    //i.rXTAction = 2
////                    val b = RxlBlock(
////                        i.getAction(),
////                        i.getDuration(),
////                        i.getLogicType(),
////                        0,
////                        i.pattern ?: ""
////                    )
////                    b.delay = i.getDelay()
////                    b.round = i.getRounds()
////                    list.add(b)
////                }
////            }
//        }
//        return RxlProgram("Rxl ${rxt.category}", 0, 0, list)
//    }

    fun getLogicType(type: Int?): life.mibo.hardware.rxl.program.RxlLight {
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