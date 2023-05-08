package com.android.muslimAssistant.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.muslimAssistant.notifications.AlarmHandler
import com.android.muslimAssistant.utils.startNewService


class PrayerTimesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "PrayerTimesWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val alarmHandler = AlarmHandler(applicationContext)
            alarmHandler.scheduleAlarms()

            startNewService(applicationContext)

            Result.success()
        } catch (e: Exception) {

//            val notification = NotificationHelper(
//                applicationContext,
//                ChannelIDs.PRIORITY_MAX.ID,
//                NotificationCodes.PRAYER_TIMES_WORKER_ERROR.code,
//                "Application Manager",
//                "Error setting alarms"
//            )
//
//            notification.startNotification()

            Result.retry()
        }
    }
}