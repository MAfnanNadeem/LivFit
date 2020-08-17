package life.mibo.android.ui.rxt.parser

import life.mibo.android.models.workout.RXT


data class RxtProgram(var name: String, var color: Int, var delay: Int = 0, var blocks: List<RxtBlock>) {

    companion object {
        fun from(rxt: RXT): RxtProgram {
            val list = ArrayList<RxtBlock>()
            val blocks = rxt.blocks
            if (blocks != null && blocks.isNotEmpty()) {
                for (i in blocks) {
                    if (i != null) {
                        //i.pattern = "1,2-3,4,5-6,7,8,9-10,8,7,6-5,4,3-2"
                        i.rXTAction = 2
                        val b = RxtBlock(i.getAction(), i.getDuration(), i.getLogicType(), 0, i.pattern
                                ?: "")
                        b.delay = i.getDelay()
                        b.round = i.getRounds()
                        list.add(b)
                    }
                }
            }
            return RxtProgram("Rxt ${rxt.category}", 0, 0, list)
        }
    }

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