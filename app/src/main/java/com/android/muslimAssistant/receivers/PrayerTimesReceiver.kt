package com.android.muslimAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.NotificationCodes
import com.android.muslimAssistant.R
import com.android.muslimAssistant.notifications.NotificationHelper
import com.android.muslimAssistant.utils.updateLanguage
import java.util.*

class PrayerTimesReceiver : BroadcastReceiver() {

    private lateinit var value: String
    private lateinit var context: Context

    override fun onReceive(context: Context, intnet: Intent) {
        updateLanguage(context)

        this.context = context
        value = intnet.getStringExtra("value").toString()

        prayersNotification()
        azkarNotification()
    }

    private fun azkarNotification() {
        when (value) {
            "الفجر" -> {
                NotificationHelper(
                    context,
                    ChannelIDs.PRIORITY_MAX.ID,
                    NotificationCodes.Azkar.code,
                    context.getString(R.string.morningRemembrance),
                    context.getString(R.string.doNotForgetMorningRemembrance)
                ).startNotification()
            }
            "العصر" -> {
                NotificationHelper(
                    context,
                    ChannelIDs.PRIORITY_MAX.ID,
                    NotificationCodes.Azkar.code,
                    context.getString(R.string.eveningRemembrance),
                    context.getString(R.string.doNotForgetEveningRemembrance)
                ).startNotification()
            }
        }
    }

    private fun prayersNotification() {
        NotificationHelper(
            context,
            ChannelIDs.PRIORITY_MAX.ID,
            NotificationCodes.PrayerTimes.code,
            context.getString(R.string.timeForPrayer),
            buildString {
                append(context.getString(R.string.prayer))
                append(" ")
                append(value)
                append(" ")
                append(context.getString(R.string.now))
            }
        ).startNotification()

        val soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ).build()

        val soundId = soundPool.load(context, R.raw.allahakbar, 1)

        soundPool.setOnLoadCompleteListener { _, _, _ ->
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }

        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    soundPool.release()
                }
            }, 10000
        )
    }
}