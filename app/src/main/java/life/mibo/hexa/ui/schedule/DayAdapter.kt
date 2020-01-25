/*
 *  Created by Sumeet Kumar on 1/25/20 12:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/25/20 12:11 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.schedule

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener
import java.util.*

class DayAdapter(
    val list: ArrayList<Day>,
    var listener: ItemClickListener<Day>? = null
) : RecyclerView.Adapter<DayAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_day_schedule, parent, false
            )
        )
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.pos?.toLong() ?: 0L
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(position: Int): Day? {
        return list[position]
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // holder.bind(list[position], listener)
        val item = list[position]
        holder.text?.text = "${item.time}"
        if (item.event.isNotEmpty()) {
            when {
                item.type == 0 -> {
                    holder.text1?.visibility = View.VISIBLE
                    holder.text2?.visibility = View.GONE
                    holder.text1?.text = "${item?.event}"
                }
                item.type == 1 -> {
                    holder.text2?.visibility = View.VISIBLE
                    holder.text1?.visibility = View.GONE
                    holder.text2?.text = "${item?.event}"
                }
                else -> {
                    holder.text2?.visibility = View.GONE
                    holder.text1?.visibility = View.GONE

                }
            }
        }
        holder.setIsRecyclable(false)
    }

    @Synchronized
    fun addEvent(id: Int, event: String, type: Int = 0) {
        //Logger.e("DayAdapter : $id - $type :: $event")
        list.forEachIndexed { _, item ->
            if (item.pos == id) {
                item.event = event
                item.type = type
               // Logger.e("DayAdapter found: $item")
            } else {
                item.event = ""
                item.type = 2

            }
        }

        notifyDataSetChanged()
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.tv_text)
        var text1: TextView? = itemView.findViewById(R.id.tv_text1)
        var text2: TextView? = itemView.findViewById(R.id.tv_text2)
        var data: Day? = null

        fun bind(item: Day?, listener: ItemClickListener<Day>?) {
            if (item == null)
                return
            data = item
            text?.text = "${item.time}"
            if (item.event.isNotEmpty()) {
                when {
                    item.type == 0 -> {
                        text1?.visibility = View.VISIBLE
                        text2?.visibility = View.GONE
                        text1?.text = "${data?.event}"
                    }
                    item.type == 1 -> {
                        text2?.visibility = View.VISIBLE
                        text1?.visibility = View.GONE
                        text2?.text = "${data?.event}"
                    }
                    else -> {
                        text2?.visibility = View.GONE
                        text1?.visibility = View.GONE

                    }
                }
            }
        }
    }

    data class Day(val pos: Int, val time: String, var type: Int = 0, var event: String = "") :
        Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readString() ?: ""
        )

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.writeInt(pos)
            dest?.writeString(time)
            dest?.writeInt(type)
            dest?.writeString(event)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Day> {
            override fun createFromParcel(parcel: Parcel): Day {
                return Day(parcel)
            }

            override fun newArray(size: Int): Array<Day?> {
                return arrayOfNulls(size)
            }
        }


    }
}