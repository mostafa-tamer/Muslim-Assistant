package com.android.muslimAssistant

import android.app.Application
import androidx.work.*
import com.android.muslimAssistant.koin.*
import com.android.muslimAssistant.workers.DownloadPrayerTimesWorker
import com.android.muslimAssistant.workers.PrayerTimesWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class MuslimAssistantApplication : Application() {

    private val coroutine = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MuslimAssistantApplication)
            modules(
                viewModelsModule,
                repositoriesModule,
                DAOs,
                databaseModule,
                servicesModule
            )
        }

        coroutine.launch {
            downloadDataWorkerSetter()
            prayerTimeWorkerSetter()
        }
    }

    private fun prayerTimeWorkerSetter() {

        val repeatingRequest = PeriodicWorkRequestBuilder<PrayerTimesWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            PrayerTimesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    private fun downloadDataWorkerSetter() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<DownloadPrayerTimesWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            DownloadPrayerTimesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }


}