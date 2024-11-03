package com.example.weatherapp.ui.save.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentSavedBinding
import com.example.weatherapp.model.Repo.RepositoryImpl
import com.example.weatherapp.network.APIClient
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.ui.save.viewmodel.SavedViewModel
import com.example.weatherapp.ui.save.viewmodel.SavedViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.downloadworker.Network.NetworkCheck
import com.example.weatherapp.database.WeatherDB
import com.example.weatherapp.database.WeatherDao
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory


class SavedFragment : Fragment() {

    private val TAG = "saved"
    private var _binding: FragmentSavedBinding? = null
    private lateinit var savedAdapter: SavedAdapter
    private lateinit var remoteSource :RemoteSource
    private lateinit var localSource : LocalSource
    private lateinit var weatherDao: WeatherDao
    private lateinit var repository : RepositoryImpl
    lateinit var location: LocationData
    private  var mediaPlayer: MediaPlayer? = null
    // Initialize the ViewModel with the repository using the ViewModelFactory
    private val savedViewModel: SavedViewModel by viewModels {
        SavedViewModelFactory(repository)
    }
    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(requireActivity().application)
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
  /*      val slideshowViewModel =
            ViewModelProvider(this).get(SavedViewModel::class.java)
*/

        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        Log.i(TAG, "onCreateView fromSaved: ")

        //val root: View = binding.root

        weatherDao =  WeatherDB.getDatabase(requireContext()).GetWeatherDao()
        remoteSource = RemoteSource(APIClient.getApiService())
        localSource = context?.let { LocalSource(weatherDao) }!!
        repository = RepositoryImpl.getRepository(remoteSource, localSource , settingViewModel)
        setUpAdapters()
        binding.btnMaps.setOnClickListener {
            //Toast.makeText(requireContext(), "map will show", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_savedFragment_to_mapSearchFragment)
            binding.mapfragmentContainerView.visibility = View.VISIBLE
        }
        lifecycleScope.launch {
            savedViewModel.getAllFavouritePlaces.collect{ locationList ->
                if (locationList.isEmpty()) {
                    binding.tvNoItems.visibility = View.VISIBLE
                    binding.imcNoSaved.visibility = View.VISIBLE
                    binding.rvFavs.visibility = View.GONE
                } else {
                    binding.tvNoItems.visibility = View.GONE
                    binding.imcNoSaved.visibility = View.GONE
                    binding.rvFavs.visibility = View.VISIBLE
                    savedAdapter.submitList(locationList)
                }

            }
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                location = savedAdapter.currentList[position]
                mediaPlayer = MediaPlayer.create(context, R.raw.delete)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null // Clear reference to avoid memory leak
                    savedViewModel.deletePlaceFromFav(location)

                    val snackbar =
                        Snackbar.make(binding.root, "Location deleted", Snackbar.LENGTH_LONG)
                    snackbar.setAction("Undo") {
                        savedViewModel.insertPlaceToFav(location)
                        lifecycleScope.launch {
                            savedViewModel.getAllFavouritePlaces.collect { locationList ->
                                if (locationList.isEmpty()) {
                                    binding.tvNoItems.visibility = View.VISIBLE
                                    binding.imcNoSaved.visibility = View.VISIBLE
                                    binding.rvFavs.visibility = View.GONE
                                } else {
                                    binding.tvNoItems.visibility = View.GONE
                                    binding.imcNoSaved.visibility = View.GONE
                                    binding.rvFavs.visibility = View.VISIBLE
                                    savedAdapter.submitList(locationList)
                                }

                            }
                        }
                    }

                    snackbar.show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvFavs)

        return binding.root
    }

    private fun setUpAdapters() {
        savedAdapter = SavedAdapter( lifecycleScope){location ->
           // Toast.makeText(requireContext(), "details of $location Will show", Toast.LENGTH_SHORT).show()
            if(NetworkCheck.isNetworkAvailable(requireContext())) {
                val bundle = Bundle().apply {
                    putString("adminArea", " ")
                    putString("countryName", location.city)
                    putDouble("latitude", location.lat)
                    putDouble("longitude", location.lng)
                }
                // Navigate to HomeFragment with the bundle
                findNavController().navigate(R.id.action_savedFragment_to_homeFragment, bundle)
            }
            else{
                Snackbar.make(binding.root, "No Network", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab).show()

            }
        }

        binding.apply {
            binding.rvFavs.layoutManager = LinearLayoutManager(context)
            rvFavs.adapter= savedAdapter
        }
    }

}