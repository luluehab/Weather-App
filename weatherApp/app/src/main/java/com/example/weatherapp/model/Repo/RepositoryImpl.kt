package com.example.weatherapp.model.Repo


import android.app.Application
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.DailyForecast
import com.example.iti.data.model.Hourly
import com.example.iti.data.model.Weather
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.database.CountryResponse
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.model.Coordinate
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response


class RepositoryImpl(
    private val remoteDataSource: RemoteSource,
    private val localDataSource: LocalSource,
    private val sharedSetting: SettingViewModel
) : Repository {


    private val lang = sharedSetting.getLanguageSetting()!!
    private val TAG = "track"

    override fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather> {
        return remoteDataSource.getWeather(lat, long , lang)
    }


    override fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly> {
        return remoteDataSource.getHourlyForecast(lat, lon,lang)
    }

    override fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast> {
        return remoteDataSource.getDailyForecast(lat, lon,lang)
    }

    /*override fun fetchCountryData(countryName: String): Flow<CountryResponse?> {
        return remoteDataSource.getCountryData(countryName)
    }*/

    override suspend fun insertPlaceToFav(location: LocationData) {
        return localDataSource.insertLocation(location)
    }

    override fun getAllFavouritePlaces(): Flow<List<LocationData>> {
        return localDataSource.getAllFavouritePlaces()
    }

    override suspend fun deletePlaceFromFav(location: LocationData) {
        return localDataSource.deletePlaceFromFav(location)
    }

    override suspend fun getWeatherCity(cityName: String): WeatherEntity? {
        return localDataSource.getWeatherCity(cityName)
    }

    /*override fun writeStringToSetting(key: String, value: String) {
        sharedSetting.writeStringToSetting(key, value)
    }

    override fun readStringFromSetting(key: String): String {
        return sharedSetting.readStringFromSetting(key)
    }

    override fun writeFloatToSetting(key: String, value: Float) {
        sharedSetting.writeFloatToSetting(key, value)
    }

    override fun readFloatFromSetting(key: String): Float {
        return sharedSetting.readFloatFromSetting(key)
    }*/

    override suspend fun insertAlarm(alarmItem: AlarmEntity) {
        localDataSource.insertAlarm(alarmItem)
    }

    override suspend fun deleteAlarm(alarmItem: AlarmEntity) {
        localDataSource.deleteAlarm(alarmItem)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return localDataSource.getAllAlarms()
    }


    // Companion object to handle repository instantiation
    companion object {
        @Volatile
        private var instance: RepositoryImpl? = null

        // Singleton method to get or create the repository instance
        fun getRepository(remote: RemoteSource , local: LocalSource , sharedSetting: SettingViewModel): RepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: RepositoryImpl(remote,local,sharedSetting ).also { instance = it }
            }
        }

    }
}