package com.example.muslimAssistant

import com.example.muslimAssistant.database.PrayerTimes
import com.example.muslimAssistant.repositories.PrayerTimesRepository
import org.koin.java.KoinJavaComponent.get

class PrayerTimesMatcher {

    private var prayerTimes: PrayerTimes = PrayerTimes(
        "01-01-1970",
        "00:00:00",
        "00:00:00",
        "00:00:00",
        "00:00:00",
        "00:00:00",
        "00:00:00"
    )

    suspend fun retrievePrayerTimes(date: String): PrayerTimes {
        val prayerTimesRepository = get<PrayerTimesRepository>(PrayerTimesRepository::class.java)

        val listOfPrayerTimes = prayerTimesRepository.retrievePrayerTimesFromDatabaseWithSuspend()

        for (i in listOfPrayerTimes) {
            if (i.date == date) {
                prayerTimes = PrayerTimes(
                    i.date,
                    i.fajr,
                    i.sunrise,
                    i.dhuhr,
                    i.asr,
                    i.maghrib,
                    i.isha
                )
            }
        }
        return prayerTimes
    }
}

