package com.example.fridgeguardian

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log

class MessageReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            sendNotification(it.title!!, it.body!!)
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "channel_id"
        val channelName = "유통기한 알림"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)  // 알림 아이콘 설정
            .setContentTitle(title)  // 알림 제목 설정
            .setContentText(messageBody)  // 알림 내용 설정
            .setAutoCancel(true)  // 알림을 클릭하면 자동으로 알림이 사라지도록 설정

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}
