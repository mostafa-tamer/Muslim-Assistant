package com.android.muslimAssistant

enum class PermissionsCodes(val code: Int) {
    ACCESS_COARSE_LOCATION(100)
}

enum class LocationCodes(val code: Int) {
    REQUEST_LOCATION_SERVICE(200)
}

enum class PendingIntentCodes(val code: Int) {
    SET_CONTENT_INTENT(600),
    NOTIFICATION_ACTION(700)
}

enum class NotificationCodes(val code: Int) {
    PrayerTimes(1000),
    Azkar(1400),
    Reminders(1500),
}

abstract class PrayerTimesPendingIntentCodes {
    abstract val fajr: Int
    abstract val dhuhur: Int
    abstract val asr: Int
    abstract val maghrib: Int
    abstract val isha: Int
}

class TodayPrayerTimesPendingIntentCodes : PrayerTimesPendingIntentCodes() {
    override val fajr: Int = 1500
    override val dhuhur: Int = 1600
    override val asr: Int = 1700
    override val maghrib: Int = 1800
    override val isha: Int = 1900
}

class TomorrowPrayerTimesPendingIntentCodes : PrayerTimesPendingIntentCodes() {
    override val fajr: Int = 2000
    override val dhuhur: Int = 2100
    override val asr: Int = 2200
    override val maghrib: Int = 2300
    override val isha: Int = 2400
}

enum class ChannelIDs(val ID: String) {
    PRIORITY_MAX("priority_max"),
    PRIORITY_MIN("priority_min"),
}
