package com.example.muslimsAssistant.repository

import com.example.muslimsAssistant.database.ReminderItem
import com.example.muslimsAssistant.database.ReminderItemsDao
import com.example.muslimsAssistant.utils.CustomList

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