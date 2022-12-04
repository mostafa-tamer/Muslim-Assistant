package com.example.muslimAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.muslimAssistant.notifications.ChannelIDs
import com.example.muslimAssistant.notifications.NotificationCodes
import com.example.muslimAssistant.notifications.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var salah: String
    private lateinit var context: Context

    override fun onReceive(context: Context, intnet: Intent) {

        this.context = context
        salah = intnet.getStringExtra("salah")!!

        if (salah == "الشروق") {
            duhaPrayerNotification()
            return
        }

        prayersNotification()
        azkarNotification()

    }

    private fun duhaPrayerNotification() {

        NotificationHelper(
            context,
            ChannelIDs.PRIORITY_MAX.ID,
            NotificationCodes.PrayerTimes.code,
            "حان وقت صلاة الضحى",
            "صلاة الضحى الآن"
        ).startNotification()
    }

    private fun azkarNotification() {
        when (salah) {
            "الفجر" -> {
                NotificationHelper(
                    context,
                    ChannelIDs.PRIORITY_MAX.ID,
                    NotificationCodes.Azkar.code,
                    "أذكار الصباح",
                    "لا تنسى أذكار الصباح"
                ).startNotification()
            }
            "العصر" -> {
                NotificationHelper(
                    context,
                    ChannelIDs.PRIORITY_MAX.ID,
                    NotificationCodes.Azkar.code,
                    "أذكار المساء",
                    "لا تنسى أذكار المساء"
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
            "صلاة $salah الآن"
        ).startNotification()
    }

}