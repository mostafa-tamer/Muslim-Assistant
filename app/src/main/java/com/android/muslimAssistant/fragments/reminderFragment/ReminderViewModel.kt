package com.android.muslimAssistant.fragments.reminderFragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.muslimAssistant.database.ReminderItem
import com.android.muslimAssistant.notifications.AlarmHandler
import com.android.muslimAssistant.repository.RemindersRepository
import com.android.muslimAssistant.utils.CustomList
import kotlinx.coroutines.launch

class ReminderViewModel(private val remindersRepository: RemindersRepository) : ViewModel() {

    val isBusy = MutableLiveData(false)

    suspend fun retData(): List<ReminderItem> = remindersRepository.retReminders()

    fun updateDB(itemsList: CustomList<ReminderItem>, context: Context) {
        if (isBusy.value!!) return
        isBusy.value = true
        viewModelScope.launch {
            remindersRepository.deleteAll()
            remindersRepository.insertReminderItems(itemsList)
            AlarmHandler(context).scheduleAlarms()
            isBusy.value = false
        }
    }
}
