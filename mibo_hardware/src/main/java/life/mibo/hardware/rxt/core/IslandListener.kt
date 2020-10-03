package life.mibo.hardware.rxt.core

public interface IslandListener {
    fun onProgramStart(data: Any?)
    fun onProgramEnd(data: Any?)
    fun onBlockStart(blockId: Int, round: Int)
    fun onBlockEnd(blockId: Int, round: Int)
    fun onCircuitProgramStart(name: String, program: Int, pause: Int)
    fun onCircuitProgramEnd(name: String, program: Int, pause: Int)
}