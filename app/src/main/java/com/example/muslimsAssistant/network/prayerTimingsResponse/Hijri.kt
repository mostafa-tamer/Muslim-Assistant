package com.example.muslimsAssistant.network.prayerTimingsResponse

data class Hijri(
    val date: String,
    val month: Month
)

data class Month(
    val ar: String
)