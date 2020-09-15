/*
 *  Created by Sumeet Kumar on 3/10/20 9:41 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/10/20 9:41 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxt.score

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.rxt.GetAllIslandPost
import life.mibo.android.models.rxt.GetAllIslands
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectIslandDialog(c: Context, var listner: ItemClickListener<GetAllIslands.Island?>?) :
    AlertDialog(c) {

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    var listener: ItemClickListener<GetAllIslands.Island>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rxt_island)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window?.attributes?.windowAnimations = R.style.DialogBounceAnimation;

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        //val close: View? = findViewById(R.id.iv_cancel)
        //val program: TextView? = findViewById(R.id.programName)
        //val completed: ImageView? = findViewById(R.id.iv_completed)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        // dialogAdapter = IslandAdapter(list)
        //program?.text = programName
        val height = context.resources.displayMetrics.heightPixels
//            if (dialogHeight > height.times(0.7)) {
//                window?.setLayout(dialogWidth, height.times(0.7).toInt())
//            }

        Logger.e("height $height")
        recyclerView?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                Logger.e("addOnLayoutChangeListener ${recyclerView?.height}")
                if (recyclerView?.height!! > height.times(0.8).toInt()) {
                    recyclerView?.layoutParams?.height = height.times(0.8).toInt()
                    Logger.e("addOnLayoutChangeListener changed.........")
                }
                recyclerView?.removeOnLayoutChangeListener(this)
            }

        })


        if (recyclerView?.layoutParams?.height!! > height.times(0.6).toInt())
            recyclerView?.layoutParams?.height = height.times(0.6).toInt()
        setCancelable(true)
        getIslands()
    }

    private fun getIslands() {
        //GetAllIsland
        val member = Prefs.get(context).member ?: return
        progressBar?.visibility = View.VISIBLE
        API.request.getApi().getAllIsland(
            GetAllIslandPost(GetAllIslandPost.Data(""), member.accessToken!!, "GetAllIsland")
        ).enqueue(object : Callback<GetAllIslands> {
            override fun onFailure(call: Call<GetAllIslands>, t: Throwable) {
                progressBar?.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<GetAllIslands>,
                response: Response<GetAllIslands>
            ) {
                progressBar?.visibility = View.GONE
                val list = ArrayList<GetAllIslands.Island>()
                val data = response?.body()?.data
                if (data != null && data.isNotEmpty()) {
                    for (d in data) {
                        d?.let {
                            list.add(it)
                        }
                    }
                }

                parseData(list)
            }
        })

    }

    private fun parseData(list: ArrayList<GetAllIslands.Island>) {
        if (list.isEmpty()) {
            Toasty.info(context, R.string.no_data_found).show()
            return
        }

        val adapter = IslandAdapter(list, listner)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter

        setCancelable(false)
    }

    class IslandAdapter(
        var list: ArrayList<GetAllIslands.Island>,
        var listener: ItemClickListener<GetAllIslands.Island?>?
    ) :
        RecyclerView.Adapter<IslandAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_rxt_select_island, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(getItem(position), listener)
        }

        private fun getItem(position: Int): GetAllIslands.Island {
            return list[position]
        }

        fun update(items: ArrayList<GetAllIslands.Island>) {
            if (items.isNotEmpty()) {
                list.clear()
                list.addAll(items)
                notifyDataSetChanged()
            }
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView? = view.findViewById(R.id.imageView)
            var island: TextView? = view.findViewById(R.id.tv_island_name)
            var tiles: TextView? = view.findViewById(R.id.tv_island_tiles)


            fun bind(
                item: GetAllIslands.Island?,
                listner: ItemClickListener<GetAllIslands.Island?>?
            ) {
                if (item != null) {
                    Glide.with(imageView!!.context).load(item.islandImage)
                        .error(R.drawable.ic_broken_image_black_24dp)
                        .fallback(R.drawable.ic_broken_image_black_24dp).into(imageView!!)
                    island?.text = item.name
                    tiles?.text = "${item.islandHeight} x ${item.islandWidth}"
                    itemView?.setOnClickListener {
                        listner?.onItemClicked(item, 0)
                    }
                }


            }
        }
    }

}