package com.example.muslimsAssistant

import java.text.SimpleDateFormat
import java.util.*

object Timing {

    private val fullDateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    private val dateTimeFormatNoSecs = SimpleDateFormat("dd-MM-yyyy HH:mm")
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")


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

    fun convertDateTimeNoSecToMillisNoSec(date: String): Long = dateTimeFormatNoSecs.parse(date)!!.time
    fun convertFullDateTimeNoSecToMillis(date: String): Long = fullDateTimeFormat.parse(date)!!.time
    fun convertMillisToFullDateTime(millis: Long): String = fullDateTimeFormat.format(Date(millis))

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


