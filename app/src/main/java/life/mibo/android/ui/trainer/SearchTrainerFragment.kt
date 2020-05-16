/*
 *  Created by Sumeet Kumar on 5/12/20 3:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/11/20 3:28 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_search_trainers.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.trainer.IndependentProfessionals
import life.mibo.android.models.trainer.Professional
import life.mibo.android.models.trainer.SearchTrainers
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchTrainerFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_trainers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupAdapter()
        recyclerView?.layoutManager = GridLayoutManager(context, 1)
        getProfessionals()
    }


    private fun getProfessionals() {
        Prefs.get(context).member

        getDialog()?.show()

        API.request.getApi()
            .searchProfessionals(SearchTrainers(token = Prefs.get(context).member?.accessToken))
            .enqueue(object : Callback<IndependentProfessionals> {
                override fun onFailure(call: Call<IndependentProfessionals>, t: Throwable) {
                    getDialog()?.dismiss()

                }

                override fun onResponse(
                    call: Call<IndependentProfessionals>,
                    response: Response<IndependentProfessionals>
                ) {
                    getDialog()?.dismiss()

                    val data = response?.body();
                    if (data != null && data.isSuccess()) {
                        parseData(data.data?.professionals)
                    } else {
                        val er = data?.errors
                        if (er != null)
                            er?.get(0)?.message?.let {
                                Toasty.snackbar(view, it)
                            }
                    }

                }

            })
    }

    val ipList = ArrayList<Professional>()
    private fun parseData(professionals: List<Professional?>?) {
        if (professionals != null) {
            ipList.clear()
            for (p in professionals) {
                if (p != null)
                    ipList.add(p)
            }


            val adapters = SearchAdapters(1, ipList, object : ItemClickListener<Professional> {
                override fun onItemClicked(item: Professional?, position: Int) {
                    item?.let {
                        ProfessionalDetailsDialog(item).show(
                            childFragmentManager,
                            "ProfessionalDetailsDialog"
                        )
                    }
                }

            })
            recyclerView?.adapter = adapters;
            adapters?.notifyDataSetChanged()
        } else {
            Toasty.snackbar(view, getString(R.string.no_professionals))
        }

    }

    fun updateGrid(grid: Boolean) {
        if (grid) {
            recyclerView?.layoutManager = GridLayoutManager(context, 2)
        } else {
            recyclerView?.layoutManager = GridLayoutManager(context, 1)
        }
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun setupAdapter(type: Int = 1) {

//        recyclerView?.layoutManager = GridLayoutManager(context, 1)
//        val list = ArrayList<Professional>()
//        for (i in 0..20) {
//            list.add(Professional(i))
//        }
//
//
//        val adapters = SearchAdapters(1, list, object : ItemClickListener<SearchItem> {
//            override fun onItemClicked(item: SearchItem?, position: Int) {
//                RememberMeDialog(300).show(childFragmentManager, "RememberMeDialog")
//            }
//
//        })
//        recyclerView?.adapter = adapters;
    }

    class SearchAdapters(
        val type: Int = 1,
        val list: ArrayList<Professional>,
        val listener: ItemClickListener<Professional>?
    ) : RecyclerView.Adapter<SearchHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
            val v = if (type == 1) LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.list_item_trainer_search,
                    parent,
                    false
                ) else LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_item_trainer_search_grid, parent, false)
            return SearchHolder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: SearchHolder, position: Int) {
            holder.bind(list[position], listener)

        }

    }

    //data class SearchItem(val id: Int, var name: String = "Trainer")

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val desc: TextView? = itemView.findViewById(R.id.tv_info)
        val img: ImageView? = itemView.findViewById(R.id.imageView)

        fun bind(item: Professional, listener: ItemClickListener<Professional>?) {
            if (item.avatar != null)
                Glide.with(itemView).load(item.avatar).error(R.drawable.ic_user_test).into(img!!)
            name?.text = item.name
            desc?.text = item.designation
            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }
}