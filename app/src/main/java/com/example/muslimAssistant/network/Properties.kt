package com.example.muslimAssistant.network

data class PrayerProperties(
    var data: List<Items>
)

data class Items(
    var timings: Times,
    var date: Dates
)

data class Dates(
    var gregorian: Gregorian
)

data class Gregorian(
    var date: String
)

data class Times(
    var Fajr: String = "null",
    var Sunrise: String = "null",
    var Dhuhr: String = "null",
    var Asr: String = "null",
    var Maghrib: String = "null",
    var Isha: String = "null",
)