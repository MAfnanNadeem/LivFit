/*
 *  Created by Sumeet Kumar on 1/12/20 8:47 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 8:47 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calendar

import android.view.View
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.fragment_calendar_day_layout.view.*

class CalendarDayHolder(view: View) : ViewContainer(view) {

    // val day = view.findViewById<TextView>(R.id.calendarDayText)
    val day = view.tv_day
    lateinit var data: CalendarDay

}