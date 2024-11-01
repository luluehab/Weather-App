package com.example.weatherapp.ui.mapsearch.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.Repo.RepositoryImpl
import com.example.weatherapp.ui.save.viewmodel.SavedViewModel

class MapViewModelFactory (   private val application: Application,private val repository: RepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(application ,repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}