package com.example.weatherapp.ui.save.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.database.WeatherDB
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Repo.FakeRepo
import kotlinx.coroutines.flow.toList
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.first
import org.hamcrest.core.IsEqual
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class SavedViewModelTest {

    lateinit var savedViewModel: SavedViewModel
    lateinit var repository: FakeRepo

    val location = LocationData(0, "Cairo", 30.0, 31.0)
    val location2 = LocationData(2, "Roma", 32.0, 33.0)

    @Before
    fun setUp() {
        repository = FakeRepo()
        savedViewModel = SavedViewModel(repository)
    }

    @Test
    fun insertPlaceToFav_AllFavourite() = runTest {


        // When
        savedViewModel.insertPlaceToFav(location2)

        //Then
        // Collect the list of favorite locations from the repo
        val favoritePlaces = savedViewModel.getAllFavouritePlaces.first()

        // Check that the location was added correctly
        assertEquals(1, favoritePlaces.size)
        assertThat(favoritePlaces[0], `is`(location2)) // Compare with the expected object
    }

    @Test
    fun deletePlaceFromFav_null()= runTest{

        savedViewModel.insertPlaceToFav(location)
        // Collect the list of favorite locations from the repo
        val favoritePlace = savedViewModel.getAllFavouritePlaces.first()

        assertEquals(1, favoritePlace.size)
        assertThat(favoritePlace[0], `is`(location))

        // Now delete it
        savedViewModel.deletePlaceFromFav(location)

        val favorite = savedViewModel.getAllFavouritePlaces.first()
        // Check that the location was removed
        assertThat(favorite, IsEqual( emptyList() )) // Ensure it returns null after deletion
       // assertTrue(favorite.isEmpty())
        // Check that the list is empty after deletion
       // println("Favorite list after deletion: $favorite")

    }

}