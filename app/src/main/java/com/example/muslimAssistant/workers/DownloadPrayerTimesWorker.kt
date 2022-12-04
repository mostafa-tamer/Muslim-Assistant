package com.example.muslimAssistant.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.muslimAssistant.notifications.AlarmGenerator
import com.example.muslimAssistant.notifications.ChannelIDs
import com.example.muslimAssistant.notifications.NotificationCodes
import com.example.muslimAssistant.notifications.NotificationHelper
import com.example.muslimAssistant.repositories.PrayerTimesRepository
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
            val prayerTimesRepository =
                get<PrayerTimesRepository>(PrayerTimesRepository::class.java)
            prayerTimesRepository.updatePrayerTimesInDatabase()

            val alarmGenerator = get<AlarmGenerator>(AlarmGenerator::class.java)
            alarmGenerator.setPrayerTimesAlarms()

            Result.success()
        } catch (e: Exception) {

            val notification = NotificationHelper(
                applicationContext,
                ChannelIDs.PRIORITY_MAX.ID,
                NotificationCodes.DOWNLOAD_DATA_WORKER_ERROR.code,
                "Application Manager",
                "Error on downloading data"
            )

            notification.startNotification()

            Result.retry()
        }
    }
}