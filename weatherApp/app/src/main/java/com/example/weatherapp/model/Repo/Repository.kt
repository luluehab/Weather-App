package com.example.weatherapp.model.Repo

import android.location.Geocoder
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.DailyForecast
import com.example.iti.data.model.Hourly
import com.example.iti.data.model.Weather
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.database.CountryResponse
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow

interface Repository {


    //remote methods
    fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather>
    fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly>
    fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast>
   // fun fetchCountryData(countryName: String): Flow<CountryResponse?>


    //local database
    suspend fun insertPlaceToFav(location: LocationData)
    fun getAllFavouritePlaces(): Flow<List<LocationData>>
    suspend fun deletePlaceFromFav(location: LocationData)


    // For Alert
    suspend fun insertAlarm(alarmItem: AlarmEntity)
    suspend fun deleteAlarm(alarmItem: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}