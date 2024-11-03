package com.example.weatherapp.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.iti.data.model.AlarmEntity
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.jupiter.api.Assertions.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [21])
@RunWith(RobolectricTestRunner::class)
class LocalSourceTest{


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var localSource: LocalSource
    private lateinit var db: WeatherDB
    private lateinit var dao: WeatherDao

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Create an in-memory Room database for testing
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDB::class.java
        ).allowMainThreadQueries().build()

        dao = db.GetWeatherDao()
        localSource = LocalSource(dao)
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
       // testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun insertLocation_insertsLocationCorrectly() = runTest {
        val location = LocationData(1, "Cairo", 30.0, 31.0)
        localSource.insertLocation(location)

        val allLocations = localSource.getAllFavouritePlaces().first() // Collect the flow

        assertThat(allLocations.size, `is`(1))
        assertThat(allLocations[0], IsEqual(location))
        //assertEquals(1, allLocations.size)
        //assertEquals(location, allLocations[0])
    }

    @Test
    fun deletePlaceFromFav_deletesLocationCorrectly() = runTest {
        val location = LocationData(1, "Vienna", 35.0, 34.0)
        localSource.insertLocation(location)
        localSource.deletePlaceFromFav(location)

        val allLocations = localSource.getAllFavouritePlaces().first()
        assertEquals(0, allLocations.size)
    }

    @Test
    fun getAllFavouritePlaces_flowOfFavouriteLocations() = runTest {
        val location = LocationData(1, "Vienna", 35.0, 34.0)
        val location1 = LocationData(2, "Cairo", 30.0, 31.0)

        localSource.insertLocation(location)
        localSource.insertLocation(location1)


        val allLocations = localSource.getAllFavouritePlaces().first()
        assertEquals(2, allLocations.size)
        assertThat(allLocations[0], `is`(location))
        assertThat(allLocations[1], `is`(location1))
    }

    @Test
    fun insertAlarm_insertsAlarmCorrectly() = runTest {
        val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")
        localSource.insertAlarm(alarm)

        val allAlarms = localSource.getAllAlarms().first() // Collect the flow
        assertEquals(1, allAlarms.size)
        assertEquals(alarm, allAlarms[0])
    }

    @Test
    fun deleteAlarm_deletesAlarmCorrectly() = runTest {
        val alarm = AlarmEntity(time = System.currentTimeMillis(), kind = "Test Alarm")
        localSource.insertAlarm(alarm)
        localSource.deleteAlarm(alarm)

        val allAlarms =  localSource.getAllAlarms().first()
        assertEquals(0, allAlarms.size)
    }

    @Test
    fun getAllAlarms_flowOfAlarms() = runTest {
        val alarm1 = AlarmEntity(time = System.currentTimeMillis(), kind = "Alarm 1")
        val alarm2 = AlarmEntity(time = System.currentTimeMillis() + 1000, kind = "Alarm 2")

        localSource.insertAlarm(alarm1)
        localSource.insertAlarm(alarm2)

        val allAlarms = localSource.getAllAlarms().first()
        assertEquals(2, allAlarms.size)
        assertThat(allAlarms.contains(alarm1), `is`(true))
        assertThat(allAlarms.contains(alarm2), `is`(true))
    }
}