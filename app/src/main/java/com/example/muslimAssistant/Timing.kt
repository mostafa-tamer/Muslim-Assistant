package com.example.muslimAssistant

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Timing {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val monthFormatter = DateTimeFormatter.ofPattern("MM")
    private val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

    private val localDate: LocalDate = LocalDate.now()

    val todayDate = localDate
        .format(dateFormatter)
        .toString()

    val tomorrowDate = LocalDate
        .parse(localDate.format(dateFormatter), dateFormatter)
        .plusDays(1)
        .format(dateFormatter)
        .toString()

    val currentMonth = localDate.format(monthFormatter).toString()
    val currentYear = localDate.format(yearFormatter).toString()

    val nextMonth = localDate.plusMonths(1).format(monthFormatter).toString()
    val nextYear = localDate.plusYears(1).format(yearFormatter).toString()

    fun convertDateTimeToMillis(date: String): Long {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val localDate: LocalDateTime = LocalDateTime.parse(date, formatter)
        return localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun convertMillisToDateTime(date: Long): String {
        return ""
    }

}

