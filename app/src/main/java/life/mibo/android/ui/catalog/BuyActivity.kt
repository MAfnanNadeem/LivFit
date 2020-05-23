/*
 *  Created by Sumeet Kumar on 5/20/20 3:37 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/20/20 3:37 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_buy_product.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.payments.PaymentActivity
import life.mibo.android.utils.Toasty


class BuyActivity : BaseActivity() {


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
    }

    fun payNow() {
        try {
            PaymentActivity.payNow(this, "200")
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
}