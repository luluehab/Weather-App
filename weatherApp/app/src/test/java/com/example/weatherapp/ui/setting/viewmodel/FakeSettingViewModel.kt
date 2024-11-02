package com.example.weatherapp.ui.setting.viewmodel

class FakeSettingViewModel (private val language: String = "en") : SettingViewModelInterface {
    override fun initializeDefaults() {
        TODO("Not yet implemented")
    }

    override fun getTemperatureUnit(): String? {
        TODO("Not yet implemented")
    }

    override fun setTemperatureUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override fun getSpeedUnit(): String? {
        TODO("Not yet implemented")
    }

    override fun setSpeedUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override fun getNotificationSetting(): String? {
        TODO("Not yet implemented")
    }

    override fun setNotificationSetting(setting: String) {
        TODO("Not yet implemented")
    }

    override fun getLocationSetting(): String? {
        TODO("Not yet implemented")
    }

    override fun setLocationSetting(setting: String) {
        TODO("Not yet implemented")
    }

    override fun getLanguageSetting(): String? {
        return language
    }

    override fun setLanguageSetting(setting: String) {
        TODO("Not yet implemented")
    }
}