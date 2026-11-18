package com.chittortech.app.data

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

/**
 * FcmNotificationManager
 * Manages Firebase Cloud Messaging (FCM) topic subscriptions to ensure that
 * push notifications for support tickets, invoice status updates, and announcements
 * are delivered directly to the Android system tray even when the app is closed or killed.
 */
object FcmNotificationManager {

    private const val TAG = "FcmNotificationManager"

    /**
     * Sanitizes an email address to conform to FCM topic format [a-zA-Z0-9-_.~%]+
     * Example: "visitchittorgarh@gmail.com" -> "visitchittorgarh_gmail.com"
     */
    fun sanitizeEmailForTopic(email: String): String {
        return email.trim().lowercase().replace(Regex("[^a-zA-Z0-9-_.~%]"), "_")
    }

    /**
     * Subscribes the device to the relevant FCM topics based on user identity and role.
     */
    fun subscribeToUserTopics(email: String?, role: String? = "client") {
        try {
            val fcm = FirebaseMessaging.getInstance()

            // 1. Global announcements topic
            fcm.subscribeToTopic("all_users").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully subscribed to FCM topic: all_users")
                } else {
                    Log.w(TAG, "Failed to subscribe to topic 'all_users'", task.exception)
                }
            }

            // 2. Role-specific topic
            val normalizedRole = role?.trim()?.lowercase() ?: "client"
            if (normalizedRole == "client") {
                fcm.subscribeToTopic("clients").addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Successfully subscribed to FCM topic: clients")
                    } else {
                        Log.w(TAG, "Failed to subscribe to topic 'clients'", task.exception)
                    }
                }
            } else if (normalizedRole == "guest") {
                fcm.subscribeToTopic("guests")
            }

            // 3. User-specific private topic (for support tickets, milestones, invoices)
            if (!email.isNullOrBlank()) {
                val sanitized = sanitizeEmailForTopic(email)
                val clientTopic = "client_$sanitized"
                fcm.subscribeToTopic(clientTopic).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Successfully subscribed to private FCM client topic: $clientTopic")
                    } else {
                        Log.w(TAG, "Failed to subscribe to topic '$clientTopic'", task.exception)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in subscribeToUserTopics", e)
        }
    }

    /**
     * Unsubscribes from user-specific private topics upon logout.
     */
    fun unsubscribeFromUserTopics(email: String?) {
        try {
            val fcm = FirebaseMessaging.getInstance()
            if (!email.isNullOrBlank()) {
                val sanitized = sanitizeEmailForTopic(email)
                val clientTopic = "client_$sanitized"
                fcm.unsubscribeFromTopic(clientTopic).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Unsubscribed from client topic: $clientTopic")
                    }
                }
            }
            fcm.unsubscribeFromTopic("clients")
        } catch (e: Exception) {
            Log.e(TAG, "Error in unsubscribeFromUserTopics", e)
        }
    }

    /**
     * Re-syncs subscriptions based on the active session stored in SessionManager.
     */
    fun syncCurrentSession(context: Context) {
        val result = SessionManager.validateSession(context)
        when (result) {
            is SessionValidationResult.Valid -> {
                Log.d(TAG, "Syncing FCM topics for logged in client: ${result.email}")
                subscribeToUserTopics(result.email, result.role)
            }
            else -> {
                Log.d(TAG, "No active client session. Subscribing to public 'all_users' topic.")
                try {
                    FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                } catch (e: Exception) {
                    Log.w(TAG, "Error subscribing to all_users", e)
                }
            }
        }
    }
}
