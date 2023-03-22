package com.example.muslimsAssistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TasbeehDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTasbeehCounter(value: Tasbeeh)

    @Query("select * from Tasbeeh")
    suspend fun retTasbeehCounter(): Tasbeeh?
}