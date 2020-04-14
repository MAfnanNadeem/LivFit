/*
 *  Created by Sumeet Kumar on 1/12/20 1:48 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 5:43 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.android.R
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.ui.base.ItemClickListener
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class CaloriesAdapter(
    var list: ArrayList<CaloriesData>,
    var listener: ItemClickListener<CaloriesData>? = null
) : RecyclerView.Adapter<CaloriesAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_calendar_calories,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(position: Int): CaloriesData? {
        return list[position]
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Logger.e("onBindViewHolder $position")
        holder.bind(getItem(position))
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView? = itemView.findViewById(R.id.tv_action)
        var minutes: TextView? = itemView.findViewById(R.id.tv_minutes)
        var calories: TextView? = itemView.findViewById(R.id.tv_calories)
        var date: TextView? = itemView.findViewById(R.id.tv_date_value)
        var excersise: TextView? = itemView.findViewById(R.id.tv_exercise_name)
        var data: CaloriesData? = null

        fun bind(item: CaloriesData?) {
            Logger.e("CaloriesAdapter bind item $item")
            if (item == null)
                return
            data = item

            time?.text = getDiff(item.startDatetime, item.endDatetime)
            minutes?.text = "minutes"
            calories?.text = "${item.caloriesBurnt} cal"
            date?.text = "${item.startDatetime}"
            excersise?.text = item.programCircuitName

        }

        fun getDiff(start: String?, end: String?): String? {
            Logger.e("CaloriesAdapter getDiff call")
            //val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var time = ""
            try {
                val s = LocalTime.parse(start?.split(" ")!![1])
                val e = LocalTime.parse(end?.split(" ")!![1])
                val d = org.threeten.bp.Duration.between(s, e)
                Logger.e("CaloriesAdapter getDiff $d")
                time = "" + ChronoUnit.MINUTES.between(s, e)
                //format.parse(end)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return time
        }
    }


}