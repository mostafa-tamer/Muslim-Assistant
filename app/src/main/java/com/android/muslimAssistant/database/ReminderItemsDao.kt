package com.android.muslimAssistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReminderItemsDao {
    @Insert
    suspend fun insertReminderItem(reminderItemEveryDay: ReminderItem)

    @Insert
    suspend fun insertReminderItems(reminderItemEveryDay: List<ReminderItem>)

    @Query("delete from reminderItem")
    suspend fun deleteAll()

    @Query("select * from reminderItem")
    suspend fun retReminderItems(): List<ReminderItem>
}