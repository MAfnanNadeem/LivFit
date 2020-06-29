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
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.catalog.NewAddressActivity
import life.mibo.android.ui.catalog.OrdersFragment
import life.mibo.android.ui.fit.FitnessHelper
import life.mibo.android.ui.main.Navigator
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

        tv_invoices?.setOnClickListener {
            navigate(Navigator.INVOICES, OrdersFragment.create(1))
        }

        tv_address?.setOnClickListener {
            navigate(Navigator.INVOICES, OrdersFragment.create(2))
            activity?.title = getString(R.string.address_titles)
        }

        tv_add_address?.setOnClickListener {
            startActivity(Intent(requireContext(), NewAddressActivity::class.java))
            //navigate(Navigator.INVOICES, null)
        }

        tv_google_fit?.setOnClickListener {
            checkPermission()
        }

//        tv_orders?.setOnClickListener {
//            navigate(Navigator.ORDERS, null)
//        }
        checkGoogleConnected()
    }


    private fun checkGoogleConnected() {
        try {
            if (isGoogleConnected()) {
                tv_google_fit_status?.visibility = View.VISIBLE
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
       // val fit = FitnessOptions.builder()
        //    .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        //    .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        //    .build()
        val options = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .addDataType(DataType.TYPE_WORKOUT_EXERCISE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
            .addDataType(DataType.TYPE_HEART_RATE_BPM)
            .addDataType(DataType.TYPE_MOVE_MINUTES)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED)
            .build()
        return GoogleSignIn.hasPermissions(
            GoogleSignIn.getLastSignedInAccount(requireContext()), options
        )
    }

    private fun connectFit() {

        if (isGoogleConnected()) {
            subscribeGoogleFit(true)
        } else {
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build()
            GoogleSignIn.requestPermissions(
                this,
                FitnessHelper.GOOGLE_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(requireContext()),
                fitnessOptions
            )
        }

        //navigate(GOOGLE_FIT, null)
        //201589375301-6mfkekfog0lhdo3813f0j206g6ti9sn7.apps.googleusercontent.com
        //201589375301-nmuvhdnos8pets17tb18ju12n1h1hslf.apps.googleusercontent.com


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            FitnessHelper.GOOGLE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    subscribeGoogleFit(false)
                } else {

                }
            }
        }
    }


}