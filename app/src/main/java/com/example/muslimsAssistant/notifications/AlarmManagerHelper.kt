package com.example.muslimsAssistant.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.muslimsAssistant.PrayerTimesPendingIntentCodes
import com.example.muslimsAssistant.Timing
import com.example.muslimsAssistant.TodayPrayerTimesPendingIntentCodes
import com.example.muslimsAssistant.TomorrowPrayerTimesPendingIntentCodes
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.receivers.PrayerTimesReceiver
import com.example.muslimsAssistant.receivers.ReminderReceiver
import com.example.muslimsAssistant.repository.PrayerTimesRepository
import com.example.muslimsAssistant.repository.RemindersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class AlarmManagerHelper(
    private val context: Context
) {
    private val timing by lazy { Timing() }

    fun scheduleAlarms() {
        CoroutineScope(Dispatchers.Default).launch {
            schedulePrayerTimesNotifications()
            scheduleRemindersNotifications()
            this.cancel()
        }
    }

    private suspend fun scheduleRemindersNotifications() {
        val remindersRepository = get<RemindersRepository>(RemindersRepository::class.java)
        val remindersList = remindersRepository.retReminders()

        remindersList.forEach { i ->
            val dateTimeString = "${timing.getTodayDate()} ${i.hours}:${i.minutes}"
            val dateTimeInMillis = timing.convertDmyHmToMillis(dateTimeString)
            val dmyHmPlus24 = timing.addOneDayToDmyHm(dateTimeString)
            val plus24DateTimeMillis = timing.convertDmyHmToMillis(dmyHmPlus24)

            if (dateTimeInMillis > System.currentTimeMillis()) {
                generateAlarm(
                    context,
                    dateTimeInMillis,
                    i.description,
                    i.id,
                    ReminderReceiver::class.java
                )
            } else {
                generateAlarm(
                    context,
                    plus24DateTimeMillis,
                    i.description,
                    i.id,
                    ReminderReceiver::class.java
                )
            }
        }
    }

    private fun <T> generateAlarm(
        context: Context,
        dateTimeInMillis: Long,
        value: String,
        requestCode: Int,
        receiver: Class<T>
    ) {
        if (dateTimeInMillis < System.currentTimeMillis()) return

        println(
            "${timing.convertMillisToDmy(dateTimeInMillis)} ${
                timing.convertHmTo12HrsFormat(
                    timing.convertMillisToHm(
                        dateTimeInMillis
                    )
                )
            } $value $requestCode"
        )

        val intent = Intent(context, receiver)
        intent.putExtra("value", value)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent
        )
    }

    private suspend fun schedulePrayerTimesNotifications() {
        val prayerTimesRepository = get<PrayerTimesRepository>(PrayerTimesRepository::class.java)

        val todayPrayerTimes = prayerTimesRepository.retDayPrayerTimes(timing.getTodayDate())
        val tomorrowPrayerTimes = prayerTimesRepository.retDayPrayerTimes(timing.getTomorrowDate())

        val mutableListOfTodayPrayerTimes =
            preparePrayerTimes(todayPrayerTimes, TodayPrayerTimesPendingIntentCodes())
        val mutableListTomorrowPrayerTimes =
            preparePrayerTimes(tomorrowPrayerTimes, TomorrowPrayerTimesPendingIntentCodes())

        mutableListOfTodayPrayerTimes.forEach { i ->
            val prayerTimeInMillis = timing.convertDmyHmToMillis(i.time)
            if (prayerTimeInMillis > System.currentTimeMillis()) {
                generateAlarm(
                    context,
                    prayerTimeInMillis,
                    i.name,
                    i.requestCode,
                    PrayerTimesReceiver::class.java
                )
            }
        }

        mutableListTomorrowPrayerTimes.forEach { i ->
            val prayerTimeInMillis = timing.convertDmyHmToMillis(i.time)
            generateAlarm(
                context,
                prayerTimeInMillis,
                i.name,
                i.requestCode,
                PrayerTimesReceiver::class.java
            )
        }
    }

    private fun preparePrayerTimes(
        prayerTimes: PrayerTimes,
        requestCode: PrayerTimesPendingIntentCodes
    ): MutableList<ScheduledPrayerTimes> {
        return mutableListOf(
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.fajr}",
                "الفجر",
                requestCode.fajr
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.dhuhr}",
                "الظهر",
                requestCode.dhuhur
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.asr}",
                "العصر",
                requestCode.asr
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.maghrib}",
                "المغرب",
                requestCode.maghrib
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.isha}",
                "العشاء",
                requestCode.isha
            )
        )
    }
}

