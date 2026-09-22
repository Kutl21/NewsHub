package com.example.newshubapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.newshubapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging Service
 * Handles push notifications for breaking news
 * Reference: https://firebase.google.com/docs/cloud-messaging
 */
class NotificationService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "NotificationService"
        private const val CHANNEL_ID = "news_channel"
        private const val CHANNEL_NAME = "News Updates"
        private const val NOTIFICATION_ID = 1001
    }

    /**
     * Called when a new notification message is received
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message received: ${remoteMessage.from}")

        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notification: ${notification.title}")
            sendNotification(notification.title, notification.body)
        }
    }

    /**
     * Called when a new token is generated
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "New token: $token")
        // Send token to server if needed
    }

    /**
     * Send notification to user
     */
    private fun sendNotification(title: String?, body: String?) {
        // Create notification channel (Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Breaking news and updates"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title ?: "NewsHub")
            .setContentText(body ?: "New news available")
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}