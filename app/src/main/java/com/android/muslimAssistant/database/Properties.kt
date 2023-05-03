package com.android.muslimAssistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PrayerTimes(
    @PrimaryKey
    var dateGregorian: String = "01-01-1970",
    var dateHijri: String = "01-01-1970",
    var monthHijri: String = "Muharram",
    var fajr: String = "00:00:00",
    var sunrise: String = "00:00:00",
    var dhuhur: String = "00:00:00",
    var asr: String = "00:00:00",
    var maghrib: String = "00:00:00",
    var isha: String = "00:00:00",
)

@Entity
data class LatLng(
    @PrimaryKey
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

@Entity
data class Tasbeeh(
    @PrimaryKey
    val id: Int = 0,
    val tasbeehCounter: Int
)

@Entity
class ReminderItem(
    @PrimaryKey
    val id: Int,
    val description: String,
    val hours: Int,
    val minutes: Int,
    val timeString: String,
    val type: Boolean
)