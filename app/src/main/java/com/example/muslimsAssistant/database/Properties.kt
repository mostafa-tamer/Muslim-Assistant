package com.example.muslimsAssistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PrayerTimes(
    @PrimaryKey
    val dateGregorian: String = "01-01-1970",
    val dateHigri: String = "01-01-1970",
    val monthHijri: String = "محرم",
    val fajr: String = "00:00:00",
    val sunrise: String = "00:00:00",
    val dhuhr: String = "00:00:00",
    val asr: String = "00:00:00",
    val maghrib: String = "00:00:00",
    val isha: String = "00:00:00",
)

@Entity
data class UserApi(
    @PrimaryKey
    val name: String = "User",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val method: String = "5"
)

@Entity
data class Tasbeeh(
    @PrimaryKey
    val id: Int = 0,
    val tasbeehCounter: Int
)

@Entity
data class ReminderItem(
    @PrimaryKey
    val id: Int,
    val description: String,
    val hours: String,
    val minutes: String,
)


