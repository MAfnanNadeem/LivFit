/*
 *  Created by Sumeet Kumar on 6/3/20 2:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/14/20 10:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.SessionReadResponse
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.fragment_google_fit_history.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import java.text.DateFormat
import java.util.concurrent.TimeUnit

class FitHistoryFragment : BaseFragment() {

    val TAG = "GoogleFitFragment"

    companion object {
        fun create(type: Int): FitHistoryFragment {
            val frg = FitHistoryFragment()
            val b = Bundle()
            b.putInt("type_type", type)
            frg.arguments = b
            return frg
        }
    }

    private var helper: FitnessHelper? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_google_fit_history, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getInt("type_type", 0) ?: 0

        if (type == 0) {
            if (getFit().isConnected()) {
                getFit().readyWeekly(OnSuccessListener<DataReadResponse> {
                    parseData(it)
                })
            }
        } else if (type == 1) {
            if (getFit().isConnected()) {
                getFit().readyMonthly(OnSuccessListener<DataReadResponse> {
                    parseData(it)
                })
            }
        } else if (type == 2) {
            val dataType = DataType.TYPE_ACTIVITY_SEGMENT
            if (getFit().isConnected(dataType)) {
                getFit().getSessions(dataType, OnSuccessListener<SessionReadResponse> {
                    parseData(it)
                })
            } else {
                getFit().connect(dataType, object : FitnessHelper.Listener<Int> {
                    override fun onComplete(success: Boolean, data: Int?, ex: Exception?) {
                        getFit().getSessions(dataType, OnSuccessListener<SessionReadResponse> {
                            parseData(it)
                        })
                    }

                })
            }
        }

    }

    private fun getFit(): GoogleFit {
        if (helper == null)
            helper = FitnessHelper(this)
        return helper!!.getGoogleFit()
    }


    fun parseData(data: SessionReadResponse) {
        log("SessionReadResponse $data")
        log("SessionReadResponse ${data.status}")
        val sessions = data?.sessions
        val list = ArrayList<Item>()
        val dateFormat: DateFormat = DateFormat.getDateTimeInstance()
        log("SessionReadResponse sessions size ${sessions.size}")
        if (sessions.isNotEmpty()) {

            for (s in sessions) {
                val text1 = ""
                log("SessionReadResponse s:: $s")
                val dataSets: List<DataSet> = data.getDataSet(s)
                for (dataSet in dataSets) {

                    for (dp in dataSet.dataPoints) {
                        GoogleFit.log("DataBucket point: $dp")
                        GoogleFit.log("\tType: " + dp.dataType.name)
                        GoogleFit.log("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                        GoogleFit.log("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                        var fData = ""
                        for (field in dp.dataType.fields) {
                            fData += "Field: " + field.name + " Value: " + dp.getValue(field)
                            GoogleFit.log(fData)
                        }

                        list.add(
                            Item(
                                fData,
                                "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)),
                                "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
                            )
                        )
                    }
                }
            }
        }




        log("SessionReadResponse $data")
        recycler_view?.layoutManager = LinearLayoutManager(context)
        recycler_view?.adapter = HistoryAdapters(list, null)

    }

    fun parseData(dataReadResult: DataReadResponse) {
        val dateFormat: DateFormat = DateFormat.getDateTimeInstance()
        val list = ArrayList<Item>()
        if (dataReadResult.buckets.size > 0) {
            GoogleFit.log("Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                val dataSets = bucket.dataSets

                for (dataSet in dataSets) {
                    GoogleFit.log("Data returned for Data type: " + dataSet.dataType.name)

                    for (dp in dataSet.dataPoints) {
                        GoogleFit.log("DataBucket point: $dp")
                        GoogleFit.log("\tType: " + dp.dataType.name)
                        GoogleFit.log("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                        GoogleFit.log("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                        var fData = ""
                        for (field in dp.dataType.fields) {
                            fData += "Field: " + field.name + " Value: " + dp.getValue(field)
                            GoogleFit.log(fData)
                        }

                        list.add(
                            Item(
                                fData,
                                "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)),
                                "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
                            )
                        )
                    }
                }
            }
        }


        if (dataReadResult.dataSets.size > 0) {
            list.add(Item("END-------------", "", ""))
            GoogleFit.log("Number of returned DataSets is: " + dataReadResult.dataSets.size)
            for (dataSet in dataReadResult.dataSets) {
                for (dp in dataSet.dataPoints) {
                    GoogleFit.log("DataSet point:")
                    GoogleFit.log("\tType: " + dp.dataType.name)
                    GoogleFit.log("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                    GoogleFit.log("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                    var fData = ""
                    for (field in dp.dataType.fields) {
                        fData += "Field: " + field.name + " Value: " + dp.getValue(field)
                        GoogleFit.log(fData)
                    }
                    list.add(
                        Item(
                            fData,
                            "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)),
                            "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
                        )
                    )
                }
            }
        }
        // [END parse_read_data_result]

        recycler_view?.layoutManager = LinearLayoutManager(context)
        recycler_view?.adapter = HistoryAdapters(list, null)

    }

    class Item(var text1: String, var text2: String, var text3: String)

    class HistoryAdapters(
        val list: ArrayList<Item>,
        val listener: ItemClickListener<Item>?
    ) : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_google_fit_history,
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
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val desc: TextView? = itemView.findViewById(R.id.tv_info)
        val price: TextView? = itemView.findViewById(R.id.tv_info2)

        fun bind(item: Item?, listener: ItemClickListener<Item>?) {
            if (item == null)
                return
            name?.text = item.text1
            desc?.text = item.text2
            price?.text = item.text3
            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }
}