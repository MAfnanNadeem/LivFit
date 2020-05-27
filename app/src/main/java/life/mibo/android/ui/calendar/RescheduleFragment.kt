/*
 *  Created by Sumeet Kumar on 5/18/20 12:29 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/16/20 11:48 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_reschedule.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.session.RescheduleMemberSession
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.main.MessageDialog
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.schedule.DayAdapter
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class RescheduleFragment : BaseFragment() {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_reschedule, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    var trainerName = ""
    var serverName = ""
    var sessionId: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        trainerName = arguments?.getString("trainer_name") ?: ""
        serverName = arguments?.getString("service_name") ?: ""
        //tv_program?.text = arguments?.getString("trainer_name")
        //tv_service?.text = arguments?.getString("service_name")
        sessionId = arguments?.getInt("session_id") ?: 0

        iv_date?.setOnClickListener {
            datePickerDialog()
        }
        tv_date?.setOnClickListener {
            datePickerDialog()
        }
        iv_action?.setOnClickListener {
            timePickerDialog()
        }
        tv_action?.setOnClickListener {
            timePickerDialog()
        }

        val now = Calendar.getInstance()
        tv_date?.text = String.format(
            "%02d/%02d/%d",
            now.get(Calendar.DAY_OF_MONTH),
            now.get(Calendar.MONTH).plus(1),
            now.get(Calendar.YEAR)
        )
        val hours = now.get(Calendar.HOUR_OF_DAY)
        tv_action?.text = String.format(
            "%02d:%02d %s",
            now.get(Calendar.HOUR_OF_DAY) % 12,
            now.get(Calendar.MINUTE),
            if (hours > 12) "PM" else "AM"
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
        if (!isDateSet) {
            Toasty.error(requireContext(), getString(R.string.select_date)).show()
            return
        }

        if (!isTimeSet) {
            Toasty.error(requireContext(), getString(R.string.select_time)).show()
            return
        }

        //val date = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        val date = SimpleDateFormat("dd-MM-yyyy").format(calendar.time)
        val time = SimpleDateFormat("hh:mm:ss").format(calendar.time)
        //val time = SimpleDateFormat("HH:mm").format(calendar.time)
        MessageDialog(
            requireContext(),
            "Reschedule Session",
            "Are you sure want to reschedule session on $date at $time",
            "Cancel",
            "Reschedule",
            object : MessageDialog.Listener {
                override fun onClick(button: Int) {
                    if (button == MessageDialog.POSITIVE) {
                        bookSession(sessionId, calendar.time)
                    }
                }

            }).show()
    }


    fun bookSession(programId: Int, date: Date) {
        val member = Prefs.get(context).member ?: return

        getDialog()?.show()

        val post = RescheduleMemberSession(
            RescheduleMemberSession.Data(
                programId,
                SimpleDateFormat("yyyy-MM-dd").format(date),
                SimpleDateFormat("HH:mm:ss").format(date)
            ), member.accessToken
        )

        API.request.getApi().rescheduleMemberSession(post)
            .enqueue(object : Callback<ResponseData> {

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(requireContext(), R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            Toasty.info(
                                requireContext(),
                                "${data.data?.message}",
                                Toasty.LENGTH_LONG,
                                false
                            ).show()
                           // var sessionId = "${data.data?.sessionID}"

                        } else if (data.status.equals("error", true)) {
                            Toasty.error(
                                requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()

                            MiboEvent.log("bookAndStartConsumerSession :: error $data")
                        }
                    } else {
                        Toasty.error(requireContext(), R.string.error_occurred).show()
                    }
                }
            })
    }


    var calendar: Calendar = Calendar.getInstance()
    var isDateSet = false
    var isTimeSet = false

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
                //tv_day?.text = SimpleDateFormat("EEEE").format(calendar.time)
                isDateSet = true
                addEvent()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.minDate = now
        dpd.accentColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        dpd.show(childFragmentManager, "DatePickerDialog")
    }


    private fun timePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = TimePickerDialog.newInstance(
            { view, hourOfDay, minute, second ->
                //var am = if (hourOfDay > 12) "PM" else "AM"
                tv_action?.text = String.format(
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
        dpd.accentColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        // dpd.
        dpd.show(childFragmentManager, "TimePickerDialog")
    }

    private fun timePickerDialog2() {
        val now = Calendar.getInstance()
        val listener = android.app.TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

        }
        val dialog = android.app.TimePickerDialog(
            requireContext(),
            listener,
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        )
        dialog.show()
    }

    private fun datePickerDialog2() {
        val now = Calendar.getInstance()
        val dialog = MaterialDatePicker.Builder.datePicker()
        dialog.build().show(childFragmentManager, "MaterialDatePicker")
    }


    //val events = ArrayList<IEvent>()
    //val popups = ArrayList<IPopup>();


    fun addEvent() {
        if (!isDateSet || !isTimeSet)
            return

        val eventColor = resources.getColor(R.color.eventColor)
        val end = calendar.clone() as Calendar
        //end.add(Calendar.HOUR_OF_DAY, 1)
        val type = if (end[Calendar.MINUTE] > 30) 1 else 0
        //end.add(Calendar.MINUTE, program?.duration?.valueInt()!!.div(60))
        end.add(Calendar.MINUTE, 0)
        //val event = CalendarEvent(3, start, end, "${program?.name}", "house", eventColor)
        //event.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.avatar))
        //event.name = "MI.BO ${program?.name}"
        //events.add(event)

        val h = end.get(Calendar.HOUR_OF_DAY)
        //adapter?.addEvent(h, "MI.BO ${program?.name}", type)
        adapter?.addEvent(h, "$serverName\n ${end.get(Calendar.HOUR)}:${String.format("%02d", end.get(Calendar.MINUTE))}", type)
        // calendarDayView.setLimitTime(0,23)
        //calendarDayView.setEvents(events)
        // calendarDayView
        log("CalendarDayView setEvents")
        // val d = com.android.calendar.DayView
        //calendarDayView.invalidate()
        Single.just(h).delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
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


    override fun onStop() {
        super.onStop()
        //controller.onStop()
    }

}