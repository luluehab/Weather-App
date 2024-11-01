package com.example.weatherapp.ui.splash.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivitySplashBinding
import com.example.weatherapp.ui.splash.viewmodel.SharedViewModel
import com.example.weatherapp.ui.splash.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.Observer
import com.example.weatherapp.model.Repo.LocationRepository
import com.example.weatherapp.ui.save.viewmodel.SavedViewModel
import com.example.weatherapp.ui.save.viewmodel.SavedViewModelFactory
import com.example.weatherapp.utils.Permission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale



class SplashActivity : AppCompatActivity(){
    interface LocationFromGPS {
        fun getlocationFromGPS()
    }
    private val TAG: String = "track"
    private lateinit var binding: ActivitySplashBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
   // private lateinit var txtAddress: TextView
    private lateinit var streetAddress: String
  //  private var permissionDeniedCounts = 0
  //  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val REQUEST_LOCATION_CODE: Int = 100
    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    private lateinit var locationCallback: LocationFromGPS

    override fun onStart() {
        super.onStart()
        if(checkPermission()){
            if(isLocationEnable()){
                getFreshLocation()
            }
            else{
                enableLocationServices()
            }
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_CODE)
        }
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if(requestCode == REQUEST_LOCATION_CODE)
            {
                if(grantResults.size>1 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED){
                    getFreshLocation()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set the location callback to this instance of SplashActivity
        locationCallback = object : LocationFromGPS {
            override fun getlocationFromGPS() {
                Log.i(TAG, "getlocationFromGPS: ")
                getFreshLocation()
            }
        }
        getFreshLocation()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    fun checkPermission(): Boolean{
        return  checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    fun isLocationEnable(): Boolean{
        val locationManager : LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    fun enableLocationServices(){
        Toast.makeText(this,"turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    fun getFreshLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            com.google.android.gms.location.LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback(){
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    Log.i(TAG, "onLocationResult: lat " + p0.lastLocation?.latitude?.toString() + "long"+ p0.lastLocation?.longitude?.toString() )

                    // Safely assign latitude and longitude using let
                    p0.lastLocation?.let { location ->
                        latitude = location.latitude
                        longitude = location.longitude

                        // Now latitude and longitude are guaranteed to be non-null here
                        getAddressFromLocation(latitude!!, longitude!!)
                    } ?: run {
                       // txtAddress.text = "Address not found!"
                    }
                    /*latitude = p0.lastLocation?.latitude!!
                    longitude = p0.lastLocation?.longitude!!

                    if (latitude != null && longitude != null) {
                        // Call function to get address
                        getAddressFromLocation(latitude, longitude)
                    } else {
                        txtAddress.text = "Address not found!"
                    }*/
                }
            }, Looper.myLooper())
    }
    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            Log.i(TAG, "getAddressFromLocation: $addresses")
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val adminArea = address.adminArea // Get the administrative area
                val countryName = address.countryName // Get the country name
                streetAddress = address.getAddressLine(0)
                // Log the values to ensure they are retrieved correctly
                Log.i(TAG, "Admin Area: $adminArea, Country Name: $countryName")

                // Start new activity with adminArea and countryName data
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("adminArea", adminArea)
                    putExtra("countryName", countryName)
                    putExtra("latitude", latitude)
                    putExtra("longitude" , longitude)
                    putExtra("streetAddress", streetAddress)

                }
                startActivity(intent)
                finish() // Finish current activity if needed

            } else {

            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to get the address.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

}

