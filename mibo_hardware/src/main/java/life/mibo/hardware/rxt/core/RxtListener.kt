package life.mibo.hardware.rxt.core

import life.mibo.hardware.rxt.RxtTile


public interface RxtListener {
    fun onDispose()

    //fun createCompositeDisposable()
    //fun startExercise(cycle: Int, duration: Int)
    fun startProgram(cycle: Int, duration: Int)

    fun nextCycle(cycle: Int, pause: Int, duration: Int)
    fun sendColorEvent(
        device: RxtTile, color: Int,
        action: Int, playerId: Int, observe: Boolean
    )

    fun sendDelayColorEvent(
        device: RxtTile, color: Int,
        action: Int, playerId: Int, delay: Int, observe: Boolean
    )

    fun endProgram(cycle: Int, duration: Int)
    fun onTime(islandId: Int, time: Long)
    fun onBlockStart(blockId: Int, cycle: Int)
    fun onCircuitProgramStart(name: String, programId: Int, pause: Int)
}