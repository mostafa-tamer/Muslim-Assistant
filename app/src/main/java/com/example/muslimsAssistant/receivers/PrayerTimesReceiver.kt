package com.example.muslimsAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.example.muslimsAssistant.ChannelIDs
import com.example.muslimsAssistant.NotificationCodes
import com.example.muslimsAssistant.R
import com.example.muslimsAssistant.notifications.NotificationHelper
import java.lang.Thread.sleep

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
        sleep(5000 * 60)
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
        MediaPlayer.create(context, R.raw.allahakbar).start()
    }
}