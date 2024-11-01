package com.example.weatherapp.ui.splash.viewmodel


import android.Manifest
import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.ThemedSpinnerAdapter.Helper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.downloadworker.Network.NetworkCheck
import com.example.weatherapp.model.Repo.LocationRepository
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.Helpers
import com.example.weatherapp.utils.Permission
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import android.location.Location

class SharedViewModel(private val locationRepository: LocationRepository) : ViewModel() {


   // private val locationRepository = LocationRepository(activity.applicationContext)
    private val _locationLiveData = MutableLiveData<Location>()
    private val _permissionDeniedLiveData = MutableLiveData<Boolean>()
    private val _locationFetchFailedLiveData = MutableLiveData<Boolean>()

    val locationLiveData: LiveData<Location> get() = _locationLiveData
    val permissionDeniedLiveData: LiveData<Boolean> get() = _permissionDeniedLiveData
    val locationFetchFailedLiveData: LiveData<Boolean> get() = _locationFetchFailedLiveData

    fun fetchLocation() {
        locationRepository.getLastLocation(object : LocationRepository.OnLocationFetchedListener {
            override fun onLocationFetched(location: Location) {
                _locationLiveData.postValue(location)
            }

            override fun onLocationFetchFailed() {
                _locationFetchFailedLiveData.postValue(true)
            }

            override fun onPermissionDenied() {
                _permissionDeniedLiveData.postValue(true)
            }
        })
    }












    /* private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private val _locationData = MutableLiveData<LocationData>()
    val locationData: LiveData<LocationData> = _locationData
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val context = getApplication<Application>().applicationContext

    private val _locationStatusMutableStateFlow: MutableStateFlow<String> = MutableStateFlow("")
    val locationStatusStateFlow: StateFlow<String> get() = _locationStatusMutableStateFlow

    companion object {
        private const val TAG = "SplashViewModel"
    }

    fun getLocationData(context: Context) {
        if (NetworkCheck.isNetworkAvailable(context)) {
           // when (readStringFromSettingSP(Constants.LOCATION)) {
            //    Constants.GPS -> {
                    if (Permission.checkPermission(context)) {
                        if (Permission.isLocationIsEnabled(context)) {
                            viewModelScope.launch {
                                getLocation()
                            }
                        }
                         else {
                            _locationStatusMutableStateFlow.value = Constants.SHOW_DIALOG
                        }
                    } else {
                        _locationStatusMutableStateFlow.value = Constants.REQUEST_PERMISSION
                    }
                //}

           // }
        }
    }
    fun startLocationUpdates() {
        //if (checkPermission()) {
            if (isLocationEnabled()) {
                getLocation()
            } else {
                _error.value = "Please enable location services"
            }
        //} else {
         //   _error.value = "Permission not granted"

            // Notify the Activity to request permissions if needed
       // }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }

    private fun getLocation() {
         if (checkPermission()) {
            val locationRequest = LocationRequest.Builder(10000).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            getCityFromLocation(location.latitude, location.longitude)
                        }
                    }
                },
                Looper.getMainLooper()
            )
        }
    }

    private fun getCityFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val locationData = LocationData(
                    latitude,
                    longitude,
                    address.countryName ?: "",
                    address.adminArea ?: "",
                    address.getAddressLine(0) ?: ""
                )
                _locationData.value = locationData
            } else {
                _error.value = "Address not found!"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            _error.value = "Unable to get the address."
        }
    }

    data class LocationData(
        val latitude: Double,
        val longitude: Double,
        val countryName: String,
        val adminArea: String,
        val streetAddress: String
    )*/
}
