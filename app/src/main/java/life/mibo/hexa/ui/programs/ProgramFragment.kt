/*
 *  Created by Sumeet Kumar on 1/8/20 5:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 10:30 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.programs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.kroegerama.kaiteki.bcode.ui.BarcodeFragment
import kotlinx.android.synthetic.main.fragment_add_product.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener


class ProgramFragment : BaseFragment(), ProgramObserver {
    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    override fun onDataReceived(list: ArrayList<Program>) {
        val adapter = ProgramAdapter(list)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        recyclerView?.adapter = adapter
    }

    override fun onItemClicked(item: Program?) {

    }


    private lateinit var controller: ProgramController
    var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_add_product, container, false)

        recyclerView = root.findViewById(R.id.productRecycler)
        // setRecycler(recyclerView!!)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = ProgramController(this@ProgramFragment, this)
        //controller.setRecycler(recyclerView)
        controller.getProduct()

    }


    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}