/*
 *  Created by Sumeet Kumar on 3/10/20 8:55 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/10/20 8:54 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.utils.Utils
import java.io.Serializable


class ScoreAdapter(var list: ArrayList<ScoreItem>) : RecyclerView.Adapter<ScoreAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_rxl_score, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun getItem(position: Int): ScoreItem {
        return list[position]
    }

    fun update(items: ArrayList<ScoreItem>) {
        if (items.isNotEmpty()) {
            list.clear()
            list.addAll(items)
            notifyDataSetChanged()
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var view1: View? = view.findViewById(R.id.view1)
        var view2: View? = view.findViewById(R.id.view2)

        // View1
        var hexa: ImageView? = view.findViewById(R.id.hexa_image)
        var image: ImageView? = view.findViewById(R.id.iv_image)
        var name: TextView? = view.findViewById(R.id.tv_name)
        var count: TextView? = view.findViewById(R.id.tv_count)

        // View2
        var imageViewBg: ImageView? = view.findViewById(R.id.imageViewBg)

        //var img_user_bg: ImageView = view.findViewById(R.id.img_user_bg)
        //var img_user: ImageView = view.findViewById(R.id.img_user)
        //var image_hits: ImageView = view.findViewById(R.id.image_hits)
        var playerName: TextView? = view.findViewById(R.id.tv_player_name)
        var hits: TextView? = view.findViewById(R.id.tv_hits_count)
        var missed: TextView? = view.findViewById(R.id.tv_missed_count)

        // var data: ScoreItem? = null

        fun bind(item: ScoreItem?) {
            if (item == null)
                return
            Logger.e("ScoreAdapter ${item.print()}")
            // data = item
            if (item.isMulti) {
                if (item.isInitial) {
                    view1?.visibility = View.VISIBLE
                    view2?.visibility = View.GONE
                    if (item.hexaRes != 0)
                        hexa?.setImageResource(item.hexaRes)
                    if (item.imageRes != 0)
                        image?.setImageResource(item.imageRes)
                    if (item.nameRes != 0)
                        name?.setText(item.nameRes)
                    else name?.text = item.name
                    count?.text = item.count

                } else {
                    view2?.visibility = View.VISIBLE
                    view1?.visibility = View.GONE
                    if (item.playerColor != 0)
                        imageViewBg?.background = getDrawable(
                            item.playerColor, Color.DKGRAY,
                            Utils.dpToPixel(2, imageViewBg?.context),
                            Utils.dpToPixel(12, imageViewBg?.context)
                        )
                    playerName?.setText(item?.name)
                    hits?.setText(item.count)
                    missed?.setText(item.missedCount)

                }

            } else {
                view1?.visibility = View.VISIBLE
                view2?.visibility = View.GONE

                if (item.hexaRes != 0)
                    hexa?.setImageResource(item.hexaRes)
                if (item.imageRes != 0)
                    image?.setImageResource(item.imageRes)
                if (item.nameRes != 0)
                    name?.setText(item.nameRes)
                else name?.setText(item.name)
                count?.setText(item.count)

            }
        }

        fun getDrawable(
            color: Int,
            strokeColor: Int,
            strokeWidth: Int,
            corner: Int
        ): GradientDrawable {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(color)
            gradientDrawable.cornerRadius = corner.toFloat()
            gradientDrawable.setStroke(strokeWidth, strokeColor)
            return gradientDrawable
        }

    }

    class ScoreItem(
        var playerId: Int, var name: String, var count: String,
        var imageRes: Int, var hexaRes: Int, var playerColor: Int = 0, var nameRes: Int = 0
    ) : Serializable {

        constructor(
            playerId: Int, playerName: String, hitCount: String,
            playerColor: Int, missedCount: String,
            imageRes: Int = 0, playerImageRes: Int = 0, initial: Boolean = false
        ) : this(playerId, playerName, hitCount, imageRes, 0, playerColor, 0) {
            this.isInitial = initial
            this.missedCount = missedCount
            isMulti = true
        }

        fun initial(initial: Boolean): ScoreItem {
            this.isInitial = initial
            return this
        }

        fun print(): String {
            return "ScoreItem(playerId=$playerId, name='$name', count='$count', imageRes=$imageRes, hexaRes=$hexaRes, playerColor=$playerColor, nameRes=$nameRes, missedCount='$missedCount', totalTime=$totalTime, isMulti=$isMulti, isInitial=$isInitial)"
        }

        var missedCount = "0"
        var totalTime = 0
        var isMulti = false
        var isInitial = false


    }
}