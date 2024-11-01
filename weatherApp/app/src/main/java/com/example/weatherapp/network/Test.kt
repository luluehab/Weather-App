package com.example.weatherapp.network

import android.util.Log
import com.example.iti.data.model.DailyForecast
import com.example.iti.data.model.Hourly
import com.example.iti.data.model.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Test {
/*
    companion object {
        private const val TAG = "TestClass"

        // Function to fetch and log all weather data
        fun fetchAndLogAllData(lat: Double, lon: Double) {
            // Launch a coroutine on the IO dispatcher for network calls
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get current weather
                    val weatherResponse = RemoteSource.getWeather(lat, lon)
                    if (weatherResponse.isSuccessful) {
                        val weather: Weather? = weatherResponse.body()
                        Log.d(TAG, "Weather: $weather")
                    } else {
                        Log.e(TAG, "Failed to fetch weather data: ${weatherResponse.errorBody()}")
                    }

                    // Get hourly forecast
                    val hourlyResponse = RemoteSource.getHourlyForecast(lat, lon)
                    if (hourlyResponse.isSuccessful) {
                        val hourly: Hourly? = hourlyResponse.body()
                        Log.d(TAG, "Hourly Forecast: $hourly")
                    } else {
                        Log.e(TAG, "Failed to fetch hourly forecast: ${hourlyResponse.errorBody()}")
                    }

                    // Get daily forecast
                    val dailyResponse = RemoteSource.getDailyForecast(lat, lon)
                    if (dailyResponse.isSuccessful) {
                        val daily: DailyForecast? = dailyResponse.body()
                        Log.d(TAG, "Daily Forecast: $daily")
                    } else {
                        Log.e(TAG, "Failed to fetch daily forecast: ${dailyResponse.errorBody()}")
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Error occurred: ${e.message}")
                }
            }
        }
    }*/
}