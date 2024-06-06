package com.caucapstone.peeper.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.caucapstone.peeper.R

class BluetoothService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1234, getServiceNotification())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getServiceNotification(): Notification {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "RunningBackground",
                "Running at Background",
                NotificationManager.IMPORTANCE_MIN
            )
            channel.description = "Running at Background"
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(applicationContext, "RunningBackground")
            .setAutoCancel(false)
            .setContentText("Service is Running")
            .setContentTitle("PEEPER")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
}
