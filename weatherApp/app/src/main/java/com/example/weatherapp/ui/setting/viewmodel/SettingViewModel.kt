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

class SettingViewModel  (application: Application) : ViewModel(), SettingViewModelInterface {
    private val sharedPref = application.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)


    private val TAG = "track"

    override fun initializeDefaults() {

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
    override fun getTemperatureUnit(): String? {
        return sharedPref.getString("temperature", Constants.CELSIUS_SHARED) ?: Constants.CELSIUS_SHARED
    }

    override fun setTemperatureUnit(unit: String) {
        sharedPref.edit().putString("temperature", unit).apply()
    }

    override fun getSpeedUnit(): String? {
        return sharedPref.getString("speed", Constants.METER_PER_SECOND) ?: Constants.METER_PER_SECOND
    }

    override fun setSpeedUnit(unit: String) {
        sharedPref.edit().putString("speed", unit).apply()
    }

    override fun getNotificationSetting(): String? {
        return sharedPref.getString("notification", Constants.NOTIFICATION) ?: Constants.NOTIFICATION
    }

    override fun setNotificationSetting(setting: String) {
        sharedPref.edit().putString("notification", setting).apply()
    }

    override fun getLocationSetting(): String? {
        return sharedPref.getString("location", Constants.GPS) ?: Constants.GPS
    }

    override fun setLocationSetting(setting: String) {
        sharedPref.edit().putString("location", setting).apply()
    }

    override fun getLanguageSetting(): String? {
        return sharedPref.getString("language", Constants.ENGLISH_SHARED) ?: Constants.ENGLISH_SHARED
    }

    override fun setLanguageSetting(setting: String) {
        Log.i(TAG, "setLanguageSetting: $setting")
        sharedPref.edit().putString("language", setting).apply()
    }



}