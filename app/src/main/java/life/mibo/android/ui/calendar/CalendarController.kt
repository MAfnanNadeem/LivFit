/*
 *  Created by Sumeet Kumar on 1/9/20 8:33 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:11 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calendar

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.PostData
import life.mibo.android.models.calories.Calories
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.models.login.Member
import life.mibo.android.models.member.MemberCalendar
import life.mibo.android.models.member.MemberCalendarPost
import life.mibo.android.models.trainer.TrainerCalendarResponse
import life.mibo.android.models.trainer.TrainerCalendarSession
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

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

    fun setUpCalendar(
        calendar: CalendarView,
        weeks: Array<DayOfWeek>,
        list: List<MemberCalendar.Data?>?
    ) {
        Logger.e("setUpCalendar $list")

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
                        holder.day.setTextColor(
                            ContextCompat.getColor(
                                fragment.requireContext(),
                                R.color.button_color_app
                            )
                        )
                        holder.parent.setBackgroundColor(
                            ContextCompat.getColor(
                                fragment.requireContext(),
                                R.color.textColorApp
                            )
                        )
                    } else {
                        holder.day.background = null
                    }

                    if (day.date.dayOfMonth % 3 == 0)
                        holder.event1.visibility = View.VISIBLE
                    else holder.event1.visibility = View.INVISIBLE

                    holder.parent?.setOnClickListener {

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
                Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
               // Toasty.error(fragment.context!!, "Unable to connect").show()
            }

            override fun onResponse(call: Call<Calories>, response: Response<Calories>) {

                fragment.getDialog()?.dismiss()
                val data = response.body()
                if (data != null && data.status.equals("success", true)) {
                    parseData(data)
                } else {
                    val err = data?.errors?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                    else Toasty.error(fragment.requireContext(), err, Toasty.LENGTH_LONG).show()
                }


            }
        })
    }


    fun getCalender(member: Member?) {
        if (member == null)
            return
        //  val member = Prefs.get(this.fragment.context).member  ?: return

        if (!member.isMember()) {
            getTrainerCalender(member)
            return
        }
        fragment.getDialog()?.show()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val post =
            MemberCalendarPost(
                MemberCalendarPost.Data(
                    "${member.id}",
                    formatter.format(LocalDate.now().minusMonths(3)),
                    formatter.format(LocalDate.now().plusMonths(3))
                ), member.accessToken
            )
        API.request.getApi().getMemberCalendar(post).enqueue(object : Callback<MemberCalendar> {
            override fun onFailure(call: Call<MemberCalendar>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
                observer.onMemberCalendar(null)
                // Toasty.error(fragment.context!!, "Unable to connect").show()
            }

            override fun onResponse(
                call: Call<MemberCalendar>,
                response: Response<MemberCalendar>
            ) {

                fragment.getDialog()?.dismiss()
                val data = response.body()
                if (data != null && data.isSuccess()) {
                    //  parseData(data)
                    observer.onMemberCalendar(data.data)
                } else {
                    observer.onMemberCalendar(null)
                    val err = data?.errors?.get(0)?.message
                    if (err.isNullOrEmpty())
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                    else Toasty.error(fragment.requireContext(), err, Toasty.LENGTH_LONG).show()
                }


            }
        })
    }

    fun getTrainerCalender(member: life.mibo.android.models.login.Member?) {
        if(member == null)
            return

        fragment.getDialog()?.show()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val post =
            TrainerCalendarSession(
                TrainerCalendarSession.Data(
                    "${member.id}",
                    formatter.format(LocalDate.now().minusMonths(3)),
                    formatter.format(LocalDate.now().plusMonths(3))
                ), member.accessToken
            )
        API.request.getTrainerApi().getTrainerCalendarSession(post)
            .enqueue(object : Callback<TrainerCalendarResponse> {
                override fun onFailure(call: Call<TrainerCalendarResponse>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.requireContext(), R.string.unable_to_connect).show()
                    // Toasty.error(fragment.context!!, "Unable to connect").show()
                    observer.onTrainerCalendar(null)
                }

                override fun onResponse(
                    call: Call<TrainerCalendarResponse>,
                    response: Response<TrainerCalendarResponse>
                ) {

                    fragment.getDialog()?.dismiss()
                    val data = response.body()
                    if (data != null && data.isSuccess()) {
                        //  parseData(data)
                        observer.onTrainerCalendar(data.data)
                    } else {
                        observer.onTrainerCalendar(null)
                        val err = data?.errors?.get(0)?.message
                        if (err.isNullOrEmpty())
                            Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        else Toasty.error(fragment.requireContext(), err, Toasty.LENGTH_LONG).show()
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