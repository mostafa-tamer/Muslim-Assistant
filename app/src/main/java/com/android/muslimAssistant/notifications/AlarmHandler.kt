package com.android.muslimAssistant.notifications

import android.app.AlarmManager
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import com.android.muslimAssistant.*
import com.android.muslimAssistant.database.PrayerTimes
import com.android.muslimAssistant.receivers.PrayerTimesReceiver
import com.android.muslimAssistant.receivers.ReminderReceiver
import com.android.muslimAssistant.repository.PrayerTimesRepository
import com.android.muslimAssistant.repository.RemindersRepository
import com.android.muslimAssistant.utils.separator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class AlarmHandler(
    private val context: Context
) {
    private val timing by lazy { Timing() }

    fun scheduleAlarms() {
        CoroutineScope(Dispatchers.Default).launch {
            schedulePrayerTimesNotifications()
            println(separator)
            scheduleRemindersNotifications()
            println(separator)
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

            if (i.type) {
                if (dateTimeInMillis > System.currentTimeMillis()) {
                    generateAlarmExactTime(
                        context,
                        dateTimeInMillis,
                        i.description,
                        i.id,
                        ReminderReceiver::class.java
                    )
                } else {
                    generateAlarmExactTime(
                        context,
                        plus24DateTimeMillis,
                        i.description,
                        i.id,
                        ReminderReceiver::class.java
                    )
                }
            } else {
                val interval: Long =
                    timing.convertHmToMillis(i.hours, i.minutes)

                generateAlarmPeriodicTime(
                    context,
                    interval,
                    i.description,
                    i.id,
                    ReminderReceiver::class.java
                )
            }
        }
    }

    private fun <T> generateAlarmPeriodicTime(
        context: Context,
        interval: Long,
        value: String,
        requestCode: Int,
        receiver: Class<T>
    ) {

        val intent = Intent(context, receiver)
        intent.putExtra("value", value)

        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (
            getBroadcast(
                context,
                requestCode,
                intent,
                FLAG_NO_CREATE or FLAG_IMMUTABLE
            ) == null
        ) {
            val pendingIntent = getBroadcast(
                context,
                requestCode,
                intent,
                FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
            )

            println(
                "periodic alarm => ${
                    timing.convertMillisToHmUTC(
                        interval
                    )
                } $value $requestCode"
            )

            val triggerTime = System.currentTimeMillis() + interval

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                interval,
                pendingIntent
            )
        } else {
            println(
                "periodic alarm => ${
                    timing.convertMillisToHmUTC(
                        interval
                    )
                } $value $requestCode is already running"
            )
        }
    }

    private fun <T> generateAlarmExactTime(
        context: Context,
        dateTimeInMillis: Long,
        value: String,
        requestCode: Int,
        receiver: Class<T>
    ) {
        if (dateTimeInMillis < System.currentTimeMillis()) return

        val intent = Intent(context, receiver)
        intent.putExtra("value", value)

        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = getBroadcast(
            context,
            requestCode,
            intent,
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent
        )

        println(
            "Exact time alarm => ${timing.convertMillisToDmy(dateTimeInMillis)} ${
                timing.convertHmTo12HrsFormat(
                    timing.convertMillisToHm(
                        dateTimeInMillis
                    )
                )
            } $value $requestCode"
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
                generateAlarmExactTime(
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
            generateAlarmExactTime(
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
                context.getString(R.string.fajr),
                requestCode.fajr
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.dhuhur}",
                context.getString(R.string.dhuhur),
                requestCode.dhuhur
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.asr}",
                context.getString(R.string.asr),
                requestCode.asr
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.maghrib}",
                context.getString(R.string.maghrib),
                requestCode.maghrib
            ),
            ScheduledPrayerTimes(
                "${prayerTimes.dateGregorian} ${prayerTimes.isha}",
                context.getString(R.string.isha),
                requestCode.isha
            )
        )
    }
}

