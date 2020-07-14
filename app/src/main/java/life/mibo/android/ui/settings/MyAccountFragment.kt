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
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import kotlinx.android.synthetic.main.fragment_my_account.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.catalog.NewAddressActivity
import life.mibo.android.ui.fit.FitnessHelper
import life.mibo.android.ui.fit.GoogleFit
import life.mibo.android.ui.fit.fitbit.Fitbit
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.member.OrdersFragment
import life.mibo.android.utils.Toasty


class MyAccountFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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


}