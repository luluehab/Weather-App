package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favlocations")
data class LocationData (
    @PrimaryKey
    var city: String,
    var lat: Double,
    var lng: Double
    )
