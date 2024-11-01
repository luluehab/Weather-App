package com.example.weatherapp.ui.mapsearch.viewmodel

import android.R.attr.description
import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Repo.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream


class MapViewModel (private val application: Application,private val repository: RepositoryImpl) : ViewModel() {


    private val TAG ="track"
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredLocations = MutableStateFlow<List<LocationData>>(emptyList())
    val filteredLocations: StateFlow<List<LocationData>> = _filteredLocations

    private val countries: List<String> by lazy {
        application.resources.getStringArray(R.array.countries).toList()
    }

    private val geocoder: Geocoder by lazy {
        Geocoder(application)
    }

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchLocations(query)
                    Log.i(TAG, "viewModel:$query ")
                }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            Log.i(TAG, "viewModelsearch :$query ")
        }
    }

    private suspend fun fetchLocations(query: String) {

        withContext(Dispatchers.IO) {
            val results = performLocationSearch(query)
            _filteredLocations.value = results
            Log.i(TAG, "viewModel filter Result :$results ")
        }
    }

    private fun performLocationSearch(query: String): List<LocationData> {
        return countries.filter { it.contains(query, ignoreCase = true) }
            .mapNotNull { countryName ->
                val addresses = geocoder.getFromLocationName(countryName, 1) ?: return@mapNotNull null
                Log.i(TAG, "viewModel Perform location:$addresses ")
                addresses.firstOrNull()?.let {
                    LocationData(
                        city = it.locality ?: it.adminArea ?: countryName,
                        lat = it.latitude,
                        lng = it.longitude
                    )
                }
            }
    }

    // Function to save the favorite location
    fun addLocationToFavorites(locationData: LocationData) {
        viewModelScope.launch(Dispatchers.IO) {
            // Implement repository call or local database interaction to save the favorite location
            repository.insertPlaceToFav(locationData)
        }
    }

   /* fun searchLocation(input: String, inputStream: InputStream) {
      //  val workbook = WorkbookFactory.create(inputStream)
        val workbook = XSSFWorkbook(inputStream)  // For .xlsx files
        val sheet = workbook.getSheetAt(0)

        for (row in 1..sheet.lastRowNum) { // Start from 1 to skip header
            val city = sheet.getRow(row).getCell(0).stringCellValue
            val lat = sheet.getRow(row).getCell(2).numericCellValue
            val lng = sheet.getRow(row).getCell(3).numericCellValue
            val country = sheet.getRow(row).getCell(4).stringCellValue

            // Check if the input matches city or country
            if (city.equals(input, true) || country.equals(input, true)) {
                _locationStateFlow.value = LocationData(city, lat, lng, country)
                return
            }
        }
        _locationStateFlow.value = null // No match found
    }*/

}