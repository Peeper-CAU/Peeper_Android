package com.caucapstone.peeper.Service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.caucapstone.peeper.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCMService", "Message data payload: ${remoteMessage.data}")
            showNotification(
                remoteMessage.data["title"].toString(),
                remoteMessage.data["body"].toString()
            )
        } else {
            remoteMessage.notification?.let {
                showNotification(
                    remoteMessage.notification!!.title.toString(),
                    remoteMessage.notification!!.body.toString()
                )
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "PeeperNotification",
                "PEEPER Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "PEEPER Notification"
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "PeeperNotification")
            .setAutoCancel(false)
            .setContentText(body)
            .setContentTitle(title)
            .setOngoing(false)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.mipmap.ic_launcher)

        notificationManager.notify(0, notificationBuilder.build())
    }
}