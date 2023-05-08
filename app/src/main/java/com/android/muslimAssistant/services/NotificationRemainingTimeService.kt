package com.android.muslimAssistant.services

import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.R
import com.android.muslimAssistant.Timing
import com.android.muslimAssistant.database.PrayerTimes
import com.android.muslimAssistant.notifications.mainActivityPendingIntent
import com.android.muslimAssistant.repository.PrayerTimesRepository
import com.android.muslimAssistant.utils.updateLanguage
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import java.util.*

class NotificationRemainingTimeService : Service() {
    private val timer = Timer()
    private val timing by lazy { Timing() }
    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        updateNotification(0)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateLanguage(this@NotificationRemainingTimeService)

        updateNotification(startId)

        return START_STICKY
    }

    private fun updateNotification(startId: Int) {
        val prayerTimes: List<PrayerTimes> = runBlocking {
            val prayerTimesRepository =
                KoinJavaComponent.get<PrayerTimesRepository>(PrayerTimesRepository::class.java)
            prayerTimesRepository.retPrayerTimesSuspend()
        }

        if (prayerTimes.isEmpty())
            return

        val notificationBuilder =
            NotificationCompat.Builder(
                this@NotificationRemainingTimeService,
                ChannelIDs.PRIORITY_MIN.ID
            )
                .setContentTitle(this@NotificationRemainingTimeService.getString(R.string.app_name))
                .setSmallIcon(R.drawable.pray)
                .setContentText(setRemainingTime(prayerTimes))
                .setContentIntent(mainActivityPendingIntent(this@NotificationRemainingTimeService))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setAutoCancel(true)


        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    println("service $startId is running")
                    notificationBuilder.setContentText(setRemainingTime(prayerTimes))
                    try {
                        startForeground(1, notificationBuilder.build())
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            },
            0,
            1000
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun getPrayerTimesSelectedData(
        prayerTimesList: List<PrayerTimes>,
        date: String
    ): PrayerTimes? {
        val searchIndex =
            prayerTimesList.binarySearch { it.dateGregorian.compareTo(date) }
        if (searchIndex >= 0) {
            return prayerTimesList[searchIndex]
        }
        return null
    }

    private fun setRemainingTime(prayerTimesList: List<PrayerTimes>): String {
        val currentTimeMillis = System.currentTimeMillis()

        // Check remaining time for today's prayers
        getPrayerTimesSelectedData(
            prayerTimesList,
            timing.getTodayDate()
        )?.let { todayPrayerTimes ->
            val prayerTimesStrings = mapOf(
                todayPrayerTimes.fajr to this@NotificationRemainingTimeService.getString(R.string.fajr),
                todayPrayerTimes.dhuhur to this@NotificationRemainingTimeService.getString(R.string.dhuhur),
                todayPrayerTimes.asr to this@NotificationRemainingTimeService.getString(R.string.asr),
                todayPrayerTimes.maghrib to this@NotificationRemainingTimeService.getString(R.string.maghrib),
                todayPrayerTimes.isha to this@NotificationRemainingTimeService.getString(R.string.isha)
            )
            prayerTimesStrings.forEach { (prayerTime, prayerName) ->
                val prayerTimeMillis =
                    timing.convertDmyHmToMillis("${timing.getTodayDate()} $prayerTime")
                if (currentTimeMillis < prayerTimeMillis) {
                    val remainingTimeInMillis = prayerTimeMillis - currentTimeMillis
                    val remainingTimeString =
                        timing.convertMillisToHMS(remainingTimeInMillis, "HH:mm:ss")
                    return buildString {
                        append(this@NotificationRemainingTimeService.getString(R.string.remainingTime))
                        append(" ")
                        append(prayerName)
                        append("\n")
                        append(remainingTimeString)
                    }
                }
            }
        }

        // Check remaining time for tomorrow's Fajr prayer
        getPrayerTimesSelectedData(
            prayerTimesList,
            timing.getTomorrowDate()
        )?.let { tomorrowPrayerTimes ->
            val tomorrowFajrTimeMillis =
                timing.convertDmyHmToMillis("${timing.getTomorrowDate()} ${tomorrowPrayerTimes.fajr}")
            if (currentTimeMillis < tomorrowFajrTimeMillis) {
                val remainingTimeInMillis = tomorrowFajrTimeMillis - currentTimeMillis
                val remainingTimeString =
                    timing.convertMillisToHMS(remainingTimeInMillis, "HH:mm:ss")
                return buildString {
                    append(this@NotificationRemainingTimeService.getString(R.string.time_remaining_to_fajr_prayer))
                    append("\n")
                    append(remainingTimeString)
                }
            }
        }

        return String()
    }
}