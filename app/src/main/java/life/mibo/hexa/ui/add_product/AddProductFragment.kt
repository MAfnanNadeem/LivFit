/*
 *  Created by Sumeet Kumar on 1/8/20 8:10 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 */

package life.mibo.hexa.ui.add_product

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


class AddProductFragment : BaseFragment(), ProductObserver {
    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    override fun onDataReceived(list: ArrayList<ProductItem>) {
        val adapter = AddProductAdapter(list)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        recyclerView?.adapter = adapter
    }

    override fun onItemClicked(item: ProductItem?) {

    }


    private lateinit var controller: AddProductController
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
        controller = AddProductController(this@AddProductFragment, this)
        //controller.setRecycler(recyclerView)
        controller.getProduct()
        btn_qr_code?.setOnClickListener {
           navigate(R.id.navigation_barcode, null)
        }
    }


    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}