/*
 *  Created by Sumeet Kumar on 2/20/20 4:21 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 4:21 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

/*
 *  Created by Sumeet Kumar on 1/16/20 12:10 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/16/20 12:10 PM
 *  Mibo Hexa - app
 */


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.Observable
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.rxl.adapter.ReflexFilterAdapter


class FilterDialog(c: Context, val listener: ItemClickListener<Program>?) : BottomSheetDialog(c) {

    var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_reactions_filters_dialog)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //recyclerView = findViewById(R.id.recyclerView)
//        val wlp = window?.attributes;
//
//        if(wlp != null){
//            wlp.gravity = Gravity.BOTTOM;
//            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
//            window?.attributes = wlp;
//        }
        textView = findViewById(R.id.tv_title)
        val cancel: View? = findViewById(R.id.iv_cancel)
       // setCancelable(false)

        setFilters(
            findViewById(R.id.recyclerViewTypes),
            ReactionLightController.Filter.PROGRAM_TYPE
        )
        setFilters(findViewById(R.id.recyclerViewPods), ReactionLightController.Filter.NO_OF_PODS)
        setFilters(findViewById(R.id.recyclerViewAcces), ReactionLightController.Filter.ACCESSORIES)

        cancel?.setOnClickListener {
            dismiss()
        }


    }

    @SuppressLint("CheckResult")
    fun setFilters(view: RecyclerView?, type: ReactionLightController.Filter) {
        if (view == null)
            return
        val list = ArrayList<ReflexFilterAdapter.ReflexFilterModel>()
        Observable.fromCallable {
            when (type) {

                ReactionLightController.Filter.PROGRAM_TYPE -> {

                    list.add(ReflexFilterAdapter.ReflexFilterModel(21, "Agility"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(22, "Balanced"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(23, "Core"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(24, "Cardio"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(25, "Coordination"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(26, "Fitness Test"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(27, "Flexibility"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(28, "Functional"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(29, "Power"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(30, "Reaction Time"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(31, "Speed"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(32, "Stamina"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(33, "Strength"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(34, "Suspension"))
                }
                ReactionLightController.Filter.NO_OF_PODS -> {
                    for (i in 1..16) {
                        list.add(ReflexFilterAdapter.ReflexFilterModel(i, "$i"))
                    }
                }
                ReactionLightController.Filter.LIGHT_LOGIC -> {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(41, "Random"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(42, "Sequence"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(43, "All at once"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(44, "Focus"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(45, "Home Base"))
                }
                ReactionLightController.Filter.PLAYERS -> {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(51, "1"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(52, "2"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(53, "3"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(54, "4"))
                }
                ReactionLightController.Filter.ACCESSORIES -> {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(61, "No Accessories"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(62, "Battle Rope"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(63, "Laddar"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(64, "Medicine Ball"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(65, "Mirror"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(66, "Poll"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(67, "Pul Up Bar"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(68, "Rig"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(69, "Suspension Straps"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(70, "Tree"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(71, "Resistance Band"))
                }
            }
        }.subscribe {
            val adapter = ReflexFilterAdapter(list, 5)
            val manager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
           // val manager2 = GridAutofitLayoutManager(this.context, LinearLayoutManager.HORIZONTAL)

            adapter.setListener(object : ReflexFilterAdapter.Listener {
                override fun onClick(data: ReflexFilterAdapter.ReflexFilterModel?) {

                }

            })
            view.layoutManager = manager
            view.adapter = adapter
            Logger.e("FilterDialog ${list.size}")
            //view.isNestedScrollingEnabled = false
        }
    }
}