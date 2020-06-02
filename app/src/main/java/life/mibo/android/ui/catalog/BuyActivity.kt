/*
 *  Created by Sumeet Kumar on 5/20/20 3:37 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/20/20 3:37 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_buy_product.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.models.product.Product
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.payments.PaymentActivity
import life.mibo.android.utils.Toasty


class BuyActivity : BaseActivity() {


    companion object {
        fun launch(context: Context, item: Product) {
            val i = Intent(context, ProductDetailsActivity::class.java)
            i.putExtra("product_type", 1)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }
    }

    var type = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_product)


        var type = intent?.getIntExtra("type_type", 0) ?: 0

        if (type == 2) {
//            card1?.visibility = View.GONE
//            card2?.visibility = View.GONE
//            addNewCard?.visibility = View.GONE
//            card3?.visibility = View.VISIBLE
        } else {
            card1?.visibility = View.GONE
            card2?.visibility = View.GONE
            addNewCard?.visibility = View.GONE
            card3?.visibility = View.VISIBLE
        }
        btn_pay_now?.setOnClickListener {
            payNow()
        }

        addNewCard?.setOnClickListener {
            AddressDialog().show(supportFragmentManager, "AddressDialog")
        }
        setupCartItems()
    }

    var cartAdapters: CartAdapters? = null
    var cartItem: CartItem? = null
    fun setupCartItems() {
        val list = intent?.getParcelableArrayListExtra<CartItem>("type_list")
        if (list != null) {
            recyclerView?.layoutManager = LinearLayoutManager(this)
            cartAdapters = CartAdapters(list, object : ItemClickListener<CartItem> {
                override fun onItemClicked(item: CartItem?, position: Int) {
                    cartItem = item
                }

            })

            recyclerView?.adapter = cartAdapters

        }
    }

    fun payNow() {
        try {
            val price = cartItem!!.getAmount()
            if (price > 0)
                PaymentActivity.payNow(this, "$price", Prefs.get(this).member?.id())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    class AddressDialog : DialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.activity_add_new_address, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            view?.findViewById<View?>(R.id.btn_save)?.setOnClickListener {
                Toasty.info(requireContext(), "Address saved!").show()
                dismiss()
            }
        }

        override fun onStart() {
            super.onStart()
            if (dialog != null) {
                dialog?.window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }


    class CartAdapters(
        val list: ArrayList<CartItem>,
        val listener: ItemClickListener<CartItem>?
    ) : RecyclerView.Adapter<CartHolder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
            return CartHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_cart, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: CartHolder, position: Int) {
            holder.bind(list[position], listener)

        }

        fun update(ipList: ArrayList<CartItem>) {
            list.clear()
            list.addAll(ipList)
            this.notifyDataSetChanged()

        }
    }


    class CartHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val quantity: TextView? = itemView.findViewById(R.id.tv_product_quantity_value)
        val price: TextView? = itemView.findViewById(R.id.tv_product_price_value)
        val img: ImageView? = itemView.findViewById(R.id.iv_image)
        val plus: View? = itemView.findViewById(R.id.btn_plus)
        val minus: View? = itemView.findViewById(R.id.btn_minus)

        fun bind(item: CartItem?, listener: ItemClickListener<CartItem>?) {
            if (item == null)
                return
            name?.text = item.name
            price?.text = "${item.currencyType} ${item.getAmount()}"
            quantity?.text = "${item.quantity}"
            if (item.image != null && item.image!!.isNotEmpty()) {
                img?.visibility = View.VISIBLE
                Glide.with(img!!).load(item.image).error(R.drawable.ic_broken_image_black_24dp)
                    .fitCenter().into(img)
            } else {
                img?.visibility = View.GONE
            }
            plus?.setOnClickListener {
                if (item.quantity < 10) {
                    item.quantity++
                    price?.text = "${item.currencyType} ${item.getAmount()}"
                    quantity?.text = "${item.quantity}"
                    listener?.onItemClicked(item, adapterPosition)
                }
            }

            minus?.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    price?.text = "${item.currencyType} ${item.getAmount()}"
                    quantity?.text = "${item.quantity}"
                    listener?.onItemClicked(item, adapterPosition)
                }
            }

//            itemView?.setOnClickListener {
//                listener?.onItemClicked(item, adapterPosition)
//            }
        }
    }
}