/*
 *  Created by Sumeet Kumar on  1/9/20 8:32 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:10 PM
 *  Mibo Hexa - app
 */


package life.mibo.hexa.ui.calendar

import com.kizitonwose.calendarview.model.CalendarMonth
import life.mibo.hexa.models.calories.CaloriesData
import life.mibo.hexa.ui.home.HomeItem

interface CalendarObserver {
    fun onDataReceived(list: ArrayList<CaloriesData>)
    fun onItemClicked(item: HomeItem?)
    fun onMonthChanged(calender: CalendarMonth)
}