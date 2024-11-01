package com.example.weatherapp.ui.alert.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iti.data.model.AlarmEntity
import com.example.weatherapp.model.Repo.Repository
import com.example.weatherapp.network.ApiState
import com.example.weatherapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel (private val repo: Repository)  : ViewModel() {

    private val _alarmsMutableStateFlow: MutableStateFlow<List<AlarmEntity>> = MutableStateFlow(
        emptyList()
    )
    val alarmsStateFlow: StateFlow<List<AlarmEntity>> get() = _alarmsMutableStateFlow

    private val _weatherResponseMutableStateFlow: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Loading)
    val weatherResponseStateFlow: StateFlow<ApiState> get() = _weatherResponseMutableStateFlow


    fun insertAlarm(alarmItem: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlarm(alarmItem)
        }
    }

    fun deleteAlarm(alarmItem: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlarm(alarmItem)
        }
    }

    fun getAllAlarms() {
        viewModelScope.launch {
            repo.getAllAlarms().collectLatest {
                _alarmsMutableStateFlow.value = it
            }
        }
    }


}