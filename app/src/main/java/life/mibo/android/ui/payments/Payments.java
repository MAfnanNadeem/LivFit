/*
 *  Created by Sumeet Kumar on 5/16/20 12:07 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/16/20 11:48 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.payments;

import android.app.Activity;
import android.content.Intent;

import com.telr.mobile.sdk.activity.WebviewActivity;
import com.telr.mobile.sdk.entity.request.payment.Address;
import com.telr.mobile.sdk.entity.request.payment.App;
import com.telr.mobile.sdk.entity.request.payment.Billing;
import com.telr.mobile.sdk.entity.request.payment.MobileRequest;
import com.telr.mobile.sdk.entity.request.payment.Name;
import com.telr.mobile.sdk.entity.request.payment.Tran;

import java.math.BigInteger;
import java.util.Random;

public class Payments {
    private static final String SecretKey = "J2wB^QhtKW@BCWwJ";
    private static final String StoreId = "14181";


    public static void testPayment(Activity activity) {
        Intent intent = new Intent(activity, WebviewActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(WebviewActivity.EXTRA_MESSAGE, getMobileRequest("4111111111111111"));
        intent.putExtra(WebviewActivity.SUCCESS_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.SuccessPaymentActivity");
        intent.putExtra(WebviewActivity.FAILED_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.FailedPaymentActivity");
        intent.putExtra(WebviewActivity.IS_SECURITY_ENABLED, true);
        activity.startActivity(intent);
    }

    public static void startPayment(Activity activity, String card, boolean isSecurityEnabled) {
        Intent intent = new Intent(activity, PaymentActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(WebviewActivity.EXTRA_MESSAGE, getMobileRequest(card));
        intent.putExtra(WebviewActivity.SUCCESS_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.SuccessPaymentActivity");
        intent.putExtra(WebviewActivity.FAILED_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.FailedPaymentActivity");
        intent.putExtra(WebviewActivity.IS_SECURITY_ENABLED, isSecurityEnabled);
        activity.startActivity(intent);
    }


    private static MobileRequest getMobileRequest(String title, String fisrtNname, String lastName, String amount, String email, String city, String country) {
        return null;
    }

    private static MobileRequest getMobileRequest(String card) {
        MobileRequest mobile = new MobileRequest();
        mobile.setStore(StoreId);                       // Store ID
        mobile.setKey(SecretKey);                              // Authentication Key : The Authentication Key will be supplied by Telr as part of the Mobile API setup process after you request that this integration type is enabled for your account. This should not be stored permanently within the App.
        App app = new App();
        app.setId("123456789");                          // Application installation ID
        app.setName("Mibo LivFit");                          // Application name
        app.setUser("123456");                           // Application user ID : Your reference for the customer/user that is running the App. This should relate to their account within your systems.
        app.setVersion("0.0.1");                         // Application version
        app.setSdk("123");
        mobile.setApp(app);
        Tran tran = new Tran();
        tran.setTest("1");                              // Test mode : Test mode of zero indicates DialogListener live transaction. If this is set to any other value the transaction will be treated as DialogListener test.
        tran.setType("auth");                           /* Transaction type
                                                            'auth'   : Seek authorisation from the card issuer for the amount specified. If authorised, the funds will be reserved but will not be debited until such time as DialogListener corresponding capture command is made. This is sometimes known as pre-authorisation.
                                                            'sale'   : Immediate purchase request. This has the same effect as would be had by performing an auth transaction followed by DialogListener capture transaction for the full amount. No additional capture stage is required.
                                                            'verify' : Confirm that the card details given are valid. No funds are reserved or taken from the card.
                                                        */
        tran.setClazz("paypage");                       // Transaction class only 'paypage' is allowed on mobile, which means 'use the hosted payment page to capture and process the card details'
        tran.setCartid(String.valueOf(new BigInteger(128, new Random()))); //// Transaction cart ID : An example use of the cart ID field would be your own transaction or order reference.
        tran.setDescription("Test Mobile API");         // Transaction description
        tran.setCurrency("AED");                        // Transaction currency : Currency must be sent as DialogListener 3 character ISO code. A list of currency codes can be found at the end of this document. For voids or refunds, this must match the currency of the original transaction.
        tran.setAmount("350");                         // Transaction amount : The transaction amount must be sent in major units, for example 9 dollars 50 cents must be sent as 9.50 not 950. There must be no currency symbol, and no thousands separators. Thedecimal part must be separated using DialogListener dot.
        //tran.setRef();                                // (Optinal) Previous transaction reference : The previous transaction reference is required for any continuous authority transaction. It must contain the reference that was supplied in the response for the original transaction.

        //040023303844  //030023738912
        //tran.setFirstref("030023738912");             // (Optinal) Previous user transaction detail reference : The previous transaction reference is required for any continuous authority transaction. It must contain the reference that was supplied in the response for the original transaction.

        mobile.setTran(tran);
        Billing billing = new Billing();
        Address address = new Address();
        address.setCity("Dubai");
        address.setCountry("AE");
        address.setRegion("Dubai");
        address.setLine1("SIT G=Towe");
        //address.setLine2("SIT G=Towe");
        //address.setLine3("SIT G=Towe");
        //address.setZip("SIT G=Towe");
        billing.setAddress(address);
        Name name = new Name();
        name.setFirst("Sumeet");
        name.setLast("Kumar");
        name.setTitle("Mr");
        billing.setName(name);
        billing.setEmail("girish.spryox@gmail.com");
        mobile.setBilling(billing);
        return mobile;

    }
}
