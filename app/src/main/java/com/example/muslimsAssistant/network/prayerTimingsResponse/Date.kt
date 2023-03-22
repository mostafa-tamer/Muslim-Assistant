package com.example.muslimsAssistant.network.prayerTimingsResponse

data class Date(
    val gregorian: Gregorian,
    val readable: String,
    val timestamp: String
)