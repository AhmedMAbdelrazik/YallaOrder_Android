package com.isoft.yallaorder.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.isoft.yallaorder.R


class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                makeNotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT
                )
            }

            val builder = NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.notification!!.title)
                .setContentText(remoteMessage.notification!!.body)
                .setPriority(remoteMessage.notification!!.notificationPriority!!)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(remoteMessage.notification!!.tag,101,builder.build())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int
    ) {
        val channel = NotificationChannel(
            channelId, channelName, importance
        )
        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(
            channel
        )
    }

    override fun onNewToken(token: String) {

    }
}