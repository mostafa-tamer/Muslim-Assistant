package com.android.muslimAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.R
import com.android.muslimAssistant.notifications.NotificationHelper
import com.android.muslimAssistant.utils.requestCodeGenerator

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper(
            context,
            ChannelIDs.PRIORITY_MAX.ID,
            requestCodeGenerator(),
            context.getString(R.string.reminder),
            intent.getStringExtra("value").toString()
        ).startNotification()
    }
}