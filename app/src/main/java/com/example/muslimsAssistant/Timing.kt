package com.example.muslimsAssistant

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Timing {
    private val localDefault = Locale.getDefault()
    private val fullDateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", localDefault)
    private val dateTimeFormatNoSecs = SimpleDateFormat("dd-MM-yyyy HH:mm", localDefault)
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", localDefault)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", localDefault)


    init {
        println("Today Date: " + getTodayDate())
        println("Tomorrow Date: " + getTomorrowDate())
        println("Current Month: " + getCurrentMonth())
        println("Year Of Current Month: " + getYearOfCurrentMonth())
        println("Next Month: " + getNextMonth())
        println("Year Of Next Month: " + getYearOfNextMonth())
    }

    fun getTodayDate(): String = dateFormat.format(Calendar.getInstance().time)

    fun getTomorrowDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        return dateFormat.format(calendar.time)
    }

    fun getCurrentMonth(): String {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        return if (currentMonth == 12) {
            "12"
        } else {
            currentMonth.toString()
        }
    }

    fun getNextMonth(): String {
        return if (getCurrentMonth() == "12")
            "1"
        else
            (getCurrentMonth().toInt() + 1).toString()
    }

    fun getYearOfCurrentMonth(): String = Calendar.getInstance().get(Calendar.YEAR).toString()

    fun getYearOfNextMonth(): String {
        return if (getNextMonth() == "1")
            (getYearOfCurrentMonth().toInt() + 1).toString()
        else
            getYearOfCurrentMonth()
    }

    fun convertDateTimeNoSecToMillisNoSec(date: String): Long =
        dateTimeFormatNoSecs.parse(date)!!.time

    fun convertFullDateTimeNoSecToMillis(date: String): Long = fullDateTimeFormat.parse(date)!!.time
    fun convertMillisToHMS(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    fun convertTo12HourFormat(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm\na", Locale.getDefault())
        val date = inputFormat.parse(time24)
        return outputFormat.format(date)
    }
    fun addOneDayToDate(dateString: String): String {
        val date = dateFormat.parse(dateString) ?: return dateString
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DATE, 1)
        }
        return dateFormat.format(calendar.time)
    }

    fun addOneDayToDateTime(dateString: String): String {
        val date = dateTimeFormatNoSecs.parse(dateString) ?: return dateString
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DATE, 1)
        }
        return dateTimeFormatNoSecs.format(calendar.time)
    }
}


