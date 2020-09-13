/*
 *  Created by Sumeet Kumar on 6/3/20 2:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/14/20 10:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_recycler.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.catalog.NewAddressActivity
import life.mibo.android.ui.fit.FitnessHelper
import life.mibo.android.ui.fit.GoogleFit
import life.mibo.android.ui.fit.fitbit.Fitbit
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.member.OrdersFragment
import life.mibo.android.ui.member.ViewSessionsFragment
import life.mibo.android.utils.Toasty


class MyAccountFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
        //return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters(Prefs.get(context).member?.isMember() ?: false)
        //initViews()
    }

    fun initViews() {
        if (Prefs.get(context).member?.isMember() == true) {
            view_member?.visibility = View.VISIBLE
            view_ip?.visibility = View.GONE
            tv_invoices?.setOnClickListener {
                navigate(Navigator.INVOICES, OrdersFragment.create(1))
                activity?.title = getString(R.string.orders_titles)
            }

            tv_address?.setOnClickListener {
                navigate(Navigator.INVOICES, OrdersFragment.create(2))
                activity?.title = getString(R.string.address_titles)
            }

            tv_add_address?.setOnClickListener {
                startActivity(Intent(requireContext(), NewAddressActivity::class.java))
                //navigate(Navigator.INVOICES, null)
            }

            tv_measurement?.setOnClickListener {
                navigate(Navigator.VIEW_MEASUREMENT, OrdersFragment.create(2))
            }
            try {
                val cal = Prefs.get(this.context).get(Prefs.CALORIES, -1)
                if (cal > 0) {
                    view_sessions?.visibility = View.VISIBLE
                    tv_session?.setOnClickListener {
                        navigate(Navigator.VIEW_SESSIONS, OrdersFragment.create(2))
                    }
                }
            } catch (e: java.lang.Exception) {

            }


//            tv_sessions?.setOnClickListener {
//                navigate(Navigator.VIEW_MEASUREMENT, OrdersFragment.create(2))
//            }


        } else {
            view_member?.visibility = View.GONE
            view_ip?.visibility = View.VISIBLE

            tv_sales?.setOnClickListener {
                navigate(Navigator.MY_SALES, OrdersFragment.create(1))
            }
            tv_customers?.setOnClickListener {
                navigate(Navigator.MY_CLIENTS, OrdersFragment.create(1))
            }
            tv_services?.setOnClickListener {
                navigate(Navigator.MY_SERVICES, OrdersFragment.create(1))
            }
        }


        view_google_fit?.setOnClickListener {
            checkPermission()
        }

        view_fitbit?.setOnClickListener {
            loginToFitbit()
        }

//        view_samsung?.setOnClickListener {
//           // loginToSHealth()
//        }

        tv_units?.setOnClickListener {
            navigate(Navigator.SETTINGS_UNIT, null)
        }

//        tv_orders?.setOnClickListener {
//            navigate(Navigator.ORDERS, null)
//        }
        checkGoogleConnected()
        checkFitbitConnected()
        checkSamsungConnected()
    }

    // TODO Google Fit
    private fun checkGoogleConnected() {
        try {
            if (isGoogleConnected()) {
                tv_google_fit_status?.visibility = View.VISIBLE
                // tv_fitbit_status?.visibility = View.VISIBLE
            } else {
                tv_google_fit_status?.visibility = View.GONE
            }
        } catch (e: Exception) {

        }
    }

    private fun checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermissionHelper.requestPermission(
                this@MyAccountFragment, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
            ) {
                connectFit()
            }
        } else {
            connectFit()
        }
    }


    private fun isGoogleConnected(): Boolean {
        return GoogleSignIn.hasPermissions(
            GoogleSignIn.getLastSignedInAccount(requireContext()), GoogleFit.getFitOptions()
        )
    }


    private fun connectFit() {

        if (isGoogleConnected()) {
            subscribeGoogleFit(true)
        } else {
            val fitnessOptions = GoogleFit.getFitOptions()
            GoogleSignIn.requestPermissions(
                this,
                FitnessHelper.GOOGLE_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(requireContext()),
                fitnessOptions
            )
        }
    }

    fun showDisConnectDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogPhoto)
        builder.setTitle(R.string.google_fit)
        builder.setMessage(R.string.google_fit_logout)
        builder.setPositiveButton(R.string.disconnect) { dialog, which ->
            dialog?.dismiss()
            val fit = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build()
            val google = GoogleSignIn.getLastSignedInAccount(requireContext())

        }
        builder.setNegativeButton(R.string.no_text) { dialog, which ->
            dialog?.dismiss()
        }

        builder.show()
    }


    private fun subscribeGoogleFit(dialog: Boolean) {
        Fitness.getRecordingClient(
            requireContext(), GoogleSignIn.getLastSignedInAccount(requireContext())!!
        ).subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toasty.grey(requireContext(), "Google Fit Subscribed").show()
                    checkGoogleConnected()
                } else {
                    Toasty.grey(requireContext(), "Google Fit " + it.exception?.message).show()

                }
            }
    }

    private fun readDailySteps() {
        Fitness.getHistoryClient(
            requireContext(),
            GoogleSignIn.getLastSignedInAccount(requireContext())!!
        )
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total =
                    if (dataSet.isEmpty) 0 else dataSet.dataPoints[0]
                        .getValue(Field.FIELD_STEPS).asInt().toLong()
                //Log.i(TAG, "Total steps: $total")
            }
            .addOnFailureListener { e ->

            }
    }


    // TODO FITBIT
    private fun isFitbitConnected(): Boolean {
        // val token = Prefs.getEncrypted(this@MyAccountFragment.requireContext()).get(Fitbit.token_key)
        return Fitbit.isLogged()
    }


    private fun checkFitbitConnected() {
        try {
            //FitbitManager.isLoggedIn()
            if (isFitbitConnected()) {
                tv_fitbit_status?.visibility = View.VISIBLE
                // tv_fitbit_status?.visibility = View.VISIBLE
            } else {
                tv_fitbit_status?.visibility = View.GONE
            }
        } catch (e: Exception) {

        }
//        try {
//            if (Fitbit(context).isConnected()) {
//                tv_fitbit_status?.visibility = View.VISIBLE
//                // tv_fitbit_status?.visibility = View.VISIBLE
//            } else {
//                tv_fitbit_status?.visibility = View.GONE
//            }
//        } catch (e: Exception) {
//
//        }
    }


    fun loginToFitbit() {
        Fitbit().loginToFitbit(this)
        //FitbitManager.login(this)
    }

    private fun subscribeFitbit(value: Boolean) {

    }

    // TODO Samsung Health

    private fun loginToSHealth() {

    }

    private fun checkSamsungConnected() {
//        try {
//            if (isFitbitConnected()) {
//                tv_fitbit_status?.visibility = View.VISIBLE
//                // tv_fitbit_status?.visibility = View.VISIBLE
//            } else {
//                tv_fitbit_status?.visibility = View.GONE
//            }
//        } catch (e: Exception) {
//
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("onActivityResult $requestCode, $resultCode, $data")

        when (requestCode) {
            FitnessHelper.GOOGLE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    subscribeGoogleFit(false)
                } else {

                }
            }
            Fitbit.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    subscribeFitbit(false)
                } else {

                }
            }
            else -> {
                log("FitbitManager $requestCode, $resultCode, $data")

//                FitbitManager.onActivityResult(
//                    requestCode, resultCode, data
//                ) {
//                    checkFitbitConnected()
//                };
            }
        }
    }

    var isRxt = true

    private fun setAdapters(isMember: Boolean) {
        swipeToRefresh?.isEnabled = false

        val list = ArrayList<Item>()
        if (isMember) {

            list.add(Item(TYPE.PURCHASES_HEADER, isHeader = true))
            list.add(Item(TYPE.MY_ORDERS, isSingle = true))

            list.add(Item(TYPE.SHIPMENT_HEADER, isHeader = true))
            list.add(Item(TYPE.MANAGE_ADDRESS, isTop = true))
            list.add(Item(TYPE.ADD_ADDRESS, isBottom = true))


            list.add(Item(TYPE.MEASUREMENT_HEADER, isHeader = true))
            list.add(Item(TYPE.VIEW_MEASURE, isSingle = true))


            try {
                val cal = Prefs.get(this.context).get(Prefs.CALORIES, -1)
                if (cal > 0) {
                    list.add(Item(TYPE.SESSIONS_HEADER, isHeader = true))
                    list.add(Item(TYPE.VIEW_SESSION, isSingle = true))
                }
            } catch (e: java.lang.Exception) {

            }


        } else {

            list.add(Item(TYPE.SALES_HEADER, isHeader = true))
            list.add(Item(TYPE.MY_SALES, isTop = true))
            list.add(Item(TYPE.MY_SERVICES, isMiddle = true))
            list.add(Item(TYPE.MY_CLIENTS, isBottom = true))


        }

        list.add(Item(TYPE.CONNECT_HEADER, isHeader = true))
        list.add(Item(TYPE.GOOGLE_FIT, isTop = true))
        list.add(Item(TYPE.FITBIT, isBottom = true))

        if (isRxt) {
            list.add(Item(TYPE.RXT_HEADER, isHeader = true))
            list.add(Item(TYPE.REACT_SESSION, isSingle = true))
           // list.add(Item(TYPE.REACT_SESSION2, isSingle = true))
        }

        list.add(Item(TYPE.SETTINGS_HEADER, isHeader = true))
        list.add(Item(TYPE.COUNTRY_LANGUAGE, isTop = true))
        list.add(Item(TYPE.NOTIFICATIONS, isMiddle = true))
        list.add(Item(TYPE.POLICIES, isMiddle = true))
        list.add(Item(TYPE.UNITS, isBottom = true))

        list.add(Item(TYPE.ABOUT_HEADER, isHeader = true))
        list.add(Item(TYPE.ABOUT_APP, isSingle = true, arrow = false))

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val adapters =
            AccountAdapters(
                list,
                object : ItemClickListener<Item> {
                    override fun onItemClicked(item: Item?, position: Int) {
                        if (item != null)
                            onClick(item)
                    }
                })

        recyclerView?.adapter = adapters
    }

    fun onClick(item: Item) {
        when (item.id) {
            TYPE.MY_SALES -> {
                navigate(Navigator.MY_SALES, null)
            }
            TYPE.MY_SERVICES -> {
                navigate(Navigator.MY_SERVICES, null)
            }
            TYPE.MY_CLIENTS -> {
                navigate(Navigator.MY_CLIENTS, null)
            }
            TYPE.MY_ORDERS -> {
                navigate(Navigator.INVOICES, OrdersFragment.create(1))
                activity?.title = getString(R.string.orders_titles)
            }
            TYPE.ADD_ADDRESS -> {
                startActivity(Intent(requireContext(), NewAddressActivity::class.java))
            }
            TYPE.MANAGE_ADDRESS -> {
                navigate(Navigator.INVOICES, OrdersFragment.create(2))
                activity?.title = getString(R.string.address_titles)
            }
            TYPE.VIEW_MEASURE -> {
                navigate(Navigator.VIEW_MEASUREMENT, null)
            }
            TYPE.VIEW_SESSION -> {
                navigate(Navigator.VIEW_SESSIONS, ViewSessionsFragment.create(1))
            }
            TYPE.REACT_SESSION -> {
                navigate(Navigator.VIEW_SESSIONS, ViewSessionsFragment.create(2))
            }
            TYPE.GOOGLE_FIT -> {
                checkPermission()
            }
            TYPE.FITBIT -> {
                loginToFitbit()
            }

            TYPE.UNITS -> {
                navigate(Navigator.SETTINGS_UNIT, SettingsFragment.create(1))
            }

            TYPE.NOTIFICATIONS -> {
                navigate(Navigator.SETTINGS_UNIT, SettingsFragment.create(2))
                activity?.title = getString(R.string.notifications)
            }
            TYPE.POLICIES -> {
                navigate(Navigator.SETTINGS_UNIT, SettingsFragment.create(3))
                activity?.title = getString(R.string.policies)
            }

            else -> {
//                Toasty.snackbar(
//                    recyclerView,
//                    item?.id?.resId ?: R.string.error_occurred
//                )
            }
        }
    }


    // Dynamic View

    enum class TYPE(var resId: Int) {
        SALES_HEADER(R.string.sales),
        PURCHASES_HEADER(R.string.purchase),
        SHIPMENT_HEADER(R.string.shipment_address),
        MEASUREMENT_HEADER(R.string.measurement),
        SESSIONS_HEADER(R.string.sessions),
        CONNECT_HEADER(R.string.connect),
        SETTINGS_HEADER(R.string.settings),
        ABOUT_HEADER(R.string.about_us),
        RXT_HEADER(R.string.rxt_sessions),
        MY_SALES(R.string.my_sales),
        MY_SERVICES(R.string.my_services),
        MY_CLIENTS(R.string.my_customers),
        MY_ORDERS(R.string.my_orders),
        ADD_ADDRESS(R.string.add_a_new_address),
        MANAGE_ADDRESS(R.string.manage_address),
        GOOGLE_FIT(R.string.google_fit),
        FITBIT(R.string.fitbit),
        VIEW_SESSION(R.string.view_sessions),
        VIEW_MEASURE(R.string.view_measurement),
        ABOUT(R.string.about_us),
        CONTACT(R.string.contact_us),
        UNITS(R.string.unit),
        COUNTRY_LANGUAGE(R.string.country_language),
        NOTIFICATIONS(R.string.notifications),
        POLICIES(R.string.policies),
        ABOUT_APP(R.string.app_name_version),
        REACT_SESSION(R.string.rxt_sessions),
        REACT_SESSION2(R.string.rxl_sessions),
        //SESSION(R.string.view_sessions),
    }

    data class Item(
        val id: TYPE,
        val arrow: Boolean = true,
        var isSelected: Boolean = false,
        val isHeader: Boolean = false,
        val isSingle: Boolean = false,
        val isTop: Boolean = false,
        val isMiddle: Boolean = false,
        val isBottom: Boolean = false
    )

    class AccountAdapters(
        val list: ArrayList<Item>,
        val listener: ItemClickListener<Item>?
    ) : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_account_header,
                        parent,
                        false
                    )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(list[position], listener)
        }

        override fun getItemViewType(position: Int): Int {
            //if (list[position].isHeader)
            //     return 1
            return super.getItemViewType(position)
        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val header: TextView? = itemView.findViewById(R.id.tv_header)
        private val singleView: TextView? = itemView.findViewById(R.id.tv_single)
        private val topView: TextView? = itemView.findViewById(R.id.tv_top)
        private val middleView: TextView? = itemView.findViewById(R.id.tv_middle)
        private val bottomView: TextView? = itemView.findViewById(R.id.tv_bottom)
        //private val arrow: ImageView? = itemView.findViewById(R.id.imageView)

        fun bind(
            item: Item?,
            listener: ItemClickListener<Item>?
        ) {
            if (item == null)
                return
            header?.visibility = View.GONE
            singleView?.visibility = View.GONE
            topView?.visibility = View.GONE
            middleView?.visibility = View.GONE
            bottomView?.visibility = View.GONE

            when {
                item.isHeader -> {
                    header?.visibility = View.VISIBLE
                    header?.setText(item.id?.resId)
                }
                item.isSingle -> {
                    singleView?.visibility = View.VISIBLE
                    singleView?.setText(item.id?.resId)
                    if (item.arrow) {

                    } else {
                        singleView?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                }
                item.isTop -> {
                    topView?.visibility = View.VISIBLE
                    topView?.setText(item.id?.resId)
                }
                item.isMiddle -> {
                    middleView?.visibility = View.VISIBLE
                    middleView?.setText(item.id?.resId)
                }
                item.isBottom -> {
                    bottomView?.visibility = View.VISIBLE
                    bottomView?.setText(item.id?.resId)
                }
            }

            if (item.arrow) {

            } else {

            }

            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }


}