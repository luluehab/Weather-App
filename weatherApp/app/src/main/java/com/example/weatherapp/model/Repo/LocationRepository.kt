package com.example.weatherapp.model.Repo

// LocationRepository.kt
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class LocationRepository(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun getLastLocation(listener: OnLocationFetchedListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionDenied()
            return
        }

        fusedLocationClient.lastLocation.addOnCompleteListener(OnCompleteListener<Location> { task ->
            if (task.isSuccessful && task.result != null) {
                listener.onLocationFetched(task.result)
            } else {
                listener.onLocationFetchFailed()
            }
        })
    }

    interface OnLocationFetchedListener {
        fun onLocationFetched(location: Location)
        fun onLocationFetchFailed()
        fun onPermissionDenied()
    }
}
