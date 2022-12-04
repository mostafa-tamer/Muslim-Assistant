package com.example.muslimAssistant.prayerTimesFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muslimAssistant.database.PrayerTimes
import com.example.muslimAssistant.repositories.PrayerTimesRepository
import kotlinx.coroutines.launch

class PrayerTimesViewModel(private val prayerTimesRepository: PrayerTimesRepository) : ViewModel() {

    private var _prayerTimesList: LiveData<List<PrayerTimes>> =
        retrievePrayerTimesFromDatabase()
    val prayerTimesList get() = _prayerTimesList

    val date = MutableLiveData<String>()
    val remain = MutableLiveData<String>()
    val fajr = MutableLiveData<String>()
    val sunrise = MutableLiveData<String>()
    val dhuhr = MutableLiveData<String>()
    val asr = MutableLiveData<String>()
    val maghrib = MutableLiveData<String>()
    val isha = MutableLiveData<String>()


    init {
        updatePrayerTimesDatabase()
    }

    private fun updatePrayerTimesDatabase() {
        viewModelScope.launch {
            prayerTimesRepository.updatePrayerTimesInDatabase()
        }
    }

    private fun retrievePrayerTimesFromDatabase() =
        prayerTimesRepository.retrievePrayerTimesFromDatabaseWithLiveData()


}
