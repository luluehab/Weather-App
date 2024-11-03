package com.example.weatherapp.database

import android.content.Context
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream


class LocalSource(private val weatherDao: WeatherDao) : LocalSourceInterface {

   // private val weatherDao: WeatherDao = WeatherDB.getDatabase(context).GetWeatherDao()

     override suspend fun insertLocation(location: LocationData) {

         withContext(Dispatchers.IO) {
             weatherDao.insertLocation(location)
         }
    }

     override suspend fun deletePlaceFromFav(location: LocationData) {
         withContext(Dispatchers.IO) {
             weatherDao.deleteLocationFromFav(location)
         }
    }

     override fun getAllFavouritePlaces(): Flow<List<LocationData>> {
        return weatherDao.getAllFavouriteLocations()
    }


     override suspend fun insertAlarm(alarmItem: AlarmEntity) {
         withContext(Dispatchers.IO) {
             weatherDao.insertAlarm(alarmItem)
         }
    }

     override suspend fun deleteAlarm(alarmItem: AlarmEntity) {
         withContext(Dispatchers.IO) {
             weatherDao.deleteAlarm(alarmItem)
         }
    }

     override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return weatherDao.getAllAlarms()
    }
}