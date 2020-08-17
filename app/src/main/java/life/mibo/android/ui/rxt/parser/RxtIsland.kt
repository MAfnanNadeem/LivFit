package life.mibo.android.ui.rxt.parser

import kotlin.random.Random


data class RxtIsland(var id: Int, var program: RxtProgram, var tiles: List<RxtTile>, var name: String = "", var playerName: String = "", var color: Int = 0) {

    var isTapReceived: Boolean = false
    var isStarted: Boolean = false
    var events = ArrayList<Event>()
    var wrongEvents = ArrayList<Event>()


    fun color(): Int {
        if (color == 0)
            color = program.color
        return color
    }

    var currentCycle = 0
    fun getDuration(): Int {
        return program.duration(currentCycle)
    }

    fun getAction(): Int {
        return program.action(currentCycle)
    }

    fun getPause(): Int {
        return program.pause(currentCycle)
    }

    fun getCycles(): Int {
        return program.blocks.size
    }

    fun getTileCount(): Int {
        return tiles.size
    }

    fun hasNextCycle(): Boolean {
        return currentCycle < program.blocks.size
    }

    fun first(): RxtBlock {
        return program.blocks.get(0)
    }

//    fun next(): RxtBlock {
//        currentCycle++
//        return program.blocks.get(currentCycle)
//    }

    fun nextBlock(): RxtBlock? {
        currentCycle++
        if (currentCycle < program.blocks.size)
            return program.blocks.get(currentCycle)
        return null
    }

    fun currentBlock(): RxtBlock {
        return program.blocks.get(currentCycle)
    }

    // for test
    private var currentTile = -1;
    private var tileCount = 0;

    fun nextTile(): RxtTile {
        currentTile++
        if (currentTile >= tiles.size)
            currentTile = 0
        return tiles[currentTile]
    }

    fun nextTile(id: Int): RxtTile {
        if (id < tiles.size)
            return tiles[id]
        return tiles[0]
    }

    fun next(id: Int): RxtTile {
        return tiles[id]
    }

    fun nextSeq(): RxtTile {
        currentTile++
        if (currentTile >= tiles.size)
            currentTile = 0
        return tiles[currentTile]
    }

    fun getLastTile() = currentTile

    fun initCount() {
        tileCount = tiles.size
    }

    fun nextRandomTile(): RxtTile {
        var id = Random.nextInt(tileCount)
        if (id == currentTile)
            id = Random.nextInt(tileCount)
        currentTile = id
        if (currentTile >= tileCount)
            currentTile = 0
        return tiles[currentTile]
    }

}