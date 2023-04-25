package com.android.muslimAssistant.widgets

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.android.muslimAssistant.Timing
import com.android.muslimAssistant.R
import com.android.muslimAssistant.database.PrayerTimes
import com.android.muslimAssistant.repository.PrayerTimesRepository
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import java.util.*

class PrayerTimesWidgetService : Service() {
    private val timer = Timer()
    private val timing by lazy { Timing() }
    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        try {
            startForeground(1, createNotification())
        } catch (e: java.lang.Exception) {
            println(e.message)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val prayerTimes: List<PrayerTimes> = runBlocking {
            val prayerTimesRepository =
                KoinJavaComponent.get<PrayerTimesRepository>(PrayerTimesRepository::class.java)
            prayerTimesRepository.retPrayerTimesSuspend()
        }

        val appWidgetManager =
            AppWidgetManager.getInstance(this@PrayerTimesWidgetService)
        val appWidgetIds =
            appWidgetManager.getAppWidgetIds(
                ComponentName(
                    this@PrayerTimesWidgetService,
                    PrayerTimesWidget::class.java
                )
            )

        timer.schedule(
            object : TimerTask() {
                override fun run() {

                    println("service $startId is running")
                    appWidgetIds.forEach { appWidgetId ->
                        val views = RemoteViews(
                            packageName, R.layout.prayer_times_widget
                        )
                        views.setTextViewText(
                            R.id.remaining_time_widget,
                            setRemainingTime(prayerTimes)
                        )
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            },
            0,
            1000
        )
        return START_STICKY
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
        val todayPrayerTimes = getPrayerTimesSelectedData(
            prayerTimesList,
            timing.getTodayDate()
        )

        if (todayPrayerTimes != null) {
            val prayerTimesStrings = mapOf(
                todayPrayerTimes.fajr to "الفجر",
                todayPrayerTimes.dhuhur to "الظهر",
                todayPrayerTimes.asr to "العصر",
                todayPrayerTimes.maghrib to "المغرب",
                todayPrayerTimes.isha to "العشاء",
            )
            for (i in prayerTimesStrings) {

                val prayerTimeMillis =
                    timing.convertDmyHmToMillis("${timing.getTodayDate()} ${i.key}")

                val currentTimeMillis = System.currentTimeMillis()

                if (currentTimeMillis < prayerTimeMillis) {

                    val remainingTimeInMillis = prayerTimeMillis - currentTimeMillis
                    val remainingTimeString = timing.convertMillisToHMS(
                        remainingTimeInMillis,
                        "HH:mm:ss"
                    )
                    return "الوقت المتبقي على صلاة ${i.value}" + "\n" + remainingTimeString
                }
            }
        }

        val tomorrowPrayerTimes = getPrayerTimesSelectedData(
            prayerTimesList,
            timing.getTomorrowDate()
        )

        if (tomorrowPrayerTimes != null) {
            val tomorrowFajrTimeMillis =
                timing.convertDmyHmToMillis("${timing.getTomorrowDate()} ${tomorrowPrayerTimes.fajr}")
            val currentTimeMillis = System.currentTimeMillis()

            if (currentTimeMillis < tomorrowFajrTimeMillis) {
                val remainingTimeInMillis = tomorrowFajrTimeMillis - currentTimeMillis
                val remainingTimeString = timing.convertMillisToHMS(
                    remainingTimeInMillis,
                    "HH:mm:ss"
                )
                return "الوقت المتبقي على صلاة الفجر\n$remainingTimeString"
            }
        }
        return ""
    }


    private fun createNotification(): Notification {
        val channelId = "time_remaining_service"
        val channel =
            NotificationChannel(channelId, "WidgetChannel", NotificationManager.IMPORTANCE_LOW)
        val service = getSystemService(NotificationManager::class.java)
        service.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Muslim Assistant")
            .setContentText("Running in the background")
            .setSmallIcon(R.drawable.pray)
            .build()
    }
}