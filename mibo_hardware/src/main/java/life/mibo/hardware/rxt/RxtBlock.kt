package life.mibo.hardware.rxt

data class RxtBlock(var action: Int, var duration: Int, var logicType: Int, var color: Int = 0, var pattern: String = "") {

    private var sequence: IntArray = IntArray(1)
    var round = 1;
    var delay = 0;
    var blockPause = 0;
    //var workoutPause = 0;

    fun sequence(tiles: List<RxtTile>) {
        if (isSequence()) {
            if (pattern == null || pattern.isEmpty()) {
                val size = tiles.size
                sequence = IntArray(size)
                for (i in 0 until size) {
                    sequence[i] = i.plus(1)
                }
            } else {
                val s = pattern.split(",")
                sequence = IntArray(s.size)
                s.forEachIndexed { index, i ->
                    sequence[index] = getSeq(i)
                }
            }
        }
    }

   private fun getSeq(s: String?): Int {
        return try {
            s!!.toInt()
        } catch (e: Exception) {
            1
        }
    }

    fun setSequence() {
        logicType = 1;
    }

    fun setRandom() {
        logicType = 2;
    }

    fun isSequence(): Boolean {
        return logicType == 1
    }

    fun isRandom(): Boolean {
        return logicType == 2
    }
}