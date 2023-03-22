package com.example.muslimsAssistant.fragments.tasbeehFragment

import androidx.lifecycle.ViewModel
import com.example.muslimsAssistant.database.Tasbeeh
import com.example.muslimsAssistant.database.TasbeehDao

class TasbeehViewModel(private val tasbeehDao: TasbeehDao) : ViewModel() {

    suspend fun retTasbeehCounter() = tasbeehDao.retTasbeehCounter()
    suspend fun updateTasbeehCounter(value: Int) {
        tasbeehDao.updateTasbeehCounter(Tasbeeh(tasbeehCounter = value))
    }
}