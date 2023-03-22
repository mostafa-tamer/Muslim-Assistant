package com.example.muslimsAssistant.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.muslimsAssistant.ChannelIDs
import com.example.muslimsAssistant.NotificationCodes
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import com.example.muslimsAssistant.notifications.NotificationHelper


class PrayerTimesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "PrayerTimesWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val alarmManagerHelper = AlarmManagerHelper(applicationContext)
            alarmManagerHelper.setPrayerTimes()
            Result.success()
        } catch (e: Exception) {

            val notification = NotificationHelper(
                applicationContext,
                ChannelIDs.PRIORITY_MAX.ID,
                NotificationCodes.PRAYER_TIMES_WORKER_ERROR.code,
                "Application Manager",
                "Error setting alarms"
            )

            notification.startNotification()

            Result.retry()
        }
    }
}