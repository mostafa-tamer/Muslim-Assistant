package com.example.muslimAssistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PrayerTimes(
    @PrimaryKey
    val date: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
)
