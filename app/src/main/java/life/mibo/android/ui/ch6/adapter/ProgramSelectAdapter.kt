/*
 *  Created by Sumeet Kumar on 3/24/20 10:07 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/16/20 12:03 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.ch6.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import life.mibo.android.R
import life.mibo.android.models.program.Program


class ProgramSelectAdapter(var c: Context, val res: Int, val list: ArrayList<Program?>) :
    ArrayAdapter<Program>(c, res, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getView(position, convertView, parent)
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getDropDownView(position, convertView, parent)
        return createView(position, convertView, parent)
    }

    fun createView(position: Int, view: View?, parent: ViewGroup?): View {
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
        holder.duration?.text = "${item?.duration?.value?.toInt()?.div(60)} Minutes"
        holder.desc?.text = "${item?.description}"
        return row
    }

    override fun getDropDownViewTheme(): Resources.Theme? {
        return super.getDropDownViewTheme()
    }

    class Holder() {
        var name: TextView? = null
        var duration: TextView? = null
        var desc: TextView? = null
    }


}