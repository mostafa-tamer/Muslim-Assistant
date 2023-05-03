package com.android.muslimAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.NotificationCodes
import com.android.muslimAssistant.R
import com.android.muslimAssistant.activities.MainActivity
import com.android.muslimAssistant.notifications.cancelNotificationPendingIntent
import com.android.muslimAssistant.notifications.mainActivityPendingIntent
import com.android.muslimAssistant.notifications.pushNotification
import com.android.muslimAssistant.utils.startNewService
import com.android.muslimAssistant.utils.updateLanguage
import com.android.muslimAssistant.widgets.PrayerTimesWidgetService
import java.lang.Thread.sleep
import java.util.*

class PrayerTimesReceiver : BroadcastReceiver() {

    private lateinit var value: String
    private lateinit var context: Context

    override fun onReceive(context: Context, intnet: Intent) {
        updateLanguage(context)

        this.context = context
        value = intnet.getStringExtra("value").toString()

        startNewService(context)

        prayersNotification()
        sound()
        azkarNotification()
    }

    private fun azkarNotification() {
        when (value) {
            "1" -> {
                pushNotification(
                    context,
                    NotificationCompat.Builder(context, ChannelIDs.PRIORITY_MAX.ID)
                        .setContentTitle(context.getString(R.string.morningRemembrance))
                        .setSmallIcon(R.drawable.pray)
                        .setContentText(context.getString(R.string.doNotForgetMorningRemembrance))
                        .setContentIntent(mainActivityPendingIntent(context))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .addAction(
                            R.drawable.pray,
                            context.getString(R.string.clear_notifications),
                            cancelNotificationPendingIntent(context)
                        ),
                    NotificationCodes.Azkar.code
                )
            }
            "3" -> {
                pushNotification(
                    context,
                    NotificationCompat.Builder(context, ChannelIDs.PRIORITY_MAX.ID)
                        .setContentTitle(context.getString(R.string.eveningRemembrance))
                        .setSmallIcon(R.drawable.pray)
                        .setContentText(context.getString(R.string.doNotForgetEveningRemembrance))
                        .setContentIntent(mainActivityPendingIntent(context))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .addAction(
                            R.drawable.pray,
                            context.getString(R.string.clear_notifications),
                            cancelNotificationPendingIntent(context)
                        ),
                    NotificationCodes.Azkar.code
                )
            }
        }
    }

    private fun prayersNotification() {
        val prayer = when (value) {
            "1" -> {
                context.resources.getString(R.string.fajr)
            }
            "2" -> {
                context.resources.getString(R.string.dhuhur)
            }
            "3" -> {
                context.resources.getString(R.string.asr)
            }
            "4" -> {
                context.resources.getString(R.string.maghrib)
            }
            else -> {
                context.resources.getString(R.string.isha)
            }
        }

        pushNotification(
            context,
            NotificationCompat.Builder(context, ChannelIDs.PRIORITY_MAX.ID)
                .setContentTitle(context.getString(R.string.timeForPrayer))
                .setSmallIcon(R.drawable.pray)
                .setContentText(buildString {
                    append(context.getString(R.string.prayer))
                    append(" ")
                    append(prayer)
                    append(" ")
                    append(context.getString(R.string.now))
                })
                .setContentIntent(mainActivityPendingIntent(context))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(
                    R.drawable.pray,
                    context.getString(R.string.clear_notifications),
                    cancelNotificationPendingIntent(context)
                ),
            NotificationCodes.PrayerTimes.code
        )
    }

    private fun sound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.allahakbar)
        mediaPlayer.start()
        sleep(10000)

        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    mediaPlayer.release()
                }
            }, 10000
        )
    }
}