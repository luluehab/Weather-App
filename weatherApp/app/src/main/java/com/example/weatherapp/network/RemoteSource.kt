package com.example.weatherapp.network

import androidx.fragment.app.viewModels
import com.example.iti.data.model.DailyForecast
import com.example.iti.data.model.Hourly
import com.example.iti.data.model.Weather
import com.example.weatherapp.database.CountryResponse
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import com.example.weatherapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response

import androidx.fragment.app.viewModels
class RemoteSource(private val apiService: ApiServices ) : RemoteSourceInterface {


    override fun getWeather(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Weather> = flow {
        // return apiService.getWeather(lat , lon, Constants.API_KEY, Constants.UNITS,Constants.ENGLISH_SHARED)

        val response = apiService.getWeather(
            lat,
            lon,
            Constants.API_KEY,
            Constants.UNITS,
            lang
        )
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Throwable("Error retrieving weather data")
        }

    }

    override fun getHourlyForecast(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Hourly> = flow {
        //return apiService.getHourlyForecast(lat , lon, Constants.API_KEY, Constants.UNITS,Constants.ENGLISH_SHARED)
        val response = apiService.getHourlyForecast(
            lat,
            lon,
            Constants.API_KEY,
            Constants.UNITS,
            lang
        )
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Throwable("Error retrieving weather data")
        }
    }

    override fun getDailyForecast(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<DailyForecast> = flow {
        // return apiService.getDailyForecast(lat , lon,Constants.API_KEY, Constants.UNITS,Constants.ENGLISH_SHARED)
        val response = apiService.getDailyForecast(
            lat,
            lon,
            Constants.API_KEY,
            Constants.UNITS,
            lang
        )
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Throwable("Error retrieving weather data")
        }
    }

   /* override fun getCountryData(countryName: String): Flow<CountryResponse?> = flow {
        try {
            val response = CountryClient.instance.getCountry(countryName, Country_API_KEY)
            if (response.isSuccessful) {
                emit(response.body()) // Emit the country response
            } else {
                emit(null) // Emit null on error
            }
        } catch (e: HttpException) {
            // Handle HTTP exceptions
            emit(null)
        } catch (e: Exception) {
            // Handle other exceptions
            emit(null)
        }
    }*/
}