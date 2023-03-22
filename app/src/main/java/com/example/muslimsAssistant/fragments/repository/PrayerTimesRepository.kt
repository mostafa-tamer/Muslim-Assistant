package com.example.muslimsAssistant.fragments.repository

import androidx.lifecycle.LiveData
import com.example.muslimsAssistant.Timing.addOneDayToDate
import com.example.muslimsAssistant.Timing.getTodayDate
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.database.PrayerTimesDao
import com.example.muslimsAssistant.database.UserApi
import com.example.muslimsAssistant.database.UserDao
import com.example.muslimsAssistant.network.ApiService
import com.example.muslimsAssistant.network.prayerTimingsResponse.Data
import java.io.IOException

class PrayerTimesRepository(
    private val prayerTimesDataSource: PrayerTimesDao,
    private val userDataSource: UserDao,
    private val apiService: ApiService,
) {

    fun retPrayerTimesLiveData() = prayerTimesDataSource.retPrayerTimesLiveData()

    suspend fun retPrayerTimesSuspend() = prayerTimesDataSource.retPrayerTimesSuspend()

    suspend fun updateUser(latitude: Double, longitude: Double) {
        userDataSource.insertUser(
            UserApi(
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    suspend fun updatePrayerTimesDatabase() {
        userDataSource.retUserSuspend()?.let { user ->
            val listOfPrayerTimes: List<Data> =
                getPrayerTimesData(user.latitude, user.longitude, user.method)

            prayerTimesDataSource.insertPrayerTimes(listOfPrayerTimes.map {
                PrayerTimes(
                    date = it.date.gregorian.date,
                    fajr = it.timings.Fajr,
                    sunrise = it.timings.Sunrise,
                    dhuhr = it.timings.Dhuhr,
                    asr = it.timings.Asr,
                    maghrib = it.timings.Maghrib,
                    isha = it.timings.Isha
                )
            })
        }
    }

    private suspend fun getPrayerTimesData(
        latitude: Double, longitude: Double, method: String
    ): List<Data> {

        var dateIterator = getTodayDate()
        val prayerTimesForMonth = mutableListOf<Data>()

        for (i in 1..7) {
            val prayerTimesResponse = apiService.getPrayerTimings(
                dateIterator, latitude, longitude, method
            )

            if (prayerTimesResponse.code() == 200) {
                if (prayerTimesResponse.body() != null) {
                    prayerTimesForMonth += prayerTimesResponse.body()!!.data.apply {
                        this.timings.Fajr = this.timings.Fajr.substring(0, 5)
                        this.timings.Sunrise = this.timings.Sunrise.substring(0, 5)
                        this.timings.Dhuhr = this.timings.Dhuhr.substring(0, 5)
                        this.timings.Asr = this.timings.Asr.substring(0, 5)
                        this.timings.Maghrib = this.timings.Maghrib.substring(0, 5)
                        this.timings.Isha = this.timings.Isha.substring(0, 5)
                    }
                } else {
                    throw IOException("Body == null")
                }
            } else {
                throw IOException("Status code != 200")
            }

            dateIterator = addOneDayToDate(dateIterator)
        }
        return prayerTimesForMonth
    }

    fun retUser(): LiveData<List<UserApi>> = userDataSource.retUserLiveData()
}