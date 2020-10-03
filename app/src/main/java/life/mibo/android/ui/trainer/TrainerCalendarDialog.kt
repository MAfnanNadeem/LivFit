/*
 *  Created by Sumeet Kumar on 5/11/20 3:28 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/11/20 3:28 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_trainer_calendar.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.core.security.Encrypt
import life.mibo.android.models.trainer.TrainerCalendarResponse
import life.mibo.android.models.trainer.TrainerCalendarSession
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import life.mibo.hardware.encryption.MCrypt
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TrainerCalendarDialog(var calendarListener: ItemClickListener<TrainerSession>) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trainer_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val formatter = DateTimeFormatter.ofPattern("EEE dd MMM hm:mm a")
        //tv_program?.text = arguments?.getString("trainer_name")
        // tv_service?.text = arguments?.getString("service_name")
        //tv_day?.text = formatter.format(org.threeten.bp.LocalDateTime.now())


        setRecycler()

        button_next?.isEnabled = false
        button_next?.setOnClickListener {
            showConfirmDialog()
        }
        getTrainerCalender()

    }

    fun showConfirmDialog() {
        //Are you sure you want to start the session?
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogPhoto)
        builder.setTitle(R.string.attendance)
        builder.setMessage(getString(R.string.start_session_text))
        builder.setPositiveButton(R.string.yes_text) { dialog, which ->
            if (selectedItem != null) {
                calendarListener?.onItemClicked(selectedItem, 100)
                dismiss()
            }
        }
        builder.setNegativeButton(R.string.no_text) { dialog, which ->

        }

        builder.show()
    }

    private val dayList = ArrayList<TrainerSession>()
    private var adapter: DayAdapter? = null
    private var selectedItem: TrainerSession? = null
    fun setRecycler() {

        adapter = DayAdapter(dayList, object : ItemClickListener<TrainerSession> {
            override fun onItemClicked(item: TrainerSession?, position: Int) {
                if (item == null)
                    return
                adapter?.updateSelection(item)

                //if (item?.started == 1 || item?.completed == 1) {
                Logger.e("DayAdapter onItemClicked ${item.duration}")
                if (item?.completed == 1) {
                    button_next?.isEnabled = false
                    selectedItem = null
                    return
                } else {
                    if (item.duration < 16 && item.duration > -30) {
                        button_next?.isEnabled = true
                        selectedItem = item
                    } else {
                        button_next?.isEnabled = false
                        selectedItem = null
                    }
                }
            }

        })
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = adapter
    }


    fun getTrainerCalender() {
        val member = Prefs.get(context).member ?: return

        //getmDialog()?.show()
        progressBar?.visibility = View.VISIBLE

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val post =
            TrainerCalendarSession(
                TrainerCalendarSession.Data(
                    "${member.id}",
                    formatter.format(LocalDate.now().minusDays(21)),
                    formatter.format(LocalDate.now().plusDays(7))
                ), member.accessToken
            )
        API.request.getTrainerApi().getTrainerCalendarSession(post)
            .enqueue(object : Callback<TrainerCalendarResponse> {
                override fun onFailure(call: Call<TrainerCalendarResponse>, t: Throwable) {
                    progressBar?.visibility = View.GONE
                    // getmDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(requireContext(), R.string.unable_to_connect).show()
                    // Toasty.error(fragment.context!!, "Unable to connect").show()
                }

                override fun onResponse(
                    call: Call<TrainerCalendarResponse>,
                    response: Response<TrainerCalendarResponse>
                ) {

                    //getmDialog()?.dismiss()
                    val data = response.body()
                    if (data != null && data.isSuccess()) {
                        //  parseData(data)
                        onTrainerCalendar(data.data)
                        return
                    } else {
                        onTrainerCalendar(null)
                        val err = data?.errors?.get(0)?.message
                        if (err.isNullOrEmpty())
                            Toasty.error(requireContext(), R.string.error_occurred).show()
                        else Toasty.error(requireContext(), err, Toasty.LENGTH_LONG).show()
                    }

                    progressBar?.visibility = View.GONE

                }
            })
    }

    private fun onTrainerCalendar(data: TrainerCalendarResponse.Data?) {
        dayList.clear()
        if (data?.sessions != null) {
            if (data?.sessions?.size ?: 0 > 0) {
                tv_empty?.visibility = View.GONE

                val crypt = MCrypt()
                val dateTimeParser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val parser = DateTimeFormatter.ofPattern("hh:mm a")
                val formatter = DateTimeFormatter.ofPattern("EEEE dd MMM")
                val now = LocalDateTime.now()
                //Logger.e("canStart Duration ${d.toMinutes()}")
                //val date = LocalDate.now()
                for (s in data.sessions!!) {
                    if (s != null) {
                        if (s.sessionType?.toLowerCase() == "private") {
                            val duration = Duration.between(
                                now,
                                LocalDateTime.parse(s.startDatetime, dateTimeParser)
                            ).toMinutes()
                            val member = s.members?.get(0)
                            var name = ""
                            var weight = ""
                            try {
                                name = String(crypt.decrypt(member?.firstName)) + " " + String(
                                    crypt.decrypt(member?.lastName)
                                )
                                weight = String(crypt.decrypt(member?.weight))

                            } catch (e: Exception) {

                            }
                            dayList.add(
                                TrainerSession(
                                    s.sessionId ?: 0,
                                    member?.id ?: 0,
                                    formatter.format(
                                        LocalDateTime.parse(
                                            s.startDatetime,
                                            dateTimeParser
                                        )
                                    ),
                                    parser.format(LocalTime.parse(s.startDatetime, dateTimeParser)),
                                    parser.format(LocalTime.parse(s.endDatetime, dateTimeParser)),
                                    name,
                                    member?.profileImg,
                                    s.notes,
                                    member?.age,
                                    member?.height,
                                    weight,
                                    s.started,
                                    s.completed,
                                    duration
                                )
                            )
                        }

                    }
                }

                if (MiboApplication.DEBUG) {
                    dayList.add(
                        TrainerSession(
                            100,
                            100,
                            formatter.format(LocalDate.now()),
                            parser.format(LocalDateTime.now()),
                            parser.format(LocalDateTime.now().plusMinutes(30)),
                            "Sumeet",
                            "",
                            "Test Session",
                            "28",
                            "178",
                            "68",
                            0,
                            0,
                            30
                        )
                    )
                }

                activity?.runOnUiThread {
                    progressBar?.visibility = View.GONE
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            } else {
                tv_empty?.visibility = View.VISIBLE
            }
        } else {
            tv_empty?.visibility = View.VISIBLE
        }
        activity?.runOnUiThread {
            progressBar?.visibility = View.GONE
        }


    }

    fun canStart(time: LocalDateTime): Boolean {
        Logger.e("canStart time $time")
        val now = LocalDate.now()
        Logger.e("canStart Now $now")

        val d = Duration.between(LocalDateTime.now(), time)

        Logger.e("canStart Duration ${d.toMinutes()}")
        Logger.e("canStart Duration ${d.toHours()}")

        Logger.e("canStart ofMinutes ${Duration.ofMinutes(15)}")

        return false
    }


    class DayAdapter(
        val list: java.util.ArrayList<TrainerSession>,
        var listener: ItemClickListener<TrainerSession>? = null
    ) : RecyclerView.Adapter<DayAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_day_schedule_trainer2, parent, false
                )
            )
        }

        override fun getItemId(position: Int): Long {
            return getItem(position)?.sessionId?.toLong() ?: 0L
        }

        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        private fun getItem(position: Int): TrainerSession? {
            return list[position]
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(list[position], listener)
            // val item = list[position]

        }

        fun updateSelection(item: TrainerSession) {
            Logger.e("updateSelection ${item.sessionId}")
            try {
                for (d in list) {
                    d.isSelected = d.sessionId == item.sessionId
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            notifyDataSetChanged()
        }


        @Synchronized
        fun addEvent(
            id: Int,
            memberName: String?,
            memberPic: String?,
            service: String?,
            type: Int = 0
        ) {
            Logger.e("DayAdapter : $id - $type :: $service")
            list.forEachIndexed { _, item ->
                if (item.sessionId == id) {
                    item.event = service
                    item.member = memberName
                    item.profile = memberPic
                    item.type = type
                    // Logger.e("DayAdapter found: $item")
                }
            }

            notifyDataSetChanged()
        }


        class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var parrent: View? = itemView.findViewById(R.id.parent_view)
            var time: TextView? = itemView.findViewById(R.id.tv_text)
            var start: TextView? = itemView.findViewById(R.id.tv_start)
            var end: TextView? = itemView.findViewById(R.id.tv_end)
            var viewStarted: View? = itemView.findViewById(R.id.viewStarted)
            var text1: TextView? = itemView.findViewById(R.id.tv_text1)
            var text2: TextView? = itemView.findViewById(R.id.tv_text2)
            var completed: TextView? = itemView.findViewById(R.id.tv_completed)
            var image: ImageView? = itemView.findViewById(R.id.imageView)
            var data: TrainerSession? = null

            fun bind(item: TrainerSession?, listener: ItemClickListener<TrainerSession>?) {
                if (item == null)
                    return
                data = item
                time?.text = "${item.time}"
                text1?.text = item?.member
                text2?.text = item?.event
                start?.text = item?.startTime
                end?.text = item?.endTime
                if (item.profile != null && item.profile!!.isNotEmpty()) {
                    Glide.with(itemView).load(item.profile).centerCrop()
                        .error(R.drawable.ic_user_test).fallback(R.drawable.ic_user_test)
                        .into(image!!)
                } else {
                    Glide.with(itemView).load(R.drawable.ic_user_test).into(image!!)
                }
                if (item.started == 1) {
                    viewStarted?.setBackgroundColor(Color.RED)
                    completed?.visibility = View.VISIBLE
                    if (item.completed == 1) {
                        completed?.setText(R.string.completed)
                    } else {
                        completed?.setText(R.string.in_progress)
                    }
                } else if (item.completed == 1) {
                    viewStarted?.setBackgroundColor(Color.RED)
                    completed?.visibility = View.VISIBLE
                    completed?.setText(R.string.completed)
                } else {
                    if (item.duration < -30) {
                        completed?.visibility = View.VISIBLE
                        completed?.setText(R.string.missed_session)
                    } else {
                        completed?.visibility = View.GONE
                    }
                    viewStarted?.setBackgroundColor(Color.GREEN)
                    //completed?.visibility = View.GONE
                }

                if (item.isSelected)
                    parrent?.setBackgroundColor(0xfff1f1f1.toInt())
                else
                    parrent?.setBackgroundColor(0xffffffff.toInt())

                itemView?.setOnClickListener {
                    listener?.onItemClicked(data, adapterPosition)
                }
            }
        }


    }

    data class TrainerSession(
        val sessionId: Int,
        val memberId: Int,
        val time: String,
        val startTime: String,
        val endTime: String,
        var member: String? = "",
        var profile: String? = "",
        var event: String? = "",
        var age: String? = "",
        var height: String? = "",
        var weight: String? = "",
        var started: Int? = 0,
        var completed: Int? = 0,
        var duration: Long = 0
    ) {
        var type = 0
        var isSelected = false
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog?.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}