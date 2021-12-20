package com.voinismartiot.voni.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var logTag = MyFirebaseMessagingService::class.java.simpleName

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(logTag, " onNewToken $token ")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(
            logTag,
            "FirebaseMessagingService: remoteMessage Body ${remoteMessage.notification?.body}"
        )

        showNotificationWithFullScreenIntent(
            title = remoteMessage.notification?.title ?: "",
            description = remoteMessage.notification?.body ?: ""
        )
    }

}