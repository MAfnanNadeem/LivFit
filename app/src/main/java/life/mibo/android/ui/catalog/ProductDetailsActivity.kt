/*
 *  Created by Sumeet Kumar on 5/31/20 12:08 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/20/20 4:22 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_catalog_details.*
import life.mibo.android.R
import life.mibo.android.models.product.Product
import life.mibo.android.models.product.Services
import life.mibo.android.ui.base.BaseActivity


class ProductDetailsActivity : BaseActivity() {


    companion object {
        fun launch(context: Context, item: Product) {
            val i = Intent(context, ProductDetailsActivity::class.java)
            i.putExtra("product_type", 1)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }

        fun launch(context: Context, item: Services.Data) {
            val i = Intent(context, ProductDetailsActivity::class.java)
            i.putExtra("product_type", 2)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }
    }


    var product: Product? = null
    var service: Services.Data? = null
    var type = 0
    var quantity = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_catalog_details)


        val type = intent?.getIntExtra("product_type", 0) ?: 0
        if (type == 1)
            product = intent?.getSerializableExtra("product_data") as Product
        else if (type == 2)
            service = intent?.getSerializableExtra("product_data") as Services.Data?

        // var data: CatalogFragment.CartItem?
        if (product == null && service == null) {
            finish()
            return
        }

        if (product != null) {
            setup(product!!)
            return
        }

        if (service != null) {
            setup(service!!)
            return
        }


    }

    fun setup(data: Product) {
        //if (data?.type == 2)
        tv_product_name?.text = data.productName
        tv_product_desc?.text = data.description
        tv_product_desc_full?.text = data.longForm
        tv_product_location?.text = "add your address"
        tv_product_quantity_value?.text = "$quantity"
        tv_product_price_value?.text = "${data?.unitPrice}"

        btn_plus?.setOnClickListener {
            if (quantity < 10) {
                quantity++
                tv_product_quantity_value?.text = "$quantity"
            }
        }
        btn_minus?.setOnClickListener {
            if (quantity > 0) {
                quantity--
                tv_product_quantity_value?.text = "$quantity"
            }

        }

        val list = ArrayList<Int>()
        // list.add("https://live.staticflickr.com/4561/38054606355_26429c884f_b.jpg")
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)

        //userImage?.setImages(list)
        userImage?.setUrls(data.subImages)
        log("images >> ${data.subImages}")

        btn_buy_now?.setOnClickListener {
            buyClicked()
        }
    }

    fun setup(data: Services.Data) {
        //if (data?.type == 2)
        tv_product_location?.text = "add your address"
        tv_product_quantity_value?.text = "$quantity"
        tv_product_price_value?.text = "${data?.currency}"

        btn_plus?.setOnClickListener {
            if (quantity < 10) {
                quantity++
                tv_product_quantity_value?.text = "$quantity"
            }
        }
        btn_minus?.setOnClickListener {
            if (quantity > 0) {
                quantity--
                tv_product_quantity_value?.text = "$quantity"
            }

        }

        val list = ArrayList<Int>()
        // list.add("https://live.staticflickr.com/4561/38054606355_26429c884f_b.jpg")
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)
        list.add(R.drawable.ic_rxl_pods_icon_200)

        userImage?.setImages(list)

        btn_buy_now?.setOnClickListener {
            buyClicked()
        }
    }

    private fun buyClicked() {
        try {
            //PaymentActivity.payNow(activity, "200")
            val i = Intent(this, BuyActivity::class.java)
            i.putExtra("type_type", product)
            startActivity(i)
            finish()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}