package com.example.weatherapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.weatherapp.R
import com.example.weatherapp.utils.Constants.CELSIUS_SHARED
import com.example.weatherapp.utils.Constants.FAHRENHEIT_SHARED
import com.example.weatherapp.utils.Constants.KELVIN_SHARED
import com.example.weatherapp.utils.Constants.METER_PER_SECOND
import com.example.weatherapp.utils.Constants.MILES_PER_HOUR
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Helpers {
    fun convertTemperature(tempInCelsius: Double, unit: String): Double {
        return when (unit) {
            CELSIUS_SHARED -> tempInCelsius
            FAHRENHEIT_SHARED -> (tempInCelsius * 9 / 5) + 32
            KELVIN_SHARED -> tempInCelsius + 273.15
            else -> tempInCelsius
        }
    }

    fun getUnitSymbol(unit: String): String {
        return when (unit) {
            CELSIUS_SHARED -> "C"
            FAHRENHEIT_SHARED -> "F"
            KELVIN_SHARED -> "K"
            else -> "C"
        }
    }

    fun getWindSpeedUnitSymbol(unit: String): Int {
        return when (unit) {
            METER_PER_SECOND -> R.string.m_s
            MILES_PER_HOUR -> R.string.mph
            else -> R.string.m_s
        }
    }

    fun convertWindSpeed(speed: Double, fromUnit: String, toUnit: String): Double {
        return when (toUnit) {
            MILES_PER_HOUR -> if (fromUnit == METER_PER_SECOND) speed * 2.23694 else speed
            METER_PER_SECOND -> if (fromUnit == MILES_PER_HOUR) speed / 2.23694 else speed
            else -> speed
        }
    }
    fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp * 1000))
    }

    @SuppressLint("SimpleDateFormat")
    fun date(): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    fun getHourFromUnixTime(unixTime: Long): Int {
        val date = Date(unixTime * 1000L)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        return calendar.get(java.util.Calendar.HOUR_OF_DAY)  // Get hour in 24-hour format
    }

    fun changeLanguage(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    // Function to check network availability
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    fun formatLongToAnyString(dateTimeInMillis: Long, pattern: String): String {
        val resultFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val date = Date(dateTimeInMillis)
        return resultFormat.format(date)
    }

    fun formatFromStringToLong(dateText: String, timeText: String): Long {
        val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
        val dateAndTime = "$dateText $timeText}"
        val date = dateFormat.parse(dateAndTime)
        return date?.time ?: -1
    }

    fun formatHourMinuteToString(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun formatDatePlusYears(timeInMillis: Long): String {
        return SimpleDateFormat("hh:mm a, MMM dd yyyy", Locale.getDefault()).format(
            Date(
                timeInMillis
            )
        )
    }

}