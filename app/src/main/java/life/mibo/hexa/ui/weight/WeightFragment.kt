/*
 *  Created by Sumeet Kumar on 1/8/20 5:09 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 10:10 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.weight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener
import life.mibo.hexa.ui.home.HomeItem


class WeightFragment : BaseFragment(), WeightObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: WeightController
    var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_weight, container, false)
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = WeightController(this@WeightFragment, this)
        //controller.setRecycler(recyclerView!!)
    }

    override fun onDataRecieved(list: ArrayList<HomeItem>) {
    }

    override fun onItemClicked(item: HomeItem?) {

    }


    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}