package com.example.muslimsAssistant.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
@RequiresApi(Build.VERSION_CODES.O)
class ChannelHelper constructor(
    private val context: Context,
    private val channelID: String,
    private val channelName: String,
) {
    init {
        createChannel()
    }


    private fun createChannel() {
        val channel =
            NotificationChannel(
                channelID, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

        channel.setShowBadge(true)

        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}