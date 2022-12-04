package com.example.muslimAssistant.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.muslimAssistant.notifications.AlarmGenerator
import com.example.muslimAssistant.notifications.ChannelIDs
import com.example.muslimAssistant.notifications.NotificationCodes
import com.example.muslimAssistant.notifications.NotificationHelper
import org.koin.java.KoinJavaComponent.get


class PrayerTimesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "PrayerTimesWorker"
    }

    override suspend fun doWork(): Result {

        return try {
            val alarmGenerator = get<AlarmGenerator>(AlarmGenerator::class.java)
            alarmGenerator.setPrayerTimesAlarms()

            Result.success()
        } catch (e: Exception) {

            val notification = NotificationHelper(
                applicationContext,
                ChannelIDs.PRIORITY_MAX.ID,
                NotificationCodes.PRAYER_TIMES_WORKER_ERROR.code,
                "Application Manager",
                "Error on setting alarms"
            )

            notification.startNotification()

            Result.retry()
        }
    }
}