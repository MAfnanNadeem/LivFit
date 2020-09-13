/*
 *  Created by Sumeet Kumar on 2/11/20 2:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 12:46 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.models.rxl.RxlProgram
import life.mibo.android.models.workout.RXL
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.hardware.core.Logger
import java.util.*


class RxlBlocksAdapter(var list: ArrayList<RXL.RXLBlock?>?) :
    RecyclerView.Adapter<RxlBlocksAdapter.BlockHolder>() {

    //var list: ArrayList<Item>? = null
    private var listener: ItemClickListener<RxlProgram>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockHolder {
        return BlockHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_rxl_blocks,
                parent,
                false
            )
        )
    }

    fun setListener(listener: ItemClickListener<RxlProgram>) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): RXL.RXLBlock? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: BlockHolder, position: Int) {
        Logger.e("ReflexAdapter: onBindViewHolder $position")

        holder.bind(getItem(position), listener)
    }


    class BlockHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = itemView.findViewById(R.id.tv_name)
        var duration: TextView? = itemView.findViewById(R.id.tv_select_duration)
        var pause: TextView? = itemView.findViewById(R.id.tv_select_pause)
        var cycles: TextView? = itemView.findViewById(R.id.tv_select_cycles)
        var action: TextView? = itemView.findViewById(R.id.tv_select_action)


        fun bind(item: RXL.RXLBlock?, listener: ItemClickListener<RxlProgram>?) {
            Logger.e("ReflexHolder $item")
            if (item == null)
                return
            name?.text = "" + item.rXLType
            duration?.text = "" + item.rXLTotalDuration
            pause?.text = "" + item.rXLPause
            cycles?.text = "" + item.rXLRound
            action?.text = "" + item.rXLAction

        }

    }
}