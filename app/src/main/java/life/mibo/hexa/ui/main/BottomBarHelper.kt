/*
 *  Created by Sumeet Kumar on 1/14/20 4:46 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/14/20 4:45 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.main

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import life.mibo.hexa.ui.base.ItemClickListener

class BottomBarHelper() : View.OnClickListener {

    override fun onClick(v: View?) {
        when (v) {
            item1 -> {
                select(1)
            }
            item2 -> {
                select(2)
            }
            item3 -> {
                select(3)
            }
            item4 -> {
                select(4)
            }
        }
    }

    var item1: ImageView? = null
    var item2: ImageView? = null
    var item3: ImageView? = null
    var item4: ImageView? = null
    var listener: ItemClickListener<Any>? = null

    fun register(view1: ImageView, view2: ImageView, view3: ImageView, view4: ImageView) {
        item1 = view1
        item2 = view2
        item3 = view3
        item4 = view4

        item1?.setOnClickListener(this)
        //item2?.setOnClickListener(this)
        //item3?.setOnClickListener(this)
        //item4?.setOnClickListener(this)
        unselect()
    }

    fun hide() {
        if(::barView.isInitialized)
            barView.visibility = View.GONE

    }

    fun show() {
        if(::barView.isInitialized)
            barView.visibility = View.VISIBLE

    }

    lateinit var barView: View
    fun bind(view: View?) {
        if (view != null)
            barView = view
    }

    private fun select(position: Int) {
        unselect()
        when (position) {
            0 -> {
                item1?.setColorFilter(Color.BLUE)
            }
            1 -> {
                item1?.setColorFilter(Color.parseColor("#09B189"))
            }
            2 -> {
                item2?.setColorFilter(Color.parseColor("#09B189"))
            }
            3 -> {
                item3?.setColorFilter(Color.parseColor("#09B189"))
            }
            4 -> {
                item4?.setColorFilter(Color.parseColor("#09B189"))
            }
        }
        listener?.onItemClicked(null, position)
    }

    private fun unselect() {
        item1?.setColorFilter(Color.GRAY)
        item2?.setColorFilter(Color.GRAY)
        item3?.setColorFilter(Color.GRAY)
        item4?.setColorFilter(Color.GRAY)
    }


}