package com.example.weatherapp.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.iti.data.model.AlarmEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.jupiter.api.Assertions.*


@RunWith(AndroidJUnit4::class)
class LocalSourceTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var localSource: LocalSource
    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDB

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Create an in-memory Room database for testing
        db = Room.inMemoryDatabaseBuilder(
            context,
            WeatherDB::class.java
        ).allowMainThreadQueries().build()

        weatherDao = db.GetWeatherDao()
        localSource = LocalSource(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `test insertLocation inserts location correctly`() = runBlocking {
        val location = LocationData(0, "Cairo", 30.0, 31.0)
        localSource.insertLocation(location)

        val allLocations = weatherDao.getAllFavouriteLocations().first() // Collect the flow

        assertThat(allLocations.size, `is`(1))
        assertThat(allLocations[0], IsEqual(location))
        //assertEquals(1, allLocations.size)
        //assertEquals(location, allLocations[0])
    }

    @Test
    fun `test deletePlaceFromFav deletes location correctly`() = runBlocking {
        val location = LocationData(0, "Vienna", 35.0, 34.0)
        localSource.insertLocation(location)
        localSource.deletePlaceFromFav(location)

        val allLocations = weatherDao.getAllFavouriteLocations().first()
        assertEquals(0, allLocations.size)
    }

    @Test
    fun `test getAllFavouritePlaces returns flow of favourite locations`() = runBlocking {
        val location = LocationData(0, "Vienna", 35.0, 34.0)
        localSource.insertLocation(location)

        val flow = localSource.getAllFavouritePlaces()
        flow.collect { allLocations ->
            assertEquals(1, allLocations.size)
            assertEquals(location, allLocations[0])
        }
    }

    @Test
    fun `test insertAlarm inserts alarm correctly`() = runBlocking {
        val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")
        localSource.insertAlarm(alarm)

        val allAlarms = weatherDao.getAllAlarms().first() // Collect the flow
        assertEquals(1, allAlarms.size)
        assertEquals(alarm, allAlarms[0])
    }

    @Test
    fun `test deleteAlarm deletes alarm correctly`() = runBlocking {
        val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")
        localSource.insertAlarm(alarm)
        localSource.deleteAlarm(alarm)

        val allAlarms = weatherDao.getAllAlarms().first()
        assertEquals(0, allAlarms.size)
    }

    @Test
    fun `test getAllAlarms returns flow of alarms`() = runBlocking {
        val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")
        localSource.insertAlarm(alarm)

        val flow = localSource.getAllAlarms()
        flow.collect { allAlarms ->
            assertEquals(1, allAlarms.size)
            assertEquals(alarm, allAlarms[0])
        }
    }
}