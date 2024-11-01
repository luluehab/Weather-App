package com.example.weatherapp.ui.mapsearch.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentMapSearchBinding
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.Repo.RepositoryImpl
import com.example.weatherapp.network.APIClient
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.ui.mapsearch.viewmodel.MapViewModel
import com.example.weatherapp.ui.mapsearch.viewmodel.MapViewModelFactory
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.log


class MapSearchFragment : Fragment(){

    private val TAG ="track"
    //private val _locationStateFlow = MutableList<LocationData>(null)
    private var _binding: FragmentMapSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var remoteSource : RemoteSource
    private lateinit var localSource : LocalSource
    private lateinit var repository : RepositoryImpl
    private lateinit var marker: Marker
    private lateinit var currentLocation :LocationData
   // private lateinit var mapViewModel: MapViewModel
    private lateinit var mMap: MapView
    private var lat =30.0986824
    private var long =31.3427256
    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(requireActivity().application)
    }
    private val mapViewModel: MapViewModel by viewModels {
        MapViewModelFactory(requireActivity().application , repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          _binding = FragmentMapSearchBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView fromSearch: ")
        remoteSource = RemoteSource(APIClient.getApiService())
        localSource = context?.let { LocalSource(it) }!!
        repository = RepositoryImpl.getRepository(remoteSource , localSource , settingViewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize map and marker
        mMap = view.findViewById(R.id.mapView)
        marker = Marker(mMap)
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.setMultiTouchControls(true)
        val ctx = requireContext().applicationContext
         Configuration.getInstance().userAgentValue = ctx.packageName
        val startPoint = GeoPoint(lat, long) // Example coordinates
        mMap.controller.setZoom(19.0)
        mMap.controller.setCenter(startPoint)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Current Location"
        mMap.overlays.add(marker)

        setupSearchView()

        // Observe search results
        lifecycleScope.launch {
            mapViewModel.filteredLocations.collect { locations ->
                if (locations.isNotEmpty()) {
                    val location = locations.first() // Take the first result
                   // currentLocation = location
                    Log.i(TAG, "onViewCreated:${location.lat}  ${location.lng}  ${location.city} ")
                    placeMarkerOnMap(location.lat, location.lng, location.city , location )
                }
            }
        }
        binding.btnSaveLocation.setOnClickListener {
            //viewModel.addCurrentLocationToFavorites()
            Toast.makeText(requireContext(), "will add", Toast.LENGTH_SHORT).show()
            // mapViewModel.addLocationToFavorites(currentLocation)
            //Log.i(TAG, "onViewCreated: add to fav  ${currentLocation}" )
            /* mapViewModel.filteredLocations.value.firstOrNull()?.let {
                Log.i(TAG, "onViewCreated: add to fav  $it" )
                mapViewModel.addLocationToFavorites(it)
                Toast.makeText(requireContext(), "Location saved to favorites!", Toast.LENGTH_SHORT).show()
            }*/

              lifecycleScope.launch {
                //mapViewModel.filteredLocations.collect { locations ->
                  //  if (locations.isNotEmpty()) {
                    //    val location = locations.first() // Take the first result
                        mapViewModel.addLocationToFavorites(currentLocation)
                      //  Log.i(TAG, "btnSaveLocation:${location.lat}  ${location.lng}  ${location.city} ")
                        Toast.makeText(requireContext(), "${currentLocation.city} saved to favorites!", Toast.LENGTH_SHORT).show()
                   // }
               // }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSearchView() {
        val debounceTime = 300L
        var lastQuery: String? = null
        val handler = Handler(Looper.getMainLooper())

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.orEmpty()
                lastQuery = query
                Log.i(TAG, "onQueryTextChange: " + lastQuery)
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    if (query == lastQuery) {
                        Log.i(TAG, "onQueryTextChange: postDelayed " + lastQuery)
                        mapViewModel.search(query)
                    }
                }, debounceTime)

                return true
            }
        })


    }
    private fun showProgressBar() {
        binding.btnSaveLocation.visibility = View.GONE
        binding.prProgressMap.visibility = View.VISIBLE
    }
    private fun placeMarkerOnMap(lat: Double, lng: Double, title: String , location : LocationData){
        currentLocation = location
        val geoPoint = GeoPoint(lat, lng)
        Log.i(TAG, "placeMarkerOnMap: $lat:, $lng: , $title: ")
        marker.position = geoPoint
        marker.title = title
        mMap.controller.apply {
            setZoom(19.0)
            setCenter(geoPoint)
        }
    }
}