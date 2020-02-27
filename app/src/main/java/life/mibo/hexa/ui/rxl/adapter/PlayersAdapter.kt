/*
 *  Created by Sumeet Kumar on 2/24/20 10:09 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/24/20 10:09 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener
import java.io.Serializable

class PlayersAdapter(var list: ArrayList<PlayerItem>, var listener: ItemClickListener<PlayerItem>) :
    RecyclerView.Adapter<PlayersAdapter.Holder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_select_player, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(getItem(position), listener)
    }

    private fun getItem(position: Int): PlayerItem {
        return list[position]
    }

    fun update(items: ArrayList<PlayerItem>) {
        if (items.isNotEmpty()) {
            list.clear()
            list.addAll(items)
            notifyDataSetChanged()
        }
    }

    fun updateColor(item: PlayerItem, color: Int) {
        Logger.e("updateColor id $item , color $color")
        if (list.isNotEmpty()) {
            var pos = -1
            list.forEachIndexed { i, it ->
                Logger.e("updateColor match.... id $item , it.id ${it.id}")
                if (it.id == item.id) {
                    it.playerColor = color
                    it.playerName = item.playerName
                    pos = i
                }
            }
            Logger.e("updateColor pos $pos")
            if (pos >= 0) {
                notifyItemChanged(pos)
                Logger.e("updateColor pos >= 0 $pos")
            }
        }
    }

    fun updateError(id: Int, error: Int) {
        if (list.isNotEmpty()) {
            var pos = -1
            list.forEachIndexed { i, it ->
                Logger.e("updateColor match.... id $id , it.id ${it.id}")
                if (it.id == id) {
                    it.playerError = error
                    pos = i
                }
            }
            Logger.e("updateColor pos $pos")
            if (pos >= 0) {
                notifyItemChanged(pos)
                Logger.e("updateColor pos >= 0 $pos")
            }
        }
    }


    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        var name: AppCompatEditText = view.findViewById(R.id.player_name)
        var image: ImageView = view.findViewById(R.id.player_color)
        var data: PlayerItem? = null

        fun bind(item: PlayerItem?, listener: ItemClickListener<PlayerItem>?) {
            if (item == null)
                return
            data = item
            name.hint = item.playerHint
            name.setText(item.playerName)

            if (item.playerError != 0) {
                name?.error = name.context?.getString(item.playerError)
                item.playerError = 0
                //showError(name.context?.getString(item.playerError), name)
            }

            name?.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    if (name?.error != null)
                        name?.error = null
                    data?.playerName = s.toString()
                }
            })

            if (item.playerColor != 0) {
                image.setImageDrawable(ColorDrawable(item.playerColor))
                Logger.e("Holder bind item $item color change")
            } else {
                image.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            image.setOnClickListener {
                listener?.onItemClicked(item, 2)
            }
        }

//        private fun showError(error: String?, editText: TextView?) {
//            editText?.error = error
//            editText?.addTextChangedListener(object : TextWatcher {
//
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//
//                override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                }
//
//                override fun afterTextChanged(s: Editable) {
//                    if (editText?.error != null)
//                        editText?.error = null
//                }
//            })
//        }
    }

    data class PlayerItem(
        var id: Int,
        var playerName: String,
        var playerHint: String,
        var playerError: Int = 0,
        var playerColor: Int = 0
    ) : Serializable

}