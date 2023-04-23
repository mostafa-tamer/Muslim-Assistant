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
import java.util.*

class PrayerTimesReceiver : BroadcastReceiver() {

    private lateinit var value: String
    private lateinit var context: Context

    override fun onReceive(context: Context, intnet: Intent) {

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
                    "أذكار الصباح",
                    "لا تنسَ أذكار الصباح"
                ).startNotification()
            }
            "العصر" -> {
                NotificationHelper(
                    context,
                    ChannelIDs.PRIORITY_MAX.ID,
                    NotificationCodes.Azkar.code,
                    "أذكار المساء",
                    "لا تنسَ أذكار المساء"
                ).startNotification()
            }
        }
    }

    private fun prayersNotification() {
        NotificationHelper(
            context,
            ChannelIDs.PRIORITY_MAX.ID,
            NotificationCodes.PrayerTimes.code,
            "حان وقت الصلاة",
            "صلاة $value الآن"
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
//        val mediaPlayer = MediaPlayer.create(context, R.raw.allahakbar)
//        mediaPlayer.start()
//
//        mediaPlayer.setOnCompletionListener {
//            mediaPlayer.release()
//        }
    }
}