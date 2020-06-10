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

import androidx.fragment.app.FragmentActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import life.mibo.android.R;

public class Facebook {

    private CallbackManager callbackManager;
    private Context mContext;
    private SocialHelper.Listener listener;
    private LoginManager loginManager;

    public void onPause(FragmentActivity activity) {
    }


    public Facebook(Context context, SocialHelper.Listener l) {
        this.mContext = context;
        callbackManager = CallbackManager.Factory.create();
        listener = l;
        loginManager = LoginManager.getInstance();
    }

    public void login() {

        if (loginManager != null) {
            //Mark: Set permissions
            loginManager.logInWithReadPermissions((Activity) mContext,
                    Arrays.asList("public_profile", "email"));
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    getFacebookData(loginResult);
                }

                @Override
                public void onCancel() {
                    generateError(mContext.getString(R.string.facebook_request_cancel));
                }

                @Override
                public void onError(FacebookException error) {
                    generateError(error.getLocalizedMessage());
                }
            });
        } else {
            generateError(mContext.getString(R.string.facebook_login_error));
        }

    }

    public void logout() {
        if (loginManager != null) {
            loginManager.logOut();
        } else {
            generateError(mContext.getString(R.string.facebook_logout_error));
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            generateError(mContext.getString(R.string.general_error));
        }
    }

    private void getFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
            Bundle bundle = new Bundle();
            try {
                bundle.putString("raw_data", object.toString());
                Iterator iter = object.keys();
                while (iter.hasNext()) {
                    //Object obj = iter.next();
                    String key = (String) iter.next();
                    String value = object.getString(key);
                    bundle.putString(key, value);
                }
            }
            catch (Exception e){

            }
            listener.onResponse(SocialHelper.FACEBOOK, bundle, false);
        });
        //Here we put the requested fields to be returned from the JSONObject
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void generateError(String msg) {

        Bundle bundle = new Bundle();
        bundle.putString("message", msg);
        listener.onResponse(SocialHelper.FACEBOOK, bundle, true);
    }
}
