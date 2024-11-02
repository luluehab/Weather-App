package com.example.weatherapp.ui.setting.viewmodel

interface SettingViewModelInterface {
    fun initializeDefaults()

    // Getters for each setting, retrieving directly from SharedPreferences
    fun getTemperatureUnit(): String?
    fun setTemperatureUnit(unit: String)
    fun getSpeedUnit(): String?
    fun setSpeedUnit(unit: String)
    fun getNotificationSetting(): String?
    fun setNotificationSetting(setting: String)
    fun getLocationSetting(): String?
    fun setLocationSetting(setting: String)
    fun getLanguageSetting(): String?
    fun setLanguageSetting(setting: String)
}