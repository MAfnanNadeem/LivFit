/*
 *  Created by Sumeet Kumar on 5/17/20 6:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/17/20 6:44 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_notifications.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener


class NotificationsFragment : BaseFragment() {

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
        fragment_notifications()

    }

    var isGrid = false
    val ipList = ArrayList<Notify>()
    var searchAdapters: SearchAdapters? = null

    private fun fragment_notifications() {
        ipList.clear()
        val member = Prefs.get(this.context).member?.isMember() ?: true

        if (member) {
            ipList.add(
                Notify(
                    0,
                    "MI.BO Team",
                    "Thank you for your order, our team will call you for confirmation!"
                )
            )

            ipList.add(
                Notify(
                    0,
                    "Information",
                    "Congrats! your order has been placed successfully"
                )
            )

            for (p in 1..10) {
                ipList.add(
                    Notify(
                        p,
                        "Jose Armando",
                        "Your requested has been accepted by professional", member
                    )
                )
            }
        } else {
            ipList.add(
                Notify(
                    0,
                    "New Order!",
                    "You have new order from Mr Alex", isMember = true, isAccepted = false
                )
            )

            ipList.add(
                Notify(
                    0,
                    "Mary Johnson",
                    "Session has been rescheduled on 1st June, 2020 at 2:30 PM ",
                    isMember = false,
                    isAccepted = false,
                    isConfirm = true
                )
            )

            ipList.add(
                Notify(
                    0,
                    "Support Team",
                    "Your Service request has been rejected ", isMember = false, isAccepted = true
                )
            )

            ipList.add(
                Notify(
                    0,
                    "Support",
                    "You have new service request", isMember = false, isAccepted = true
                )
            )

            for (p in 1..10) {
                ipList.add(
                    Notify(
                        p,
                        "Jose Armando",
                        "You have new invitation request", member, false
                    )
                )
            }

        }




        searchAdapters = SearchAdapters(1, ipList, object : ItemClickListener<Notify> {
            override fun onItemClicked(item: Notify?, position: Int) {

            }

        })
        recyclerView?.adapter = searchAdapters;
        searchAdapters?.notifyDataSetChanged()
    }


    class SearchAdapters(
        val type: Int = 1,
        val list: ArrayList<Notify>,
        val listener: ItemClickListener<Notify>?
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

    data class Notify(
        val id: Int,
        var name: String,
        var desc: String,
        var isMember: Boolean = true,
        var isAccepted: Boolean = true,
        var isConfirm: Boolean = false
    )

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val desc: TextView? = itemView.findViewById(R.id.tv_desc)
        val img: ImageView? = itemView.findViewById(R.id.imageView)
        val trainer: View? = itemView.findViewById(R.id.ll_trainer)
        val accept: Button? = itemView.findViewById(R.id.btn_accept)

        fun bind(item: Notify, listener: ItemClickListener<Notify>?) {
            name?.text = item.name
            desc?.text = item.desc
            when {
                item.isMember -> {
                    trainer?.visibility = View.GONE
                }
                item.isAccepted -> {
                    trainer?.visibility = View.GONE
                }
                item.isConfirm -> {
                    trainer?.visibility = View.VISIBLE
                    accept?.setText("Confirm")
                }
                else -> {
                    trainer?.visibility = View.VISIBLE
                    accept?.setText("Accept")
                }
            }
            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }
}