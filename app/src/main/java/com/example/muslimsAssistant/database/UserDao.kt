package com.example.muslimsAssistant.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userApi: UserApi)

    @Query("select * from UserApi")
    fun retUserLiveData(): LiveData<List<UserApi>>

    @Query("select * from UserApi where UserApi.name = 'User'")
    suspend fun retUserSuspend(): UserApi?


}