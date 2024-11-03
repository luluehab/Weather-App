package com.example.weatherapp.ui.home.view


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.iti.data.model.DailyForecastElement
import com.example.iti.data.model.Hourly
import com.example.iti.data.model.Weather
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.database.WeatherDB
import com.example.weatherapp.database.WeatherDao
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.model.Repo.RepositoryImpl
import com.example.weatherapp.network.APIClient
import com.example.weatherapp.network.ApiState
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.ui.home.viewModel.HomeViewModel
import com.example.weatherapp.ui.home.viewModel.HomeViewModelFactory
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.Constants.METER_PER_SECOND
import com.example.weatherapp.utils.Constants.TEMPERATURE_FORMAT
import com.example.weatherapp.utils.Helpers.convertTemperature
import com.example.weatherapp.utils.Helpers.convertWindSpeed
import com.example.weatherapp.utils.Helpers.date
import com.example.weatherapp.utils.Helpers.formatTime
import com.example.weatherapp.utils.Helpers.getUnitSymbol
import com.example.weatherapp.utils.Helpers.getWindSpeedUnitSymbol
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class HomeFragment : Fragment() {

    private val TAG: String = "Home Fragment"
    private var _binding: FragmentHomeBinding? = null

    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var remoteSource :RemoteSource
    private lateinit var localSource : LocalSource
    private lateinit var repository : RepositoryImpl
    private lateinit var weatherDao: WeatherDao
    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(requireActivity().application)
    }
    // Initialize the ViewModel with the repository using the ViewModelFactory
    private val weatherViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(repository)
    }
    private var lat: Double = 0.0
    private var long: Double =0.0
    private var isViewOnly: Boolean = false
    private var adminArea: String? = null
    private var countryName: String? = null
     lateinit var temp: String
     lateinit var date: String
     lateinit var pressure:String
     lateinit var humidity:String
     lateinit var cloud:String
     lateinit var wind:String
     lateinit var sunset:String
     lateinit var sunrise:String


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        weatherDao =  WeatherDB.getDatabase(requireContext()).GetWeatherDao()
        remoteSource = RemoteSource(APIClient.getApiService())
        localSource = context?.let { LocalSource(weatherDao) }!!
        repository = RepositoryImpl.getRepository(remoteSource , localSource , settingViewModel)

        setUpAdapters()
        getLagLongCity()
        fetchDataBasedOnLatAndLong()
        setUpCollector()
      //  Log.i(TAG, "onCreateView: $temp")
        return root
    }

    private fun getLagLongCity() {
        if (getArguments() != null) {
            countryName = arguments?.getString("countryName")
            adminArea = arguments?.getString("adminArea")
            lat = arguments?.getDouble("latitude") ?: 0.0
            long = arguments?.getDouble("longitude") ?: 0.0


        }
        Log.i(TAG, "getLagLongCity: $lat , $long , $countryName , $adminArea ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun setUpAdapters() {
        val unit = settingViewModel.getTemperatureUnit()
        dailyAdapter = unit?.let { DailyAdapter( lifecycleScope, it) }!!
        hourlyAdapter = unit?.let { HourlyAdapter(lifecycleScope, it) }!!

        binding.apply {
            rvHourlyDegrees.adapter = hourlyAdapter
            rvDetailedDays.adapter = dailyAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDataBasedOnLatAndLong() {
        lifecycleScope.launch {
            try {
                weatherViewModel.fetchCurrentWeatherDataByCoordinates(lat,long)
                weatherViewModel.fetchHourlyWeatherByCoordinates(lat, long)
                weatherViewModel.fetchDailyWeatherByCoordinates(lat, long)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    getString(com.example.weatherapp.R.string.no_network_using_saved_data),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUpCollector() {
        gettingWeatherDataFromViewModel()
        gettingHourlyWeatherDataFromViewModel()
        gettingDailyWeatherDataFromViewModel()
    }



    private fun gettingWeatherDataFromViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherDataStateFlow.collect { apiState ->
                    when (apiState) {
                        is ApiState.Loading -> {
                            showLoading(true)
                            binding.cardDaysDetails.visibility = View.GONE
                            setVisibilityOfViewsOnScreen(true)
                        }

                        is ApiState.Success -> {
                            delay(600)
                            showLoading(false)
                            slideInAndScaleView(binding.cardDaysDetails)
//                        binding.cardDaysDetails.visibility = View.VISIBLE
                            setVisibilityOfViewsOnScreen(false)
                            val weatherData = apiState.data as Weather
                            launch {
                                updateUi(weatherData) // Update UI
                            }
                            //saveWeatherDataToSharedPreferences(weatherData) // Save to SharedPreferences
                        }

                        is ApiState.Failure -> {
                            showLoading(false)
                            setVisibilityOfViewsOnScreen(false)
                            binding.rvHourlyDegrees.visibility = View.GONE
                            binding.rvDetailedDays.visibility = View.GONE
                            Log.e(
                                "WeatherError",
                                "Error retrieving weather data ${apiState.message}"
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    @SuppressLint("DefaultLocale", "SetTextI18n", "StringFormatMatches")
    private fun updateUi(weather: Weather) {

        val unit = settingViewModel.getTemperatureUnit()
        val windSpeedUnit = settingViewModel.getSpeedUnit()

        //set Lottie based on weather
        val lottieAnimation = context?.let { checkWeatherDescription(it, weather) }
        if (lottieAnimation != null) {
            binding.animWeather.setAnimation(lottieAnimation)
        }
        binding.animWeather.playAnimation()

        //update Temp
        val currentTemp = unit?.let { convertTemperature(weather.main.temp, it) }
        binding.tvCurrentDegree.text =
            String.format(TEMPERATURE_FORMAT, currentTemp, unit?.let { getUnitSymbol(it) })

        val minTemp = unit?.let { convertTemperature(weather.main.temp_min, it) }
        binding.tvTempMin.text = String.format(TEMPERATURE_FORMAT, minTemp,
            unit?.let { getUnitSymbol(it) })
        val maxTemp = unit?.let { convertTemperature(weather.main.temp_max, it) }
        binding.tvTempMax.text = String.format(TEMPERATURE_FORMAT, maxTemp,
            unit?.let { getUnitSymbol(it) })


        Log.i(TAG, "updateUi: $countryName,$adminArea")
        binding.tvCityName.text = "$countryName\n$adminArea"

        binding.tvWeatherStatus.text = weather.weather[0].description
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        binding.tvDate.text = date()

        binding.tvPressureValue.text =
            getString(com.example.weatherapp.R.string.hpa, weather.main.pressure)
        binding.tvHumidityValue.text = "${weather.main.humidity} %"

        val windSpeed = unit?.let {
            windSpeedUnit?.let { unit ->
                convertWindSpeed(weather.wind.speed, METER_PER_SECOND, unit)
            }
        } ?: 0.0  // default value if null
        binding.tvWindValue.text = String.format(
            Locale.getDefault(),
            "%.0f %s",
            windSpeed,
            windSpeedUnit?.let { getWindSpeedUnitSymbol(it) }?.let { getString(it) } ?: ""
        )


        // additional info
        binding.tvCloudValue.text = "${weather.clouds.all} %"

        binding.tvSunriseValue.text = formatTime(weather.sys.sunrise)

        binding.tvSunsetValue.text = formatTime(weather.sys.sunset)



        // for lulu assistant
        temp = String.format(TEMPERATURE_FORMAT, currentTemp, unit?.let { getUnitSymbol(it) })
        date =  date()
        pressure =  getString(com.example.weatherapp.R.string.hpa, weather.main.pressure)
        humidity = "${weather.main.humidity} %"
        wind = String.format(
            Locale.getDefault(),
            "%.0f %s",
            windSpeed,
            windSpeedUnit?.let { getWindSpeedUnitSymbol(it) }?.let { getString(it) }
        )
        cloud = "${weather.clouds.all} %"
        sunrise =  formatTime(weather.sys.sunrise)
        sunset = formatTime(weather.sys.sunset)

        val result = Bundle().apply {
            putString("dataKey", "Hello from HomeFragment!")
        }
        parentFragmentManager.setFragmentResult("requestKey", result)

        weatherViewModel.setTemp(temp)
        Log.i(TAG, "updateUi: $temp $date")
    }

   /* fun getTemperature(): String {
        if (!::temp.isInitialized) {
            throw UninitializedPropertyAccessException("temp has not been initialized")
        }
        return temp
    }
*/
    private fun gettingHourlyWeatherDataFromViewModel() {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    weatherViewModel.hourlyForecastDataStateFlow.collect {state ->
                            when (state) {
                                is ApiState.Loading -> {
                                }

                                is ApiState.Success -> {
                                    val hourlyList = (state.data as Hourly).list.take(9)
                                    hourlyAdapter.submitList(hourlyList)

                                }

                                is ApiState.Failure -> {
                                    Log.e(
                                        "WeatherError",
                                        "Error retrieving hourly forecast data ${state.message}"
                                    )
                                }


                            }
                        }
                    }
            }
    }

   private fun gettingDailyWeatherDataFromViewModel() {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    weatherViewModel.dailyForecastDataStateFlow.collect { apiState ->
                        when (apiState) {
                            is ApiState.Loading -> {
                                binding.rvDetailedDays.visibility = View.GONE
                                binding.cardDaysDetails.visibility = View.GONE
                            }

                            is ApiState.Success -> {
                                delay(600)
                                binding.rvDetailedDays.visibility = View.VISIBLE
                                binding.cardDaysDetails.visibility = View.VISIBLE
                                val dailyList =
                                    (apiState.data as List<*>).filterIsInstance<DailyForecastElement>()
                                dailyAdapter.submitList(dailyList)
                            }

                            is ApiState.Failure -> {
                                binding.rvDetailedDays.visibility = View.GONE
                                binding.cardDaysDetails.visibility = View.GONE
                                Log.e(
                                    "WeatherError",
                                    "Error retrieving daily forecast data ${apiState.message}"
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

   private fun fetchDataFromDataBaseOrFromRemoteIfNetworkAvailable(weatherEntity: WeatherEntity) {
            weatherViewModel.fetchCurrentWeatherDataByCoordinates(
                weatherEntity.latitude,
                weatherEntity.longitude
            )
           // setCityNameBasedOnLatAndLong(weatherEntity.latitude, weatherEntity.longitude)
          /*  binding.swipeToRefresh.setOnRefreshListener {
                weatherViewModel.fetchCurrentWeatherDataByCoordinates(
                    weatherEntity.latitude,
                    weatherEntity.longitude
                )
                binding.swipeToRefresh.isRefreshing = false
            }*/
            Log.e("HomeScreenActivity", "Loaded data from database")
        }

   private fun setVisibilityOfViewsOnScreen(isLoading: Boolean) {
            if (isLoading) {
                binding.tvCityName.visibility = View.GONE
                binding.tvCurrentDegree.visibility = View.GONE
                binding.tvWeatherStatus.visibility = View.GONE
                binding.tvTempMin.visibility = View.GONE
                binding.tvTempMax.visibility = View.GONE
                binding.cardWeatherDetails.visibility = View.GONE
                binding.rvHourlyDegrees.visibility = View.GONE
                binding.rvDetailedDays.visibility = View.GONE
                binding.tvDate.visibility = View.GONE
            } else {
                slideInFromLeft(binding.tvCityName)
                slideInFromLeft(binding.tvDate)
                dynamicTextAnimation(binding.tvCurrentDegree)
                slideInAndScaleView(binding.tvWeatherStatus)
                dynamicTextAnimation(binding.tvTempMin)
                dynamicTextAnimation(binding.tvTempMax)
                slideInAndScaleView(binding.cardWeatherDetails)
                slideInAndScaleView(binding.rvHourlyDegrees)
                slideInAndScaleView(binding.rvDetailedDays)
            }
        }


}

    fun slideInFromLeft(view: View) {
        if (view.visibility != View.VISIBLE) {
            view.apply {
                alpha = 0f
                translationX = -200f
                visibility = View.VISIBLE


                animate()
                    .alpha(1f) // Fade in
                    .translationX(0f)
                    .setDuration(1000) // Animation duration
                    .setInterpolator(android.view.animation.DecelerateInterpolator()) // Smooth deceleration
                    .start()
            }
        }
    }
    fun dynamicTextAnimation(view: View) {
        if (view.visibility != View.VISIBLE) { // Check if the view is not already visible
            view.apply {
                alpha = 0f // Start fully transparent
                scaleX = 0.5f // Start smaller
                scaleY = 0.5f
                rotation = -30f

                visibility = View.VISIBLE

                // Animate with scaling, rotation, and fading
                animate()
                    .alpha(1f) // Fade in
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .rotation(0f)
                    .setDuration(1200)
                    .setInterpolator(android.view.animation.BounceInterpolator()) // Adds a fun bounce effect at the end
                    .withEndAction { // Return to original scale after bounce effect
                        animate()
                            .scaleX(1f) // Return to normal scale
                            .scaleY(1f)
                            .setDuration(300)
                            .start()
                    }
                    .start()
            }
        }
    }

    fun slideInAndScaleView(view: View) {
        if (view.visibility != View.VISIBLE) { // Check if the view is not already visible
            view.apply {
                scaleX = 0f
                scaleY = 0f
                translationY = 100f
                visibility = View.VISIBLE
                // Animate scaling and sliding in
                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0f)
                    .setDuration(1000)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }
        }
    }
    fun checkWeatherDescription(context: Context, weather: Weather): Int {
      val lottieAnimation = when (weather.weather[0].description.lowercase()) {
        context.getString(R.string.clear_sky) -> R.raw.clear_sky_anim
        context.getString(R.string.few_clouds) -> R.raw.few_clouds
        context.getString(R.string.scattered_clouds) -> R.raw.scattered_clouds_anim
        context.getString(R.string.broken_clouds) -> R.raw.broken_cloud_anim
        context.getString(R.string.overcast_clouds) -> R.raw.overcast_clouds_anim
        "light intensity shower rain" -> R.raw.rain_anim    // wait
        context.getString(R.string.light_rain) -> R.raw.rain_anim
        context.getString(R.string.moderate_rain) -> R.raw.rain_anim
        context.getString(R.string.light_snow) -> R.raw.snow_anim
        context.getString(R.string.snow) -> R.raw.snow_anim
        //underTesting
        "thunderstorm" -> R.raw.thunderstorm
        "mist" -> R.raw.mist
        else -> R.raw.clear_sky_anim
    }
    return lottieAnimation
}



