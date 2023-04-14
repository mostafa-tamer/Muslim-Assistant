package com.example.muslimsAssistant.repository

import androidx.lifecycle.LiveData
import com.example.muslimsAssistant.Timing
import com.example.muslimsAssistant.database.LatLng
import com.example.muslimsAssistant.database.LatLngDao
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.database.PrayerTimesDao
import com.example.muslimsAssistant.network.ApiService
import com.example.muslimsAssistant.network.prayerTimingsResponse.Data
import java.io.IOException

class PrayerTimesRepository(
    private val prayerTimesDataSource: PrayerTimesDao,
    private val userDataSource: LatLngDao,
    private val apiService: ApiService,
) {

    private val timing  by lazy { Timing() }

    fun retPrayerTimesLiveData() = prayerTimesDataSource.retPrayerTimesLiveData()

    suspend fun retPrayerTimesSuspend() = prayerTimesDataSource.retPrayerTimesSuspend()

    suspend fun updateUser(latitude: Double, longitude: Double) {
        userDataSource.insertLatLng(
            LatLng(
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    suspend fun retDayPrayerTimes(date: String): PrayerTimes {
        val listOfPrayerTimes = retPrayerTimesSuspend()
        val searchIndex =
            listOfPrayerTimes.binarySearch { it.dateGregorian.compareTo(date) }
        return if (searchIndex >= 0) {
            val item = listOfPrayerTimes[searchIndex]
            PrayerTimes(
                item.dateGregorian,
                item.dateHigri,
                item.monthHijri,
                item.fajr,
                item.sunrise,
                item.dhuhr,
                item.asr,
                item.maghrib,
                item.isha
            )
        } else {
            PrayerTimes()
        }
    }

    suspend fun updatePrayerTimesDatabase() {
        userDataSource.retUserSuspend()?.let { latLng ->
            val listOfPrayerTimes: List<Data> =
                getPrayerTimesData(latLng.latitude, latLng.longitude, "5")

            prayerTimesDataSource.insertPrayerTimes(listOfPrayerTimes.map {
                PrayerTimes(
                    dateGregorian = it.date.gregorian.date,
                    dateHigri = it.date.hijri.date,
                    monthHijri = it.date.hijri.month.en,
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

        var dateIterator = timing.getTodayDate()
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

            dateIterator = timing.addOneDayToDmy(dateIterator)
        }
        return prayerTimesForMonth
    }

    fun retUser(): LiveData<List<LatLng>> = userDataSource.retUserLiveData()
}