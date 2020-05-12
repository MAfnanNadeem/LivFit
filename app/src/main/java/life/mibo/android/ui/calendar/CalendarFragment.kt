/*
 *  Created by Sumeet Kumar on  1/9/20 8:32 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarMonth
import kotlinx.android.synthetic.main.fragment_calendar.*
import life.mibo.android.R
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.home.HomeItem
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

class CalendarFragment : BaseFragment(), CalendarObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: CalendarController
    var recyclerView: RecyclerView? = null
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = CalendarController(this@CalendarFragment, this)
        recyclerView = view.findViewById(R.id.recyclerView)
        //controller.setRecycler(recyclerView!!)
        val weeks = controller.daysOfWeek()
        weeksLayout2.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = weeks[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    .toUpperCase(Locale.ENGLISH)
                //setTextColor(Color.WHITE)
            }
        }
        controller.setUpCalendar(calendarView, weeks)
        controller.getCalories()
        // tv_month.text = monthTitleFormatter.format(it.yearMonth)
//        iv_user_pic.setImageDrawable(
////            ContextCompat.getDrawable(
////                this@CalendarFragment.context!!,
////                R.drawable.ic_person_black_24dp
////            )
////        )

    }

    override fun onDataReceived(list: ArrayList<CaloriesData>) {
        log("onDataReceived $list")
        val adapter = CaloriesAdapter(list, null)
        recyclerView?.layoutManager = LinearLayoutManager(this@CalendarFragment.context)
        recyclerView?.adapter = adapter
    }

    override fun onItemClicked(item: HomeItem?) {

    }

    override fun onMonthChanged(calender: CalendarMonth) {
        //super.onMonthChanged(calender)
        tv_month?.text = monthFormatter.format(calender?.yearMonth).toUpperCase() + " ("+ calender?.year +")"
    }


    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}