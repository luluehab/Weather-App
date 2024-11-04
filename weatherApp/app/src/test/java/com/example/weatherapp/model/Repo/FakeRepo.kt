package com.example.weatherapp.model.Repo

import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.DailyForecast
import com.example.iti.data.model.Hourly
import com.example.iti.data.model.Weather
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepo :Repository {


    private val favoritePlaces = mutableListOf<LocationData>()
    private val alarms = mutableListOf<AlarmEntity>()

    val location = LocationData( "Cairo", 30.0, 31.0)
    val location1 = LocationData( "Vienna", 31.0, 32.0)
    val location2 = LocationData( "Roma", 32.0, 33.0)
    override fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather> {
        TODO("Not yet implemented")
    }

    override fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly> {
        TODO("Not yet implemented")
    }

    override fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlaceToFav(location: LocationData) {
        favoritePlaces.add(location)
    }

    override fun getAllFavouritePlaces(): Flow<List<LocationData>> {
        return flow { emit(favoritePlaces) } // Emit the current list of favorite places
    }

    override suspend fun deletePlaceFromFav(location: LocationData) {
        favoritePlaces.remove(location)
    }

    override suspend fun insertAlarm(alarmItem: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarmItem: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        TODO("Not yet implemented")
    }
}