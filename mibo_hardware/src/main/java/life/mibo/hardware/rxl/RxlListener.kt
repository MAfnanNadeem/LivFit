/*
 *  Created by Sumeet Kumar on 3/12/20 5:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/9/20 8:46 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.rxl

interface RxlListener {
    fun onExerciseStart()
    fun onExerciseEnd()
    fun onCycleStart(cycle: Int, duration: Int)
    fun onCycleEnd(cycle: Int)
    fun onCyclePaused(cycle: Int, time: Int)
    fun onCycleResumed(cycle: Int)
    fun onPod(podId: Int, time: Int)
    fun onTapColorSent(playerId: Int)
    fun onExerciseResumed(cycle: Int, totalTime: Int, remaining: Int)
    fun onExercisePaused(cycle: Int, totalTime: Int, remaining: Int)
}