/*
 *  Created by Sumeet Kumar on 5/16/20 12:05 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/16/20 12:05 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.payments;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.telr.mobile.sdk.activity.WebviewActivity;
import com.telr.mobile.sdk.entity.response.status.Auth;
import com.telr.mobile.sdk.entity.response.status.StatusResponse;

import org.jetbrains.annotations.NotNull;

import life.mibo.android.R;
import life.mibo.android.core.API;
import life.mibo.android.core.Prefs;
import life.mibo.android.models.base.ResponseStatus;
import life.mibo.android.models.catalog.SaveOrderDetails;
import life.mibo.android.models.login.Member;
import life.mibo.android.ui.base.BaseActivity;
import life.mibo.android.ui.catalog.CartItem;
import life.mibo.android.ui.dialog.MyDialog;
import life.mibo.android.ui.main.MiboEvent;
import life.mibo.android.utils.Toasty;
import life.mibo.hardware.core.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuccessPaymentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        Logger.e("Success PaymentActivity ", getIntent().getExtras());

        try {
            getResponse();
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(this, e.getMessage(), Toasty.LENGTH_LONG).show();
        }


        try {
            save();
            findViewById(R.id.btn_close).setOnClickListener(v -> {
                finish();
            });
            MiboEvent.INSTANCE.event("PAYMENT_SUCCESS_ACTIVITY", "user purchased", "auth " + telrAuth);
            MiboEvent.INSTANCE.fbLog(this, "SuccessPaymentActivity ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Auth telrAuth;
    private void getResponse() {

        StatusResponse status = (StatusResponse) getIntent().getParcelableExtra(WebviewActivity.PAYMENT_RESPONSE);
       // TextView textView = (TextView) findViewById(R.id.textViewInfo0);
       // textView.setText(textView.getText() + " : " + status.getTrace());

        telrAuth = status.auth;
//        if (status.getAuth() != null) {
//            status.getAuth().getStatus();   // Authorisation status. A indicates an authorised transaction. H also indicates an authorised transaction, but where the transaction has been placed on hold. Any other value indicates that the request could not be processed.
//            status.getAuth().getAvs();      /* Result of the AVS check:
//                                            Y = AVS matched OK
//                                            P = Partial match (for example, post-code only)
//                                            N = AVS not matched
//                                            X = AVS not checked
//                                            E = Error, unable to check AVS */
//            status.getAuth().getCode();     // If the transaction was authorised, this contains the authorisation code from the card issuer. Otherwise it contains a code indicating why the transaction could not be processed.
//            status.getAuth().getMessage();  // The authorisation or processing error message.
//            status.getAuth().getCa_valid();
//            status.getAuth().getCardcode(); // Code to indicate the card type used in the transaction. See the code list at the end of the document for a list of card codes.
//            status.getAuth().getCardlast4();// The last 4 digits of the card number used in the transaction. This is supplied for all payment types (including the Hosted Payment Page method) except for PayPal.
//            status.getAuth().getCvv();      /* Result of the CVV check:
//                                           Y = CVV matched OK
//                                           N = CVV not matched
//                                           X = CVV not checked
//                                           E = Error, unable to check CVV */
//            status.getAuth().getTranref(); //The payment gateway transaction reference allocated to this request.
//            log("hany : " + status.getAuth().getTranref());
//            status.getAuth().getCardfirst6(); // The first 6 digits of the card number used in the transaction, only for version 2 is submitted in Tran -> Version
//
//            //setTransactionDetails(status.getAuth().getTranref(), status.getAuth().getCardlast4());
//        }
    }

    private MyDialog myDialog() {
        MyDialog dialog = getDialog();
        if (dialog == null) {

        }

        return dialog;
    }

    boolean debug = true;

    private void save() {

        //Prefs.getTemp(SuccessPaymentActivity.this).remove("cart_item");
        Member member = Prefs.get(this).getMember();
        log("save " + member);
        if (member == null)
            return;
        CartItem cartItem = Prefs.get(this).getJson(Prefs.CART_ITEM, CartItem.class);
        log(Prefs.CART_ITEM + " - " + cartItem);
        Prefs.getTemp(this).setJson(Prefs.CART_ITEM, cartItem);
        Prefs.getTemp(SuccessPaymentActivity.this).set("cart_item_failed", "1");
        String adviceNumber = cartItem.getAdviceNumber();

        StringBuilder logs = new StringBuilder();
        String transactionId = "";
        String paidStatus = "";
        if (telrAuth != null) {
            try {
//                log(" StatusResponse auth9: " + telrAuth.avs);
//                log(" StatusResponse auth8: " + telrAuth.ca_valid);
//                log(" StatusResponse auth7: " + telrAuth.cardcode);
//                log(" StatusResponse auth6: " + telrAuth.cardfirst6);
//                log(" StatusResponse auth5: " + telrAuth.cardlast4);
//                log(" StatusResponse auth4: " + telrAuth.code);
//                log(" StatusResponse auth3: " + telrAuth.message);
//                log(" StatusResponse auth2: " + telrAuth.status);
//                log(" StatusResponse auth1: " + telrAuth.tranref);
                transactionId = telrAuth.tranref;
                //paidStatus = telrAuth.status;
                paidStatus = telrAuth.message;
                logs.append(telrAuth.message).append("; ");
                logs.append(telrAuth.status).append("; ");
                logs.append(telrAuth.tranref).append("; ");
                logs.append(telrAuth.code).append("; ");
                logs.append(telrAuth.cardfirst6).append("; ");
                logs.append(telrAuth.cardlast4).append("; ");
                logs.append(telrAuth.cardcode).append("; ");
                logs.append(telrAuth.ca_valid).append("; ");
                logs.append(telrAuth.avs).append("; ");
            } catch (Exception ee) {
                logs.append(" error=").append(ee.getMessage());

            }
        }

        logs.append(" (promo==").append(cartItem.getPromo()).append("amount").append(cartItem.getTotalAmount()).append(")");

        MiboEvent.INSTANCE.fbPurchase(this, cartItem.getBillable(), cartItem.getCurrencyType());
        String type = "product";
        if (cartItem.isService())
            type = "service";
        else if (cartItem.isPackage())
            type = "package";

        SaveOrderDetails.Data data = new SaveOrderDetails.Data(logs.toString(), cartItem.getServiceLocationId(), member.getId(),
                cartItem.getId(), cartItem.getTotalAmount(), cartItem.getQuantity(), cartItem.getLocationId(), transactionId, type, cartItem.getVat(), cartItem.getCurrencyType(), paidStatus, adviceNumber, cartItem.getStartDate(), cartItem.getEndDate(), cartItem.getPromoCode());

        log("SaveOrderDetails " + data);

        // if (debug)
        //    return;
        myDialog().show();
        API.Companion.getRequest().getApi().saveOrderDetails(new SaveOrderDetails(data, member.getAccessToken())).enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(@NotNull Call<ResponseStatus> call, @NotNull Response<ResponseStatus> response) {
                ResponseStatus body = response.body();
               // log("onResponse " + body);
                if (body != null && body.isSuccess()) {
                    // log("onResponse isSuccess");
                    Prefs.getTemp(SuccessPaymentActivity.this).remove(Prefs.CART_ITEM);
                    Prefs.getTemp(SuccessPaymentActivity.this).set(Prefs.CART_ITEM, "");
                    MiboEvent.INSTANCE.event("PAYMENT_API_SUCCESS", "response", body + "" + telrAuth);
                } else {
                    MiboEvent.INSTANCE.event("PAYMENT_API_ERROR", "response", body + "" + telrAuth);
                }
                myDialog().dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<ResponseStatus> call, @NotNull Throwable t) {
                myDialog().dismiss();
                t.printStackTrace();
                //log("onFailure " + t.getMessage());
                Prefs.getTemp(SuccessPaymentActivity.this).set("cart_item_failed", "7");
                MiboEvent.INSTANCE.event("PAYMENT_API_FAILED", "error", t.getMessage() + "" + telrAuth);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
