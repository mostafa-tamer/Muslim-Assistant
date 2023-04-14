package com.example.muslimsAssistant.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LatLngDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatLng(location: LatLng)

    @Query("select * from LatLng")
    fun retUserLiveData(): LiveData<List<LatLng>>

    @Query("select * from LatLng")
    suspend fun retUserSuspend(): LatLng?
}