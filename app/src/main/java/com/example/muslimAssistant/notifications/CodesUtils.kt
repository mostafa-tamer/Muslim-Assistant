package com.example.muslimAssistant.notifications

enum class NotificationCodes(val code: Int) {
    PrayerTimes(1000),
    DOWNLOAD_DATA_WORKER_ERROR(1200),
    PRAYER_TIMES_WORKER_ERROR(1300),
    Azkar(1400)
}

abstract class PrayerTimesPendingIntentCodes {
    abstract val fajr: Int
    abstract val sunrise: Int
    abstract val duhur: Int
    abstract val asr: Int
    abstract val magrib: Int
    abstract val isha: Int
}

class TodayPrayerTimesPendingIntentCodes : PrayerTimesPendingIntentCodes() {
    override val fajr: Int = 1500
    override val sunrise: Int = 1600
    override val duhur: Int = 1700
    override val asr: Int = 1800
    override val magrib: Int = 1900
    override val isha: Int = 2000
}

class TomorrowPrayerTimesPendingIntentCodes : PrayerTimesPendingIntentCodes() {
    override val fajr: Int = 2100
    override val sunrise: Int = 2200
    override val duhur: Int = 2300
    override val asr: Int = 2400
    override val magrib: Int = 2500
    override val isha: Int = 2600
}

enum class PendingIntentCodes(val code: Int) {
    SET_CONTENT_INTENT(600),
    CLEAR_NOTIFICATIONS_ACTION(700)
}

enum class ChannelIDs(val ID: String) {
    PRIORITY_MAX("priority_max")
}