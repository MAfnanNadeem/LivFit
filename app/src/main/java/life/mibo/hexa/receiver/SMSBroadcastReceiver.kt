/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.receiver

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

        Logger.e("SMSBroadcastReceiver onReceive : $intent")
        Logger.e("SMSBroadcastReceiver Extras : ${intent.extras?.toString()}")
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {

            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status? ?: return

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {

                    // Get SMS message contents
                    val otp: String = extras!!.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    val pattern = Pattern.compile("(\\d{4})")
                    val matcher = pattern.matcher(otp)

                    // Extract one-time code from the message and complete verification
                    var value = ""
                    if (matcher.find()) {
                        Logger.e("SMSBroadcastReceiver "+matcher.group(1))
                        value = matcher.group(1)
                    }

                    Logger.e("SMSBroadcastReceiver : $value  :: $otp")
                    otpReceiver?.onOTPReceived(value)
                }

                CommonStatusCodes.TIMEOUT ->{
                    // Waiting for SMS timed out (5 minutes)
                    otpReceiver?.onOTPTimeOut()
                }
                else -> {
                    Logger.e("SMSBroadcastReceiver : $status")
                }
            }
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String?)

        fun onOTPTimeOut()
    }
}