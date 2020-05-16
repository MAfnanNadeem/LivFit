/*
 *  Created by Sumeet Kumar on 5/16/20 11:51 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/16/20 11:51 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.payments;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.telr.mobile.sdk.activity.WebviewActivity;
import com.telr.mobile.sdk.entity.response.status.StatusResponse;

import life.mibo.android.R;
import life.mibo.android.ui.base.BaseActivity;
import life.mibo.android.utils.Toasty;
import life.mibo.hardware.core.Logger;

public class FailedPaymentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);

        Logger.e("Failed PaymentActivity ", getIntent().getExtras());
        try {
            getFailedResponse();
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(this, e.getMessage(), Toasty.LENGTH_LONG).show();
        }
    }


    public void getFailedResponse() {
        Object object = getIntent().getParcelableExtra(WebviewActivity.PAYMENT_RESPONSE);
        TextView textView = (TextView) findViewById(R.id.textViewInfo0);

        if (object instanceof StatusResponse) {
            StatusResponse status = (StatusResponse) object;
            textView.setText(textView.getText() + " : " + status.getTrace());

            if (status.getAuth() != null) {
                status.getAuth().getStatus();   // Authorisation status. A indicates an authorised transaction. H also indicates an authorised transaction, but where the transaction has been placed on hold. Any other value indicates that the request could not be processed.
                status.getAuth().getAvs();      /* Result of the AVS check:
                                            Y = AVS matched OK
                                            P = Partial match (for example, post-code only)
                                            N = AVS not matched
                                            X = AVS not checked
                                            E = Error, unable to check AVS */
                status.getAuth().getCode();     // If the transaction was authorised, this contains the authorisation code from the card issuer. Otherwise it contains a code indicating why the transaction could not be processed.
                status.getAuth().getMessage();  // The authorisation or processing error message.
                status.getAuth().getCa_valid();
                status.getAuth().getCardcode(); // Code to indicate the card type used in the transaction. See the code list at the end of the document for a list of card codes.
                //  status.getAuth().getCardlast4();// The last 4 digits of the card number used in the transaction. This is supplied for all payment types (including the Hosted Payment Page method) except for PayPal.
                status.getAuth().getCvv();      /* Result of the CVV check:
                                           Y = CVV matched OK
                                           N = CVV not matched
                                           X = CVV not checked
                                           E = Error, unable to check CVV */
                status.getAuth().getTranref(); //The payment gateway transaction reference allocated to this request.
                status.getAuth().getAvs();     /* Result of the AVS check:
                                           Y = AVS matched OK
                                           P = Partial match (for example, post-code only)
                                           N = AVS not matched
                                           X = AVS not checked
                                           E = Error, unable to check AVS */
            }
        } else if (object instanceof String) {
            String errorMessage = (String) object;
            Toasty.error(this, errorMessage, Toasty.LENGTH_LONG).show();
            textView.setText(errorMessage);
        }
    }
}
