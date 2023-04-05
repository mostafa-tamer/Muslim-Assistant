package com.example.muslimsAssistant.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.muslimsAssistant.PrayerTimesPendingIntentCodes
import com.example.muslimsAssistant.Timing
import com.example.muslimsAssistant.Timing.getTodayDate
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.database.ReminderItemsDao
import com.example.muslimsAssistant.fragments.repository.PrayerTimesRepository
import com.example.muslimsAssistant.receivers.PrayerTimesReceiver
import com.example.muslimsAssistant.receivers.ReminderReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class AlarmManagerHelper(
    private val context: Context
) {

    fun setPrayerTimes() {
        CoroutineScope(Dispatchers.Default).launch {
            schedulePrayerTimesNotifications()
            scheduleRemindersNotifications()
            this.cancel()
        }
    }


    private suspend fun scheduleRemindersNotifications() {
        val remindersDao = get<ReminderItemsDao>(ReminderItemsDao::class.java)
        val remindersList = remindersDao.retReminderItems()

        for (i in remindersList) {
            generateAlarmIfNotTodayThenTomorrow(
                context,
                "${getTodayDate()} ${i.hours}:${i.minutes}",
                i.description,
                i.id,
                ReminderReceiver::class.java
            )
        }
    }

    private fun <T> generateAlarmIfNotTodayThenTomorrow(
        context: Context,
        dateTime: String,
        value: String,
        requestCode: Int,
        receiver: Class<T>
    ) {

        val dateTimeInMillis = Timing.convertDateTimeNoSecToMillisNoSec(dateTime)
        if (dateTime != "01-01-1970 00:00:00") {
            if (dateTimeInMillis > System.currentTimeMillis()) {
                println("$dateTime $value $requestCode")
                generateAlarm(context, dateTimeInMillis, value, requestCode, receiver)
            } else {
                val dateTimePlus24 = Timing.addOneDayToDateTime(dateTime)
                println("$dateTimePlus24 $value $requestCode")
                val plus24DateTimeMillis =
                    Timing.convertDateTimeNoSecToMillisNoSec(dateTimePlus24)
                generateAlarm(context, plus24DateTimeMillis, value, requestCode, receiver)
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
        val todayPrayerTimes = retDayPrayerTimes(getTodayDate())

        val mapOfPrayerTimes = preparePrayerTimes(todayPrayerTimes)

        for (i in mapOfPrayerTimes) {
            generateAlarmIfNotTodayThenTomorrow(
                context,
                i.key.first,
                i.key.second,
                i.value,
                PrayerTimesReceiver::class.java
            )
        }
    }

    suspend fun retDayPrayerTimes(date: String): PrayerTimes {
        val prayerTimesRepository =
            get<PrayerTimesRepository>(PrayerTimesRepository::class.java)
        val listOfPrayerTimes = prayerTimesRepository.retPrayerTimesSuspend()
        val searchIndex =
            listOfPrayerTimes.binarySearch { it.dateGregorian.compareTo(date) }
        return if (searchIndex >= 0) {
            val item = listOfPrayerTimes[searchIndex]
            PrayerTimes(
                item.dateGregorian,
                item.dateHigri,
                item.monthHijri,
                item.fajr,
                item.sunrise,
                item.dhuhr,
                item.asr,
                item.maghrib,
                item.isha
            )
        } else {
            PrayerTimes()
        }
    }

    private fun preparePrayerTimes(
        prayerTimes: PrayerTimes
    ): Map<Pair<String, String>, Int> {
        return mapOf(
            "${prayerTimes.dateGregorian} ${prayerTimes.fajr}"
                    to "الفجر"
                    to PrayerTimesPendingIntentCodes.FAJR.code,
            "${prayerTimes.dateGregorian} ${prayerTimes.dhuhr}"
                    to "الظهر"
                    to PrayerTimesPendingIntentCodes.DUHUR.code,
            "${prayerTimes.dateGregorian} ${prayerTimes.asr}"
                    to "العصر"
                    to PrayerTimesPendingIntentCodes.ASR.code,
            "${prayerTimes.dateGregorian} ${prayerTimes.maghrib}"
                    to "المغرب"
                    to PrayerTimesPendingIntentCodes.MAGRIB.code,
            "${prayerTimes.dateGregorian} ${prayerTimes.isha}"
                    to "العشاء"
                    to PrayerTimesPendingIntentCodes.ISHA.code
        )
    }
}
