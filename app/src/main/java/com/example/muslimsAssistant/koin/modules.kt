package com.example.muslimsAssistant.koin


import androidx.room.Room
import com.example.muslimsAssistant.database.Database
import com.example.muslimsAssistant.fragments.prayerTimesFragment.PrayerTimesViewModel
import com.example.muslimsAssistant.fragments.reminderFragment.ReminderViewModel
import com.example.muslimsAssistant.fragments.tasbeehFragment.TasbeehViewModel
import com.example.muslimsAssistant.network.ApiService
import com.example.muslimsAssistant.repository.PrayerTimesRepository
import com.example.muslimsAssistant.repository.RemindersRepository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val viewModelsModule = module {
    viewModel {
        PrayerTimesViewModel(repository = get())
    }
    viewModel {
        ReminderViewModel(remindersRepository = get())
    }
    viewModel {
        TasbeehViewModel(tasbeehDao = get())
    }
}

val repositoriesModule = module {
    factory {
        PrayerTimesRepository(
            prayerTimesDataSource = get(),
            userDataSource = get(),
            apiService = get()
        )
    }
    factory {
        RemindersRepository(remindersDao = get())
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            Database::class.java,
            "prayer_times_database"
        ).build()
    }
}

val DAOs = module {
    single {
        get<Database>().prayerTimesDao
    }
    single {
        get<Database>().latLngDao
    }
    single {
        get<Database>().remindersDao
    }
    single {
        get<Database>().tasbeehDao
    }
}

val servicesModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/v1/timings/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            ).build()
            .create(ApiService::class.java)
    }
}


