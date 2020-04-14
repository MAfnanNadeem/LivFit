/*
 *  Created by Sumeet Kumar on 4/5/20 6:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/24/20 10:07 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.ch6.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.android.R
import life.mibo.android.models.program.Program
import life.mibo.android.ui.base.ItemClickListener


class ProgramAdapter(val list: ArrayList<Program?>, var listener: ItemClickListener<Program>?) :
    RecyclerView.Adapter<ProgramAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_select_programs, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        list?.get(position)?.let {
            holder.bind(it, listener)
        }
    }

    fun select(id: Int?) {
        if (id == null)
            return
        for (p in list) {
            if (id == p?.id) {
                p.isSelected = true
            } else p?.isSelected = false
        }

        notifyDataSetChanged()
    }


    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView? = view.findViewById(R.id.tv_name_value)
        var duration: TextView? = view.findViewById(R.id.tv_duration_value)
        var desc: TextView? = view.findViewById(R.id.tv_desc_value)
        var child: View? = view.findViewById(R.id.childView)
        var selected: View? = view.findViewById(R.id.selectedView)

        fun bind(program: Program, listener: ItemClickListener<Program>?) {
            //Logger.e("Program Holder bind $program")
            name?.text = "${program?.name}"
            duration?.text = "${program?.duration?.value?.toInt()?.div(60)} Minutes"
            desc?.text = "${program?.description}"

            if (program.isSelected)
                selected?.visibility = View.VISIBLE
            else
                selected?.visibility = View.INVISIBLE

            itemView?.setOnClickListener {
               // program.isSelected = program.isSelected.not()
                listener?.onItemClicked(program, adapterPosition)
            }
            child?.setBackgroundResource(getBg(adapterPosition.plus(1)))

        }

        private fun getBg(position: Int): Int {
            Logger.e("Program Holder getBg $position")
            if ((position % 3) == 0)
                return R.drawable.bg_select_program_3
            if ((position % 2) == 0)
                return R.drawable.bg_select_program_2
            return R.drawable.bg_select_program_1
        }
    }


}