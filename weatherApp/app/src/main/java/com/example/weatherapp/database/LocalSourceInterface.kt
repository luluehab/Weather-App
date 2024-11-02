package com.example.weatherapp.database

import android.content.Context
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow

interface LocalSourceInterface {
    suspend fun insertLocation(location: LocationData)
    suspend fun deletePlaceFromFav(location: LocationData)
    fun getAllFavouritePlaces(): Flow<List<LocationData>>


    suspend fun insertAlarm(alarmItem: AlarmEntity)
    suspend fun deleteAlarm(alarmItem: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}