/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.twilio.verification.TwilioVerification;
import com.twilio.verification.external.VerificationStatus;

import life.mibo.hardware.core.Logger;

public class MyVerificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.e("MyVerificationReceiver receive " + intent);
        VerificationStatus status = TwilioVerification.getVerificationStatus(intent);
        Logger.e("MyVerificationReceiver status " + status);
        // NOT_STARTED, STARTED, AWAITING_VERIFICATION, SUCCESS, ERROR
        Logger.e("MyVerificationReceiver state ==  " + status.getState());
        switch (status.getState()) {
            case STARTED:

                break;
            case AWAITING_VERIFICATION:

                break;
            case SUCCESS:

                break;
            case ERROR:

                break;
            default:

                break;
        }
    }
}