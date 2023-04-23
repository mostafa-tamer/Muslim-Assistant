package com.android.muslimAssistant.fragments.mainFragment

import androidx.lifecycle.ViewModel
import com.android.muslimAssistant.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow

class MainFragmentViewModel(private val sharedPreferencesRepository: SharedPreferencesRepository) :
    ViewModel() {

    suspend fun updateMethod(method: String) {
        sharedPreferencesRepository.updateMethod(method)
    }

    suspend fun updateLanguageSelection(lang: String) {
        sharedPreferencesRepository.updateLanguageSelection(lang)
    }

    fun readMethod(): Flow<String> {
        return sharedPreferencesRepository.method
    }
}