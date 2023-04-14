package com.example.muslimsAssistant.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PrayerTimes::class, LatLng::class, ReminderItem::class, Tasbeeh::class],
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract val prayerTimesDao: PrayerTimesDao
    abstract val latLngDao: LatLngDao
    abstract val remindersDao: ReminderItemsDao
    abstract val tasbeehDao: TasbeehDao
}

