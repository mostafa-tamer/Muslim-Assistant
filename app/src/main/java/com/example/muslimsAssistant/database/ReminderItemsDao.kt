package com.example.muslimsAssistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReminderItemsDao {
    @Insert
    suspend fun insertReminderItem(reminderItem: ReminderItem)
    @Insert
    suspend fun insertReminderItems(reminderItem: List<ReminderItem>)
    @Query("delete from reminderItem")
    suspend fun deleteAll()
    @Query("select * from reminderItem")
    suspend fun retReminderItems(): List<ReminderItem>
}