/*
 *  Created by Sumeet Kumar on 5/16/20 11:41 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/16/20 11:41 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.payments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Keep;

import com.telr.mobile.sdk.activity.WebviewActivity;
import com.telr.mobile.sdk.entity.request.payment.Address;
import com.telr.mobile.sdk.entity.request.payment.App;
import com.telr.mobile.sdk.entity.request.payment.Billing;
import com.telr.mobile.sdk.entity.request.payment.MobileRequest;
import com.telr.mobile.sdk.entity.request.payment.Name;
import com.telr.mobile.sdk.entity.request.payment.Tran;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import life.mibo.android.R;
import life.mibo.android.ui.base.BaseActivity;
import life.mibo.hardware.core.Logger;

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

    public static class PaymentData {
        private String successActivity = "life.mibo.android.ui.payments.SuccessPaymentActivity";
        private String failedActivity = "life.mibo.android.ui.payments.FailedPaymentActivity";
        private String mr = "Mr";

        private String address1;
        private String address2;
        private String appUid = "Mr";
        private String transactionId = "";
        private String email;
        private Double amount;
        private String userId;
        private String fName;
        private String lName;
        private String currency;
        private String city;
        private String region;
        // private String country;
        private String countryCode;
        private String type = "paypage";
        //private String auth = "auth";
        private String auth = "sale";
        private String mode = "1";
        private String appName = "MI.BO LivFit";
        private String appSdk = "3.5";
        private String appVersion = "";
        //private String desc = "MI.BO LivFit Android App";
        private String desc = "";

        public PaymentData(String userId, String title, String fName, String lName, String email, String currency, Double amount, String address1, String city, String region, String countryCode, String appVersion, String appId, String desc) {
            this.address1 = address1;
            this.email = email;
            this.amount = amount;
            this.userId = userId;
            this.fName = fName;
            this.lName = lName;
            this.currency = currency;
            this.city = city;
            this.region = region;
            this.appUid = appId;
            this.mr = title;
            // this.country = country;
            this.countryCode = countryCode;
            this.desc = "Android Purchased {" + desc + "}";
            String id;
            try {
                id = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
            } catch (Exception e) {
                id = userId + "000000";
            }
            this.transactionId = id + userId;
            this.appVersion = appVersion;
        }

        public void pay(Activity activity) {

        }

        @Override
        public String toString() {
            return "PaymentData{" +
                    "successActivity='" + successActivity + '\'' +
                    ", failedActivity='" + failedActivity + '\'' +
                    ", mr='" + mr + '\'' +
                    ", address1='" + address1 + '\'' +
                    ", address2='" + address2 + '\'' +
                    ", appUid='" + appUid + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                    ", email='" + email + '\'' +
                    ", amount=" + amount +
                    ", userId='" + userId + '\'' +
                    ", fName='" + fName + '\'' +
                    ", lName='" + lName + '\'' +
                    ", currency='" + currency + '\'' +
                    ", city='" + city + '\'' +
                    ", region='" + region + '\'' +
                    ", countryCode='" + countryCode + '\'' +
                    ", type='" + type + '\'' +
                    ", auth='" + auth + '\'' +
                    ", mode='" + mode + '\'' +
                    ", appName='" + appName + '\'' +
                    ", appSdk='" + appSdk + '\'' +
                    ", appVersion='" + appVersion + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }

    public static void payNow(Activity activity, PaymentData data) {
        //String uid = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        Logger.ee("payNow :: " + data);


        MobileRequest mobile = new MobileRequest();
        mobile.setStore(StoreId);                       // Store ID
        mobile.setKey(SecretKey);                              // Authentication Key : The Authentication Key will be supplied by Telr as part of the Mobile API setup process after you request that this integration type is enabled for your account. This should not be stored permanently within the App.

        App app = new App();
        app.setId(data.appUid);                          // Application installation ID
        app.setName(data.appName);                          // Application name
        app.setUser(data.userId);                           // Application user ID : Your reference for the customer/user that is running the App. This should relate to their account within your systems.
        app.setVersion(data.appVersion);                         // Application version
        app.setSdk(data.appSdk);

        mobile.setApp(app);

        Tran tran = new Tran();
        tran.setTest(data.mode);                              // Test mode : Test mode of zero indicates a live transaction. If this is set to any other value the transaction will be treated as a test.
        tran.setType(data.auth);                           /* Transaction type
                                                            'auth'   : Seek authorisation from the card issuer for the amount specified. If authorised, the funds will be reserved but will not be debited until such time as a corresponding capture command is made. This is sometimes known as pre-authorisation.
                                                            'sale'   : Immediate purchase request. This has the same effect as would be had by performing an auth transaction followed by a capture transaction for the full amount. No additional capture stage is required.
                                                            'verify' : Confirm that the card details given are valid. No funds are reserved or taken from the card.
                                                        */
        tran.setClazz(data.type);                       // Transaction class only 'paypage' is allowed on mobile, which means 'use the hosted payment page to capture and process the card details'
        //tran.setCartid(String.valueOf(new BigInteger(128, new Random()))); //// Transaction cart ID : An example use of the cart ID field would be your own transaction or order reference.
        tran.setCartid(data.transactionId);
        tran.setDescription(data.desc);         // Transaction description
        tran.setCurrency(data.currency);                        // Transaction currency : Currency must be sent as a 3 character ISO code. A list of currency codes can be found at the end of this document. For voids or refunds, this must match the currency of the original transaction.
        tran.setAmount("" + data.amount);                         // Transaction amount : The transaction amount must be sent in major units, for example 9 dollars 50 cents must be sent as 9.50 not 950. There must be no currency symbol, and no thousands separators. The decimal part must be separated using a dot.
        //tran.setRef();                                // (Optinal) Previous transaction reference : The previous transaction reference is required for any continuous authority transaction. It must contain the reference that was supplied in the response for the original transaction.

        //040023303844  //030023738912
        //tran.setFirstref("030023738912");             // (Optinal) Previous user transaction detail reference : The previous transaction reference is required for any continuous authority transaction. It must contain the reference that was supplied in the response for the original transaction.

        mobile.setTran(tran);

        Billing billing = new Billing();
        Address address = new Address();
        address.setCity(data.city);                       // City : the minimum required details for a transaction to be processed
        address.setCountry(data.countryCode);                       // Country : Country must be sent as a 2 character ISO code. A list of country codes can be found at the end of this document. the minimum required details for a transaction to be processed
        address.setRegion(data.city);                     // Region
        address.setLine1(data.address1);

        if (data.address2 != null)// Street address – line 1: the minimum required details for a transaction to be processed
            address.setLine2(data.address2);
        //address.setLine2("SIT G=Towe");               // (Optinal)
        //address.setLine3("SIT G=Towe");               // (Optinal)
        //address.setZip("SIT G=Towe");                 // (Optinal)
        billing.setAddress(address);

        Name name = new Name();
        name.setFirst(data.fName);                          // Forename : the minimum required details for a transaction to be processed
        name.setLast(data.lName);                          // Surname : the minimum required details for a transaction to be processed
        name.setTitle("");                           // Title
        name.setTitle(data.mr);                           // Title

        billing.setName(name);
        billing.setEmail(data.email); //the minimum required details for a transaction to be processed.
        mobile.setBilling(billing);


        Intent intent = new Intent(activity, WebviewActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(WebviewActivity.EXTRA_MESSAGE, mobile);
        intent.putExtra(WebviewActivity.SUCCESS_ACTIVTY_CLASS_NAME, data.successActivity);
        intent.putExtra(WebviewActivity.FAILED_ACTIVTY_CLASS_NAME, data.failedActivity);
        intent.putExtra(WebviewActivity.IS_SECURITY_ENABLED, isSecurityEnabled);
        activity.startActivity(intent);
        Logger.ee("payNow :: launched");
    }

    public static void payNow(Activity activity, String amount, String userId) {
        String uid = new SimpleDateFormat("yyMMddHHmmss").format(new Date());

        MobileRequest mobile = new MobileRequest();
        mobile.setStore(StoreId);                       // Store ID
        mobile.setKey(SecretKey);                              // Authentication Key : The Authentication Key will be supplied by Telr as part of the Mobile API setup process after you request that this integration type is enabled for your account. This should not be stored permanently within the App.
        App app = new App();
        app.setId(uid + userId);                          // Application installation ID
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
        name.setFirst("Sumeet");                          // Forename : the minimum required details for a transaction to be processed
        name.setLast("Kumar");                          // Surname : the minimum required details for a transaction to be processed
        name.setTitle("Mr");                           // Title
        billing.setName(name);
        billing.setEmail("sumeetkumar@gmail.com"); //stackfortytwo@gmail.com : the minimum required details for a transaction to be processed.
        mobile.setBilling(billing);


        Intent intent = new Intent(activity, WebviewActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(WebviewActivity.EXTRA_MESSAGE, mobile);
        intent.putExtra(WebviewActivity.SUCCESS_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.SuccessPaymentActivity");
        intent.putExtra(WebviewActivity.FAILED_ACTIVTY_CLASS_NAME, "life.mibo.android.ui.payments.FailedPaymentActivity");
        intent.putExtra(WebviewActivity.IS_SECURITY_ENABLED, isSecurityEnabled);
        activity.startActivity(intent);
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