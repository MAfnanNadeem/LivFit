package life.mibo.hardware.rxl.core


public interface RxlListener {
    fun onDispose()

    fun startProgram(cycle: Int, duration: Int)

    fun nextCycle(cycle: Int, pause: Int, duration: Int)
    fun endProgram(cycle: Int, duration: Int)
    fun onTime(islandId: Int, time: Long)
}

public interface RxlProgramListener {
    fun onProgramStart(data: Any?)
    fun onProgramEnd(data: Any?)
    fun onBlockStart(blockId: Int, round: Int)
    fun onBlockEnd(blockId: Int, round: Int)
}

