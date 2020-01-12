/*
 *  Created by Sumeet Kumar on  1/9/20 8:32 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarMonth
import kotlinx.android.synthetic.main.fragment_calendar.*
import life.mibo.hexa.R
import life.mibo.hexa.models.calories.CaloriesData
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener
import life.mibo.hexa.ui.home.HomeItem
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : BaseFragment(), CalendarObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: CalendarController
    var recyclerView: RecyclerView? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

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
        weeksLayout.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = weeks[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    .toUpperCase(Locale.ENGLISH)
                setTextColor(Color.WHITE)
            }
        }
        controller.setUpCalendar(calendarView, weeks)
        controller.getCalories()
        // tv_month.text = monthTitleFormatter.format(it.yearMonth)
        iv_user_pic.setImageDrawable(
            ContextCompat.getDrawable(
                this@CalendarFragment.context!!,
                R.drawable.ic_person_black_24dp
            )
        )

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
        tv_month?.text = monthTitleFormatter.format(calender?.yearMonth).toUpperCase()
    }


    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}