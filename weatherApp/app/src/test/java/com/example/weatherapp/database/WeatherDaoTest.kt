package com.example.weatherapp.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.model.LocationData
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@Config(sdk = [19])
@RunWith(RobolectricTestRunner::class)
class WeatherDaoTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDB

    private val location1 = LocationData(id = 1 , city= "Cairo", lat = 30.0, lng = 31.0)
    private val location2 = LocationData(id = 2 , city ="Vienna", lat = 35.0, lng = 34.0)
    private val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")


    @Before
    fun setup() {
        // Create an in-memory database
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDB::class.java
        ).allowMainThreadQueries().build()
        weatherDao = db.GetWeatherDao()
    }

    @After
    fun tearDown() {
        db.close()
    }


    @Test
    fun insertLocation_insertsLocation() = runTest {
        weatherDao.insertLocation(location1)

        val allLocations = weatherDao.getAllFavouriteLocations().first()
        //assertThat(allLocations.size,  notNullValue())
        assertThat(allLocations[0], IsEqual(location1))
        //assertTrue(allLocations.contains(location1))
    }

    @Test
    fun deleteLocation_removesLocation() = runTest {
        weatherDao.insertLocation(location1)
        weatherDao.insertLocation(location2)

        weatherDao.deleteLocationFromFav(location1)

        val allLocations = weatherDao.getAllFavouriteLocations().first()
        assertThat(allLocations.size, IsEqual(1))
        assertThat(allLocations[0], IsEqual(location2))
    }

    @Test
    fun getAllFavouriteLocations_returnsAllLocations() = runTest {
        weatherDao.insertLocation(location1)
        weatherDao.insertLocation(location2)

        val allLocations = weatherDao.getAllFavouriteLocations().first()
        assertThat(allLocations.size, IsEqual(2))
        assertThat(allLocations[0], IsEqual(location1))
        assertThat(allLocations[1], IsEqual(location2))
    }

    @Test
    fun insertAlarm_insertsAlarm() = runTest {
        weatherDao.insertAlarm(alarm)

        val allAlarms = weatherDao.getAllAlarms().first()
        assertThat(allAlarms.size, IsEqual(1))
        assertThat(allAlarms[0], IsEqual(alarm))
    }

    @Test
    fun deleteAlarm_removesAlarm() = runTest {
        weatherDao.insertAlarm(alarm)

        weatherDao.deleteAlarm(alarm)

        val allAlarms = weatherDao.getAllAlarms().first()
        assertThat(allAlarms.size, IsEqual(0))
    }

    @Test
    fun getAllAlarms_returnsAllAlarms() = runTest {
        // Arrange: Insert multiple alarms into the database
        val alarm1 = AlarmEntity(time = System.currentTimeMillis(), kind = "Alarm 1")
        val alarm2 = AlarmEntity(time = System.currentTimeMillis() + 1000, kind = "Alarm 2")

        weatherDao.insertAlarm(alarm1)
        weatherDao.insertAlarm(alarm2)

        // Act: Retrieve all alarms from the database
        val allAlarms = weatherDao.getAllAlarms().first()

        // Assert: Check that the retrieved alarms match the inserted alarms
        assertThat(allAlarms.size, IsEqual(2))
        assertThat(allAlarms.contains(alarm1), IsEqual(true))
        assertThat(allAlarms.contains(alarm2), IsEqual(true))
    }

}
