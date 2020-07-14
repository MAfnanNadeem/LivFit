/*
 *  Created by Sumeet Kumar on 5/20/20 3:37 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/20/20 3:37 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_buy_product.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.catalog.*
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.payments.PaymentActivity
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class BuyActivity : BaseActivity() {


    companion object {
        var TYPE_PRODUCT = 1;
        var TYPE_SERVICE = 2;
        var TYPE_PACKAGE = 3;
        var TYPE_INVOICE = 4;

        fun launch(context: Context, item: Product) {
            val i = Intent(context, ProductDetailsActivity::class.java)
            i.putExtra("product_type", 1)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }
    }

    private var type = 0
    private var invoiceLocationId = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_product)


        type = intent?.getIntExtra("type_type", 0) ?: 0

        if (type == TYPE_PRODUCT) {
            textView3?.visibility = View.GONE
            cardPromo?.visibility = View.GONE
            addNewCard?.visibility = View.VISIBLE
            cardContract?.visibility = View.GONE
            textView4?.visibility = View.GONE
        } else if (type == TYPE_SERVICE) {
            // showServiceAddress()
            textView3?.visibility = View.VISIBLE
            cardPromo?.visibility = View.VISIBLE
            textView4?.visibility = View.VISIBLE
            cardContract?.visibility = View.VISIBLE
            addNewCard?.visibility = View.GONE
            setupPromoAndContract()
        } else if (type == TYPE_PACKAGE) {
            //showPackageAddress()
            textView3?.visibility = View.VISIBLE
            textView4?.visibility = View.VISIBLE
            cardPromo?.visibility = View.VISIBLE
            cardContract?.visibility = View.VISIBLE
            addNewCard?.visibility = View.GONE
            setupPromoAndContract()
        } else if (type == TYPE_INVOICE) {
            val invoice = intent?.getStringExtra("type_data")
            invoiceLocationId = intent?.getStringExtra("type_location") ?: "1"
            //showPackageAddress()
            textView3?.visibility = View.GONE
            cardPromo?.visibility = View.GONE
            cardContract?.visibility = View.GONE
            addNewCard?.visibility = View.GONE
            textView4?.visibility = View.GONE
            val member = Prefs.get(this).member ?: return
            geInvoicetDetails(member.id, invoice, member.accessToken)
        } else {
            finish()
            return
        }
        btn_pay_now?.setOnClickListener {
            payNow()
        }

        addNewCard?.setOnClickListener {
            //AddressDialog().show(supportFragmentManager, "AddressDialog")
            startActivityForResult(Intent(this, NewAddressActivity::class.java), 12345)
        }
        setupCartItems()

    }

    private fun setupPromoAndContract() {
        promo_apply?.setOnClickListener {
            applyPromo()
        }

        et_promo?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                applyPromo()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        tv_date_update?.setOnClickListener {
            datePickerDialog()
        }

        updateContractDate(cartItem?.startDate, cartItem?.endDate)
    }

    //var serviceStartDate = ""
    //var serviceEndDate = ""

    private fun datePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->

                var date = String.format("%02d-%02d-%d", dayOfMonth, monthOfYear.plus(1), year)

                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                cartItem?.startDate =
                    "${cal.get(Calendar.YEAR)}-${String.format(
                        "%02d",
                        cal.get(Calendar.MONTH).plus(1)
                    )}-${cal.get(Calendar.DAY_OF_MONTH)}"
                cal.add(Calendar.MONTH, cartItem?.validity ?: 1)
                cartItem?.endDate =
                    "${cal.get(Calendar.YEAR)}-${String.format(
                        "%02d",
                        cal.get(Calendar.MONTH).plus(1)
                    )}-${cal.get(Calendar.DAY_OF_MONTH)}"

                updateContractDate(cartItem?.startDate, cartItem?.endDate)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.minDate = now
        dpd.accentColor = ContextCompat.getColor(this, R.color.colorPrimary)
        dpd.show(supportFragmentManager, "DatePickerDialog")
    }

    fun updateContractDate(start: String?, end: String?) {
        runOnUiThread {
            tv_date_start?.text = getString(R.string.service_start_date, start)
            tv_date_end?.text = getString(R.string.service_end_date, end)
        }
    }

    private fun applyPromo() {
        if (et_promo?.text?.isEmpty() == true) {
            Toasty.snackbar(et_promo, getString(R.string.enter_promo))
            return
        }

        applyPromo(
            type == TYPE_SERVICE,
            cartItem?.id ?: 0,
            et_promo?.text?.toString() ?: "NULL"
        )
    }

    private var cartAdapters: CartAdapters? = null
    private var addressAdapters: AddressAdapters? = null
    private var cartItem: CartItem? = null
    private var cartList = ArrayList<CartItem>()
    private var addressItem: AddressItem? = null
    private var isAddressLoaded = false
    private fun setupCartItems() {
        cartList.clear()
        val list = intent?.getParcelableArrayListExtra<CartItem>("type_list")
        if (list != null) {
            if (list.size > 0)
                cartItem = list[0]
            cartList.addAll(list)
            recyclerView?.layoutManager = LinearLayoutManager(this)
            cartAdapters = CartAdapters(list, object : ItemClickListener<CartItem> {
                override fun onItemClicked(item: CartItem?, position: Int) {
                    cartItem = item
                }

            })

            recyclerView?.adapter = cartAdapters

        }

        if (type == TYPE_SERVICE || type == TYPE_PACKAGE) {
            textViewAddress?.setText(R.string.shipping_service_address)
            val addressList = ArrayList<AddressItem>()
            addressItem = AddressItem(
                0,
                "",
                "${cartItem?.location}",
                "",
                "",
                ""
            )
            addressItem?.selected = true
            addressList.add(addressItem!!)
            setupAddresses(addressList)
            updateContractDate(cartItem?.startDate, cartItem?.endDate)
            if (cartItem!!.promoService > 0) {
                tv_date_update?.visibility = View.GONE
            }
        } else if (type == TYPE_INVOICE) {
        } else {
            textViewAddress?.setText(R.string.shipping_address)
            getAddresses()
        }
    }

//    private fun payNow() {
//        try {
//            if (cartItem == null)
//                return
//            if (addressItem == null) {
//                Toasty.snackbar(btn_pay_now, getString(R.string.select_a_shipping_address))
//                return
//            }
//
//            val price = cartItem!!.getBillable()
//            if (price > 0) {
//                PaymentActivity.payNow(this, "$price", Prefs.get(this).member?.id())
//            } else
//                Toasty.snackbar(btn_pay_now, getString(R.string.invalid_amount))
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            Toasty.snackbar(btn_pay_now, getString(R.string.invalid_amount))
//        }
//    }

    private var country: String = "";
    private fun getCountryIsoCode(country: String): String {

        try {
            return Locale.getISOCountries().find { Locale("", it).displayCountry == country }!!
        } catch (e: java.lang.Exception) {

        }

        try {
            return Locale.getISOCountries().find { Locale("", it).country == country }!!
        } catch (e: java.lang.Exception) {

        }

        return country
    }

    private fun getAppId(): String {
        try {
            return Settings.Secure.getString(this?.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: java.lang.Exception) {

        }
        return BigInteger(128, Random()).toString()
    }


    private fun payNow() {
        //listener?.onItemClicked("", 101)
        //dismiss()
        try {
            if (cartItem == null)
                return
            if (addressItem == null) {
                Toasty.snackbar(btn_pay_now, getString(R.string.select_a_shipping_address))
                return
            }


            val price = cartItem!!.getBillable()
            if (price > 0.0) {
                cartItem?.locationId = addressItem?.id ?: 0
                cartItem?.encAmount = "$price"
                //log("paynow cart $cartItem")
                //val enc = EncryptedPrefs.AESEncyption()
                //cartItem?.encAmount = enc.encrypt("$price")
                //val key = enc.encrypt("cart_item")
                Prefs.get(this).setJson(Prefs.CART_ITEM, cartItem)
                //Prefs.getTemp(this).setJson("cart_item", cartItem)

                val prefs = Prefs.get(this).member ?: return
                val email = Prefs.get(this).get("user_email")
                val title = if (prefs.isMale()) "Mr" else "Ms"

                val data = PaymentActivity.PaymentData(
                    prefs.id(),
                    title,
                    prefs.firstName,
                    prefs.lastName,
                    email,
                    cartItem?.currencyType,
                    price,
                    addressItem?.addres ?: "",
                    addressItem?.city ?: prefs.city,
                    addressItem?.city,
                    getCountryIsoCode(addressItem?.country ?: prefs.country ?: ""),
                    getString(R.string.app_version),
                    getAppId(), cartItem?.name
                )
                PaymentActivity.payNow(this@BuyActivity, data)
                finish()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

//    class AddressDialog : DialogFragment() {
//
//        override fun onCreateView(
//            inflater: LayoutInflater,
//            container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//            return inflater.inflate(R.layout.activity_add_new_address, container, false)
//        }
//
//        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//            super.onViewCreated(view, savedInstanceState)
//
//            view?.findViewById<View?>(R.id.btn_save)?.setOnClickListener {
//                Toasty.info(requireContext(), "Address saved!").show()
//                dismiss()
//            }
//        }
//
//        override fun onStart() {
//            super.onStart()
//            if (dialog != null) {
//                dialog?.window
//                    ?.setLayout(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                    )
//                dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            }
//        }
//    }


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

        fun applyPromo(promo: PromoResponse.Promo?, code: String) {
            if (list.isNotEmpty()) {
                list[0].promo(promo, code)
                notifyDataSetChanged()

            } else {

            }

        }
    }


    class CartHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val quantity: TextView? = itemView.findViewById(R.id.tv_product_quantity_value)
        val price: TextView? = itemView.findViewById(R.id.tv_product_price_value)
        val vatTitle: TextView? = itemView.findViewById(R.id.tv_product_tax)
        val vat: TextView? = itemView.findViewById(R.id.tv_product_tax_value)
        val totalTitle: TextView? = itemView.findViewById(R.id.tv_product_total)
        val total: TextView? = itemView.findViewById(R.id.tv_product_tax_total)
        val promoHeader: TextView? = itemView.findViewById(R.id.tv_promo_amount)
        val promoValue: TextView? = itemView.findViewById(R.id.tv_promo_amount_value)
        val subHeader: TextView? = itemView.findViewById(R.id.tv_promo_total)
        val subValue: TextView? = itemView.findViewById(R.id.tv_promo_total_value)
        val img: ImageView? = itemView.findViewById(R.id.iv_image)
        val plus: View? = itemView.findViewById(R.id.btn_plus)
        val minus: View? = itemView.findViewById(R.id.btn_minus)

        fun bind(item: CartItem?, listener: ItemClickListener<CartItem>?) {
            if (item == null)
                return
            name?.text = item.name
            price?.text = getPrice(item.currencyType, item.calculatePrice())
            //price?.text = "${item.currencyType} ${item.getAmount()}"
            quantity?.text = "${item.quantity}"
            if (item.image != null && item.image!!.isNotEmpty()) {
                img?.visibility = View.VISIBLE
                Glide.with(img!!).load(item.image).error(R.drawable.ic_broken_image_black_24dp)
                    .fitCenter().into(img)
            } else {
                img?.visibility = View.GONE
            }
            if (item.isService) {
                vatTitle?.visibility = View.VISIBLE
                vat?.visibility = View.VISIBLE
                totalTitle?.visibility = View.VISIBLE
                total?.visibility = View.VISIBLE

                // total?.setText("${item.currencyType} ${item.getTotal()}")
                // vat?.setText("${item.currencyType} ${item.getVat()}")

                total?.text = getPrice(item.currencyType, item.getTotal())
                vat?.text = getPrice(item.currencyType, item.getVat())

                if (item.quantityDisable) {
                    plus?.visibility = View.INVISIBLE
                    minus?.visibility = View.INVISIBLE
                }

                if (item.havePromo()) {
                    promoHeader?.visibility = View.VISIBLE
                    promoValue?.visibility = View.VISIBLE
                    subHeader?.visibility = View.VISIBLE
                    subValue?.visibility = View.VISIBLE
                    promoValue?.text = promoValue?.context?.getString(
                        R.string.promo_value,
                        getPrice(item.currencyType, item.getPromo())
                    )
                    subValue?.text = getPrice(item.currencyType, item.getTotalAmount())
                } else {
                    promoHeader?.visibility = View.GONE
                    promoValue?.visibility = View.GONE
                    subHeader?.visibility = View.GONE
                    subValue?.visibility = View.GONE
                }
                // vat?.setText("${item.currencyType} ${item.getVat()}")

            } else if (item.isPackage) {
                vatTitle?.visibility = View.VISIBLE
                vat?.visibility = View.VISIBLE
                totalTitle?.visibility = View.VISIBLE
                total?.visibility = View.VISIBLE

                total?.text = getPrice(item.currencyType, item.getTotal())
                vat?.text = getPrice(item.currencyType, item.getVat())

                if (item.quantityDisable) {
                    plus?.visibility = View.INVISIBLE
                    minus?.visibility = View.INVISIBLE
                }

                if (item.havePromo()) {
                    promoHeader?.visibility = View.VISIBLE
                    promoValue?.visibility = View.VISIBLE
                    subHeader?.visibility = View.VISIBLE
                    subValue?.visibility = View.VISIBLE
                    promoValue?.text = promoValue?.context?.getString(
                        R.string.promo_value,
                        getPrice(item.currencyType, item.getPromo())
                    )
                    subValue?.text = getPrice(item.currencyType, item.getTotalAmount())
                } else {
                    promoHeader?.visibility = View.GONE
                    promoValue?.visibility = View.GONE
                    subHeader?.visibility = View.GONE
                    subValue?.visibility = View.GONE
                }

//                total?.setText("${item.currencyType} ${item.getTotal()}")
//                vat?.setText("${item.currencyType} ${item.getVat()}")

            } else {
                vatTitle?.visibility = View.GONE
                vat?.visibility = View.GONE
                totalTitle?.visibility = View.GONE
                total?.visibility = View.GONE

                //promo
                promoHeader?.visibility = View.GONE
                promoValue?.visibility = View.GONE
                subHeader?.visibility = View.GONE
                subValue?.visibility = View.GONE
            }
            plus?.setOnClickListener {
                if (item.quantity < 10) {
                    item.quantity++
                    //price?.text = "${item.currencyType} ${item.getAmount()}"
                    price?.text = getPrice(item.currencyType, item.calculatePrice())
                    quantity?.text = "${item.quantity}"
                    if (item.isService || item.isPackage) {
                        total?.text = getPrice(item.currencyType, item.getTotal())
                        vat?.text = getPrice(item.currencyType, item.getVat())
                        if (promoValue?.visibility == View.VISIBLE && subValue?.visibility == View.VISIBLE) {
                            promoValue?.text = promoValue?.context?.getString(
                                R.string.promo_value,
                                getPrice(item.currencyType, item.getPromo())
                            )
                            subValue?.text = getPrice(item.currencyType, item.getTotalAmount())
                        }
                    }
                    listener?.onItemClicked(item, adapterPosition)
                }
            }

            minus?.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    price?.text = getPrice(item.currencyType, item.calculatePrice())
                    quantity?.text = "${item.quantity}"
                    if (item.isService || item.isPackage) {
                        total?.text = getPrice(item.currencyType, item.getTotal())
                        vat?.text = getPrice(item.currencyType, item.getVat())

                        if (promoValue?.visibility == View.VISIBLE && subValue?.visibility == View.VISIBLE) {
                            promoValue?.text = promoValue?.context?.getString(
                                R.string.promo_value,
                                getPrice(item.currencyType, item.getPromo())
                            )
                            subValue?.text = getPrice(item.currencyType, item.getTotalAmount())
                        }
                    }
                    listener?.onItemClicked(item, adapterPosition)
                }
            }

//            itemView?.setOnClickListener {
//                listener?.onItemClicked(item, adapterPosition)
//            }
        }

        //var formator: DecimalFormat? = null
        var formator: NumberFormat? = null
        fun getPrice(currency: String?, amount: Double): String {
            try {
                if (formator == null) {
                    //java.text.NumberFormat.getCurrencyInstance()
                    formator = DecimalFormat.getCurrencyInstance()
                    formator?.maximumFractionDigits = 2
                    if (currency != null && currency.isNotEmpty())
                        formator?.currency = Currency.getInstance(currency)
                    else formator?.currency = Currency.getInstance("AED")
                }
                //Logger.e("getPrice $currency, $amount - " + formator?.format(amount))
                return formator?.format(amount) ?: "$currency $amount"
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "$currency $amount"
        }
    }

//    fun showServiceAddress() {
//
//        val list = ArrayList<AddressItem>()
//        addressItem = AddressItem(
//            1,
//            "Dummy Name",
//            "Service Address \n Lorem Ispem",
//            "Dubai, AE",
//            "United Arab Emirates",
//            "+971 55 xxx xxxx"
//        )
//        addressItem?.selected = true
//        list.add(addressItem!!)
//        setupAddresses(list)
//    }
//
//    fun showPackageAddress() {
//
//        val list = ArrayList<AddressItem>()
//        addressItem = AddressItem(
//            1,
//            "Dummy Name",
//            "Package Address \n Lorem Ispem",
//            "Dubai, AE",
//            "United Arab Emirates",
//            "+971 55 xxx xxxx"
//        )
//        addressItem?.selected = true
//        list.add(addressItem!!)
//        setupAddresses(list)
//    }

    private fun getAddresses() {
        val member = Prefs.get(this).member ?: return
        getDialog()?.show()

        API.request.getApi().getMemberShippingAddress(
            MemberPost(
                member.id(),
                member.accessToken!!,
                "GetMemberShippingAddress"
            )
        ).enqueue(object : Callback<ShipmentAddress> {
            override fun onFailure(call: Call<ShipmentAddress>, t: Throwable) {
                getDialog()?.dismiss()
            }

            override fun onResponse(
                call: Call<ShipmentAddress>,
                response: Response<ShipmentAddress>
            ) {
                getDialog()?.dismiss()
                val list = response?.body()?.data
                if (list != null && list.isNotEmpty()) {
                    val address = ArrayList<AddressItem>()
                    for (i in list) {
                        if (i != null)
                            address.add(
                                AddressItem(
                                    i.id!!,
                                    i.name,
                                    i.address,
                                    i.city,
                                    i.country,
                                    i.phone
                                )
                            )
                    }

                    runOnUiThread {
                        setupAddresses(address)
                    }
                }

            }

        })

//        val list = ArrayList<AddressItem>()
//        for (i in 0..3)
//            list.add(
//                AddressItem(
//                    i,
//                    "Dummy Name",
//                    "Dummy Address \n Lorem Ispem",
//                    "Dubai, AE",
//                    "United Arab Emirates",
//                    "+971 55 xxx xxxx"
//                )
//            )
//        setupAddresses(list)
    }

    fun setupAddresses(list: ArrayList<AddressItem>) {
        recyclerViewAddress?.layoutManager = LinearLayoutManager(this)
        addressAdapters = AddressAdapters(list, object : ItemClickListener<AddressItem> {
            override fun onItemClicked(item: AddressItem?, position: Int) {
                addressItem = item
                addressItem?.let {
                    runOnUiThread {
                        addressAdapters?.update(it)
                    }
                }
            }

        })

        recyclerViewAddress?.adapter = addressAdapters

    }


    data class AddressItem(
        var id: Int,
        var name: String?,
        var addres: String?,
        var city: String?,
        var country: String?,
        var phone: String?
    ) {
        var selected: Boolean = false
        var isViewMode: Boolean = false
        var fname: String? = ""
        var lname: String? = ""
    }

    class AddressAdapters(
        val list: ArrayList<AddressItem>,
        val listener: ItemClickListener<AddressItem>?
    ) : RecyclerView.Adapter<AddressHolder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
            return AddressHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_cart_address, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: AddressHolder, position: Int) {
            holder.bind(list[position], listener)

        }

        fun update(ipList: ArrayList<AddressItem>) {
            list.clear()
            list.addAll(ipList)
            this.notifyDataSetChanged()
        }

        fun update(item: AddressItem) {

            for (i in list) {
                i.selected = item.id == i.id
            }

            this.notifyDataSetChanged()
        }
    }


    class AddressHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.personName)
        val address: TextView? = itemView.findViewById(R.id.personAddress)
        val city: TextView? = itemView.findViewById(R.id.personCity)
        val country: TextView? = itemView.findViewById(R.id.personCountry)
        val phone: TextView? = itemView.findViewById(R.id.personNumber)
        val radio: RadioButton? = itemView.findViewById(R.id.radio_button)
        val item_view: View? = itemView.findViewById(R.id.item_view)

        fun bind(item: AddressItem?, listener: ItemClickListener<AddressItem>?) {
            if (item == null)
                return
            name?.text = item.name
            address?.text = item.addres
            if (item.city.isNullOrEmpty())
                city?.visibility = View.GONE
            else city?.text = item.city

            if (item.country.isNullOrEmpty())
                country?.visibility = View.GONE
            else country?.text = item.country

            if (item.phone.isNullOrEmpty())
                phone?.visibility = View.GONE
            else phone?.text = item.phone
            //country?.text = item.country
            //phone?.text = item.phone
            radio?.isChecked = item.selected

            if (item.isViewMode) {
                radio?.visibility = View.INVISIBLE
            }

            item_view?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 12345) {
            if (resultCode == Activity.RESULT_OK) {
                getAddresses()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private var isInvoiceLoaded = false
    private var invoiceAddress = ""
    private var invoiceCountry = ""
    private var invoiceCity = ""
    private var invoiceBookingId = ""
    private var invoiceCurrency = ""
    private var invoiceAmount: Double = 0.0
    private var invoiceVat: Double = 0.0
    private var invoiceConvFee: Double = 0.0
    private var invoiceTotal: Double = 0.0

    // TODO APIs
    private fun geInvoicetDetails(userId: Int?, booking: String?, token: String?) {
        if (userId == null || booking == null) {
            finish()
            return
        }
        getDialog()?.show()
        API.request.getApi()
            .getInvoiceDetails(GetInvoiceDetail(GetInvoiceDetail.Data(booking, userId), token))
            .enqueue(object : Callback<InvoiceDetails> {
                override fun onFailure(call: Call<InvoiceDetails>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.info(this@BuyActivity, getString(R.string.unable_to_connect)).show()

                }

                override fun onResponse(
                    call: Call<InvoiceDetails>,
                    response: Response<InvoiceDetails>
                ) {

                    try {
                        val data = response?.body();
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            parseData(list)
                            //isLoaded = true

                        } else {
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.message?.let {
                                    Toasty.snackbar(btn_pay_now, it)
                                }
                        }
                    } catch (e: Exception) {
                        Toasty.info(this@BuyActivity, getString(R.string.unable_to_process)).show()
                    }
                    //getmDialog()?.dismiss()
                    getDialog()?.dismiss()

                }

            })
    }

    private fun parseData(data: InvoiceDetails.Data?) {
        val invoice = data?.invoice
        if (invoice != null) {
            val user = invoice.user
            if (user != null) {
                invoiceAddress = user.address1 ?: ""
                invoiceCountry = user.country ?: ""
                invoiceCity = user.city ?: ""
                //tv_customerAddress?.text = address
                //tv_customerPhone?.text = user.countryCode + " " + user.areaCode + " " + user.phone
            }

            invoiceBookingId = invoice.bookingAdviceNo ?: ""
            //tv_tv_bookingDate?.text = invoice.bookingAdviceDate

            val pkg = invoice.packages?.get(0)
            if (pkg != null) {
                // tv_packageName?.text = pkg.name
                invoiceCurrency = pkg.currency ?: ""
                invoiceAmount = pkg.price ?: 0.0
                invoiceVat = pkg.vat ?: 0.0
                invoiceConvFee = invoice.conFee ?: 0.0

                //tv_tv_packageAmount?.text = "$currency $amount"
                //tv_tv_convAmount?.text = "$currency $conv"
                val grand = invoiceAmount.plus(invoiceConvFee)
                //tv_tv_subAmount?.text = "$currency $grand"

                val vp = invoiceVat.div(100)
                val vatAmount = Calculate.round(grand.times(vp))
                invoiceTotal = grand.plus(vatAmount)

                //tv_tv_vatAmount?.text = "$currency $vatAmount"

                //tv_tv_totalAmount?.text = "$currency $totalAmount"
            }

            isInvoiceLoaded = true
            cartList.clear()
            cartItem = CartItem(
                0,
                pkg?.name,
                pkg?.price ?: 0.0,
                pkg?.currency,
                "",
                1,
                pkg?.vat,
                "${user?.address1}",
                true,
                false
            )
            cartItem?.quantityDisable = true
            cartItem?.adviceNumber = invoice.bookingAdviceNo ?: ""
            cartItem?.serviceLocationId = invoiceLocationId
            // cartItem?.location = invoice.invoiceLocationId ?: ""
            cartList.add(cartItem!!)
            runOnUiThread {
                recyclerView?.layoutManager = LinearLayoutManager(this)
                cartAdapters = CartAdapters(cartList, object : ItemClickListener<CartItem> {
                    override fun onItemClicked(item: CartItem?, position: Int) {
                        cartItem = item
                    }

                })

                recyclerView?.adapter = cartAdapters

                val addressList = ArrayList<AddressItem>()
                // var prefs = Prefs.get(this).member
                // var name = prefs?.firstName + " " + prefs?.lastName
                addressItem = AddressItem(
                    0,
                    user?.firstname + " " + user?.lastname,
                    user?.address1,
                    user?.city,
                    user?.country,
                    ""
                )
                addressItem?.selected = true
                addressList.add(addressItem!!)
                setupAddresses(addressList)

            }

        } else {
            Toasty.info(this, getString(R.string.unable_to_process)).show()
        }
    }

    private fun applyPromo(isService: Boolean, serviceId: Int, promo: String) {
        if (promo.isEmpty() || serviceId < 1)
            return
        val member = Prefs.get(this).member ?: return
        getDialog()?.show()
        //val date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())
        val data = if (isService)
            CheckPromo.createService(cartItem?.startDate, member.id, serviceId, promo)
        else CheckPromo.createPackage(cartItem?.startDate, member.id, serviceId, promo)
        API.request.getApi().checkPromoCode(CheckPromo(data, member.accessToken))
            .enqueue(object : Callback<PromoResponse> {
                override fun onFailure(call: Call<PromoResponse>, t: Throwable) {
                    getDialog()?.dismiss()
                }

                override fun onResponse(
                    call: Call<PromoResponse>,
                    response: Response<PromoResponse>
                ) {
                    getDialog()?.dismiss()
                    if (response.body()?.isSuccess() == true) {
                        cartAdapters?.applyPromo(response?.body()?.data, promo)
                        promoDialog(response?.body()?.data)
                        // Toasty.snackbar(et_promo, "Promo Code applied!")
                    } else {
                        cartAdapters?.applyPromo(null, "")
                        val err = response.body()?.errors
                        if (err != null && err.isNotEmpty()) {
                            Toasty.snackbar(et_promo, err?.get(0)?.message ?: "Invalid promo code")
                        } else {
                            Toasty.snackbar(et_promo, "Invalid promo code")
                        }
                    }

                }

            })
    }

    fun promoDialog(data: PromoResponse.Promo?) {
        if (data != null) {
            var text = ""
            if (data.isFLat()) {
                text = data.currencyType + " " + data.promoValue
            } else if (data.isPercent()) {
                text = data.promoValue + " %"
            }
            PromoDialog(text).show(supportFragmentManager, "PromoDialog")
        }
    }

    fun createCartFromInvoice() {
        cartList.clear()
        val list = intent?.getParcelableArrayListExtra<CartItem>("type_list")
        if (list != null) {
            if (list.size > 0)
                cartItem = list[0]
            cartList.addAll(list)
            recyclerView?.layoutManager = LinearLayoutManager(this)
            cartAdapters = CartAdapters(list, object : ItemClickListener<CartItem> {
                override fun onItemClicked(item: CartItem?, position: Int) {
                    cartItem = item
                }

            })

            recyclerView?.adapter = cartAdapters

        }

        if (type == TYPE_SERVICE || type == TYPE_PACKAGE) {
            val addressList = ArrayList<AddressItem>()
            addressItem = AddressItem(
                1,
                "",
                "${cartItem?.location}",
                "",
                "",
                ""
            )
            addressItem?.selected = true
            addressList.add(addressItem!!)
            setupAddresses(addressList)
        } else {
            getAddresses()
        }
    }
}