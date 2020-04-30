package com.example.lab6.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.lab6.R
import com.example.lab6.model.json.account.Singleton
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "FCM service"
    override fun onNewToken(p0: String) { }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
//        if (remoteMessage.data.isNullOrEmpty()){
            sendNotification(remoteMessage)
//        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val data: Map<String, String> = remoteMessage.data
        val title  = data.get("title")
        val content = data.get("content")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANEL_ID = "KinoPoisk"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANEL_ID,
                "KinoPoisk",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.description = "KinoPoisk channel for app test FCM"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANEL_ID)

        val remoteViews =
            RemoteViews(applicationContext.packageName, R.layout.notification)

        notificationBuilder
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_like)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomBigContentView(remoteViews)

        notificationManager.notify(1, notificationBuilder.build())
    }
}