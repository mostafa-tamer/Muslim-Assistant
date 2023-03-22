package com.example.muslimsAssistant.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PrayerTimes::class, UserApi::class, ReminderItem::class,Tasbeeh::class],
    version = 1,
//    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(from = 4, to = 5)
//    ],
)
abstract class Database : RoomDatabase() {
    abstract val prayerTimesDao: PrayerTimesDao
    abstract val userDao: UserDao
    abstract val reminderItems: ReminderItemsDao
    abstract val tasbeehDao: TasbeehDao
}

