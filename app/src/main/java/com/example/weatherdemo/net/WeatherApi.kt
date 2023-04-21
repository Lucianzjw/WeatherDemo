package com.example.weatherdemo.net

import com.example.weatherdemo.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET(WeatherUrl.weatherCityUrl)
    suspend fun weatherInfo(
        @Query("key") key: String?,
        @Query("city") city: String?,
        @Query("extensions") extensions: String
    ): ApiResponse
}