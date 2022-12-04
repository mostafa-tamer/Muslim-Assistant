package com.example.muslimAssistant.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PrayerTimesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg prayerTimeProperties: PrayerTimes)

    @Query("select * from PrayerTimes")
    fun getFromLivedata(): LiveData<List<PrayerTimes>>

    @Query("select * from PrayerTimes")
    suspend fun getDataFromSuspend(): List<PrayerTimes>
}

@Database(entities = [PrayerTimes::class], version = 1)
abstract class PrayerTimesDatabase : RoomDatabase() {

    abstract val dataSource: PrayerTimesDao
}