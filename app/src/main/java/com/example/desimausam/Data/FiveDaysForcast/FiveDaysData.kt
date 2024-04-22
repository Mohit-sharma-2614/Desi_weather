package com.example.desimausam.Data.FiveDaysForcast

data class FiveDaysData(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WhichDay>,
    val message: Int
)