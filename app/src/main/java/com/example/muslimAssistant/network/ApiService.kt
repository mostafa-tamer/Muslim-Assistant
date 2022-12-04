package com.example.muslimAssistant.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/v1/calendarByCity")
    suspend fun getProperties(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: String,
        @Query("month") month: String,
        @Query("year") year: String,
    ): PrayerProperties
}