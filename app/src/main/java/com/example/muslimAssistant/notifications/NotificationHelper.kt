package com.example.muslimAssistant.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.muslimAssistant.MainActivity
import com.example.muslimAssistant.R
import com.example.muslimAssistant.receivers.ActionReceiver

class NotificationHelper(
    private val context: Context,
    private val channelID: String,
    private val notificationID: Int,
    private val notificationTitle: String,
    private val notificationText: String
) {

    private fun setupActionIntent(): PendingIntent {

        val intent = Intent(context, ActionReceiver::class.java)

        return PendingIntent.getBroadcast(
            context,
            PendingIntentCodes.CLEAR_NOTIFICATIONS_ACTION.code,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun setupClickToBackToApplicationIntent(): PendingIntent {

        val intent = Intent(context, MainActivity::class.java)

        return PendingIntent.getActivity(
            context,
            PendingIntentCodes.SET_CONTENT_INTENT.code,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun notificationCreator(): Notification {

        return NotificationCompat.Builder(context, channelID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.pray)
            .setContentIntent(setupClickToBackToApplicationIntent())
            .setAutoCancel(true)
//            .addAction(
//                R.drawable.pray,
//                "Clear Notifications",
//                setupActionIntent()
//            )
            .build()
    }

    fun startNotification() {

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(notificationID, notificationCreator())
    }
}