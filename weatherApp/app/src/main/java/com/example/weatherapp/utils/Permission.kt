package com.example.weatherapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.core.content.ContextCompat
import com.example.downloadworker.Network.NetworkCheck

object Permission {

    const val TAG = "PERMISSION"


    fun checkConnection(context: Context): Boolean {
        return NetworkCheck.isNetworkAvailable(context)
    }



    fun checkPermission(context: Context): Boolean {
        Log.d(TAG, "checkPermission: ")
        var result = false
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            result = true
        }
        return result
    }

     fun isLocationIsEnabled(context: Context): Boolean {
        Log.d(TAG, "isLocationIsEnabled: ")
         val locationManager : LocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
         return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
             LocationManager.NETWORK_PROVIDER)
    }

    fun notificationPermission(context: Context): Boolean{
        var result = false
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            result = true
        }
        return result
    }


}