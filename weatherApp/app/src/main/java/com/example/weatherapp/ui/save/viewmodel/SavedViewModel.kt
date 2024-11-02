package com.example.weatherapp.ui.save.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.downloadworker.Network.NetworkCheck
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SavedViewModel(private val repo: Repository) : ViewModel() {


     fun insertPlaceToFav(place: LocationData) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertPlaceToFav(place)
        }
    }
     val getAllFavouritePlaces: Flow<List<LocationData>> = repo.getAllFavouritePlaces()

     fun deletePlaceFromFav(place: LocationData) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletePlaceFromFav(place)
        }
    }


}