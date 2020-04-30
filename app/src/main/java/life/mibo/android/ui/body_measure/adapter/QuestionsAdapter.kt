/*
 *  Created by Sumeet Kumar on 4/22/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 12:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Utils

class QuestionsAdapter(
    var type: Int,
    var list: List<Item>,
    var listener: ItemClickListener<Item>
) :
    RecyclerView.Adapter<QuestionsAdapter.Holder>() {

    //var list: ArrayList<Item>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var item = LayoutInflater.from(parent.context)
            .inflate(
                if (type == 2) R.layout.list_item_goal_questions_activity else R.layout.list_item_goal_questions,
                parent,
                false
            )
        return Holder(
            item
        )

    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Item? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), listener, type == 2)
    }

    fun select(item: Item?) {
        if (item != null) {
            for (i in list) {
                i.selected = i.id == item.id
            }
            notifyDataSetChanged()
        }

    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.tv_title)
        var desc: TextView? = itemView.findViewById(R.id.tv_desc)
        var image1: ImageView? = itemView.findViewById(R.id.activity_image1)
        var image2: ImageView? = itemView.findViewById(R.id.activity_image2)
        var imageView: ImageView? = itemView.findViewById(R.id.imageView)

        // var hexa: HexagonImageView? = itemView.findViewById(R.id.test_image_hexa)
        fun bind(
            item: Item?,
            listener: ItemClickListener<Item>?,
            activity: Boolean
        ) {
            if (item != null) {
                text?.text = item.title
                desc?.text = item.desc
                if (activity) {
//                    if (item.selected) {
//                        text?.setTextColor(Color.WHITE)
//                        //desc?.setTextColor(Color.WHITE)
//                        imageView?.setImageResource(R.drawable.bg_goal_gues_selected)
//                    } else {
//                        text?.setTextColor(
//                            ContextCompat.getColor(
//                                text?.context!!,
//                                R.color.colorPrimary
//                            )
//                        )
//                        //desc?.setTextColor(ContextCompat.getColor(text?.context!!, R.color.textColor))
//                        imageView?.setImageResource(R.drawable.bg_item_physical_activity)
//                    }
                    //text?.setTextColor(Color.WHITE)
                    setActivity(item.id, item.isMale, item.selected)
                } else {
                    if (item.selected) {
                        text?.setTextColor(Color.WHITE)
                        //desc?.setTextColor(Color.WHITE)
                        imageView?.setImageResource(R.drawable.bg_goal_gues_selected)
                    } else {
                        text?.setTextColor(
                            ContextCompat.getColor(
                                text?.context!!,
                                R.color.colorPrimary
                            )
                        )
                        //desc?.setTextColor(ContextCompat.getColor(text?.context!!, R.color.textColor))
                        imageView?.setImageResource(R.drawable.bg_goal_gues_unselected)
                    }

                }
                imageView?.setOnClickListener {
                    //item?.selected = item?.selected?.not()
                    listener?.onItemClicked(item, adapterPosition)
                }
            }
        }

        private fun setActivity(type: Int, male: Boolean, selected: Boolean) {
            val id = R.drawable.bg_item_physical_activity
            when (type) {
                1 -> {
                    if (selected) {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0xFFFE3030.toInt())
                        )
                    } else {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0x80FE3030.toInt())
                        )
                    }
                    if (male) {
                        image1?.setImageResource(R.drawable.ic_activity_1_1)
                        image2?.setImageResource(R.drawable.ic_activity_1_2)

                    } else {
                        image1?.setImageResource(R.drawable.ic_activity_female_1_1)
                        image2?.setImageResource(R.drawable.ic_activity_female_1_2)
                    }
                }
                2 -> {
                    if (selected) {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0xFFF68D19.toInt())
                        )
                    } else {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0x80F68D19.toInt())
                        )
                    }
                    if (male) {
                        image1?.setImageResource(R.drawable.ic_activity_2_1)
                        image2?.setImageResource(R.drawable.ic_activity_2_2)

                    } else {
                        image1?.setImageResource(R.drawable.ic_activity_female_2_1)
                        image2?.setImageResource(R.drawable.ic_activity_female_2_2)
                    }
                }
                3 -> {
                    if (selected) {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0xFF86D61D.toInt())
                        )
                    } else {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0x8086D61D.toInt())
                        )
                    }
                    if (male) {
                        image1?.setImageResource(R.drawable.ic_activity_3_1)
                        image2?.setImageResource(R.drawable.ic_activity_3_2)
                    } else {
                        image1?.setImageResource(R.drawable.ic_activity_female_3_1)
                        image2?.setImageResource(R.drawable.ic_activity_female_3_2)
                    }
                }
                4 -> {
                    if (selected) {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0xFF00BE34.toInt())
                        )
                    } else {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0x8000BE34.toInt())
                        )
                    }
                    if (male) {
                        image1?.setImageResource(R.drawable.ic_activity_4_1)
                        image2?.setImageResource(R.drawable.ic_activity_4_2)

                    } else {
                        image1?.setImageResource(R.drawable.ic_activity_female_4_1)
                        image2?.setImageResource(R.drawable.ic_activity_female_4_2)
                    }
                }
                5 -> {
                    if (selected) {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0xFF17E2AB.toInt())
                        )
                    } else {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0x8017E2AB.toInt())
                        )
                    }
                    if (male) {
                        image1?.setImageResource(R.drawable.ic_activity_5_1)
                        image2?.setImageResource(R.drawable.ic_activity_5_2)

                    } else {
                        image1?.setImageResource(R.drawable.ic_activity_female_5_1)
                        image2?.setImageResource(R.drawable.ic_activity_female_5_2)
                    }
                }
                6 -> {
                    if (selected) {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0xFF035BB2.toInt())
                        )
                    } else {
                        imageView?.setImageDrawable(
                            Utils.getColorFilterDrawable(itemView.context, id, 0x80035BB2.toInt())
                        )
                    }
                    if (male) {
                        image1?.setImageResource(R.drawable.ic_activity_6_1)
                        image2?.setImageResource(R.drawable.ic_activity_6_2)

                    } else {
                        image1?.setImageResource(R.drawable.ic_activity_female_6_1)
                        image2?.setImageResource(R.drawable.ic_activity_female_6_2)
                    }
                }
            }

        }
    }

    data class Item(
        var id: Int = 0,
        var title: String,
        var desc: String,
        var selected: Boolean = false,
        var isMale: Boolean = false,
        var image1: Int = 0,
        var image2: Int = 0,
        var color: Int = 0
    )
}