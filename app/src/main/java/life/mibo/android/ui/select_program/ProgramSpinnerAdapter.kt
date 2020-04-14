/*
 *  Created by Sumeet Kumar on 1/15/20 4:44 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 4:44 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.select_program

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import life.mibo.android.R
import life.mibo.android.models.program.Program
import life.mibo.android.ui.base.ItemClickListener


class ProgramSpinnerAdapter(
    val list: ArrayList<Program?>,
    val listener: ItemClickListener<Program>?
) : BaseAdapter() {

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val holder: Holder
        val row: View
        if (view == null) {
            holder = Holder()
            row = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_spinner_programs, null)
            holder.name = row.findViewById(R.id.tv_name_value)
            holder.duration = row.findViewById(R.id.tv_duration_value)
            holder.desc = row.findViewById(R.id.tv_desc_value)
            row.tag = holder

        } else {
            holder = view.tag as Holder
            row = view
        }
        val item = getItem(position)
        holder.name?.text = "${item?.name}"
        holder.duration?.text = "${item?.duration?.valueInt()?.div(60)} Minutes"
        holder.desc?.text = "${item?.description}"
        return row
    }

    override fun getItem(position: Int): Program? {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }

    class Holder() {
        var name: TextView? = null
        var duration: TextView? = null
        var desc: TextView? = null
    }
}