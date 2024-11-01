package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData


@Database(entities = arrayOf(WeatherEntity::class , LocationData::class , AlarmEntity::class), version = 3)
abstract class WeatherDB : RoomDatabase(){

    abstract fun GetWeatherDao(): WeatherDao
  //  abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDB? = null

        fun getDatabase(context: Context): WeatherDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDB::class.java,
                    "Weather_DB"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}