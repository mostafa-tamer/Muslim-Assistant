package com.android.muslimAssistant.network

import com.android.muslimAssistant.network.prayerTimingsResponse.PrayerTimingsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

//    @GET("/v1/calendar")
//    suspend fun getAdanMonthData(
//        @Query("latitude") latitude: Double,
//        @Query("longitude") longitude: Double,
//        @Query("method") method: String,
//        @Query("month") month: String,
//        @Query("year") year: String,
//    ): AdanApiJson

// http://api.aladhan.com/v1/timings/17-07-2007?latitude=51.508515&longitude=-0.1254872&method=2


    @GET("{date}")
    suspend fun getPrayerTimings(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: String
    ): Response<PrayerTimingsResponse>
}