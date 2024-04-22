package com.example.desimausam.Interfaces

import com.example.desimausam.Data.DailyForcast.DailyForcastData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrentWeather{
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<DailyForcastData>
}