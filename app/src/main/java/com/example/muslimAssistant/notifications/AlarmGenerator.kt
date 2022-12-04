package com.example.muslimAssistant.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.muslimAssistant.PrayerTimesMatcher
import com.example.muslimAssistant.Timing
import com.example.muslimAssistant.Timing.todayDate
import com.example.muslimAssistant.Timing.tomorrowDate
import com.example.muslimAssistant.database.PrayerTimes
import com.example.muslimAssistant.receivers.AlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmGenerator(
    private val context: Context
) {

    fun setPrayerTimesAlarms() {

        CoroutineScope(Dispatchers.IO).launch {
            searchForDates()
        }
    }

    private suspend fun searchForDates() {

        val prayerTimesMatcher = PrayerTimesMatcher()

        val retrievePrayerTodayTimes = prayerTimesMatcher.retrievePrayerTimes(todayDate)
        val retrievePrayerTomorrowTimes = prayerTimesMatcher.retrievePrayerTimes(tomorrowDate)

        scheduleNotifications(
            retrievePrayerTodayTimes,
            TodayPrayerTimesPendingIntentCodes()
        )

        scheduleNotifications(
            retrievePrayerTomorrowTimes,
            TomorrowPrayerTimesPendingIntentCodes()
        )
    }

    private fun scheduleNotifications(
        prayerTimes: PrayerTimes,
        prayerTimesPendingIntentCodes: PrayerTimesPendingIntentCodes
    ) {

        val listOfSalah = mapOf(
            prayerTimes.fajr to "الفجر" to prayerTimesPendingIntentCodes.fajr,
            prayerTimes.sunrise to "الشروق" to prayerTimesPendingIntentCodes.sunrise,
            prayerTimes.dhuhr to "الظهر" to prayerTimesPendingIntentCodes.duhur,
            prayerTimes.asr to "العصر" to prayerTimesPendingIntentCodes.asr,
            prayerTimes.maghrib to "المغرب" to prayerTimesPendingIntentCodes.magrib,
            prayerTimes.isha to "العشاء" to prayerTimesPendingIntentCodes.isha
        )

        for (i in listOfSalah) {

            val dateTime = "${prayerTimes.date} ${i.key.first}"

            val prayerDateTimesMiles = Timing.convertDateTimeToMillis(dateTime)

            if (prayerDateTimesMiles > System.currentTimeMillis()) {

                val salah = i.key.second
                val requestCode = i.value

                println("$dateTime $salah $requestCode")

                val intent = Intent(context, AlarmReceiver::class.java)

                intent.putExtra("salah", salah)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager: AlarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (salah == "الشروق") {
                    // 900000 equals 15 minutes
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        prayerDateTimesMiles + 900000,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        prayerDateTimesMiles,
                        pendingIntent
                    )
                }
            }
        }
    }
}
