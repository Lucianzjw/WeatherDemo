package com.example.weatherdemo.net

import com.benhu.base.http.BaseRepository
import com.example.weatherdemo.model.ApiResponse


/**
 * @creator: ls
 * @description:
 * @datetime: 2022/8/13 17:25
 */
object WeatherRepository : BaseRepository(), WeatherApi {

    private val weatherApi by lazy { ServiceCreator.withDefault<WeatherApi>() }

    override suspend fun weatherInfo(
        key: String?,
        city: String?,
        extensions: String
    ): ApiResponse {
        return weatherApi.weatherInfo(key,city,extensions)
    }


}