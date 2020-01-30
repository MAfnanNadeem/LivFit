/*
 *  Created by Sumeet Kumar on 1/28/20 10:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 4:33 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

import android.graphics.Color
import life.mibo.hexa.pods.pod.*

class PodExercise(var name: String) {

    var id: Int = -1
    var unit: String = "ms"
    var logic: LightLogic = LightLogic.SEQUENCE
    var players: Players = Players.SINGLE
    var program: Program = Program.BALANCED
    var sequence: Sequence = Sequence()
    var pause: Pause = Pause(0)
    var delay: Delay = Delay(0)
    var colors: Colors = Colors()
    var cycles: Cycles = Cycles(1, pause)
    var duration: Duration = Duration(0, 0, cycles)
    var trigger: TriggerLogic = TriggerLogic.NOW
    var workingStaions: WorkingStaions =
        WorkingStaions(0)
    var podsType: PodType = PodType.DYNAMIC

    fun add(trigger: TriggerLogic): PodExercise {
        this.trigger = trigger
        return this
    }

    fun add(logic: LightLogic): PodExercise {
        this.logic = logic
        return this
    }

    fun add(players: Players): PodExercise {
        this.players = players
        return this
    }

    fun add(program: Program): PodExercise {
        this.program = program
        return this
    }

    fun add(sequence: Sequence): PodExercise {
        this.sequence = sequence
        return this
    }

    fun add(duration: Duration): PodExercise {
        this.duration = duration
        return this
    }

    fun add(colors: Colors): PodExercise {
        this.colors = colors
        return this
    }

    fun add(cycles: Cycles): PodExercise {
        this.cycles = cycles
        return this
    }

    fun add(delay: Delay): PodExercise {
        this.delay = delay
        return this
    }

    fun add(pause: Pause): PodExercise {
        this.pause = pause
        return this
    }


    fun getTime(time: Int, unit: String): Int {
        if (unit == "s")
            time.times(1000)
        return time
    }

    companion object {
        fun getExercise1(): PodExercise {

            return PodExercise("Exercise 1")
                .add(Players.SINGLE).add(Delay(0))
                .add(LightLogic.SEQUENCE)
                .add(Duration(20, 3, Cycles(4, Pause(10))))
                .add(Colors(Color.GREEN))
        }

        fun getExercise2(): PodExercise {

            return PodExercise("Exercise 1")
                .add(Players.SINGLE).add(Delay(0))
                .add(LightLogic.RANDOM)
                .add(Duration(60, 10, Cycles(3, Pause(10))))
                .add(Colors(Color.GREEN))
        }
        fun getExercise3(): PodExercise {

            return PodExercise("Exercise 3")
                .add(Players.SINGLE).add(Delay(0))
                .add(LightLogic.SEQUENCE)
                .add(Duration(10, 3, Cycles(4, Pause(5))))
                .add(Colors(Color.GREEN))
        }

        fun getExercise(duration: Int, actionTime: Int, pauseTime: Int, cycles: Int): PodExercise {

            return PodExercise("Exercise 3")
                .add(Players.SINGLE).add(Delay(0))
                .add(LightLogic.SEQUENCE)
                .add(Duration(duration, actionTime, Cycles(cycles, Pause(pauseTime))))
                .add(Colors(Color.GREEN))
        }
    }

}