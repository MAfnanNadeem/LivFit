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
import android.view.View
import kotlinx.android.synthetic.main.fragment_catalog_details.*
import life.mibo.android.R
import life.mibo.android.models.catalog.Packages
import life.mibo.android.models.catalog.Product
import life.mibo.android.models.catalog.Services
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.body_measure.adapter.Calculate


class ProductDetailsActivity : BaseActivity() {


    companion object {
        fun launch(context: Context, item: Product) {
            val i = Intent(context, ProductDetailsActivity::class.java)
            i.putExtra("product_type", BuyActivity.TYPE_PRODUCT)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }

        fun launch(context: Context, item: Services.Data) {
            val i = Intent(context, ProductDetailsActivity::class.java)
            i.putExtra("product_type", BuyActivity.TYPE_SERVICE)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }

        fun launch(context: Context, item: Packages.Data) {
            val i = Intent(context, ProductDetailsActivity::class.java)
            i.putExtra("product_type", BuyActivity.TYPE_PACKAGE)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }
    }


    var product: Product? = null
    var service: Services.Data? = null
    var packageData: Packages.Data? = null
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
        else if (type == 3)
            packageData = intent?.getSerializableExtra("product_data") as Packages.Data?

        // var data: CatalogFragment.CartItem?

        if (product != null) {
            setup(product!!)
        } else if (service != null) {
            setup(service!!)
        } else if (packageData != null) {
            setup(packageData!!)
        } else {
            finish()
            return
        }

        btn_add_cart?.visibility = View.GONE

    }

    var isAvailableForSale = true
    fun setup(data: Product) {
        //if (data?.type == 2)
        tv_product_name?.text = data.productName
        tv_product_desc?.text = data.description
        //tv_product_desc_full?.text = data.longForm
        tv_product_long_desc?.text = data.longForm
        tv_product_location?.text = getString(R.string.on_your_address)
        tv_product_quantity_value?.text = "$quantity"
        tv_product_price_value?.text = data.unitPrice ?: "0.0"
        tv_product_manf_name?.text = "${data.manufacturerName}"

        tv_product_session?.visibility = GONE
        tv_product_session_value?.visibility = GONE
        tv_product_validity?.visibility = GONE
        tv_product_validity_value?.visibility = GONE

        when {
            data.stock ?: 0 > 5 -> tv_product_in_stock?.setText(R.string.in_stock)
            data.stock ?: 0 == 0 -> {
                tv_product_in_stock?.setText(R.string.out_of_stock)
                isAvailableForSale = false
            }
            else -> tv_product_in_stock?.text = getString(R.string.stock_left, data.stock)
        }

        btn_plus?.setOnClickListener {
            if (quantity < 10) {
                quantity++
                tv_product_quantity_value?.text = "$quantity"
            }
        }
        btn_minus?.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tv_product_quantity_value?.text = "$quantity"
            }

        }

//        val list = ArrayList<Int>()
//        // list.add("https://live.staticflickr.com/4561/38054606355_26429c884f_b.jpg")
//        list.add(R.drawable.ic_rxl_pods_icon_200)
//        list.add(R.drawable.ic_rxl_pods_icon_200)
//        list.add(R.drawable.ic_rxl_pods_icon_200)
//        list.add(R.drawable.ic_rxl_pods_icon_200)
//        list.add(R.drawable.ic_rxl_pods_icon_200)
//        list.add(R.drawable.ic_rxl_pods_icon_200)
//        list.add(R.drawable.ic_rxl_pods_icon_200)

        //userImage?.setImages(list)
        if (data.subImages == null || data.subImages.isNullOrEmpty()) {
            userImage?.setUrls(arrayListOf(""))
        } else
            userImage?.setUrls(data.subImages)
        log("images >> ${data.subImages}")
        btn_buy_now?.isEnabled = isAvailableForSale
        btn_buy_now?.setOnClickListener {
            if (isAvailableForSale)
                buyClicked(data)
        }
    }

    val GONE = View.GONE
    val VISIBLE = View.VISIBLE

    fun setup(data: Services.Data) {
        //if (data?.type == 2)
        tv_product_name?.text = data.name
        tv_product_desc?.text = data.description
        //tv_product_desc_full?.text = data.longForm
        tv_product_long_desc?.text = data.description
        //tv_product_location?.text = "add your address"
        tv_product_quantity_value?.text = "$quantity"
        tv_product_price_value?.text = "${data?.currencyType} ${data?.currency}"
        tv_product_session_value?.text = "${data?.noOfSession}"
        tv_product_validity_value?.text = "${data?.validity} Months"
        tv_product_location?.text = "Location ${data?.locationType}"
        tv_product_manf?.text = "Professional Name"
        tv_product_manf_name?.text = "${data.createdBy}"
        // tv_product_review?.setText(R.string.service_review)


        tv_product_session?.visibility = VISIBLE
        tv_product_session_value?.visibility = VISIBLE
        tv_product_validity?.visibility = VISIBLE
        tv_product_validity_value?.visibility = VISIBLE


        tv_product_review?.visibility = GONE
        ratings?.visibility = GONE
        tv_product_info?.visibility = GONE
        tv_product_inf2?.visibility = GONE
        tv_product_inf3?.visibility = GONE
        tv_product_in_stock?.visibility = GONE
        tv_product_quantity?.visibility = GONE
        ll_quantity?.visibility = GONE

        //userImage?.setImages(list)
        userImage?.visibility = GONE

        btn_buy_now?.setOnClickListener {
            buyClicked(data)
        }
    }

    fun setup(data: Packages.Data) {
        //if (data?.type == 2)
        tv_product_name?.text = data.name
        tv_product_desc?.text = data.description
        //tv_product_desc_full?.text = data.longForm
        tv_product_long_desc?.text = data.description
        tv_product_location?.text = "add your address"
        tv_product_quantity_value?.text = "$quantity"
        tv_product_price_value?.text = "${data?.currencyType} ${data?.price}"
//        tv_product_session_value?.text = "${data?.noOfSession}"
//        tv_product_validity_value?.text = "${data?.validity} Months"
//        tv_product_location?.text = "Location ${data?.locationType}"
        tv_product_manf?.text = "Created By"
        tv_product_manf_name?.text = "${data.createdBy}"
        // tv_product_review?.setText(R.string.service_review)


        tv_product_session?.visibility = GONE
        tv_product_session_value?.visibility = GONE
        tv_product_validity?.visibility = GONE
        tv_product_validity_value?.visibility = GONE
        tv_product_location?.visibility = GONE


        tv_product_review?.visibility = GONE
        ratings?.visibility = GONE
        tv_product_info?.visibility = GONE
        tv_product_inf2?.visibility = GONE
        tv_product_inf3?.visibility = GONE
        tv_product_in_stock?.visibility = GONE
        tv_product_quantity?.visibility = GONE
        ll_quantity?.visibility = GONE

        //userImage?.setImages(list)
        userImage?.visibility = GONE

        btn_buy_now?.setOnClickListener {
            buyClicked(data)
        }
    }

    private fun buyClicked(type: Int) {
        try {
            //PaymentActivity.payNow(activity, "200")
            val i = Intent(this, BuyActivity::class.java)
            i.putExtra("type_type", type)
            startActivity(i)
            finish()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun buyClicked(product: Product) {
        try {
            //PaymentActivity.payNow(activity, "200")
            val list = ArrayList<CartItem>()
            list.add(
                CartItem(
                    product.id!!,
                    product.productName,
                    Calculate.getDouble(product.unitPrice),
                    product.currency ?: "AED",
                    product.image,
                    1, 0.0, "", false, false
                )
            )
            val i = Intent(this, BuyActivity::class.java)
            i.putExtra("type_type", BuyActivity.TYPE_PRODUCT)
            i.putParcelableArrayListExtra("type_list", list)
            startActivity(i)
            finish()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun buyClicked(services: Services.Data) {
        try {
            val list = ArrayList<CartItem>()
            list.add(
                CartItem(
                    services.id!!,
                    services.name,
                    services.currency ?: 0.0,
                    services.currencyType,
                    "",
                    1, services.vat, services.location, true, false
                )
            )
            val i = Intent(this, BuyActivity::class.java)
            i.putExtra("type_type", BuyActivity.TYPE_SERVICE)
            i.putParcelableArrayListExtra("type_list", list)
            startActivity(i)
            finish()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun buyClicked(services: Packages.Data) {
        try {
            val list = ArrayList<CartItem>()
            list.add(
                CartItem(
                    services.id!!,
                    services.name,
                    services.price ?: 0.0,
                    services.currencyType,
                    "",
                    1, services.vat, services.location, false, true
                )
            )
            val i = Intent(this, BuyActivity::class.java)
            i.putExtra("type_type", BuyActivity.TYPE_PACKAGE)
            i.putParcelableArrayListExtra("type_list", list)
            startActivity(i)
            finish()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}