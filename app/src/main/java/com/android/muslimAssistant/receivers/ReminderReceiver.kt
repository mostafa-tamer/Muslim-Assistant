package com.android.muslimAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.R
import com.android.muslimAssistant.notifications.cancelNotificationPendingIntent
import com.android.muslimAssistant.notifications.mainActivityPendingIntent
import com.android.muslimAssistant.notifications.pushNotification
import com.android.muslimAssistant.utils.requestCodeGenerator
import com.android.muslimAssistant.utils.updateLanguage

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        updateLanguage(context)

        val reminderString = intent.getStringExtra("value")
        val reminderTitle = context.getString(R.string.reminder)

        pushNotification(
            context,
            NotificationCompat.Builder(context, ChannelIDs.PRIORITY_MAX.ID)
                .setContentTitle(reminderTitle)
                .setSmallIcon(R.drawable.pray)
                .setContentText(reminderString)
                .setContentIntent(mainActivityPendingIntent(context))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(
                    R.drawable.pray,
                    context.getString(R.string.clear_notifications),
                    cancelNotificationPendingIntent(context)
                ),
            requestCodeGenerator()
        )
//
//        NotificationHelper(
//            context,
//            ChannelIDs.PRIORITY_MAX.ID,
//            requestCodeGenerator(),
//            context.getString(R.string.reminder),
//            intent.getStringExtra("value").toString()
//        ).startNotification()
    }
}