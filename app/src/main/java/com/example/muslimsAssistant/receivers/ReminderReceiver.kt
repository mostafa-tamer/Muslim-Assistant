package com.example.muslimsAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.muslimsAssistant.ChannelIDs
import com.example.muslimsAssistant.NotificationCodes
import com.example.muslimsAssistant.notifications.NotificationHelper

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper(
            context,
            ChannelIDs.PRIORITY_MAX.ID,
            System.currentTimeMillis().toInt(),
            "تذكير",
            intent.getStringExtra("value").toString()
        ).startNotification()
    }
}