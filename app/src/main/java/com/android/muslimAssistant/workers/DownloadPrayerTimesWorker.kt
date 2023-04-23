package com.android.muslimAssistant.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.muslimAssistant.notifications.AlarmHandler
import com.android.muslimAssistant.repository.PrayerTimesRepository
import org.koin.java.KoinJavaComponent.get

class DownloadPrayerTimesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "DownloadDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val repository = get<PrayerTimesRepository>(PrayerTimesRepository::class.java)
            repository.updatePrayerTimesDatabase()

            val alarmHandler = AlarmHandler(applicationContext)
            alarmHandler.scheduleAlarms()

            Result.success()
        } catch (e: Exception) {
//            val notification = NotificationHelper(
//                applicationContext,
//                ChannelIDs.PRIORITY_MAX.ID,
//                NotificationCodes.DOWNLOAD_DATA_WORKER_ERROR.code,
//                "Application Manager",
//                "Error loading data ${e.message}"
//            )
//
//            notification.startNotification()
            Result.failure()
        }
    }
}