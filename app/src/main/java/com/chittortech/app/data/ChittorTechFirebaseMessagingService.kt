package com.chittortech.app.data

import com.chittortech.app.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ChittorTechFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Automatically sync topic subscriptions when FCM generates or refreshes the token
        FcmNotificationManager.syncCurrentSession(applicationContext)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "ChittorTech Enterprise"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: "You have a new update."

        val msgId = remoteMessage.messageId ?: "fcm_${title}_${body}".hashCode().toString()

        // Delegate to NotificationHelper with persistent deduplication
        NotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            message = body,
            dedupeKey = "fcm_msg_$msgId"
        )
    }
}
