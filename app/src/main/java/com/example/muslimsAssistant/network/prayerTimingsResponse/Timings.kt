package com.example.muslimsAssistant.network.prayerTimingsResponse

data class Timings(
    var Asr: String,
    var Dhuhr: String,
    var Fajr: String,
    val Firstthird: String,
    val Imsak: String,
    var Isha: String,
    val Lastthird: String,
    var Maghrib: String,
    val Midnight: String,
    var Sunrise: String,
    val Sunset: String
)