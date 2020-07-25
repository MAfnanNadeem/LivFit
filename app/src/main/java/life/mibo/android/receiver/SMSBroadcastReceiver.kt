/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import life.mibo.hardware.core.Logger
import java.util.regex.Pattern

public class SMSBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiver: OTPReceiveListener? = null

    fun init(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {

        Logger.e("SMSBroadcastReceiver otp onReceive : $intent")
        Logger.e("SMSBroadcastReceiver  otp Extras : ${intent.extras?.toString()}")
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {

            val extras = intent.extras
            Logger.e("SMSBroadcastReceiver  otp extras : $extras", extras)
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status? ?: return
            Logger.e("SMSBroadcastReceiver  otp status.statusCode : ${status.statusCode}")
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {

                    try {
                        // Get SMS message contents
                        val messageIntent: Intent? = extras?.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                        Logger.e("SMSBroadcastReceiver otp messageIntent ", messageIntent?.extras)

                       // val otp: String? = messageIntent?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                     //   Logger.e("SMSBroadcastReceiver otp EXTRA_SMS_MESSAGE  $otp" )
                     //  // com.google.android.gms.auth.api.phone.extra.verification_token
                   //     Logger.e("SMSBroadcastReceiver otp EXTRA_SMS_MESSAGE  $otp" )
//                        val pattern = Pattern.compile("(\\d{4})")
//                        val matcher = pattern.matcher(otp)
//
//                        // Extract one-time code from the message and complete verification
//                        var value = ""
//                        if (matcher.find()) {
//                            Logger.e("SMSBroadcastReceiver otp  "+matcher.group(1))
//                            value = matcher.group(1)
//                        }

                     //   Logger.e("SMSBroadcastReceiver  otp : $value  :: $otp")
                        otpReceiver?.onOTPReceived(messageIntent)
                    }
                    catch (e: Exception){
                        Logger.e("SMSBroadcastReceiver  otp  error  :: $e")
                        e.printStackTrace()
                    }

                }

                CommonStatusCodes.TIMEOUT ->{
                    // Waiting for SMS timed out (5 minutes)
                    Logger.e("SMSBroadcastReceiver otp  TIMEOUT0 ")
                    otpReceiver?.onOTPTimeOut()
                }
                else -> {
                    Logger.e("SMSBroadcastReceiver  otp : $status")
                }
            }
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: Intent?)

        fun onOTPTimeOut()
    }
}