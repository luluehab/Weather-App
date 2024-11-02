package com.example.weatherapp.database

import android.content.Context
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream


class LocalSource(context: Context) : LocalSourceInterface {

    private val weatherDao: WeatherDao = WeatherDB.getDatabase(context).GetWeatherDao()

     override suspend fun insertLocation(location: LocationData) {

        return weatherDao.insertLocation(location)
    }

     override suspend fun deletePlaceFromFav(location: LocationData) {
         weatherDao.deleteLocationFromFav(location)
    }

     override fun getAllFavouritePlaces(): Flow<List<LocationData>> {
        return weatherDao.getAllFavouriteLocations()
    }


     override suspend fun insertAlarm(alarmItem: AlarmEntity) {
         weatherDao.insertAlarm(alarmItem)
    }

     override suspend fun deleteAlarm(alarmItem: AlarmEntity) {
         weatherDao.deleteAlarm(alarmItem)
    }

     override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return weatherDao.getAllAlarms()
    }
}