/*
 *  Created by Sumeet Kumar on 5/9/20 11:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/9/20 11:05 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.social;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SocialHelper {

    public static int GOOGLE_REQUEST_CODE = 101;
    public static int GOOGLE = 200;
    public static int FACEBOOK = 100;
    public static int TWITTER = 300;
    private Facebook facebook;
    private Google google;
    private LinkedIn linkedIn;
    private Twitter twitter;
    private Listener listener;

    public interface Listener {
        void onResponse(int type, Bundle response, boolean error);
    }

    public static void install(Application context) {
        FacebookSdk.sdkInitialize(context);
        AppEventsLogger.activateApp(context);
    }

    public SocialHelper(Listener listener) {
        this.listener = listener;
    }

    public void facebookLogin(Context context) {
        if (facebook == null)
            facebook = new Facebook(context, listener);
        facebook.login();
    }

    public void googleLogin(Context context) {
        if (google == null)
            google = new Google(context, listener);
        google.login();
    }

    public void withTwitter(Context context) {
        if (twitter == null)
            twitter = new Twitter(context, (response, error) -> {
                //if (listener != null)
                //   listener.onResponse(300, response, error);
            });
        twitter.login();
    }


    public void onPause(FragmentActivity activity) {
        if (facebook != null)
            facebook.onPause(activity);
        if (google != null)
            google.onPause(activity);
        //if (twitter != null)
        //    twitter.onPause(activity);
    }

    public void onResume(FragmentActivity activity) {
        if (google != null)
            google.onResume(activity);
        //if (twitter != null)
        //    twitter.onPause(activity);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (facebook != null)
            facebook.activityResult(requestCode, resultCode, data);
        if (google != null)
            google.activityResult(requestCode, resultCode, data);
        if (twitter != null)
            twitter.activityResult(requestCode, resultCode, data);
        if (linkedIn != null)
            linkedIn.activityResult(requestCode, resultCode, data);
    }
}
