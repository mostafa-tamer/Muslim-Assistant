package com.android.muslimAssistant

import java.text.SimpleDateFormat
import java.util.*

class Timing {
    private val localDefault = Locale.ENGLISH
    private val simpleDateFormatDmyHms = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", localDefault)
    private val simpleDateFormatDmyHm = SimpleDateFormat("dd-MM-yyyy HH:mm", localDefault)
    private val simpleDateFormatDmy = SimpleDateFormat("dd-MM-yyyy", localDefault)
    private val simpleDateFormatHm = SimpleDateFormat("HH:mm", localDefault)
    private val simpleDateFormatHmAm = SimpleDateFormat("h:mm a", localDefault)
    private val simpleDateFormatHms = SimpleDateFormat("HH:mm:ss", localDefault)
    private val utcTimeZone = TimeZone.getTimeZone("UTC")


    fun getTodayDate(): String = simpleDateFormatDmy.format(Calendar.getInstance().time)

    fun getTomorrowDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        return simpleDateFormatDmy.format(calendar.time)
    }

    fun convertDmyHmToMillis(date: String): Long {
        return simpleDateFormatDmyHm.parse(date)?.time ?: 0
    }

    fun convertHmToMillis(hours: Int, minutes: Int): Long {
        return ((hours * 60 + minutes) * 60 * 1000).toLong()
    }

    fun convertDmyHmsToMillis(date: String): Long {
        return simpleDateFormatDmyHms.parse(date)?.time ?: 0
    }

    fun convertMillisToHMS(timeInMillis: Long, format: String): String {
        val sdf = SimpleDateFormat(format, localDefault)
        sdf.timeZone = utcTimeZone
        return sdf.format(Date(timeInMillis))
    }

    fun convertMillisToHmUTC(timeInMillis: Long, format: String): String {
        val sdf = SimpleDateFormat(format, localDefault)
        sdf.timeZone = utcTimeZone
        return sdf.format(Date(timeInMillis))
    }

    fun convertMillisToDmyHm(timeInMillis: Long): String {
        return simpleDateFormatDmyHm.format(Date(timeInMillis))
    }

    fun convertMillisToHmUTC(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", localDefault)
        sdf.timeZone = utcTimeZone
        return sdf.format(Date(timeInMillis))
    }

    fun convertMillisToHm(timeInMillis: Long): String {
        return simpleDateFormatHm.format(Date(timeInMillis))
    }

    fun convertMillisToDmy(timeInMillis: Long): String {
        return simpleDateFormatDmy.format(Date(timeInMillis))
    }

    fun convertHmTo12HrsFormat(time24: String): String {
        val date: Date = simpleDateFormatHm.parse(time24) ?: Date(0)
        return simpleDateFormatHmAm.format(date)
    }

    fun addOneDayToDmy(dateString: String): String {
        val date = simpleDateFormatDmy.parse(dateString) ?: return dateString
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DATE, 1)
        }
        return simpleDateFormatDmy.format(calendar.time)
    }

    fun addOneDayToDmyHm(dateString: String): String {
        val date = simpleDateFormatDmyHm.parse(dateString) ?: return dateString
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DATE, 1)
        }
        return simpleDateFormatDmyHm.format(calendar.time)
    }
}