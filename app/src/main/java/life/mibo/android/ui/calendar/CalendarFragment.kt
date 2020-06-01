/*
 *  Created by Sumeet Kumar on  1/9/20 8:32 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.list_item_calendar_reschedule.view.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.core.security.Encrypt
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.models.member.MemberCalendar
import life.mibo.android.models.trainer.TrainerCalendarResponse
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.home.HomeItem
import life.mibo.android.ui.main.Navigator
import life.mibo.hardware.core.Logger
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

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

    var isMember = true

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

        val member = Prefs.get(this.context).member
        isMember = member?.isMember() ?: true
        // controller.setUpCalendar(calendarView, weeks)
        //controller.getCalories()
        controller.getCalender()
        //controller.getTrainerCalender()
        // tv_month.text = monthTitleFormatter.format(it.yearMonth)
//        iv_user_pic.setImageDrawable(
////            ContextCompat.getDrawable(
////                this@CalendarFragment.context!!,
////                R.drawable.ic_person_black_24dp
////            )
////        )


        scheduleAdapter = ScheduleAdapter(ArrayList(), object : ItemClickListener<Schedule> {
            override fun onItemClicked(item: Schedule?, position: Int) {
                log("edit -- $item")
                item?.let {
                    val bundle = Bundle()
                    bundle.putString("trainer_name", it.trainerName)
                    bundle.putString("service_name", it.session)
                    bundle.putString("service_date", it.startTime)
                    bundle.putInt("session_id", it.scheduleId ?: 0)
                    bundle.putInt("trainer_id", it.trainerId ?: 0)
                    navigate(Navigator.RESCHEDULE, bundle)
                }
            }

        }, isMember)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = scheduleAdapter

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (!isMember)
            inflater?.inflate(R.menu.menu_calendar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.navigation_add) {
            navigate(Navigator.SCHEDULE, null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDataReceived(list: ArrayList<CaloriesData>) {
        log("onDataReceived $list")
        val adapter = CaloriesAdapter(list, null)
        recyclerView?.layoutManager = LinearLayoutManager(this@CalendarFragment.context)
        recyclerView?.adapter = adapter
    }

    private var selectedDate: LocalDate? = null
    private var scheduleAdapter: ScheduleAdapter? = null
    private val scheduleDates = ArrayList<MemberCalendar.Data>()
    private val sessionDates = ArrayList<TrainerCalendarResponse.Session>()

    override fun onCalendar(list: List<MemberCalendar.Data?>?) {
        log("onCalendar $list")
        scheduleDates.clear()
        if (list != null)
            for (l in list) {
                if (l != null)
                    scheduleDates.add(l)
            }
        log("onCalendar $list")
        activity?.runOnUiThread {
            val weeks = controller.daysOfWeek()
            weeksLayout2.children.forEachIndexed { index, view ->
                (view as TextView).apply {
                    text = weeks[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        .toUpperCase(Locale.ENGLISH)
                    //setTextColor(Color.WHITE)
                }
            }
            setUpCalendar(calendarView, weeks, list)
        }
    }

    private fun setUpCalendar(
        calendar: CalendarView,
        weeks: Array<DayOfWeek>,
        list: List<MemberCalendar.Data?>?
    ) {
        Logger.e("setUpCalendar $list")
        val today = LocalDate.now()

        val dates = ArrayList<MemberCalendar.Data>()
        if (list != null)
            for (l in list) {
                if (l != null)
                    dates.add(l)
            }

        //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(6)
        val lastMonth = currentMonth.plusMonths(6)
        //val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        calendar.setup(firstMonth, lastMonth, weeks.first())
        calendar.scrollToMonth(currentMonth)

        calendar.dayBinder = object : DayBinder<CalendarDayHolder> {
            val white = Color.WHITE
            val dark = Color.DKGRAY

            // val grey = Color.GRAY
            val light = Color.LTGRAY

            override fun create(view: View): CalendarDayHolder {
                return CalendarDayHolder(view)
            }

            override fun bind(container: CalendarDayHolder, day: CalendarDay) {
                container.day.text = day.date.dayOfMonth.toString()
                container.event1.visibility = View.INVISIBLE
                container.event2.visibility = View.INVISIBLE
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.day.setTextColor(dark)
                    if (day.date == today) {
                        container.day.setTextColor(white)
                        container.parent.setBackgroundResource(R.drawable.bg_calendar_today)
                    } else {
                        if (day.date == selectedDate) {
                            container.parent.setBackgroundResource(R.drawable.bg_calendar_selected)
                        } else {
                            container.parent.setBackgroundColor(Color.TRANSPARENT)
                        }
                    }

                    var count = 0
                    for (d in dates) {
                        try {
                            val date = d.startDateTime?.split(" ")?.get(0) ?: ""
                            // val date = formatter.parse(d.startDateTime)
                            if (LocalDate.parse(date) == day.date) {
                                if (count == 0)
                                    container.event1.visibility = View.VISIBLE
                                else container.event2.visibility = View.VISIBLE
                                count++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    // if (day.date.dayOfMonth % 3 == 0)
                    //     container.event1.visibility = View.VISIBLE
                    // else container.event1.visibility = View.INVISIBLE
                    container.parent?.setOnClickListener {
                        onCalendarDateClicked(day)
                    }
                } else {
                    container.day.setTextColor(light)
                    //container.day.setTextColor(Color.TRANSPARENT)
                }
            }
        }
        //calendarView.scrollToMonth(currentMonth)
        calendar.monthScrollListener = {
            log("calendarView Month Scroll ${it.yearMonth} $it")
            onMonthChanged(it)
        }
    }

    fun onCalendarDateClicked(item: CalendarDay?) {
        log("onCalendarDateClicked $item")
        selectedDate = item?.date
        calendarView?.notifyCalendarChanged()
        val list = ArrayList<Schedule>()
        var count = 0
        var color1 = ContextCompat.getColor(requireContext(), R.color.textColorApp)
        var color2 = ContextCompat.getColor(requireContext(), R.color.textColorApp2)
        for (session in scheduleDates) {
            try {
                val date = session.startDateTime?.split(" ")?.get(0) ?: ""
                // val date = formatter.parse(d.startDateTime)
                if (LocalDate.parse(date) == item?.date) {
                    list.add(
                        Schedule(
                            session.sessionID,
                            session.startDateTime,
                            session.endDateTime,
                            session.trainerFullName,
                            session.serviceName,
                            if (count == 0) color1 else color2, session.completed == 1, "", session.trainerID
                        )
                    )
                    count++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        activity?.runOnUiThread {
            scheduleAdapter?.update(list)
        }

    }


    override fun onTrainerCalendar(data: TrainerCalendarResponse.Data?) {
        log("onTrainerCalendar $data")
        sessionDates.clear()
        val list = ArrayList<TrainerCalendarResponse.Session>()
        if (data?.sessions != null)
            for (session in data.sessions!!) {
                if (session != null) {
                    sessionDates.add(session)
                    list.add(session)
                }
            }
        log("onCalendar $sessionDates")
        activity?.runOnUiThread {
            val weeks = controller.daysOfWeek()
            weeksLayout2?.children?.forEachIndexed { index, view ->
                (view as TextView).apply {
                    text = weeks[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        .toUpperCase(Locale.ENGLISH)
                    //setTextColor(Color.WHITE)
                }
            }
            setUpTrainerCalendar(calendarView, weeks, list)
        }
    }


    private fun setUpTrainerCalendar(
        calendar: CalendarView,
        weeks: Array<DayOfWeek>,
        list: List<TrainerCalendarResponse.Session>
    ) {
        Logger.e("setUpCalendar $list")
        val today = LocalDate.now()

//        val dates = ArrayList<MemberCalendar.Data>()
//        if (list != null)
//            for (l in list) {
//                    dates.add(l)
//            }

        //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(6)
        val lastMonth = currentMonth.plusMonths(6)
        //val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        calendar.setup(firstMonth, lastMonth, weeks.first())
        calendar.scrollToMonth(currentMonth)

        calendar.dayBinder = object : DayBinder<CalendarDayHolder> {

            val white = Color.WHITE
            val dark = Color.DKGRAY

            // val grey = Color.GRAY
            val light = Color.LTGRAY

            override fun create(view: View): CalendarDayHolder {
                return CalendarDayHolder(view)
            }

            override fun bind(container: CalendarDayHolder, day: CalendarDay) {
                container.day.text = day.date.dayOfMonth.toString()
                container.event1.visibility = View.INVISIBLE
                container.event2.visibility = View.INVISIBLE
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.day.setTextColor(dark)
                    if (day.date == today) {
                        container.day.setTextColor(white)
                        container.parent.setBackgroundResource(R.drawable.bg_calendar_today)
                    } else {
                        if (day.date == selectedDate) {
                            container.parent.setBackgroundResource(R.drawable.bg_calendar_selected)
                        } else {
                            container.parent.setBackgroundColor(Color.TRANSPARENT)
                        }
                    }

                    var count = 0
                    for (d in list) {
                        try {
                            val date = d.startDatetime?.split(" ")?.get(0) ?: ""
                            // val date = formatter.parse(d.startDateTime)
                            if (LocalDate.parse(date) == day.date) {
                                if (count == 0)
                                    container.event1.visibility = View.VISIBLE
                                else container.event2.visibility = View.VISIBLE
                                count++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    // if (day.date.dayOfMonth % 3 == 0)
                    //     container.event1.visibility = View.VISIBLE
                    // else container.event1.visibility = View.INVISIBLE
                    container.parent?.setOnClickListener {
                        onTrainerCalendarDateClicked(day)
                    }
                } else {
                    container.day.setTextColor(light)
                }
            }
        }
        //calendarView.scrollToMonth(currentMonth)
        calendar.monthScrollListener = {
            log("calendarView Month Scroll ${it.yearMonth} $it")
            onMonthChanged(it)
        }
    }


    fun onTrainerCalendarDateClicked(item: CalendarDay?) {
        log("onCalendarDateClicked $item")
        val crypt = Encrypt()
        selectedDate = item?.date
        calendarView?.notifyCalendarChanged()
        val list = ArrayList<Schedule>()
        var count = 0
        var color1 = ContextCompat.getColor(requireContext(), R.color.textColorApp)
        var color2 = ContextCompat.getColor(requireContext(), R.color.textColorApp2)
        for (session in sessionDates) {
            try {
                val date = session.startDatetime?.split(" ")?.get(0) ?: ""
                // val date = formatter.parse(d.startDateTime)
                if (LocalDate.parse(date) == item?.date) {
                    if (session.members != null) {
                        var mmember = session.members?.get(0)
                        list.add(
                            Schedule(
                                session.sessionId,
                                session.startDatetime,
                                session.endDatetime,
                                String(crypt.decrypt(mmember?.firstName)) + " " + String(
                                    crypt.decrypt(
                                        mmember?.lastName
                                    )
                                ),
                                session.notes,
                                if (count == 0) color1 else color2, session.completed == 1
                            )
                        )
                    }
                    count++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        activity?.runOnUiThread {
            scheduleAdapter?.update(list)
        }

    }


    data class Schedule(
        val scheduleId: Int?,
        val startTime: String?,
        val endTime: String?,
        val trainerName: String?,
        val session: String?,
        val color: Int,
        var isCompleted: Boolean = false,
        var imageUrl: String = "",
        var trainerId: Int? = 0
    ) {

    }

    class ScheduleAdapter(
        val schedules: ArrayList<Schedule>,
        var listener: ItemClickListener<Schedule>?, var isMember: Boolean
    ) : RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder>() {


        //private val formatter = SimpleDateFormat("EEE'\n'dd MMM'\n'HH:mm")
        //private val formatter = DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm")
        private val formatter2 = DateTimeFormatter.ofPattern("HH:mm")
        private val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ScheduleHolder {
//            if (isMember)
//                return ScheduleHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.list_item_calendar_reschedule_trainer, parent, false)
//                )
            return ScheduleHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_calendar_reschedule, parent, false)
            )
        }

        override fun onBindViewHolder(viewHolder: ScheduleHolder, position: Int) {
            viewHolder.bind(schedules[position])
        }

        fun update(list: ArrayList<Schedule>) {
            schedules.clear()
            schedules.addAll(list)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = schedules.size

        inner class ScheduleHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

            fun bind(schedule: Schedule) {
                containerView.tv_start_time.text =
                    formatter2.format(
                        LocalDateTime.parse(
                            schedule.startTime,
                            parser
                        )
                    ) + " - " + formatter2.format(LocalDateTime.parse(schedule.endTime, parser))
                containerView.tv_trainer.text = schedule.trainerName
                containerView.tv_session.text = schedule.session
                containerView.event1.setBackgroundColor(schedule.color)
                if (schedule.isCompleted)
                    containerView.iv_edit?.visibility = View.INVISIBLE
                else containerView.iv_edit?.visibility = View.VISIBLE
//                /if(sess)
                containerView.iv_edit?.setOnClickListener {
                    if (!schedule.isCompleted)
                        listener?.onItemClicked(schedule, 0)
                }
            }
        }
    }

    override fun onItemClicked(item: HomeItem?) {

    }

    override fun onMonthChanged(calender: CalendarMonth) {
        //super.onMonthChanged(calender)
        tv_month?.text =
            monthFormatter.format(calender?.yearMonth).toUpperCase() + "  " + calender?.year + ""
    }


    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}