package com.example.muslimsAssistant.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrayerTimesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(prayerTimeProperties: List<PrayerTimes>)

    @Query("select * from PrayerTimes order by dateGregorian")
    fun retPrayerTimesLiveData(): LiveData<List<PrayerTimes>>

    @Query("select * from PrayerTimes order by dateGregorian")
    suspend fun retPrayerTimesSuspend(): List<PrayerTimes>
}