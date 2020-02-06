/*
 *  Created by Sumeet Kumar on 1/27/20 10:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 10:04 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.impl

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener
import java.io.Serializable

class ReflexDialog(
    c: Context,
    val list: ArrayList<Item?>,
    val title: String,
    var listener: ItemClickListener<Item>?, var type: Int = -1
) : AlertDialog(c) {

    private var recyclerView: RecyclerView? = null
    private var dialogAdapter: DialogAdapter? = null
    private var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog_program)
        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.tv_title)
        val close: View? = findViewById(R.id.iv_cancel)

        window?.attributes?.windowAnimations = R.style.DialogAnimation;

        recyclerView?.layoutManager = LinearLayoutManager(context)
        dialogAdapter = DialogAdapter(list)
        textView?.text = "$title"
        val height = context.resources.displayMetrics.heightPixels
//            if (dialogHeight > height.times(0.7)) {
//                window?.setLayout(dialogWidth, height.times(0.7).toInt())
//            }
        if (recyclerView?.layoutParams?.height!! > height.times(0.7).toInt())
            recyclerView?.layoutParams?.height = height.times(0.7).toInt()
        Logger.e("height $height")

        dialogAdapter?.setListener(object : ItemClickListener<Item> {
            override fun onItemClicked(item: Item?, position: Int) {
                listener?.onItemClicked(item, type)
                dismiss()
            }
        })
        recyclerView?.adapter = dialogAdapter
        close?.setOnClickListener {
            dismiss()
        }

    }

    override fun show() {
        //window?.attributes?.windowAnimations = R.style.DialogAnimation;
        super.show()
    }

    data class Item(val pos: Int, val title: String) : Serializable {

    }

    class DialogAdapter(var list: ArrayList<Item?>) :
        RecyclerView.Adapter<DialogAdapter.Holder>() {

        //var list: ArrayList<Item>? = null
        private var listener: ItemClickListener<Item>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.simple_list_item1, parent, false
                )
            )
        }

        fun setListener(listener: ItemClickListener<Item>) {
            this.listener = listener
        }

        override fun getItemCount(): Int {
            return list.size
        }

        private fun getItem(position: Int): Item? {
            return list?.get(position)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(getItem(position), listener)
        }

        class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var name: TextView? = itemView.findViewById(R.id.text1)

            fun bind(item: Item?, listener: ItemClickListener<Item>?) {
                if (item == null)
                    return
                name?.text = item.title
                name?.setOnClickListener {
                    listener?.onItemClicked(item, adapterPosition)
                }
            }

        }
    }

}