package com.example.muslimsAssistant.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.muslimsAssistant.ChannelIDs
import com.example.muslimsAssistant.NotificationCodes
import com.example.muslimsAssistant.repository.PrayerTimesRepository
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import com.example.muslimsAssistant.notifications.NotificationHelper
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
            get<PrayerTimesRepository>(PrayerTimesRepository::class.java)
                .updatePrayerTimesDatabase()
            val alarmManagerHelper = AlarmManagerHelper(applicationContext)
            alarmManagerHelper.scheduleAlarms()
            Result.success()
        } catch (e: Exception) {
            val notification = NotificationHelper(
                applicationContext,
                ChannelIDs.PRIORITY_MAX.ID,
                NotificationCodes.DOWNLOAD_DATA_WORKER_ERROR.code,
                "Application Manager",
                "Error loading data"
            )

            notification.startNotification()
            Result.retry()
        }
    }
}