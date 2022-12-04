package com.example.muslimAssistant.database

import com.example.muslimAssistant.network.Items

fun List<Items>.toDatabase(): Array<PrayerTimes> {
    return map {
        PrayerTimes(
            date = it.date.gregorian.date,
            fajr = it.timings.Fajr,
            sunrise = it.timings.Sunrise,
            dhuhr = it.timings.Dhuhr,
            asr = it.timings.Asr,
            maghrib = it.timings.Maghrib,
            isha = it.timings.Isha
        )
    }.toTypedArray()
}