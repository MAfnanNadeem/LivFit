/*
 *  Created by Sumeet Kumar on 1/9/20 9:57 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 5:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_schedule.*
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.models.program.ProgramPost
import life.mibo.hexa.models.program.SearchPrograms
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.select_program.ProgramDialog
import life.mibo.hexa.utils.Toasty
import life.mibo.views.calendardayview.CalendarEvent
import life.mibo.views.calendardayview.data.IEvent
import life.mibo.views.calendardayview.data.IPopup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : BaseFragment() {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: ScheduleControler
    //var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_schedule, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //controller = WeightController(this@ScheduleFragment, this)
        //controller.setRecycler(recyclerView!!)

        iv_date?.setOnClickListener {
            datePickerDialog()
        }
        tv_date?.setOnClickListener {
            datePickerDialog()
        }
        iv_time?.setOnClickListener {
            timePickerDialog()
        }
        tv_time?.setOnClickListener {
            timePickerDialog()
        }
        select_program?.setOnClickListener {
            programDialog?.showPrograms()
            //checkDialog()
        }
        loadPrograms()
        val now = Calendar.getInstance()
        tv_date?.text = String.format(
            "%02d/%02d/%d", now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH).plus(1), now.get(Calendar.YEAR)
        )
        val hours = now.get(Calendar.HOUR_OF_DAY)
        tv_time?.text = String.format(
            "%02d:%02d %s", now.get(Calendar.HOUR_OF_DAY) % 12, now.get(Calendar.MINUTE), if (hours > 12) "PM" else "AM"
        )
    }


    fun datePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->
                tv_date?.text = String.format(
                    "%02d/%02d/%d", dayOfMonth, monthOfYear.plus(1), year
                )
                start.set(Calendar.YEAR, year)
                start.set(Calendar.MONTH, monthOfYear)
                start.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                tv_day?.text = SimpleDateFormat("EEEE").format(start.time)
                isDateSet = true
                addEvent()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.accentColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
        dpd.show(childFragmentManager, "DatePickerDialog")
    }

    var isDateSet = false
    var isTimeSet = false
    fun timePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->
                //var am = if (hourOfDay > 12) "PM" else "AM"
                tv_time?.text = String.format(
                    "%02d:%02d %s", hourOfDay % 12, minute, if (hourOfDay > 12) "PM" else "AM"
                )
                start.set(Calendar.HOUR_OF_DAY, hourOfDay)
                start.set(Calendar.MINUTE, minute)
                isTimeSet = true
                addEvent()
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            false
        )
        dpd.accentColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
       // dpd.
        dpd.show(childFragmentManager, "TimePickerDialog")
    }

    val events = ArrayList<IEvent>()
    val popups = ArrayList<IPopup>();

    var start = Calendar.getInstance()

    fun addEvent() {
        if (program == null)
            return
        if (!isDateSet || !isTimeSet)
            return

        val eventColor = resources.getColor(R.color.eventColor)
        val end = start.clone() as Calendar
        //end.add(Calendar.HOUR_OF_DAY, 1)
        end.add(Calendar.MINUTE, program?.duration?.valueInt()!!.div(60))
        val event = CalendarEvent(3, start, end, "${program?.name}", "house", eventColor)
        //event.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.avatar))
        event.name = "MI.BO Session"
        events.add(event)

        calendarDayView.setLimitTime(0,23)
        calendarDayView.setEvents(events)
        calendarDayView
        log("CalendarDayView setEvents")
        //calendarDayView.invalidate()
    }

    fun addEvent(string: String) {
        val eventColor = resources.getColor(R.color.eventColor)
        val timeStart = Calendar.getInstance()
        timeStart.set(Calendar.HOUR_OF_DAY, 16)
        timeStart.set(Calendar.MINUTE, 15)
        val timeEnd = timeStart.clone() as Calendar
        timeEnd.add(Calendar.HOUR_OF_DAY, 1)
        timeEnd.add(Calendar.MINUTE, 30)
        val event = CalendarEvent(3, timeStart, timeEnd, "${program?.name}", "house", eventColor)
        //event.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.avatar))
        event.name = "MI.BO"
        events.add(event)

        calendarDayView.setEvents(events)
    }


    private var programDialog: ProgramDialog? = null
    private var colorDialog: ProgramDialog? = null


    private fun loadPrograms() {
        val member =
            Prefs.get(context).member ?: return

        getDialog()?.show()
        val post = ProgramPost(member.accessToken)
        API.request.getApi().searchPrograms(post).enqueue(object :
            Callback<SearchPrograms> {
            override fun onFailure(call: Call<SearchPrograms>, t: Throwable) {
                getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(context!!, getString(R.string.unable_to_connect)).show()
            }

            override fun onResponse(
                call: Call<SearchPrograms>,
                response: Response<SearchPrograms>
            ) {
                getDialog()?.dismiss()

                val data = response.body()
                if (data != null) {
                    if (data.status.equals("success", true)) {
                        parse(data.data?.programs)

                    } else if (data.status.equals("error", true)) {
                        Toasty.error(context!!, "${data.errors?.get(0)?.message}").show()
                    }
                } else {
                    Toasty.error(context!!, R.string.error_occurred).show()
                }
            }
        })
    }

    val programs = ArrayList<Program?>()
    var isProgram = false
    var program: Program? = null
    private fun parse(list: ArrayList<Program?>?) {
        if (list == null)
            return

        programs.clear()
        programs.addAll(list)

        programDialog = ProgramDialog(context!!, programs, object : ItemClickListener<Program> {

            override fun onItemClicked(item: Program?, position: Int) {
                // Toasty.info(context!!, "$position").show()
                item?.name?.let {
                    isProgram = true
                    program = item
                    select_program.text = it
                    button_next.isEnabled = true
                    addEvent()

                }
            }

        }, ProgramDialog.PROGRAMS)


    }


    override fun onStop() {
        super.onStop()
        //controller.onStop()
    }

}