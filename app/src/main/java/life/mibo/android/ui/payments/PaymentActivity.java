/*
 *  Created by Sumeet Kumar on 5/16/20 11:41 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/16/20 11:41 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.payments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.annotation.Keep;

import com.telr.mobile.sdk.activity.WebviewActivity;
import com.telr.mobile.sdk.entity.request.payment.Address;
import com.telr.mobile.sdk.entity.request.payment.App;
import com.telr.mobile.sdk.entity.request.payment.Billing;
import com.telr.mobile.sdk.entity.request.payment.MobileRequest;
import com.telr.mobile.sdk.entity.request.payment.Name;
import com.telr.mobile.sdk.entity.request.payment.Tran;
import com.telr.mobile.sdk.entity.request.status.StatusRequest;
import com.telr.mobile.sdk.entity.response.payment.MobileResponse;
import com.telr.mobile.sdk.entity.response.status.StatusResponse;
import com.telr.mobile.sdk.service.InitiatePaymentListener;
import com.telr.mobile.sdk.service.PaymentService;
import com.telr.mobile.sdk.service.StatusListener;
import com.telr.mobile.sdk.webservices.PaymentTask;
import com.telr.mobile.sdk.webservices.StatusTask;

import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.Random;

import life.mibo.android.R;
import life.mibo.android.ui.base.BaseActivity;

@Keep
public class PaymentActivity extends BaseActivity {


    EditText amount;
    private static final String SecretKey = "J2wB^QhtKW@BCWwJ";
    private static final String StoreId = "23584";
    private static final boolean isSecurityEnabled = false;

    public void onCreate(Bundle savedInstanceState) {
        PaymentActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        View pay = findViewById(R.id.btn_pay);
        amount = findViewById(R.id.et_amount);
        pay.setOnClickListener(v -> {
            payNow();
        });

    }

    private void payNow() {
        Intent intent = new Intent(this, WebviewActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String price = amount.getText().toString();

        intent.putExtra(WebviewActivity.EXTRA_MESSAGE, getMobileRequest(StoreId, SecretKey, price));
        intent.putExtra(WebviewActivity.SUCCESS_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.SuccessPaymentActivity");
        intent.putExtra(WebviewActivity.FAILED_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.FailedPaymentActivity");
        intent.putExtra(WebviewActivity.IS_SECURITY_ENABLED, isSecurityEnabled);
        startActivity(intent);
    }


    private MobileRequest getMobileRequest(String storeId, String key, String amount) {
        MobileRequest mobile = new MobileRequest();
        mobile.setStore(storeId);                       // Store ID
        mobile.setKey(key);                              // Authentication Key : The Authentication Key will be supplied by Telr as part of the Mobile API setup process after you request that this integration type is enabled for your account. This should not be stored permanently within the App.
        App app = new App();
        app.setId("123456789");                          // Application installation ID
        app.setName("TelrSDK");                          // Application name
        app.setUser("123456");                           // Application user ID : Your reference for the customer/user that is running the App. This should relate to their account within your systems.
        app.setVersion("0.0.1");                         // Application version
        app.setSdk("123");
        mobile.setApp(app);
        Tran tran = new Tran();
        tran.setTest("1");                              // Test mode : Test mode of zero indicates a live transaction. If this is set to any other value the transaction will be treated as a test.
        tran.setType("auth");                           /* Transaction type
                                                            'auth'   : Seek authorisation from the card issuer for the amount specified. If authorised, the funds will be reserved but will not be debited until such time as a corresponding capture command is made. This is sometimes known as pre-authorisation.
                                                            'sale'   : Immediate purchase request. This has the same effect as would be had by performing an auth transaction followed by a capture transaction for the full amount. No additional capture stage is required.
                                                            'verify' : Confirm that the card details given are valid. No funds are reserved or taken from the card.
                                                        */
        tran.setClazz("paypage");                       // Transaction class only 'paypage' is allowed on mobile, which means 'use the hosted payment page to capture and process the card details'
        tran.setCartid(String.valueOf(new BigInteger(128, new Random()))); //// Transaction cart ID : An example use of the cart ID field would be your own transaction or order reference.
        tran.setDescription("Test Mobile API");         // Transaction description
        tran.setCurrency("AED");                        // Transaction currency : Currency must be sent as a 3 character ISO code. A list of currency codes can be found at the end of this document. For voids or refunds, this must match the currency of the original transaction.
        tran.setAmount(amount);                         // Transaction amount : The transaction amount must be sent in major units, for example 9 dollars 50 cents must be sent as 9.50 not 950. There must be no currency symbol, and no thousands separators. Thedecimal part must be separated using a dot.
        //tran.setRef();                                // (Optinal) Previous transaction reference : The previous transaction reference is required for any continuous authority transaction. It must contain the reference that was supplied in the response for the original transaction.

        //040023303844  //030023738912
        //tran.setFirstref("030023738912");             // (Optinal) Previous user transaction detail reference : The previous transaction reference is required for any continuous authority transaction. It must contain the reference that was supplied in the response for the original transaction.

        mobile.setTran(tran);
        Billing billing = new Billing();
        Address address = new Address();
        address.setCity("Dubai");                       // City : the minimum required details for a transaction to be processed
        address.setCountry("AE");                       // Country : Country must be sent as a 2 character ISO code. A list of country codes can be found at the end of this document. the minimum required details for a transaction to be processed
        address.setRegion("Dubai");                     // Region
        address.setLine1("SIT G=Towe");                 // Street address – line 1: the minimum required details for a transaction to be processed
        //address.setLine2("SIT G=Towe");               // (Optinal)
        //address.setLine3("SIT G=Towe");               // (Optinal)
        //address.setZip("SIT G=Towe");                 // (Optinal)
        billing.setAddress(address);
        Name name = new Name();
        name.setFirst("girish");                          // Forename : the minimum required details for a transaction to be processed
        name.setLast("bodhe");                          // Surname : the minimum required details for a transaction to be processed
        name.setTitle("Mr");                           // Title
        billing.setName(name);
        billing.setEmail("sumeetkumar@gmail.com"); //stackfortytwo@gmail.com : the minimum required details for a transaction to be processed.
        mobile.setBilling(billing);
        return mobile;

    }

}