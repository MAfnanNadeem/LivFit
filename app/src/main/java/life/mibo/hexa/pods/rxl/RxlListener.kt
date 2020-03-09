/*
 *  Created by Sumeet Kumar on 3/4/20 4:58 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/4/20 4:58 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl

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