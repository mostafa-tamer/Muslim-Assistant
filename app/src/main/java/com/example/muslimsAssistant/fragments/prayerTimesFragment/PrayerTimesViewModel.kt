package com.example.muslimsAssistant.fragments.prayerTimesFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcodeReader.utils.ErrorMessage
import com.example.muslimsAssistant.repository.PrayerTimesRepository
import kotlinx.coroutines.launch
import java.io.IOException

class PrayerTimesViewModel(
    private val repository: PrayerTimesRepository
) : ViewModel() {

    val errorMessageLiveData = MutableLiveData(ErrorMessage())
    val isLoadingPrayerTimes = MutableLiveData(false)

    val dateHijri = MutableLiveData<String>()
    val monthHijri = MutableLiveData<String>()
    val fajr = MutableLiveData<String>()
    val sunrise = MutableLiveData<String>()
    val dhuhur = MutableLiveData<String>()
    val asr = MutableLiveData<String>()
    val maghrib = MutableLiveData<String>()
    val isha = MutableLiveData<String>()

    suspend fun retPrayerTimesSuspend() = repository.retPrayerTimesSuspend()
    fun retPrayerTimesLiveData() = repository.retPrayerTimesLiveData()

    fun updateUserLocationAndPrayerTimes(latitude: Double, longitude: Double) {
        isLoadingPrayerTimes.value = true
        viewModelScope.launch {
            try {
                repository.updateLatLng(latitude, longitude)
                repository.updatePrayerTimesDatabase()
                errorMessageLiveData.value = ErrorMessage()
            } catch (e: IOException) {
                println("Exception in PrayerTimesViewModel => updatePrayerTimesDatabase(): " + e.message)
                errorMessageLiveData.value =
                    ErrorMessage(true, "Error", "Error on downloading data")
            } catch (e: Exception) {
                println("Exception in PrayerTimesViewModel => updatePrayerTimesDatabase(): " + e.message)
                errorMessageLiveData.value =
                    ErrorMessage(true, "Error", "Error occurred")
            }
            isLoadingPrayerTimes.value = false
        }
    }

    fun retUser() = repository.retUser()
}
