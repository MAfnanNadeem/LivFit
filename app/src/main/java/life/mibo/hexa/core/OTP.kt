/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.core

import android.content.Context
//import com.thrivecom.ringcaptcha.RingcaptchaAPIController
//import com.thrivecom.ringcaptcha.RingcaptchaService
//import com.thrivecom.ringcaptcha.lib.handlers.RingcaptchaHandler
//import com.thrivecom.ringcaptcha.lib.models.RingcaptchaResponse
import life.mibo.hardware.core.Logger
import life.mibo.hexa.utils.Toasty


class OTP() {

    private val userApi = "fd50d709e866af8ce33a9eacee42db1a8cee8f75"
    private val key = "i2ucy2y2y3any8u5i8uz"
    private val secret = "ipoteve4umomonu3acij"
   // val controller = RingcaptchaAPIController(key)

    fun verifiy(context: Context, number: String) {
//        controller.sendCaptchaCodeToNumber(
//            context, number, RingcaptchaService.SMS, object : RingcaptchaHandler {
//
//                //Called when the response is successful
//                override fun onSuccess(response: RingcaptchaResponse) {
//
//                    //Handle SMS reception automatically (only valid for verification)
//                    Logger.e("OTP Response $response")
//                    Toasty.error(context, "OTP Error: " + response.message).show()
//                    //RingcaptchaSMSHandler
//                    RingcaptchaAPIController.setSMSHandler { s, s1 ->
//                        //Only called when SMS reception was detected
//                        //Automatically verify PIN code
//                        true
//                    }
//                }
//
//                //Called when the response is unsuccessful
//                override fun onError(e: Exception) {
//                    //Display an error to the user
//                    e.printStackTrace()
//                    Toasty.error(context, "OTP Error: " + e.message).show()
//                }
//            },
//            "fd50d709e866af8ce33a9eacee42db1a8cee8f75"
//        )

    }
}