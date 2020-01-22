/*
 *  Created by Sumeet Kumar on 1/21/20 8:42 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/21/20 8:42 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

import android.content.Context
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Pods(var context: Context, val type: PodType = PodType.UNKNOWN) {

    enum class PodType {
        PODS_4, PODS_6, PODS_10, UNKNOWN, DYNAMIC
    }

    var list = ArrayList<Pod>()

    var is4 = false
    var is6 = false

    private var lastActivePod = -1
    var disposable: Disposable? = null

    fun add(pod: Pod) {
        list.add(pod)
    }

    fun addAll(pod: Array<Pod>) {
        list.clear()
        list.addAll(pod)
    }

    fun addAll(pod: List<Pod>) {
        list.clear()
        list.addAll(pod)
    }

    fun start(exercise: PodExercise) {

    }

    fun startPods(interval: Long, count: Long) {
        log("startPods type:$type $interval $count ")
        when (type) {
            PodType.PODS_4 -> {
                disposable = Observable.interval(interval, TimeUnit.SECONDS)
                    .doOnNext {
                        lightOff(lastActivePod)
                        lightOn(next())
                    }.takeUntil { i ->
                        i == count
                    }.doOnComplete {
                        disposable?.dispose()
                    }.subscribe()
            }
            PodType.PODS_6 -> {

            }
            PodType.DYNAMIC -> {
                disposable = Observable.interval(interval, TimeUnit.SECONDS)
                    .doOnNext {
                        lightOff(lastActivePod)
                        lightOn(next())
                    }.takeUntil { i ->
                        i == count
                    }.doOnComplete {
                        disposable?.dispose()
                    }.subscribe()
            }
            else -> {

            }
        }
    }

    fun stopPods() {
        disposable?.dispose()
        disposable = null
        log("stopPods")
    }

    private fun random(): Int {
        return when (type) {
            PodType.PODS_4 -> {
                Random(4).nextInt()
            }
            PodType.PODS_6 -> {
                Random(6).nextInt()
            }

            PodType.DYNAMIC -> {
                if (list.size > 0)
                    Random(list.size).nextInt()
                return -1
            }
            else -> {
                return -1
            }
        }
    }

    fun next(): Int {
        var id = random()
        if (lastActivePod == id)
            id = random();
        return id
    }

    private fun checkPod(id: Int): Boolean {
        if (id >= 0 && id < list.size)
            return true
        //lightOn(list[id])
        return false
    }

    private fun lightOn(pod: Int) {
        if (checkPod(pod))
            lightOn(list[pod])
        log("lightOn $pod")
    }

    fun lightOn(pod: Pod?) {
        if (pod == null)
            return
        EventBus.getDefault().postSticky(PodEvent(pod.uid, pod, true))
        log("lightOn EventBus $pod")
    }

    private fun lightOff(pod: Int) {
        if (checkPod(pod))
            lightOff(list[pod])
        log("lightOff $pod")
    }

    fun lightOff(pod: Pod?) {
        if (pod == null)
            return
        EventBus.getDefault().postSticky(PodEvent(pod.uid, pod, false))
        log("lightOff EventBus $pod")
    }

    fun turnOn(pod: Int) {
        lightOn(pod)
    }

    fun turnOff(pod: Int) {
        lightOff(pod)
    }


    fun start() {
        EventBus.getDefault().register(this)
    }

    fun stop() {
        EventBus.getDefault().unregister(this)
        //if(::disposable.isInitialized)
        disposable?.dispose()
    }

    fun log(msg: String?) {
        life.mibo.hardware.core.Logger.e("PODs - $msg")
    }
}