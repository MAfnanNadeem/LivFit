/*
 *  Created by Sumeet Kumar on 6/29/20 4:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/29/20 4:11 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_share.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseActivity
import life.mibo.hardware.events.IndicationEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class TestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_share)
    }

    fun update(msg: String) {
        runOnUiThread {
            text_share?.text = msg
        }
    }


    @Subscribe
    public fun onIndicationEvent(event: IndicationEvent) {
        try {
            // update("${event.data}")
            var s = ""
            for (d in event.data) {
                //d.toInt().to
                s += d.toInt().toString(2) + " \n"
            }
            //s+= event.data[1].toInt().toString(10)
            s += "\n " + getWeight(event.data) + " KG"
            //s+= event.data[1].toInt().toString(10)
            update("$s")
        } catch (e: Exception) {
            update("${event.data.contentToString()}")

        }
    }

    fun getWeight(command: ByteArray): Double {
        val unit = command[0].toInt() shr 0 and 1
        val kg = unit == 0
        return getWeight(command, kg)
    }

    fun getWeight(command: ByteArray, kg: Boolean): Double {
        if (command.size > 2) {
            val unit = command[0].toInt() shr 0 and 1
            if (kg) {
                val a: Int = (command[1].toInt() and 0xff)
                var b: Int = (command[2].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.005)
            } else {
                val a: Int = (command[1].toInt() and 0xff)
                var b: Int = (command[2].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.01)
            }

        }
        return 0.0
    }

    fun getHeight(command: ByteArray): Double {
        if (command.size > 2) {
            val unit = command[0].toInt() shr 0 and 1
            if (unit == 0) {
                val a: Int = (command[6].toInt() and 0xff)
                var b: Int = (command[7].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.001)
            } else {
                val a: Int = (command[6].toInt() and 0xff)
                var b: Int = (command[7].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.1)
            }

        }
        return 0.0
    }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }


}