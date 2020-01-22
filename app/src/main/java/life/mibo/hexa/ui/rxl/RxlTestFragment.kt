/*
 *  Created by Sumeet Kumar on 1/12/20 8:07 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 8:07 AM
 *  Mibo Hexa - app 
 */

package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.fragment_rxl_test.*
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.models.Device
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.Navigator
import life.mibo.views.ColorSeekBar
import org.greenrobot.eventbus.EventBus

class RxlTestFragment : BaseFragment() {

    var selected: Device? = null
    var selectedId = -1

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View? {
        super.onCreateView(i, c, b)

        val view: View? = i.inflate(R.layout.fragment_rxl_test, c, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorSelectListener(color: Int) {
                tv_device_color?.setTextColor(color)
                val d = SessionManager.getInstance().userSession.rxl
                for (i in d) {
                    if (i != null) {
                        i.colorPalet = color
                        EventBus.getDefault().postSticky(ChangeColorEvent(i, i.uid))
                    }
                }
            }

            override fun onColorChangeListener(color: Int) {

            }

        })

        button_lights_on.setOnClickListener {

        }

        button_lights_off.setOnClickListener {

        }

        button_commands.setOnClickListener {

        }

        button_disconnect.setOnClickListener {
            navigate(Navigator.DISCONNECT, SessionManager.getInstance().userSession.booster)
        }

        button_device_change.setOnClickListener {

            showRxlDialog()
        }
        list.addAll(SessionManager.getInstance().userSession.rxl)
    }

    val list = ArrayList<Device>()

    var selectPosition = 0
    private fun showRxlDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Choose an animal")
        //val list = SessionManager.getInstance().userSession.rxl
        val array = arrayOfNulls<String>(list.size)
       // val array = ArrayList<String>()
        list.forEachIndexed { i, d ->
            array[i] = " $i ${d.name}"
        }


        builder.setSingleChoiceItems(array, 0) { dialog, which ->
            selectPosition = which
        }

        builder.setPositiveButton("CLOSE") { dialog, which ->
            // user clicked OK
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}