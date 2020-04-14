/*
 *  Created by Sumeet Kumar on 3/5/20 10:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/4/20 4:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.rxl.program

import life.mibo.android.core.toIntOrZero

data class RxlCycle(
    var cycleDuration: Int, var cycleAction: Int, var cyclePause: Int,
    var actionDelay: Int = 0,
    var sequence: String? = "",
    var lightType: RxlLight = RxlLight.SEQUENCE
) {

    companion object {
        fun empty() = RxlCycle(0, 0, 0, 0)
    }

    // Optionals
    var id: Int = 0
    var name: String? = ""
    private var pattern = ArrayList<Int>()
    private var count = 0
    private var devicesSize = 0

    fun initPattern() {
        if (lightType == RxlLight.SEQUENCE) {
            sequence?.let {
                if (it.isNotEmpty()) {
                    pattern.clear()
                    val str = it.split(",")
                    for (i in str) {
                        pattern.add(getInt(i))
                    }
                }
            }
        }
    }

    fun getInt(i: String): Int = try {
        i.toIntOrZero()
    } catch (e: Exception) {
        0
    }

    fun getNextSequence(size: Int): Int {
        count++
        if (count >= size)
            count = 0
        var pos = count
        if (pattern.isNotEmpty()) {
            pos = if (count < pattern.size) {
                pattern[count]
            } else {
                pattern[0]
            }

            if (pos >= size)
                pos = 0
        }

        return pos
    }

//    fun getDuration(): Int = cycleDuration
//    fun getAction(): Int = cycleAction.times(1000)
//    fun getPause(): Int = cyclePause.times(1000)
//    fun getDelay(): Int = actionDelay.times(1000)

}