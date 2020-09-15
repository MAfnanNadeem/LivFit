/*
 *  Created by Sumeet Kumar on 7/8/20 10:44 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/7/20 5:48 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.workout

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_workouts.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.workout.GetWorkout
import life.mibo.android.models.workout.Workouts
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WorkoutFragment : BaseFragment() {

    companion object {
        fun create(type: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt("type_", type)
            return bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    var isRefreshing = false
    var layoutManager: GridLayoutManager? = null

    private var type_ = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwipeRefreshColors(swipeToRefresh)
        swipeToRefresh?.setOnRefreshListener {
            log("swipeToRefresh?.setOnRefreshListener $isRefreshing")
            isRefreshing = true
            getWorkouts()
        }
        recyclerView?.layoutManager = GridLayoutManager(context, 1)

        type_ = arguments?.getInt("type_", 0) ?: 0
        layoutManager = GridLayoutManager(context, 1)

        getWorkouts()
        setChips()
        setHasOptionsMenu(true)
    }

    fun setChips() {
        val list = arrayOf("EMS", "TENS", "REACT LIGHTS", "REACT TILES", "FitFLix", "Abs")

        val textColor = requireContext().getColor(R.color.textColor2)
        var count = 1;
        for (s in list) {
            try {
                val chip = Chip(context)
                chip.text = s
                chip.setTextColor(textColor)
                chip.setChipBackgroundColorResource(R.color.white)
                chip.isClickable = true
                chip.isCheckable = true
                chip.isCheckedIconVisible = true
                chip.id = count
                chip.setChipIconTintResource(R.color.colorAccent)
                //chip.isCloseIconVisible = true
                //chip.setCheckedIconResource(R.drawable.cross_mark)
                chip_group.addView(chip)
            } catch (e: java.lang.Exception) {
            }
            count++
        }


        chip_group?.isSingleSelection = true
        chip_group?.setOnCheckedChangeListener { group, checkedId ->
            log("setOnCheckedChangeListener $checkedId")
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


    private fun getWorkouts() {
        val member = Prefs.get(context).member ?: return
        showProgress()
        API.request.getApi()
            .getWorkouts(
                GetWorkout(GetWorkout.Data(member.id, 1, 50, ""), member.accessToken)
            )
            .enqueue(object : Callback<Workouts> {
                override fun onFailure(call: Call<Workouts>, t: Throwable) {
                    hideProgress()

                }

                override fun onResponse(
                    call: Call<Workouts>,
                    response: Response<Workouts>
                ) {
                    log("ProfessionalDetails getDetails >> onResponse ")
                    try {
                        val data = response?.body();
                        log("ProfessionalDetails getDetails >> onResponse success $data")
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            parseData(list)

                        } else {
                            log("ProfessionalDetails getDetails >> onResponse failed ${response.body()}")
                            parseData(null)
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.let {
                                    if (it?.code != 404)
                                        Toasty.snackbar(recyclerView, it?.message)
                                }
                            //tv_service_no?.visibility = View.VISIBLE
                            //tv_specialization_no?.visibility = View.VISIBLE
                            //tv_certificate_no?.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    //getmDialog()?.dismiss()
                    hideProgress()
                }

            })

    }

    private fun parseData(data: Workouts.Data?) {
        if (data == null) {
            tv_empty?.visibility = View.VISIBLE
            return
        }

        val list = data?.programs
        val invoices = ArrayList<Workouts.Program>()
        if (list == null || list.isEmpty()) {
            tv_empty?.setText(R.string.no_data_found)
            tv_empty?.visibility = View.VISIBLE
            return
        }

        for (i in list) {
            i?.let {
                invoices.add(it)
            }
        }

        recyclerView?.layoutManager = layoutManager
        val addressAdapters =
            InvoiceAdapters(invoices, object : ItemClickListener<Workouts.Program> {
                override fun onItemClicked(item: Workouts.Program?, position: Int) {

                }
            })

        recyclerView?.adapter = addressAdapters

    }


    class InvoiceAdapters(
        val list: ArrayList<Workouts.Program>,
        val listener: ItemClickListener<Workouts.Program>?
    ) : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_workouts,
                        parent,
                        false
                    )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(list[position], listener)

        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView? = itemView.findViewById(R.id.tv_name)
        private val desc: TextView? = itemView.findViewById(R.id.tv_desc)
        private val dur: TextView? = itemView.findViewById(R.id.tv_dur)


        fun bind(item: Workouts.Program?, listener: ItemClickListener<Workouts.Program>?) {
            if (item == null)
                return
            name?.text = item.name
            desc?.text = item.description
            dur?.text = item?.duration?.value
            val d = item?.duration?.value?.toIntOrNull()
            if (d != null) {
                dur?.text = String.format("%02d:%02d", d.div(60), d % 60)
            } else {
                dur?.text = "00:00"
            }

            itemView.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_filter, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            if (layoutManager?.spanCount == 1) {
                layoutManager?.spanCount = 2
            } else {
                layoutManager?.spanCount = 1
            }

        }
        return super.onOptionsItemSelected(item)
    }


}