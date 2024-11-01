package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: LocationData)

    @Delete
    suspend fun deleteLocationFromFav(location: LocationData)

    @Query("SELECT * FROM favlocations")
    fun getAllFavouriteLocations(): Flow<List<LocationData>>



    @Query("SELECT * FROM Weather")
    fun getAllWeatherData(): Flow<List<WeatherEntity>>

    @Delete
    suspend fun deleteWeather(weather: WeatherEntity)

    @Query("SELECT * FROM Weather WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeatherByCity(cityName: String): WeatherEntity?



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmItem: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarmItem: AlarmEntity)

    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}