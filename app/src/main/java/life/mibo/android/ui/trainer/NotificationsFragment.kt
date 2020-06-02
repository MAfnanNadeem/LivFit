/*
 *  Created by Sumeet Kumar on 5/17/20 6:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/17/20 6:44 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_notifications.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.login.Member
import life.mibo.android.models.notification.*
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NotificationsFragment : BaseFragment() {

    var testMode = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupAdapter()
        recyclerView?.layoutManager = GridLayoutManager(context, 1)
        //fragment_notifications()
        getNotifications()



        swipeToRefresh?.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark,
            R.color.infoColor2,
            R.color.successColor
        )
        swipeToRefresh?.setOnRefreshListener {
            log("swipeToRefresh?.setOnRefreshListener $isRefreshing")
            isRefreshing = true
            getNotifications()
        }
    }

    var isRefreshing = false

    var isGrid = false
    private val notifications = ArrayList<Notifications>()
    //private val list = ArrayList<Notify>()
    var searchAdapters: NotifyAdapters? = null

//    private fun fragment_notifications() {
//        list.clear()
//        val member = Prefs.get(this.context).member?.isMember() ?: true
//
//        if (member) {
//            list.add(
//                Notify(
//                    0,
//                    "MI.BO Team",
//                    "Thank you for your order, our team will call you for confirmation!"
//                )
//            )
//
//            list.add(
//                Notify(
//                    0,
//                    "Information",
//                    "Congrats! your order has been placed successfully"
//                )
//            )
//
//            for (p in 1..10) {
//                list.add(
//                    Notify(
//                        p,
//                        "Jose Armando",
//                        "Your requested has been accepted by professional", member
//                    )
//                )
//            }
//        } else {
//            list.add(
//                Notify(
//                    0,
//                    "New Order!",
//                    "You have new order from Mr Alex", isMember = true, isAccepted = false
//                )
//            )
//
//            list.add(
//                Notify(
//                    0,
//                    "Mary Johnson",
//                    "Session has been rescheduled on 1st June, 2020 at 2:30 PM ",
//                    isMember = false,
//                    isAccepted = false,
//                    isConfirm = true
//                )
//            )
//
//            list.add(
//                Notify(
//                    0,
//                    "Support Team",
//                    "Your Service request has been rejected ", isMember = false, isAccepted = true
//                )
//            )
//
//            list.add(
//                Notify(
//                    0,
//                    "Support",
//                    "You have new service request", isMember = false, isAccepted = true
//                )
//            )
//
//            for (p in 1..10) {
//                list.add(
//                    Notify(
//                        p,
//                        "Jose Armando",
//                        "You have new invitation request", member, false
//                    )
//                )
//            }
//
//        }
//
//
////        searchAdapters = SearchAdapters(1, list, object : ItemClickListener<Notify> {
////            override fun onItemClicked(item: Notify?, position: Int) {
////
////            }
////
////        })
////        recyclerView?.adapter = searchAdapters;
////        searchAdapters?.notifyDataSetChanged()
//    }


    class NotifyAdapters(
        val type: Int = 1,
        val list: ArrayList<Notifications>,
        val listener: ItemClickListener<Notifications>?
    ) : RecyclerView.Adapter<SearchHolder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
            return SearchHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_test_notifications, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: SearchHolder, position: Int) {
            holder.bind(list[position], listener)

        }


    }

    data class Notifications(
        var id: Int?,
        var markAsRead: Int?,
        var desc: String?,
        var sentBy: String?,
        var status: String?,
        var userId: Int?,
        var type: String?,
        var createdAt: Any?,
        var updatedAt: Any?,
        var daysAgo: String?,
        var image: String?,
        var isMember: Boolean
    ) {
        fun isRead() = markAsRead == 1

        fun isAccepted() =
            status?.toLowerCase() == "confirmed" || status?.toLowerCase() == "accepted"

        fun isButtonVisible() = status?.toLowerCase() == "pending"
    }

//    data class Notify(
//        val id: Int,
//        var name: String,
//        var desc: String,
//        var isMember: Boolean = true,
//        var isAccepted: Boolean = true,
//        var isConfirm: Boolean = false
//    )

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val desc: TextView? = itemView.findViewById(R.id.tv_desc)
        val img: ImageView? = itemView.findViewById(R.id.iv_image)
        val trainer: View? = itemView.findViewById(R.id.ll_trainer)
        val accept: Button? = itemView.findViewById(R.id.btn_accept)
        val reject: Button? = itemView.findViewById(R.id.btn_reject)
        val progress: View? = itemView.findViewById(R.id.ll_trainer_progress)
        val time: TextView? = itemView.findViewById(R.id.tv_date)

        fun bind(item: Notifications, listener: ItemClickListener<Notifications>?) {
            //Logger.e("Notifications bind $item")
            name?.text = item.sentBy
            desc?.text = item.desc
            time?.text = item.daysAgo
            if (item.image != null)
                Glide.with(itemView).load(item.image).fitCenter().error(R.drawable.ic_user_test).into(img!!)
            if (item.isMember) {
                trainer?.visibility = View.GONE
            } else {
                //Logger.e("Notifications bind ${item.isButtonVisible()}")
                // Logger.e("Notifications bind ${item.type}")
                // Logger.e("Notifications bind ${item.status}")
                if (item.isButtonVisible()) {
                    when {
                        item.type?.toLowerCase() == "invite" -> {
                            trainer?.visibility = View.VISIBLE
                            accept?.text = itemView?.context?.getString(R.string.accept)
                            reject?.text = itemView?.context?.getString(R.string.reject)
                        }
                        item.type?.toLowerCase() == "reschedule" -> {
                            trainer?.visibility = View.VISIBLE
                            accept?.text = itemView?.context?.getString(R.string.confirm)
                            reject?.text = itemView?.context?.getString(R.string.decline)
                        }
                        else -> {
                            trainer?.visibility = View.GONE
                            // accept?.text = itemView?.context?.getString(R.string.confirm)
                            //reject?.text = itemView?.context?.getString(R.string.decline)
                            //reject?.text = itemView?.context?.getString(R.string.decline)
                        }
                    }
                } else {
                    trainer?.visibility = View.GONE
                }
            }

            itemView?.setOnClickListener {
                // listener?.onItemClicked(item, adapterPosition)
            }

            accept?.setOnClickListener {
                listener?.onItemClicked(item, 1001)
                if (item.isMember)
                    return@setOnClickListener
                if (item.type?.toLowerCase() == "invite") {
                    val member = Prefs.get(this.itemView.context).member
                    acceptInvite(trainer, progress, item.id, true, member?.accessToken)
                } else if (item.type?.toLowerCase() == "reschedule") {
                    val member = Prefs.get(this.itemView.context).member
                    acceptRescheduleRequest(trainer, progress, item.id, true, member?.accessToken)
                }
            }
            reject?.setOnClickListener {
                if (item.isMember)
                    return@setOnClickListener
                listener?.onItemClicked(item, 1002)
                if (item.type?.toLowerCase() == "invite") {
                    val member = Prefs.get(this.itemView.context).member
                    acceptInvite(trainer, progress, item.id, false, member?.accessToken)
                } else if (item.type?.toLowerCase() == "reschedule") {
                    val member = Prefs.get(this.itemView.context).member
                    acceptRescheduleRequest(trainer, progress, item.id, false, member?.accessToken)
                }
            }
        }

        private fun acceptInvite(
            parent: View?,
            progress: View?,
            id: Int?,
            accept: Boolean,
            token: String?
        ) {
            if (id == null || token == null)
                return
            var status = if (accept) "Accepted" else "Declined"
            parent?.visibility = View.INVISIBLE
            progress?.visibility = View.VISIBLE
            val data =
                AcceptMemberInvite(AcceptMemberInvite.Data(id, status), token)
            API.request.getApi().acceptMemberInvite(data)
                .enqueue(object : Callback<ResponseData> {
                    override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                        Toasty.snackbar(parent, R.string.unable_to_connect)
                        parent?.visibility = View.VISIBLE
                        progress?.visibility = View.GONE
                    }

                    override fun onResponse(
                        call: Call<ResponseData>,
                        response: Response<ResponseData>
                    ) {
                        val body = response.body()
                        if (body != null && body.isSuccess()) {
                            progress?.visibility = View.GONE
                            //parent?.visibility = View.INVISIBLE
                            Utils.collapse(parent)
                            val msg = body?.data?.message
                            Toasty.snackbar(parent, msg ?: "Invite $status")
                            return
                        }
                        try {
                            progress?.visibility = View.GONE
                            parent?.visibility = View.VISIBLE
                            val msg = body?.errors?.get(0)?.message
                            Toasty.snackbar(parent, msg)
                        } catch (e: Exception) {

                        }
                    }

                })
        }

        private fun acceptRescheduleRequest(
            parent: View?,
            progress: View?,
            id: Int?,
            confirm: Boolean,
            token: String?
        ) {
            if (id == null || token == null)
                return

            var status = if (confirm) "Confirmed" else "Declined"
            parent?.visibility = View.INVISIBLE
            progress?.visibility = View.VISIBLE
            val data =
                AcceptRescheduleRequest(AcceptRescheduleRequest.Data(id, status), token)
            API.request.getApi().acceptRescheduleRequest(data)
                .enqueue(object : Callback<ResponseData> {
                    override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                        Toasty.snackbar(parent, R.string.unable_to_connect)
                        parent?.visibility = View.VISIBLE
                        progress?.visibility = View.GONE
                    }

                    override fun onResponse(
                        call: Call<ResponseData>,
                        response: Response<ResponseData>
                    ) {
                        val body = response.body()
                        if (body != null && body.isSuccess()) {
                            progress?.visibility = View.GONE
                            //parent?.visibility = View.INVISIBLE
                            Utils.collapse(parent)
                            val msg = body?.data?.message
                            Toasty.snackbar(parent, msg ?: "Reschedule Request $status")
                            return
                        }
                        try {
                            progress?.visibility = View.GONE
                            parent?.visibility = View.VISIBLE
                            val msg = body?.errors?.get(0)?.message
                            Toasty.snackbar(parent, msg)
                        } catch (e: Exception) {

                        }
                    }

                })
        }
    }

    fun showProgress() {
        activity?.runOnUiThread {
            // progressBar?.visibility = View.VISIBLE
            isRefreshing = true
            swipeToRefresh?.isRefreshing = isRefreshing

        }
    }

    fun hideProgress() {
        activity?.runOnUiThread {
            //progressBar?.visibility = View.GONE
            isRefreshing = false
            swipeToRefresh?.isRefreshing = isRefreshing

        }
    }

    private fun getNotifications() {
        val member = Prefs.get(this.context).member
            ?: return

        if (member.isMember())
            getMemberNotifications(member)
        else
            getTrainerNotifications(member)
    }

    private fun getMemberNotifications(member: Member) {
        showProgress()
        val data =
            GetMemberNotifications(GetMemberNotifications.Data(member.id), member.accessToken)
        API.request.getApi().getMemberNotifications(data)
            .enqueue(object : Callback<MemberNotifications> {
                override fun onFailure(call: Call<MemberNotifications>, t: Throwable) {
                    hideProgress()
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<MemberNotifications>,
                    response: Response<MemberNotifications>
                ) {
                    hideProgress()
                    parseNotifications(response?.body()?.data)
                }

            })
    }


    private fun getTrainerNotifications(member: Member) {
        showProgress()
        val data =
            GetTrainerNotifications(GetTrainerNotifications.Data(member.id), member.accessToken)
        API.request.getApi().getTrainerNotifications(data)
            .enqueue(object : Callback<TrainerNotifications> {
                override fun onFailure(call: Call<TrainerNotifications>, t: Throwable) {
                    hideProgress()
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<TrainerNotifications>,
                    response: Response<TrainerNotifications>
                ) {
                    hideProgress()
                    parseTrainerNotifications(response?.body()?.data)
                }

            })
    }

    private fun acceptInvite(id: Int, accept: Boolean, token: String) {
        var status = if (accept) "Declined" else "Accepted"
        showProgress()
        val data =
            AcceptMemberInvite(AcceptMemberInvite.Data(id, status), token)
        API.request.getApi().acceptMemberInvite(data)
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    hideProgress()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    hideProgress()
                }

            })
    }

    private fun acceptRescheduleRequest(id: Int, confirm: Boolean, token: String) {
        var status = if (confirm) "Declined" else "Confirmed"
        showProgress()
        val data =
            AcceptRescheduleRequest(AcceptRescheduleRequest.Data(id, status), token)
        API.request.getApi().acceptRescheduleRequest(data)
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    hideProgress()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    hideProgress()
                }

            })
    }

    private fun parseNotifications(data: List<MemberNotifications.Data?>?) {
        if (data != null && data.isNotEmpty()) {
            tv_empty?.visibility = View.GONE
            notifications.clear()
            Collections.sort(data) { o2, o1 ->
                o1?.createdAt?.date?.compareTo(
                    o2?.createdAt?.date ?: ""
                ) ?: -1
            }

            for (n in data) {
                if (n != null)
                    notifications.add(
                        Notifications(
                            n.id,
                            n.markAsRead,
                            n.notification,
                            n.sentBy,
                            n.status,
                            n.memberId,
                            n.type,
                            n.createdAt,
                            n.updatedAt,
                            getDaysAgo(n.createdAt?.date),
                            n.avatar,
                            true
                        )
                    )
            }

            updateRecyclerView()
        } else {
            tv_empty?.visibility = View.VISIBLE
        }
    }

    private fun parseTrainerNotifications(data: List<TrainerNotifications.Data?>?) {
        if (data != null && data.isNotEmpty()) {
            Collections.sort(data) { o2, o1 ->
                o1?.createdAt?.date?.compareTo(
                    o2?.createdAt?.date ?: ""
                ) ?: -1
            }

            tv_empty?.visibility = View.GONE
            notifications.clear()

            for (n in data) {
                if (n != null)
                    notifications.add(
                        Notifications(
                            n.id,
                            n.markAsRead,
                            n.notification,
                            n.sentBy,
                            n.status,
                            n.trainerId,
                            n.type,
                            n.createdAt,
                            n.updatedAt,
                            getDaysAgo(n.createdAt?.date),
                            n.avatar,
                            false
                        )
                    )
            }

            updateRecyclerView()
        } else {
            tv_empty?.visibility = View.VISIBLE
        }
    }

    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    fun getDaysAgo(date: String?): String {
        if (date == null)
            return ""

        try {
            return "" + DateUtils.getRelativeTimeSpanString(inputFormat.parse(date).time)
        } catch (e: java.lang.Exception) {

        }
        return ""

    }

    private fun updateRecyclerView() {
        searchAdapters =
            NotifyAdapters(1, notifications, object : ItemClickListener<Notifications> {
                override fun onItemClicked(item: Notifications?, position: Int) {

                }

            })
        recyclerView?.adapter = searchAdapters;
        searchAdapters?.notifyDataSetChanged()
    }

}