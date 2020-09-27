/*
 *  Created by Sumeet Kumar on 3/12/20 5:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/11/20 12:07 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.rxl.program

data class RxlCycle(
    var cycleDuration: Int, var cycleAction: Int, var cyclePause: Int, var actionDelay: Int = 0,
    var sequence: String? = "", var lightType: RxlLight = RxlLight.SEQUENCE, var repeat: Int = 0
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
        Integer.parseInt(i)
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

    override fun toString(): String {
        return "RxlCycle(cycleDuration=$cycleDuration, cycleAction=$cycleAction, cyclePause=$cyclePause, actionDelay=$actionDelay, sequence=$sequence, lightType=$lightType, id=$id, name=$name, pattern=$pattern, count=$count, devicesSize=$devicesSize)"
    }


//    fun getDuration(): Int = cycleDuration
//    fun getAction(): Int = cycleAction.times(1000)
//    fun getPause(): Int = cyclePause.times(1000)
//    fun getDelay(): Int = actionDelay.times(1000)

}