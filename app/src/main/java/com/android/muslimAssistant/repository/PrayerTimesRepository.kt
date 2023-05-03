package com.android.muslimAssistant.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.android.muslimAssistant.Timing
import com.android.muslimAssistant.database.LatLng
import com.android.muslimAssistant.database.LatLngDao
import com.android.muslimAssistant.database.PrayerTimes
import com.android.muslimAssistant.database.PrayerTimesDao
import com.android.muslimAssistant.network.ApiService
import com.android.muslimAssistant.network.prayerTimingsResponse.Data
import kotlinx.coroutines.flow.first
import org.koin.java.KoinJavaComponent.get
import java.io.IOException

class PrayerTimesRepository(
    private val prayerTimesDataSource: PrayerTimesDao,
    private val userDataSource: LatLngDao,
    private val apiService: ApiService,
) {

    private val timing by lazy { Timing() }


    fun retPrayerTimesLiveData(): LiveData<List<PrayerTimes>> {
        return prayerTimesDataSource.retPrayerTimesLiveData()
    }

    suspend fun retPrayerTimesSuspend() = prayerTimesDataSource.retPrayerTimesSuspend()

    suspend fun updateLatLng(latitude: Double, longitude: Double) {
        val sharedPreferencesRepository =
            get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)
        sharedPreferencesRepository.updateLatLng(latitude, longitude)
    }

    suspend fun retDayPrayerTimes(date: String): PrayerTimes {
        val listOfPrayerTimes = retPrayerTimesSuspend()
        val searchIndex =
            listOfPrayerTimes.binarySearch { it.dateGregorian.compareTo(date) }
        return if (searchIndex >= 0) {
            val item = listOfPrayerTimes[searchIndex]
            PrayerTimes(
                item.dateGregorian,
                item.dateHijri,
                item.monthHijri,
                item.fajr,
                item.sunrise,
                item.dhuhur,
                item.asr,
                item.maghrib,
                item.isha
            )
        } else {
            PrayerTimes()
        }
    }

    suspend fun updatePrayerTimesDatabase() {
        val sharedPreferencesRepository =
            get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)

        val latitude = sharedPreferencesRepository.latitude.first()
        val longitude = sharedPreferencesRepository.longitude.first()
        val method = sharedPreferencesRepository.method.first()

        println("$latitude, $longitude, $method")

        val listOfPrayerTimes: List<Data> =
            getPrayerTimesData(latitude, longitude, method)

        prayerTimesDataSource.insertPrayerTimes(
            listOfPrayerTimes.map { data ->
                PrayerTimes(
                    dateGregorian = data.date.gregorian.date,
                    dateHijri = data.date.hijri.date,
                    monthHijri = if (sharedPreferencesRepository.language.first() == "en") {
                        data.date.hijri.month.en
                    } else {
                        data.date.hijri.month.ar
                    },
                    fajr = data.timings.Fajr,
                    sunrise = data.timings.Sunrise,
                    dhuhur = data.timings.Dhuhr,
                    asr = data.timings.Asr,
                    maghrib = data.timings.Maghrib,
                    isha = data.timings.Isha
                )
            }
        )
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
}