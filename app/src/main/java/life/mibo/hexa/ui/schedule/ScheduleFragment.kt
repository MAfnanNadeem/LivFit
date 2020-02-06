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
import androidx.recyclerview.widget.LinearLayoutManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_schedule.*
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.models.program.ProgramPost
import life.mibo.hexa.models.program.ProgramPostData
import life.mibo.hexa.models.program.SearchPrograms
import life.mibo.hexa.room.Database
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.MessageDialog
import life.mibo.hexa.ui.select_program.ProgramDialog
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ScheduleFragment : BaseFragment() {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: ScheduleController
    //var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_schedule, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = ScheduleController(this@ScheduleFragment)
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
        loadProgramObservables()
        val now = Calendar.getInstance()
        tv_date?.text = String.format(
            "%02d/%02d/%d", now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH).plus(1), now.get(Calendar.YEAR)
        )
        val hours = now.get(Calendar.HOUR_OF_DAY)
        tv_time?.text = String.format(
            "%02d:%02d %s", now.get(Calendar.HOUR_OF_DAY) % 12, now.get(Calendar.MINUTE), if (hours > 12) "PM" else "AM"
        )

        button_book?.setOnClickListener {
            bookSession()
        }
        setRecycler()
    }

    private val dayList = ArrayList<DayAdapter.Day>()
    private var adapter: DayAdapter? = null
    fun setRecycler() {
        dayList.clear()

        for (i in 1..24) {
            dayList.add(DayAdapter.Day(i, String.format("%02d:00", i)))
        }

        adapter = DayAdapter(dayList, null)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = adapter
    }

    private fun bookSession() {
        if (program == null) {
            Toasty.error(context!!, "Select Program").show()
            return
        }

        if (!isDateSet) {
            Toasty.error(context!!, "Select Date").show()
            return
        }

        if (!isTimeSet) {
            Toasty.error(context!!, "Select Time").show()
            return
        }

        //val date = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        val date = SimpleDateFormat("dd-MM-yyyy").format(calendar.time)
        val time = SimpleDateFormat("hh:mm a").format(calendar.time)
        //val time = SimpleDateFormat("HH:mm").format(calendar.time)
        MessageDialog(
            context!!,
            "Book Session",
            "Are you sure want to book session on $date at $time",
            "Cancel",
            "Book",
            object : MessageDialog.Listener {
                override fun onClick(button: Int) {
                    if (button == MessageDialog.POSITIVE) {
                        controller.bookSession(program!!.id!!, calendar.time)
                    }
                }

            }).show()
    }

    var calendar: Calendar = Calendar.getInstance()
    var isDateSet = false
    var isTimeSet = false
    val programs = ArrayList<Program?>()
    var isProgram = false
    var program: Program? = null

    private fun datePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->
                tv_date?.text = String.format(
                    "%02d/%02d/%d", dayOfMonth, monthOfYear.plus(1), year
                )
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                tv_day?.text = SimpleDateFormat("EEEE").format(calendar.time)
                isDateSet = true
                addEvent()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.minDate = now
        dpd.accentColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
        dpd.show(childFragmentManager, "DatePickerDialog")
    }


    private fun timePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->
                //var am = if (hourOfDay > 12) "PM" else "AM"
                tv_time?.text = String.format(
                    "%02d:%02d %s", hourOfDay % 12, minute, if (hourOfDay > 12) "PM" else "AM"
                )
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
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

    //val events = ArrayList<IEvent>()
    //val popups = ArrayList<IPopup>();


    fun addEvent() {
        if (program == null)
            return
        if (!isDateSet || !isTimeSet)
            return

        val eventColor = resources.getColor(R.color.eventColor)
        val end = calendar.clone() as Calendar
        //end.add(Calendar.HOUR_OF_DAY, 1)
        val type = if (end[Calendar.MINUTE] > 30) 1 else 0
        end.add(Calendar.MINUTE, program?.duration?.valueInt()!!.div(60))
        //val event = CalendarEvent(3, start, end, "${program?.name}", "house", eventColor)
        //event.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.avatar))
        //event.name = "MI.BO ${program?.name}"
        //events.add(event)

        val h = end.get(Calendar.HOUR_OF_DAY)
        adapter?.addEvent(h, "MI.BO ${program?.name}", type)
        // calendarDayView.setLimitTime(0,23)
        //calendarDayView.setEvents(events)
        // calendarDayView
        log("CalendarDayView setEvents")
        // val d = com.android.calendar.DayView
        //calendarDayView.invalidate()
        Single.just(h).delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            recyclerView.scrollToPosition(it)
            log("DayAdapter : recyclerView.scrollToPosition $h")
        }.subscribe()
    }

//    fun addEvent(string: String) {
//        val eventColor = resources.getColor(R.color.eventColor)
//        val timeStart = Calendar.getInstance()
//        timeStart.set(Calendar.HOUR_OF_DAY, 16)
//        timeStart.set(Calendar.MINUTE, 15)
//        val timeEnd = timeStart.clone() as Calendar
//        timeEnd.add(Calendar.HOUR_OF_DAY, 1)
//        timeEnd.add(Calendar.MINUTE, 30)
//        val event = CalendarEvent(3, timeStart, timeEnd, "${program?.name}", "house", eventColor)
//        //event.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.avatar))
//        event.name = "MI.BO"
//        //events.add(event)
//
//        //calendarDayView.setEvents(events)
//    }


    private var programDialog: ProgramDialog? = null


    private fun loadProgramObservables() {
        Single.fromCallable {
            val list = Database.getInstance(requireContext()).programDao().getAll()
            log("loadProgramObservables $list")
            log("loadProgramObservables ${list?.size}")
            if (list != null && list.isNotEmpty()) {
                programs.clear()
                programs.addAll(list)
                //return@fromCallable true
            }// else
            // false
            programs


        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            if (it.isEmpty()) {
                loadPrograms()
            }
            else
                parsePrograms(it)

        }.subscribe()
    }

    private fun loadPrograms() {
        //loadProgramObservables()
        val member =
            Prefs.get(context).member ?: return

        getDialog()?.show()
        val post =
            ProgramPost(
                item = ProgramPostData(),
                auth = member.accessToken!!,
                type = "SearchPrograms"
            )
        API.request.getApi().searchPrograms2(post).enqueue(object :
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
                    button_book.isEnabled = true
                    addEvent()

                }
            }

        }, ProgramDialog.PROGRAMS)

        Database.getInstance(context!!).insert(programs)
    }

    private fun parsePrograms(list: ArrayList<Program?>) {
        if (list.isEmpty()) {
            loadPrograms()
            return
        }
        programDialog = ProgramDialog(context!!, programs, object : ItemClickListener<Program> {

            override fun onItemClicked(item: Program?, position: Int) {
                // Toasty.info(context!!, "$position").show()
                item?.name?.let {
                    isProgram = true
                    program = item
                    select_program.text = it
                    button_book.isEnabled = true
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