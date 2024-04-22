package com.example.desimausam.Interfaces

import com.example.desimausam.Data.FiveDaysForcast.FiveDaysData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FiveDaysWeather {
    @GET("data/2.5/forecast")
    fun getFiveDaysWeather(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") unite: String = "metric"
    ): Call<FiveDaysData>
}