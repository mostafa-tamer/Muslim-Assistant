package com.android.muslimAssistant.repository

import com.android.muslimAssistant.database.ReminderItem
import com.android.muslimAssistant.database.ReminderItemsDao
import com.android.muslimAssistant.utils.CustomList

class RemindersRepository(private val remindersDao: ReminderItemsDao) {

    suspend fun retReminders(): List<ReminderItem> {
        return remindersDao.retReminderItems()
    }

    suspend fun deleteAll() {
        remindersDao.deleteAll()
    }

    suspend fun insertReminderItems(itemsList: CustomList<ReminderItem>) {
        remindersDao.insertReminderItems(itemsList)
    }
}