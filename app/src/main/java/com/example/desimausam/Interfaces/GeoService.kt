package com.example.desimausam.Interfaces

import com.example.desimausam.Data.LongLati.LocationNameData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//http://api.openweathermap.org/geo/1.0/zip?zip=333035,IN&appid=abd309d126d0ab6a54ca6b4ef80f63c9

interface GeoService {
    @GET("geo/1.0/zip")
    fun getCordinates(
        @Query("zip") zip : String,
        @Query("appid")appid : String
    ):Call<LocationNameData>
}