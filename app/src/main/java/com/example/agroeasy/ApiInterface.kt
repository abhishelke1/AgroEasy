package com.example.agroeasy

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface ApiInterface {
    // This method is for fetching weather data by city name
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<agroeasy>

    // This method is for fetching weather data by latitude and longitude
    @GET("weather")
    fun getWeatherDataByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<agroeasy>
}
