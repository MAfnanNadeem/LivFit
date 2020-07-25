/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import kotlinx.android.synthetic.main.fragment_update_main.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.receiver.AppSignatureHelper
import life.mibo.android.receiver.SMSBroadcastReceiver
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Toasty
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class ProfileUpdateFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_update_main, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = ArrayList<Fragment>()
        list.add(UpdateDataFragment())
        list.add(UpdateNumberFragment())
        list.add(UpdateVerifyFragment())
        viewPager?.isUserInputEnabled = false


        btn_next?.setOnClickListener {
            nextClick()
        }

        viewPager?.adapter = ViewPagerAdapter(list, this)
//        TabLayoutMediator(tabs, viewPager) { tab, position ->
//            tab.text = ""
//        }.attach()

        worm_dots_indicator?.setViewPager2(viewPager)
        worm_dots_indicator?.dotsClickable = false
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)?.supportActionBar?.setHomeButtonEnabled(false)
        // activity?.supportFinishAfterTransition()
    }

    fun nextClick() {
        if (isNextClickable()) {
            val page = viewPager?.currentItem?.plus(1) ?: 0
            log("nextClick $page")
            viewPager?.currentItem = page
            if (page == 0) {
                btn_next?.text = getString(R.string.next)
            } else if (page == 1) {
                btn_next?.text = getString(R.string.send_otp)
            } else if (page == 2) {
                btn_next?.text = getString(R.string.verify)
                isOtpSent = true
            }
        }
    }

    private var isOtpSent = false
    fun otpVerified() {
        log("otpVerified ::: $isOtpSent")
        if (isOtpSent) {
            val member = Prefs.get(context).member
            member?.numberVerify = 2

            Prefs.get(context).member = member

            navigate(Navigator.CLEAR_HOME, null)
        }

    }

    var otpStarted = false
    fun startOtpReceiver() {
        if (otpStarted)
            return
        startOtpListener()
    }

    private fun isNextClickable(): Boolean {
        try {
            //log("isNextClickable ")
            //viewPager?.adapter

            //val page: Fragment? = childFragmentManager.findFragmentByTag("android:switcher:" + viewPager?.id + ":" + viewPager?.currentItem)
            val page: Fragment? =
                childFragmentManager.findFragmentByTag("f" + viewPager.currentItem)
            log("isNextClickable $page")
            // log("isNextClickable2 $page2")
            if (page != null && page is UpdateDataFragment) {

                return page.onNextClicked()
            }
            if (page != null && page is UpdateNumberFragment) {
                if (page.onNextClicked()) {
                    //startOtpReceiver()
                    return true
                }
                return false
            }
            if (page != null && page is UpdateVerifyFragment) {
                isOtpSent = true
                return page.onNextClicked()
            }
            // if (viewPager?.currentItem == 0 && page != null) {
            //  (page as BMIFragment)
            //}
            return false
        } catch (e: java.lang.Exception) {
            return false
        }

    }

    class ViewPagerAdapter(val list: List<Fragment>, manager: Fragment) :
        FragmentStateAdapter(manager) {

        @NonNull
        override fun createFragment(position: Int): Fragment {
            return list[position]
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    override fun onStart() {
        log("onStart")
        super.onStart()
        //activity?.actionBar?.hide()
        //navigate(Navigator.HOME_STOP, null)
    }

    override fun onStop() {

        super.onStop()
        //activity?.actionBar?.show()
    }

    override fun onDetach() {
        super.onDetach()
        try {
            if (smsBroadcast != null)
                context?.unregisterReceiver(smsBroadcast)
            log("startOtpListener onStop")
        } catch (e: java.lang.Exception) {
            log("startOtpListener error onStop")
            MiboEvent.log(e)
        }
    }

    private var smsBroadcast: SMSBroadcastReceiver? = null

    private fun startOtpListener() {
        log("startOtpListener ::")
        try {
            smsBroadcast = SMSBroadcastReceiver()
            val client: SmsRetrieverClient = SmsRetriever.getClient(requireContext())
            //3KBZ8cYIYFm
            smsBroadcast?.init(object : SMSBroadcastReceiver.OTPReceiveListener {
                override fun onOTPReceived(otp: Intent?) {
                    log("startOtpListener : onOTPReceived $otp ")
                    if (otp != null)
                        startActivityForResult(otp, REQUEST_OTP)
                    //Toasty.info(context, "SMS OTP Received $otp").show()

                    //otpReceived(otp)

                }

                override fun onOTPTimeOut() {
                    //Toasty.info(context, "SMS OTP TimeOut").show()
                    log("startOtpListener : onOTPTimeOut ")
                }

            })

            client.startSmsUserConsent("MIBO").addOnSuccessListener {
                log("startOtpListener addOnSuccessListener")
                // otpTxtView.text = "Waiting for OTP"
                // Toasty.info(context, "SMS Retriever starts ").show()
            }.addOnFailureListener {
                //  otpTxtView.text = "Cannot Start SMS Retriever"
                log("startOtpListener addOnFailureListener")
            }

//            client.startSmsRetriever()
//                .addOnSuccessListener {
//                    log("startOtpListener addOnSuccessListener")
//                    // otpTxtView.text = "Waiting for OTP"
//                    // Toasty.info(context, "SMS Retriever starts ").show()
//                }.addOnFailureListener {
//                    //  otpTxtView.text = "Cannot Start SMS Retriever"
//                    log("startOtpListener addOnFailureListener")
////                    Toasty.info(
////                        requireContext(),
////                        "SMSBroadcastReceiver Error " + it?.message,
////                        Toast.LENGTH_LONG
////                    ).show()
//                }


            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

            context?.registerReceiver(smsBroadcast, intentFilter)
            otpStarted = true
            //Used to generate hash signature
            //O2KCiTrZWLq
            //3KBZ8cYIYFm - debug
            log(
                "RegisterController appSignatures ${AppSignatureHelper(context).appSignatures}"
            )
            log(
                "RegisterController appSignatures ${Arrays.toString(AppSignatureHelper(context).appSignatures.toArray())}"
            )
        } catch (e: java.lang.Exception) {
            log("startOtpListener : error > $e")
            e.printStackTrace()
        }
    }

    private fun otpReceived(otp: String?) {
        val page: Fragment? = childFragmentManager.findFragmentByTag("f" + viewPager.currentItem)
        log("otpReceived $page $otp")
        // log("isNextClickable2 $page2")
        if (page != null && page is UpdateVerifyFragment) {
            log("startOtpListener : sent to UpdateVerifyFragment ")
            return page.otpReceived(otp)
        }
    }

    val REQUEST_OTP = 2345
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log("onActivityResult otp requestCode : $requestCode   $data ")
        log("onActivityResult otp extras   ${data?.extras} ")
        if (requestCode == REQUEST_OTP) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    log("onActivityResult otp extras   ${data?.extras} ")
                    val message = data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    //Toasty.info(requireContext(), "OTP $message").show()
                    //otpReceived(message)
                    val pattern = Pattern.compile("(|^)\\d{4}")
                    val matcher: Matcher = pattern.matcher(message)
                    if (matcher.find()) {
                        otpReceived(matcher.group(0))
                        //otpText.setText(matcher.group(0))
                    }
                } catch (e: Exception) {

                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onNavBackPressed(): Boolean {
        return false
    }


}