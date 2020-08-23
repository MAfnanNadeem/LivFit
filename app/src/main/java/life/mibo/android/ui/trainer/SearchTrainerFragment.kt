/*
 *  Created by Sumeet Kumar on 5/12/20 3:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/11/20 3:28 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import life.mibo.android.utils.Utils
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
        setHasOptionsMenu(true)
    }


    private fun getProfessionals() {
        //Prefs.get(context).member

        getDialog()?.show()

        API.request.getApi()
            .searchProfessionals(SearchTrainers(token = Prefs.get(context).member?.accessToken))
            .enqueue(object : Callback<IndependentProfessionals> {
                override fun onFailure(call: Call<IndependentProfessionals>, t: Throwable) {
                   // log("searchProfessionals failed $t")
                    getDialog()?.dismiss()
                   // t?.printStackTrace()
                    Toasty.info(requireContext(), R.string.error_occurred).show()
                }

                override fun onResponse(
                    call: Call<IndependentProfessionals>,
                    response: Response<IndependentProfessionals>
                ) {
                    getDialog()?.dismiss()
                    val data = response?.body();
                    //log("searchProfessionals onResponse $data")
                    if (data != null && data.isSuccess()) {
                        parseData(data.data?.professionals)
                    } else {
                        checkSession(data)
                        val er = data?.errors
                        if (er != null)
                            er?.get(0)?.message?.let {
                                Toasty.snackbar(view, it)
                            }
                    }

                }

            })
    }

    private val ipList = ArrayList<Professional>()
    private val backupList = ArrayList<Professional>()
    private var adapters: SearchAdapters? = null
    private fun parseData(professionals: List<Professional?>?) {
        log("parseData professionals $professionals")
        if (professionals != null) {
            ipList.clear()
            backupList.clear()
            for (p in professionals) {
                if (p != null) {
                    ipList.add(p)
                    backupList.add(p)
                }
            }

            log("parseData professionals ${ipList.size}")
            adapters = SearchAdapters(1, ipList, object : ItemClickListener<Professional> {
                override fun onItemClicked(item: Professional?, position: Int) {
                    log("onItemClicked item $item")
                    item?.let {
                        ProfessionalDetailsActivity.launch(
                            requireContext(),
                            ProfessionalDetailsActivity.create(it),
                            9
                        )
//                        ProfessionalDetailsDialog(item).show(
//                            childFragmentManager,
//                            "ProfessionalDetailsDialog"
//                        )
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

        fun filter(backup: ArrayList<Professional>, text: String?) {
            list.clear()
            if (text.isNullOrEmpty()) {
                for (i in backup)
                    list.add(i)
            } else {
                val q = text.toLowerCase()
                for (item in backup) {
                    if (item.match(q)) {
                        list.add(item)
                    }
                }
            }
            notifyDataSetChanged()
        }
    }

    //data class SearchItem(val id: Int, var name: String = "Trainer")

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val desc: TextView? = itemView.findViewById(R.id.tv_info)
        val info: TextView? = itemView.findViewById(R.id.tv_info2)
        val img: ImageView? = itemView.findViewById(R.id.imageView)

        fun bind(item: Professional, listener: ItemClickListener<Professional>?) {
            Utils.loadImage(img, item.avatar, item.gender?.toLowerCase() == "male")
//            var url = item.avatar
//            var def = if (item.gender?.toLowerCase() == "male") R.drawable.ic_user_male else R.drawable.ic_user_female
//
//            if (url != null && (url.endsWith("jpg") || url.endsWith("png"))) {
//                Glide.with(itemView).load(item.avatar).error(def).fallback(def).fitCenter()
//                    .into(img!!)
//            } else {
//                Glide.with(itemView).load(def).error(def).fallback(def).fitCenter()
//                    .into(img!!)
//            }
            name?.text = item.name
            desc?.text = item.designation
            info?.text = "${item.city}, ${item.country}"
            val drw = Utils.getColorFilterDrawable(itemView.context, R.drawable.ic_location_on_black_24dp, Color.GRAY)
            info?.setCompoundDrawablesWithIntrinsicBounds(drw, null, null, null)
            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_search, menu)
        try {
            val item = menu.findItem(R.id.action_search)
            val searchView: SearchView? =
                SearchView((activity as AppCompatActivity?)?.supportActionBar?.themedContext)
            // MenuItemCompat.setShowAsAction(item, //MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | //MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            //  MenuItemCompat.setActionView(item, searchView);
            // These lines are deprecated in API 26 use instead
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            item.actionView = searchView
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapters?.filter(backupList, newText)
                    return true
                }
            })
            searchView?.setOnClickListener {
                log("search clicked")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            //updateGrid()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}