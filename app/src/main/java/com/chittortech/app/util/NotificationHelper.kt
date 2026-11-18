package com.chittortech.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.chittortech.app.MainActivity
import com.chittortech.app.R

object NotificationHelper {

    const val CHANNEL_ID = "chittortech_realtime_alerts"
    const val CHANNEL_NAME = "ChittorTech Alerts & Activity"
    const val CHANNEL_DESC = "Real-time updates for invoices, tickets, leads, and server deployments"

    private const val PREFS_NAME = "chittortech_notif_history"

    private val inMemoryDedupe = java.util.concurrent.ConcurrentHashMap<String, Long>()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 100, 250)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Checks if a persistent notification key (like "ticket_TKT-123_RESOLVED") was already notified.
     */
    fun wasAlreadyNotified(context: Context, key: String): Boolean {
        if (key.isBlank()) return false
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.contains(key)
    }

    /**
     * Records that a persistent notification key was notified so it never repeats across launches.
     */
    fun markAsNotified(context: Context, key: String) {
        if (key.isBlank()) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(key, System.currentTimeMillis()).apply()
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        dedupeKey: String? = null,
        notificationId: Int? = null
    ) {
        val cleanTitle = title.trim()
        val cleanMsg = message.trim()
        val effectiveKey = dedupeKey?.trim()?.ifBlank { null } ?: "$cleanTitle|$cleanMsg"
        val now = System.currentTimeMillis()

        // 1. Persistent deduplication check: If this exact key was already dispatched, DROP it!
        if (dedupeKey != null && wasAlreadyNotified(context, effectiveKey)) {
            return
        }

        // 2. In-memory rapid-fire drop (within 10 seconds for identical alerts)
        val lastSeen = inMemoryDedupe[effectiveKey]
        if (lastSeen != null && (now - lastSeen) < 10_000L) {
            return
        }
        inMemoryDedupe[effectiveKey] = now

        // Persist so it never fires again across app restarts
        if (dedupeKey != null) {
            markAsNotified(context, effectiveKey)
        }

        // Housekeeping in-memory map
        if (inMemoryDedupe.size > 100) {
            val iterator = inMemoryDedupe.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (now - entry.value > 60_000L) {
                    iterator.remove()
                }
            }
        }

        createNotificationChannel(context)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val targetId = notificationId ?: (Math.abs(effectiveKey.hashCode()) % 100000)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            targetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(cleanTitle)
            .setContentText(cleanMsg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cleanMsg))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(targetId, notification)
    }
}
