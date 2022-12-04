package com.example.muslimAssistant.koin


import androidx.room.Room
import com.example.muslimAssistant.database.PrayerTimesDatabase
import com.example.muslimAssistant.network.ApiService
import com.example.muslimAssistant.notifications.AlarmGenerator
import com.example.muslimAssistant.prayerTimesFragment.PrayerTimesViewModel
import com.example.muslimAssistant.repositories.PrayerTimesRepository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val viewModelsModule = module {
    viewModel {
        PrayerTimesViewModel(prayerTimesRepository = get())
    }
}

val repositoriesModule = module {
    factory {
        PrayerTimesRepository(dataSource = get(), apiService = get())
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            PrayerTimesDatabase::class.java,
            "prayer_times_database"
        ).build()
    }
}

val dataSourcesModule = module {
    single {
        get<PrayerTimesDatabase>().dataSource
    }
}

val appClassesModule = module {
    factory {
        AlarmGenerator(context = androidContext())
    }
}

val servicesModule = module {
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    single {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl("https://api.aladhan.com")
            .build()
            .create(ApiService::class.java)
    }
}


