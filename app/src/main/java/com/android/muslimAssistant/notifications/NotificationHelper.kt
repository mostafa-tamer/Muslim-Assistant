package com.android.muslimAssistant.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.muslimAssistant.activities.MainActivity
import com.android.muslimAssistant.PendingIntentCodes
import com.android.muslimAssistant.R
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
    private val notificationTitle: String,
    private val notificationText: String
) {
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
            .setSmallIcon(R.drawable.pray)
            .setContentText(notificationText)
            .setContentIntent(setupClickToBackToApplicationIntent())
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

    fun startNotification() {
        val sharedPreferencesRepository =
            get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            if (sharedPreferencesRepository.isNotification.first()) {
                val notificationManagerCompat = NotificationManagerCompat.from(context)
                notificationManagerCompat.notify(notificationID, notificationCreator())
            }
            this.cancel()
        }
    }
}