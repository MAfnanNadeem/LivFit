/*
 *  Created by Sumeet Kumar on 5/11/20 2:31 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/11/20 2:31 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.services;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import life.mibo.hardware.core.Logger;

public class FirebaseService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Logger.e("FirebaseService", "onNewToken ");

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("FirebaseService", "onCreate ");

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Logger.e("FirebaseService", "onMessageReceived " + remoteMessage);
        if (remoteMessage != null) {
            Logger.e("FirebaseService", "onMessageReceived2 " + remoteMessage.getData());
            Logger.e("FirebaseService", "onMessageReceived3 " + remoteMessage.getNotification());
        }
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
