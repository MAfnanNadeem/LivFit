/*
 *  Created by Sumeet Kumar on 1/16/20 12:10 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/16/20 12:10 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.select_program

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.utils.Utils

class ProgramDialog(
    c: Context,
    val list: ArrayList<Program?>,
    val listener: ItemClickListener<Program>,
    val type: Int
) :
    AlertDialog(c) {

    companion object {
        val COLORS = 2
        val PROGRAMS = 1
    }

    var recyclerView: RecyclerView? = null
    var programAdpter: ProgramDialogAdapter? = null
    var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog_program)
        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.tv_title)
        var cancel: View? = findViewById(R.id.iv_cancel)

        if (type == 1) {
            recyclerView?.layoutManager = LinearLayoutManager(context)
            programAdpter = ProgramDialogAdapter(list, type)
            textView?.text = "Select Program"
//            val lp = WindowManager.LayoutParams()
//            lp.copyFrom(window?.attributes)
//            val dialogWidth = lp.width
//            val dialogHeight = lp.height
            val height = context.resources.displayMetrics.heightPixels
//            if (dialogHeight > height.times(0.7)) {
//                window?.setLayout(dialogWidth, height.times(0.7).toInt())
//            }
            recyclerView?.layoutParams?.height = height.times(0.7).toInt()
            Logger.e("height $height")
        } else {
            getColors()
            textView?.text = "Select Color"

            recyclerView?.layoutManager = GridLayoutManager(context, 4)
            programAdpter = ProgramDialogAdapter(list, type)

        }
        programAdpter?.setListener(object : ItemClickListener<Program> {
            override fun onItemClicked(item: Program?, position: Int) {
                listener?.onItemClicked(item, position)
                dismiss()
            }

        })
        recyclerView?.adapter = programAdpter
        cancel?.setOnClickListener {
            dismiss()
        }

    }

    private fun getColors() {
        list.clear()
        list.addAll(Utils.getColors())
//        list.add(Program(0xFFFF0000.toInt()))
//        list.add(Program(0xFF00FF00.toInt()))
//        list.add(Program(0xFF0000FF.toInt()))
//        list.add(Program(0xFFFFFF00.toInt()))
//        list.add(Program(0xFFFF00FF.toInt()))
//        list.add(Program(0xFF00b75b.toInt()))
//        list.add(Program(0xFF800000.toInt()))
//        list.add(Program(0xFF808000.toInt()))
//        list.add(Program(0xFF000080.toInt()))
//        list.add(Program(0xFF800080.toInt()))
//        list.add(Program(0xFF008080.toInt()))
//        list.add(Program(0xFFa7d129.toInt()))
//        list.add(Program(0xFF111111.toInt()))
//        list.add(Program(0xFFfa8072.toInt()))
//        list.add(Program(0xFFFFFFFF.toInt()))
    }


    fun showPrograms() {
        //type = 1;
        super.show()
    }

    fun showColors() {
        //type = 2;
        super.show()
    }
}