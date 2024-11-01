package com.example.weatherapp.ui.setting.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.weatherapp.ui.splash.view.MainActivity
import com.example.weatherapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale

class SettingViewModel  (application: Application) : ViewModel(){
    private val sharedPref = application.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_CODE: Int = 100
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var countryName: String = ""
    private var adminArea: String = ""
    private var streetAddress: String = ""
    private val args = Bundle()

    private val TAG = "track"

    fun initializeDefaults() {

        if (sharedPref.getBoolean("first_launch", true)) {
            setTemperatureUnit(Constants.CELSIUS_SHARED)
            setSpeedUnit(Constants.METER_PER_SECOND)
            setNotificationSetting(Constants.NOTIFICATION )
            setLocationSetting(Constants.GPS)
            setLanguageSetting(Constants.ENGLISH_SHARED)
            setFirstLaunch(false)
            Log.i(TAG, "initializeDefaults: done")
        }
        Log.i(TAG, "initializeDefaults: outside if ")
    }

    private fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPref.edit().putBoolean("first_launch", isFirstLaunch).apply()
    }

    // Getters for each setting, retrieving directly from SharedPreferences
    fun getTemperatureUnit(): String? {
        return sharedPref.getString("temperature", Constants.CELSIUS_SHARED) ?: Constants.CELSIUS_SHARED
    }

    fun setTemperatureUnit(unit: String) {
        sharedPref.edit().putString("temperature", unit).apply()
    }

    fun getSpeedUnit(): String? {
        return sharedPref.getString("speed", Constants.METER_PER_SECOND) ?: Constants.METER_PER_SECOND
    }

    fun setSpeedUnit(unit: String) {
        sharedPref.edit().putString("speed", unit).apply()
    }

    fun getNotificationSetting(): String? {
        return sharedPref.getString("notification", Constants.NOTIFICATION) ?: Constants.NOTIFICATION
    }

    fun setNotificationSetting(setting: String) {
        sharedPref.edit().putString("notification", setting).apply()
    }

    fun getLocationSetting(): String? {
        return sharedPref.getString("location", Constants.GPS) ?: Constants.GPS
    }

    fun setLocationSetting(setting: String) {
        sharedPref.edit().putString("location", setting).apply()
    }

    fun getLanguageSetting(): String? {
        return sharedPref.getString("language", Constants.ENGLISH_SHARED) ?: Constants.ENGLISH_SHARED
    }

    fun setLanguageSetting(setting: String) {
        Log.i(TAG, "setLanguageSetting: $setting")
        sharedPref.edit().putString("language", setting).apply()
    }



}