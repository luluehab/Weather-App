package com.example.weatherapp.model.Repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.iti.data.model.AlarmEntity
import com.example.weatherapp.database.FakeLocal
import com.example.weatherapp.network.FakeRemote
import com.example.weatherapp.ui.setting.viewmodel.FakeSettingViewModel
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class RepositoryImplTest{
    private lateinit var fakeLocalSource: FakeLocal
    private lateinit var fakeRemoteSource: FakeRemote
    private lateinit var fakeSettingViewModel: FakeSettingViewModel
    private lateinit var repository: RepositoryImpl


    val location1 = LocationData("egypt" , 30.0 , 31.0)
    val location2 = LocationData("veinna" , 35.0 , 34.0)
    //private val alarm1 = AlarmEntity( 07.00, true)
    //private val alarm2 = AlarmEntity( 19.00, false)
    val locations = mutableListOf(
        location1,location2
    )
    @Before
    fun setup() {

        fakeLocalSource = FakeLocal(locations)
        fakeRemoteSource = FakeRemote()
        fakeSettingViewModel = FakeSettingViewModel(language = "en")
        repository = RepositoryImpl(fakeRemoteSource, fakeLocalSource, fakeSettingViewModel)
    }

    // Remote
    @Test
    fun fetchCurrentWeather_weatherdata() = runTest {

        // Collect the first emission from the flow
        val emittedWeather = repository.fetchCurrentWeather(30.0, 31.0).first()
        // Expected data from FakeRemote
        val expectedWeather = fakeRemoteSource.weatherTestData
        // Assertion
        assertThat(emittedWeather, equalTo(expectedWeather))

    }


    // Local
    @Test
    fun insertPlaceToFav_addsLocation() = runTest {
        val newLocation = LocationData( "New Location", 25.0, 30.0)
        repository.insertPlaceToFav(newLocation)

        val allLocations = repository.getAllFavouritePlaces().first()
        assertThat(allLocations.size, IsEqual(3)) // Assuming there were initially 2 locations
        assertThat(allLocations.contains(newLocation), IsEqual(true))
    }

    @Test
    fun deletePlaceFromFav_removesLocation() = runTest {
        repository.deletePlaceFromFav(location1)

        val allLocations = repository.getAllFavouritePlaces().first()
        assertThat(allLocations.size, IsEqual(1)) // One location should be removed
        assertThat(allLocations.contains(location1), IsEqual(false))
    }

    @Test
    fun insertAlarm_addsAlarm() = runTest {
        val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")
        repository.insertAlarm(alarm)

        val allAlarms = repository.getAllAlarms().first()
        assertThat(allAlarms.size, IsEqual(1)) // Assuming there are no alarms initially
        assertThat(allAlarms.contains(alarm), IsEqual(true))
    }

    @Test
    fun deleteAlarm_removesAlarm() = runTest {
        val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")
        repository.insertAlarm(alarm)
        repository.deleteAlarm(alarm)

        val allAlarms = repository.getAllAlarms().first()
        assertThat(allAlarms.size, IsEqual(0)) // The alarm should be removed
    }



}