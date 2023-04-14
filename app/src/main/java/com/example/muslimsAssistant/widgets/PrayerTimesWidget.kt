package com.example.muslimsAssistant.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.muslimsAssistant.MainActivity
import com.example.muslimsAssistant.R
import com.example.muslimsAssistant.Timing
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.repository.PrayerTimesRepository
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.get
import java.util.*


class PrayerTimesWidget : AppWidgetProvider() {

    private val timing by lazy { Timing() }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        runAlarmManager(context)
//        val serviceIntent = Intent(context, PrayerTimesWidgetService::class.java)
//        context.applicationContext.startService(serviceIntent)

        appWidgetIds.forEach { appWidgetId ->

            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                34564356,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.prayer_times_widget)
            views.setOnClickPendingIntent(R.id.prayer_times_container, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }

        updateWidget(context)

//        NotificationHelper(
//            context,
//            ChannelIDs.PRIORITY_MAX.ID,
//            190000,
//            "Muslim Assistant Manager", "onUpdate!"
//        ).startNotification()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateWidget(context)
//        NotificationHelper(
//            context,
//            ChannelIDs.PRIORITY_MAX.ID,
//            9000078,
//            "Muslim Assistant Manager", "onReceiver!"
//        ).startNotification()
    }

    private fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context, PrayerTimesWidget::class.java))

        runBlocking {
            val prayerTimesRepository =
                get<PrayerTimesRepository>(PrayerTimesRepository::class.java)

            val prayerTimes: PrayerTimes =
                prayerTimesRepository.retDayPrayerTimes(timing.getTodayDate())

            appWidgetIds.forEach { appWidgetId ->

                val views = RemoteViews(
                    context.packageName, R.layout.prayer_times_widget
                )

                views.setTextViewText(R.id.fajr, timing.convertHmTo12HrsFormat(prayerTimes.fajr))
                views.setTextViewText(
                    R.id.sunrise,
                    timing.convertHmTo12HrsFormat(prayerTimes.sunrise)
                )
                views.setTextViewText(R.id.dhuhr, timing.convertHmTo12HrsFormat(prayerTimes.dhuhr))
                views.setTextViewText(R.id.asr, timing.convertHmTo12HrsFormat(prayerTimes.asr))
                views.setTextViewText(
                    R.id.maghrib,
                    timing.convertHmTo12HrsFormat(prayerTimes.maghrib)
                )
                views.setTextViewText(R.id.isha, timing.convertHmTo12HrsFormat(prayerTimes.isha))
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
            prayerTimes
        }
    }

    private fun runAlarmManager(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerTimesWidget::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1231231313,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val interval = AlarmManager.INTERVAL_DAY

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC,
            calendar.timeInMillis,
            interval,
            pendingIntent
        )
    }


}





