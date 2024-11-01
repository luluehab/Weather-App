package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favlocations")
data class LocationData (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var city: String,
    var lat: Double,
    var lng: Double
    )