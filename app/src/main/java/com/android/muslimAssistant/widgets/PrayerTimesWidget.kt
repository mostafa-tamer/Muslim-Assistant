package com.android.muslimAssistant.widgets

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.android.muslimAssistant.activities.LauncherActivity
import com.android.muslimAssistant.Timing
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.R
import com.android.muslimAssistant.database.PrayerTimes
import com.android.muslimAssistant.notifications.NotificationHelper
import com.android.muslimAssistant.repository.PrayerTimesRepository
import com.android.muslimAssistant.utils.dayInMillis
import com.android.muslimAssistant.utils.requestCodeGenerator
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.get
import java.util.*


class PrayerTimesWidget : AppWidgetProvider() {

    private val timing by lazy { Timing() }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        runAlarmManager(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        println("onUpdate")

//        try {
//            val stopIntent = Intent(context, PrayerTimesWidgetService::class.java)
//            context.stopService(stopIntent)
//            val startIntent = Intent(context, PrayerTimesWidgetService::class.java)
//            context.startService(startIntent)
//        } catch (e: Exception) {
//            println(e.message)
//            NotificationHelper(
//                context,
//                ChannelIDs.PRIORITY_MAX.ID,
//                (System.currentTimeMillis() % getMaxInt).toInt(),
//                "Muslim Assistant Manager",
//                "Please open muslim assistant to let the widget work properly!"
//            ).startNotification()
//        }


        updateWidget(context)

        appWidgetIds.forEach { appWidgetId ->
            handleWidgetClickListener(context, appWidgetManager, appWidgetId)
        }
    }

    private fun isForegroundServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        for (service in runningServices) {
            val componentName = service.service
            if (componentName.className == serviceClass.name) {
                return true
            }
        }
        return false
    }


    private fun handleWidgetClickListener(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            requestCodeGenerator(),
            Intent(context, LauncherActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val views = RemoteViews(context.packageName, R.layout.prayer_times_widget)
        views.setOnClickPendingIntent(R.id.prayer_times_container, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
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

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.getStringExtra("value") == "reset") {
            updateWidget(context)
            NotificationHelper(
                context,
                ChannelIDs.PRIORITY_MAX.ID,
                requestCodeGenerator(),
                "Muslim Assistant Manager", "Widget is updated!"
            ).startNotification()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        println("onDeleted")
//        if (appWidgetIds.isEmpty()) {
//            val stopIntent = Intent(context, PrayerTimesWidgetService::class.java)
//            context.stopService(stopIntent)
//        }
    }

    private fun runAlarmManager(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerTimesWidget::class.java)
        intent.putExtra("value", "reset")
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCodeGenerator(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val interval = AlarmManager.INTERVAL_DAY

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val timeInMillis = calendar.timeInMillis + dayInMillis
        println("time in widget $timeInMillis")
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            interval,
            pendingIntent
        )
    }
}