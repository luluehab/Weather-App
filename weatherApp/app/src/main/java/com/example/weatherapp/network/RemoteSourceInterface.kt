package com.example.weatherapp.network

import com.example.iti.data.model.DailyForecast
import com.example.iti.data.model.Hourly
import com.example.iti.data.model.Weather
import com.example.weatherapp.database.CountryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RemoteSourceInterface {
    fun getWeather(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Weather>

    fun getHourlyForecast(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Hourly>

    fun getDailyForecast(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<DailyForecast>

}