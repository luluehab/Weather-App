package com.example.weatherapp.ui.setting.view

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSavedBinding
import com.example.weatherapp.databinding.FragmentSettingBinding
import com.example.weatherapp.ui.save.viewmodel.SavedViewModel
import com.example.weatherapp.ui.save.viewmodel.SavedViewModelFactory
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.Constants.ARABIC
import com.example.weatherapp.utils.Constants.CELSIUS_SHARED
import com.example.weatherapp.utils.Constants.DISABLE
import com.example.weatherapp.utils.Constants.ENGLISH_SHARED
import com.example.weatherapp.utils.Constants.FAHRENHEIT_SHARED
import com.example.weatherapp.utils.Constants.GPS
import com.example.weatherapp.utils.Constants.KELVIN_SHARED
import com.example.weatherapp.utils.Constants.MAP
import com.example.weatherapp.utils.Constants.METER_PER_SECOND
import com.example.weatherapp.utils.Constants.MILES_PER_HOUR
import com.example.weatherapp.utils.Constants.NOTIFICATION
import java.util.Locale
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.splash.view.SplashActivity


class SettingFragment : Fragment() {

    private val TAG = "track"
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(requireActivity().application)
    }
    private lateinit var temperatureUnit : String
    private lateinit var speedUnit : String
    private lateinit var languageSetting : String
    private lateinit var notificationSetting : String
    private lateinit var locationSetting : String
    private var previousLanguageSetting: String = ENGLISH_SHARED // Default to English or get initial value from SharedPreferences
    private var locationCallback: SplashActivity.LocationFromGPS? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        // Save initial language setting
        previousLanguageSetting = settingViewModel.getLanguageSetting() ?: ENGLISH_SHARED

        onTemperatureSelection()
        onWindSpeedSelection()
        onLanguageSelection()
        onLocationSelection()
        onNotificationSelection()


        temperatureUnit = settingViewModel.getTemperatureUnit()!!
        when (temperatureUnit) {
            CELSIUS_SHARED -> binding.radioCelsius.isChecked = true
            FAHRENHEIT_SHARED -> binding.radioFahrenheit.isChecked = true
            KELVIN_SHARED -> binding.radioKelvin.isChecked = true
        }

        speedUnit = settingViewModel.getSpeedUnit()!!
        when (speedUnit) {
            MILES_PER_HOUR -> binding.radioMilesPerHour.isChecked = true
            METER_PER_SECOND -> binding.radioMeterPerSecond.isChecked = true
        }


        languageSetting = settingViewModel.getLanguageSetting()!!
        when (languageSetting) {
            ENGLISH_SHARED -> binding.radioEnglish.isChecked = true
            ARABIC -> binding.radioArabic.isChecked = true
        }

        notificationSetting = settingViewModel.getNotificationSetting()!!
        when (notificationSetting) {
            NOTIFICATION -> binding.radioOnNotification.isChecked = true
            DISABLE -> binding.radioOffNotification.isChecked = true
        }

        locationSetting = settingViewModel.getLocationSetting()!!
        when (locationSetting) {
            GPS -> binding.radioGPS.isChecked = true
            MAP -> binding.radioMap.isChecked = true
        }


        
        return binding.root
    }

    private fun onTemperatureSelection() {
        Log.i(TAG, "onTemperatureSelection: ")
        binding.radioKelvin.setOnClickListener {
            settingViewModel.setTemperatureUnit(KELVIN_SHARED)
            Log.i(TAG, "onTemperatureSelection KELVIN_SHARED: ")
        }
        binding.radioCelsius.setOnClickListener {
            settingViewModel.setTemperatureUnit(CELSIUS_SHARED)
            Log.i(TAG, "onTemperatureSelection CELSIUS_SHARED: ")
        }
        binding.radioFahrenheit.setOnClickListener {
            settingViewModel.setTemperatureUnit(FAHRENHEIT_SHARED)
            Log.i(TAG, "onTemperatureSelection FAHRENHEIT_SHARED: ")
        }
    }

    private fun onWindSpeedSelection() {
        binding.radioMeterPerSecond.setOnClickListener {
            settingViewModel.setSpeedUnit(METER_PER_SECOND)
        }
        binding.radioMilesPerHour.setOnClickListener {
            settingViewModel.setSpeedUnit(MILES_PER_HOUR)
        }
    }
    private fun onLanguageSelection() {
        binding.radioArabic.setOnClickListener {
            showLanguageChangeDialog(ARABIC)
        }
        binding.radioEnglish.setOnClickListener {
            showLanguageChangeDialog(ENGLISH_SHARED)
        }
    }

    private fun onLocationSelection() {
        binding.radioGPS.setOnClickListener {
            settingViewModel.setLocationSetting(GPS)
            val intent = Intent(requireActivity(), SplashActivity::class.java)
            startActivity(intent)
            //locationCallback?.getlocationFromGPS()
        }
        binding.radioMap.setOnClickListener {
            settingViewModel.setLocationSetting(MAP)
            // go to map
            // Create a bundle to pass data
            val bundle = Bundle().apply {
                putString("location", "Setting") // Change "yourLocationData" to your actual data
            }

            // Navigate to MapSearchFragment with the bundle
            findNavController().navigate(R.id.action_savedFragment_to_mapSearchFragment, bundle)
            //findNavController().navigate(R.id.action_savedFragment_to_mapSearchFragment)
            //binding.mapfragmentContainerView.visibility = View.VISIBLE
        }
    }

    private fun onNotificationSelection() {
        binding.radioOnNotification.setOnClickListener {
            settingViewModel.setNotificationSetting(NOTIFICATION)
        }
        binding.radioOffNotification.setOnClickListener {
            settingViewModel.setNotificationSetting(DISABLE)
        }
    }



    private fun showLanguageChangeDialog(languageCode: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.language_change))
            .setMessage(getString(R.string.changing_the_language_will_exit_the_app_please_reopen_the_app_to_see_the_changes))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                settingViewModel.setLanguageSetting(languageCode)
                previousLanguageSetting = languageCode
                switchLanguage(languageCode)
            }
            .setNegativeButton(R.string.cancel){ _, _ ->
                // User canceled, revert to previous language selection
                if (previousLanguageSetting == ENGLISH_SHARED) {
                    binding.radioEnglish.isChecked = true
                } else {
                    binding.radioArabic.isChecked = true
                }
            }// User canceled, do nothing
            .create()

        dialog.setOnShowListener {
            val buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val buttonCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Change text color
            buttonOk.setTextColor(
                resources.getColor(
                    R.color.lulu,
                    null
                )
            ) // Change to your desired color
            buttonCancel.setTextColor(
                resources.getColor(
                    R.color.lulu,
                    null
                )
            ) // Change to your desired color
        }
        dialog.show()
    }

    private fun switchLanguage(language: String) {
        Log.i(TAG, "switchLanguage: $language ")
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(requireActivity().resources.configuration)
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
        // Restart the activity to apply the language change
        requireActivity().recreate()
    }

    override fun onDetach() {
        super.onDetach()
        locationCallback = null
    }

}