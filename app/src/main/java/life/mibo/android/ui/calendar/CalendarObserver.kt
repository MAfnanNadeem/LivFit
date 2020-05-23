/*
 *  Created by Sumeet Kumar on  1/9/20 8:32 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:10 PM
 *  Mibo Hexa - app
 */


package life.mibo.android.ui.calendar

import com.kizitonwose.calendarview.model.CalendarMonth
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.models.member.MemberCalendar
import life.mibo.android.models.trainer.TrainerCalendarResponse
import life.mibo.android.ui.home.HomeItem

interface CalendarObserver {
    fun onDataReceived(list: ArrayList<CaloriesData>)
    fun onCalendar(list: List<MemberCalendar.Data?>?)
    fun onTrainerCalendar(data: TrainerCalendarResponse.Data?)
    fun onItemClicked(item: HomeItem?)
    fun onMonthChanged(calender: CalendarMonth)
}