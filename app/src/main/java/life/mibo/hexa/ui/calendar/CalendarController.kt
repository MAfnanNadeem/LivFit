/*
 *  Created by Sumeet Kumar on 1/9/20 8:33 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:11 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.calendar

import android.graphics.Color
import android.view.View
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.base.PostData
import life.mibo.hexa.models.calories.Calories
import life.mibo.hexa.models.calories.CaloriesData
import life.mibo.hexa.models.login.Member
import life.mibo.hexa.utils.Toasty
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CalendarController(val fragment: CalendarFragment, val observer: CalendarObserver) :
    CalendarFragment.Listener {

    override fun onCreate(view: View?, data: Any?) {

    }

    override fun onResume() {

    }

    override fun onStop() {

    }

    override fun onHomeItemClicked(position: Int) {

    }

    //private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val today = LocalDate.now()

    fun setUpCalendar(calendar: CalendarView, weeks: Array<DayOfWeek>) {
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(6)
        val lastMonth = currentMonth.plusMonths(6)
        //val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        calendar.setup(firstMonth, lastMonth, weeks.first())
        calendar.scrollToMonth(currentMonth)

        calendar.dayBinder = object : DayBinder<CalendarDayHolder> {

            override fun create(view: View): CalendarDayHolder {
                return CalendarDayHolder(view)
            }

            override fun bind(holder: CalendarDayHolder, day: CalendarDay) {
                holder.day.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    holder.day.setTextColor(Color.WHITE)
                    if (day.date == today) {
                        holder.day.setBackgroundResource(R.drawable.bg_calendar_date_today)
                    } else {
                        holder.day.background = null
                    }
                } else {
                    holder.day.setTextColor(Color.GRAY)
                }
            }
        }
        //calendarView.scrollToMonth(currentMonth)
        calendar.monthScrollListener = {
            fragment.log("calendarView Month Scroll ${it.yearMonth} $it")
            observer.onMonthChanged(it)
        }
    }


    fun daysOfWeek(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }

    fun getCalories() {
        val member =
            Prefs.get(this.fragment.context).member
                ?: return
        fragment.getDialog()?.show()
        val post = PostData("${member.id}", member.accessToken, "CaloriesBurnt")
        API.request.getApi().getAllCaloriesBurnt(post).enqueue(object : Callback<Calories> {
            override fun onFailure(call: Call<Calories>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, "Unable to connect").show()
            }

            override fun onResponse(call: Call<Calories>, response: Response<Calories>) {

                fragment.getDialog()?.dismiss()
                val data = response.body()
                if (data != null && data.status.equals("success")) {
                    parseData(data)
                } else {
                    val err = data?.errors?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.context!!, R.string.error_occurred).show()
                    else Toasty.error(fragment.context!!, err, Toasty.LENGTH_LONG).show()
                }


            }
        })
    }

    fun parseData(calories: Calories?) {
        val list = ArrayList<CaloriesData>()
        if (calories != null) {
            calories.data?.forEach {
                list.add(it!!)
            }
        }

        observer.onDataReceived(list)
    }

}