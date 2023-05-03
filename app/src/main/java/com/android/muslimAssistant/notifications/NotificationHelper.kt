package com.android.muslimAssistant.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.muslimAssistant.PendingIntentCodes
import com.android.muslimAssistant.R
import com.android.muslimAssistant.activities.MainActivity
import com.android.muslimAssistant.receivers.NotificationActionReceiver
import com.android.muslimAssistant.repository.SharedPreferencesRepository
import com.android.muslimAssistant.utils.requestCodeGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class NotificationHelper(
    private val context: Context,
    private val channelID: String,
    private val notificationID: Int,
    private val notificationTitle: String = "",
    private val notificationText: String = ""
) {
    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    private fun setupClickToBackToApplicationIntent(): PendingIntent {
        return PendingIntent.getActivity(
            context,
            PendingIntentCodes.SET_CONTENT_INTENT.code,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun notificationCreator(): Notification {
        return NotificationCompat.Builder(context, channelID)
            .setContentTitle(notificationTitle)
            .setSmallIcon(R.drawable.pray)
            .setContentText(notificationText)
            .setContentIntent(setupClickToBackToApplicationIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                R.drawable.pray,
                "Clear notifications",
                PendingIntent.getBroadcast(
                    context,
                    requestCodeGenerator(),
                    Intent(context, NotificationActionReceiver::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
    }

//    fun startNotification() {
//        val sharedPreferencesRepository =
//            get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)
//        CoroutineScope(Dispatchers.IO).launch {
//            if (sharedPreferencesRepository.isNotification.first()) {
//                notificationManagerCompat.notify(notificationID, notificationCreator())
//            }
//            this.cancel()
//        }
//    }
//
//    fun changeContentInterval(remainingTime: String) {
//        notificationManagerCompat.setContentText(notificationText)
//        notificationManagerCompat.notify(notificationId, notificationCreator())
//    }
}

private fun notificationManagerCompat(context: Context): NotificationManagerCompat {
    return NotificationManagerCompat.from(context)
}

fun cancelNotificationPendingIntent(context: Context): PendingIntent {
    return PendingIntent.getBroadcast(
        context,
        requestCodeGenerator(),
        Intent(context, NotificationActionReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )
}

fun mainActivityPendingIntent(context: Context): PendingIntent {
    return PendingIntent.getActivity(
        context,
        PendingIntentCodes.SET_CONTENT_INTENT.code,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )
}


fun pushNotification(
    context: Context,
    notificationBuilder: NotificationCompat.Builder,
    notificationID: Int
) {
    val sharedPreferencesRepository =
        get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)
    CoroutineScope(Dispatchers.IO).launch {
        if (sharedPreferencesRepository.isNotification.first()) {
            notificationManagerCompat(context).notify(notificationID, notificationBuilder.build())
        }
        this.cancel()
    }
}