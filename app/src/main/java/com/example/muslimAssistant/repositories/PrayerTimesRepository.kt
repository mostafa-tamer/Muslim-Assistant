package com.example.muslimAssistant.repositories

import com.example.muslimAssistant.Timing.currentMonth
import com.example.muslimAssistant.Timing.currentYear
import com.example.muslimAssistant.Timing.nextMonth
import com.example.muslimAssistant.Timing.nextYear
import com.example.muslimAssistant.database.PrayerTimesDao
import com.example.muslimAssistant.database.toDatabase
import com.example.muslimAssistant.network.ApiService
import com.example.muslimAssistant.network.Items

class PrayerTimesRepository(
    private val dataSource: PrayerTimesDao,
    private val apiService: ApiService
) {

    fun retrievePrayerTimesFromDatabaseWithLiveData() = dataSource.getFromLivedata()

    suspend fun retrievePrayerTimesFromDatabaseWithSuspend() = dataSource.getDataFromSuspend()

    suspend fun updatePrayerTimesInDatabase() {

        try {
            val listOfCurrentMonth: List<Items> = getDataFromRetrofit(currentMonth, nextMonth).data

            val listOfNextMonth: List<Items> = if (currentMonth == "12") {
                getDataFromRetrofit(nextMonth, nextYear).data
            } else {
                getDataFromRetrofit(nextMonth, currentYear).data
            }

            cleanData(listOfCurrentMonth)
            cleanData(listOfNextMonth)

            val list =
                listOfCurrentMonth.toDatabase() + listOfNextMonth.toDatabase()

            dataSource.insert(*list)

        } catch (e: Exception) {
            println("Error:" + e.message)
        }
    }

    private fun cleanData(listOfCurrentMonth: List<Items>) {
        for (i in listOfCurrentMonth) {
            i.timings.Fajr = i.timings.Fajr.substring(0, 5) + ":00"
            i.timings.Sunrise = i.timings.Sunrise.substring(0, 5) + ":00"
            i.timings.Dhuhr = i.timings.Dhuhr.substring(0, 5) + ":00"
            i.timings.Asr = i.timings.Asr.substring(0, 5) + ":00"
            i.timings.Maghrib = i.timings.Maghrib.substring(0, 5) + ":00"
            i.timings.Isha = i.timings.Isha.substring(0, 5) + ":00"
        }
    }

    private suspend fun getDataFromRetrofit(getToMonth: String, getToYear: String) =
        apiService.getProperties(
            "cairo",
            "Egypt",
            "5",
            getToMonth,
            getToYear
        )
}