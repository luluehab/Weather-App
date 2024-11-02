package com.example.weatherapp.database

import com.example.iti.data.model.AlarmEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocal(private val locationData: MutableList<LocationData>? = mutableListOf(), private val alarmEntity: MutableList<AlarmEntity>? = mutableListOf() ) : LocalSourceInterface{

    //private val locationData = mutableListOf<LocationData>()
    //private val alarmEntity = mutableListOf<AlarmEntity>()


    override suspend fun insertLocation(location: LocationData) {
        locationData?.add(location)
    }

    override suspend fun deletePlaceFromFav(location: LocationData) {
        locationData?.remove(location)
    }

    override fun getAllFavouritePlaces(): Flow<List<LocationData>> {
        return flow {
            if (locationData != null) {
                emit(locationData)
            }
        }
    }



    override suspend fun insertAlarm(alarmItem: AlarmEntity) {
        alarmEntity?.add(alarmItem)
    }

    override suspend fun deleteAlarm(alarmItem: AlarmEntity) {
        alarmEntity?.remove(alarmItem)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return flow {
            if (alarmEntity != null) {
                emit(alarmEntity)
            }
        }
    }


}