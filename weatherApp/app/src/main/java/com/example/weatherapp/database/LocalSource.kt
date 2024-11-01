package com.example.weatherapp.database

import android.content.Context
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class LocalSource(context: Context) {
    private val weatherDao: WeatherDao = WeatherDB.getDatabase(context).GetWeatherDao()
     suspend fun insertLocation(location: LocationData) {

        return weatherDao.insertLocation(location)
    }

     suspend fun deletePlaceFromFav(location: LocationData) {
         weatherDao.deleteLocationFromFav(location)
    }

     fun getAllFavouritePlaces(): Flow<List<LocationData>> {
        return weatherDao.getAllFavouriteLocations()
    }

     fun getAllWeatherData(): Flow<List<WeatherEntity>> {
        return weatherDao.getAllWeatherData()
    }

     suspend fun deleteWeather(weather: WeatherEntity) {
        return weatherDao.deleteWeather(weather)
    }

     suspend fun getWeatherCity(cityName: String): WeatherEntity? {
        return weatherDao.getWeatherByCity(cityName)
    }

    fun readExcelFile(context: Context): List<List<String>> {
        val data = mutableListOf<List<String>>()
        val assetManager = context.assets

        try {
            val inputStream: InputStream = assetManager.open("data.xlsx")
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // Get the first sheet

            for (row in sheet) {
                val rowData = mutableListOf<String>()
                for (cell in row) {
                    rowData.add(cell.toString()) // Convert cell value to String
                }
                data.add(rowData)
            }

            workbook.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return data
    }


     suspend fun insertAlarm(alarmItem: AlarmEntity) {
         weatherDao.insertAlarm(alarmItem)
    }

     suspend fun deleteAlarm(alarmItem: AlarmEntity) {
         weatherDao.deleteAlarm(alarmItem)
    }

     fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return weatherDao.getAllAlarms()
    }
}