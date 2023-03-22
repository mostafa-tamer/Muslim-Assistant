package com.example.muslimsAssistant.fragments.reminderFragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muslimsAssistant.database.ReminderItem
import com.example.muslimsAssistant.database.ReminderItemsDao
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import com.example.muslimsAssistant.utils.CustomList
import kotlinx.coroutines.launch

class ReminderViewModel(private val reminderItemsDao: ReminderItemsDao) : ViewModel() {

    val isBusy = MutableLiveData(false)


    suspend fun retData(): List<ReminderItem> = reminderItemsDao.retReminderItems()
    fun updateDB(itemsList: CustomList<ReminderItem>, context: Context) {
        if (isBusy.value!!) return
        isBusy.value = true
        viewModelScope.launch {
            reminderItemsDao.deleteAll()
            reminderItemsDao.insertReminderItems(itemsList)
            AlarmManagerHelper(context).setPrayerTimes()
            isBusy.value = false
        }
    }

}
