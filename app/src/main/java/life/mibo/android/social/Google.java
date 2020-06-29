/*
 *  Created by Sumeet Kumar on 5/9/20 10:01 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/9/20 10:01 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import life.mibo.android.R;
import life.mibo.hardware.core.Logger;

public class Google implements GoogleApiClient.OnConnectionFailedListener {
    private Context mContext;
    private SocialHelper.Listener listener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        generateError("" + connectionResult.getErrorMessage());
    }


    public Google(Context mContext, SocialHelper.Listener listener) {
        this.mContext = mContext;
        this.listener = listener;

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(((FragmentActivity) mContext), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public void connect() {
        try {
            mGoogleApiClient.connect();
            Logger.e("Google Connected ");
        } catch (Exception eee) {
            eee.printStackTrace();
            Logger.e("Google Connected error "+eee);
        }
    }

    public void login() {

        if (mGoogleApiClient != null) {
            //logout();
            if (mGoogleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                    Logger.e("Google sign out success " + status);
                });
            }
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            if (mContext instanceof Activity) {
                ((Activity) mContext).startActivityForResult(signInIntent, SocialHelper.GOOGLE_REQUEST_CODE);
            } else {
                generateError(mContext.getString(R.string.context_error));
            }
        } else {
            generateError(mContext.getString(R.string.general_error));
        }
    }


    public void onPause(FragmentActivity activity) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(activity);
            mGoogleApiClient.disconnect();
        }
    }

    public void onResume(FragmentActivity activity) {
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }


    public void logout() {
        Logger.e("Google logout called....... ");
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                Logger.e("Google logout connected success ");
                //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                Single.just("").subscribeOn(Schedulers.newThread()).doOnError(throwable -> {

                }).doOnSuccess(s -> {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                        Logger.e("Google sign out success " + status);
                    });
                }).subscribe();
            } else {
                Logger.e("Google logout not connected success ");
                Single.just("").subscribeOn(Schedulers.newThread()).doOnError(throwable -> {

                }).doOnSuccess(s -> {
                    mGoogleApiClient.connect();
                    Single.just("").delay(4, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).doOnError(throwable -> {
                        Logger.e("Google logout not connected.." );
                    }).doOnSuccess(s2 -> {
                        Logger.e("Google logout not connected.... " +s2);

                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                                Logger.e("Google sign out success " + status);
                            });
                        }
                    }).subscribe();
                }).subscribe();


            }


        } else {
            generateError(mContext.getString(R.string.general_error));
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        Logger.e("Google activityResult ");
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Logger.e("Google activityResult " + result);
        Logger.e("GoogleSignIn", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(mContext, "" + acct.getDisplayName(), Toast.LENGTH_SHORT).show();
            //JSONObject jsonObject = new JSONObject();
            Bundle bundle = new Bundle();
            Logger.e("GoogleSignIn", "GoogleSignInAccount:" + acct);
            try {
                bundle.putString("id", acct.getId());
                bundle.putString("displayName", acct.getDisplayName());
                bundle.putString("email", acct.getEmail());
                bundle.putString("familyName", acct.getFamilyName());
                bundle.putString("givenName", acct.getGivenName());
                bundle.putString("idToken", acct.getIdToken());
                bundle.putString("photoUrl", acct.getPhotoUrl().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            listener.onResponse(SocialHelper.GOOGLE, bundle, false);
        }

    }

    private void generateError(String msg) {

        Bundle bundle = new Bundle();
        bundle.putString("message", msg);
        listener.onResponse(SocialHelper.GOOGLE, bundle, true);
    }
}