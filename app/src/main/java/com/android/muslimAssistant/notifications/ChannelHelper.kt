package com.android.muslimAssistant.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context

class ChannelHelper(
    private val context: Context,
    private val channelID: String,
    private val channelName: String,
    private val priority: Int
) {
    init {
        createChannel()
    }

    private fun createChannel() {
        val channel =
            NotificationChannel(
                channelID,
                channelName,
                priority,
            )

        channel.setShowBadge(true)

        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}