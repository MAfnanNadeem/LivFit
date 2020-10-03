/*
 *  Created by Sumeet Kumar on 9/8/20 5:25 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/8/20 5:25 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.core


class RxlProgram(var name: String, var color: Int, var delay: Int = 0, var blocks: List<RxlBlock>) {




    var key: String = ""
    fun cycles(): Int {
        return blocks.size
    }

    private var totalDuration_: Int = 0

    fun getTotalDuration(): Int {
        if (totalDuration_ == 0) {
            for (b in blocks) {
                // totalDuration_ += b.duration.plus(delay)
                totalDuration_ += b.duration
            }
        }
        return totalDuration_
    }

    fun duration(cycle: Int): Int {
        if (cycle < blocks.size)
            return blocks[cycle].duration
        return 0
    }

    fun action(cycle: Int): Int {
        //Logger.e("action :: cycle $cycle >> " + blocks[cycle].action)
        if (cycle < blocks.size)
            return blocks[cycle].action
        return 0
    }

//    fun color(cycle: Int): Int {
//        if (cycle < blocks.size)
//            return blocks[cycle].color
//        return Color.GRAY
//    }

    fun pause(currentCycle: Int): Int {
        // if (cycle < blocks.size)
        //      return blocks[cycle].
        return 0
    }

    fun logic(): String {
        return logic(0)
    }

    fun logic(cycle: Int): String {
        if (isSequence(cycle))
            return "Sequence"
        if (isRandom(cycle))
            return "Random"
        return "Unknown"
    }

    fun getSequence(cycle: Int): String? {
        if (cycle < blocks.size)
            return blocks[cycle].pattern
        return ""
    }

    fun isSequence(cycle: Int): Boolean {
        if (cycle < blocks.size)
            return blocks[cycle].logicType == 1
        return false
    }

    fun isRandom(cycle: Int): Boolean {
        if (cycle < blocks.size)
            return blocks[cycle].logicType == 2
        return false

    }
}
