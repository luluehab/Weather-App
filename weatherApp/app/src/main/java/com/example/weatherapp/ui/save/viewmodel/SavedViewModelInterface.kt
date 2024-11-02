package com.example.weatherapp.ui.save.viewmodel

import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow

interface SavedViewModelInterface {
    val getAllFavouritePlaces: Flow<List<LocationData>>
    fun insertPlaceToFav(place: LocationData)
    fun deletePlaceFromFav(place: LocationData)
}