package com.example.muslimsAssistant

enum class NotificationCodes(val code: Int) {
    PrayerTimes(1000),
    DOWNLOAD_DATA_WORKER_ERROR(1200),
    PRAYER_TIMES_WORKER_ERROR(1300),
    Azkar(1400),
    Reminder(1500)
}

enum class PermissionsCodes(val code: Int) {
    ACCESS_COARSE_LOCATION(100)
}

enum class PendingIntentCodes(val code: Int) {
    SET_CONTENT_INTENT(600),
    NOTIFICATION_ACTION(700)
}

enum class ChannelIDs(val ID: String) {
    PRIORITY_MAX("priority_max")
}

enum class PrayerTimesPendingIntentCodes(val code: Int) {
    FAJR(1500),
    SUNRISE(1600),
    DUHUR(1700),
    ASR(1800),
    MAGRIB(1900),
    ISHA(2000),
}
