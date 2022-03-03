package com.voinismartiot.voni.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        showNotificationWithFullScreenIntent(
            title = remoteMessage.notification?.title ?: "",
            description = remoteMessage.notification?.body ?: ""
        )
    }

}