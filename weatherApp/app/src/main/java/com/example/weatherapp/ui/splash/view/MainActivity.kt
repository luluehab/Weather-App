package com.example.weatherapp.ui.splash.view

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.home.view.HomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.model.Repo.RepositoryImpl
import com.example.weatherapp.network.APIClient
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.ui.splash.viewmodel.SharedViewModel
import com.example.weatherapp.ui.splash.viewmodel.SharedViewModelFactory
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.model.Coordinate
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings
import androidx.fragment.app.viewModels
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var countryName: String = ""
    private var adminArea: String = ""
    private var streetAddress: String = ""
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var remoteSource :RemoteSource
    private lateinit var localSource : LocalSource
    private lateinit var repository : RepositoryImpl

    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(this.application)
    }
    private val TAG: String = "Main Activity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set init fot Setting
        settingViewModel.initializeDefaults()
        // Check for microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
        remoteSource = RemoteSource(APIClient.getApiService())
        localSource = LocalSource(this)
        repository = RepositoryImpl.getRepository(remoteSource , localSource , settingViewModel)


       /* setDefaultLanguage()*/

        // Retrieve the data from the intent
        latitude = intent.getDoubleExtra("latitude", 0.0)  // Default to 0.0 if no data found
        longitude = intent.getDoubleExtra("longitude", 0.0)
        countryName = intent.getStringExtra("countryName").toString()
        adminArea = intent.getStringExtra("adminArea").toString()
        streetAddress = intent.getStringExtra("streetAddress").toString()

        //Log.i(TAG, "onCreate: ${homeFragment.}")
        val args = Bundle()
        args.putDouble("latitude", latitude)
        args.putDouble("longitude", longitude)
        args.putString("countryName" , countryName)
        args.putString("adminArea" , adminArea)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.fab.setOnClickListener { view ->
            startSpeechToText()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object  : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
                Snackbar.make(binding.root, "Lulu Listening...", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab).show()

            }
            // Other RecognitionListener methods
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(p0: Int) {
                Toast.makeText(this@MainActivity, "Error recognizing speech: $p0", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(p0: Bundle?) {
                // Get the recognized speech results
                val matches = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val recognizedText = matches[0] // Take the first result
                    Toast.makeText(this@MainActivity, recognizedText , Toast.LENGTH_SHORT).show()
                    handleRecognizedText(recognizedText)
                }
            }
            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })


        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale("ar")  // Set to Arabic or change to desired locale
            } else {
                Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show()
            }
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_alert, R.id.nav_saved , R.id.nav_setting
            ), drawerLayout
        )



        Log.i(TAG, "onCreate: $latitude, $longitude , $adminArea , $countryName ")
        // Navigate to the home fragment (or any other fragment) with the bundle
        navController.navigate(R.id.nav_home, args)  // Passing the bundle here
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun attachBaseContext(newBase: Context?) {

        val sharedPreferences = newBase?.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences?.getString("language", Locale.getDefault().language)
            ?: Constants.ENGLISH_SHARED
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        val context = newBase?.createConfigurationContext(config)
        super.attachBaseContext(newBase)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun startSpeechToText() {
        // Create an Intent for recognizing speech
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        // Start listening for speech
        speechRecognizer.startListening(intent)
    }

    private fun handleRecognizedText(recognizedText: String) {
        when {
            recognizedText.equals("hello lulu", ignoreCase = true) -> {
                textToSpeech.speak("hello alb lulu", TextToSpeech.QUEUE_FLUSH, null, null)
            }
            recognizedText.equals("hi lulu", ignoreCase = true) -> {
                textToSpeech.speak("hi alb lulu", TextToSpeech.QUEUE_FLUSH, null, null)
            }
            recognizedText.equals("how are you", ignoreCase = true) -> {
                textToSpeech.speak("Better than you", TextToSpeech.QUEUE_FLUSH, null, null)
            }

            recognizedText.equals("send address sms", ignoreCase = true )-> {
                if (streetAddress.isNotEmpty()) {
                    textToSpeech.speak("ok i will send it", TextToSpeech.QUEUE_FLUSH, null, null)
                    sendSMSWithLocation()
                }
                else {
                    textToSpeech.speak("Sorry, I do not have data to send it", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            recognizedText.equals("show map", ignoreCase = true )-> {
                if ( streetAddress.isNotEmpty()) {
                    textToSpeech.speak("ok i will show it", TextToSpeech.QUEUE_FLUSH, null, null)
                    //openMapFragment()
                }
                else {
                    textToSpeech.speak("Sorry, I don't have data to show it", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            recognizedText.equals("where am I", ignoreCase = true) -> {
                if ( streetAddress.isNotEmpty()) {
                    val response = "you are in $streetAddress"
                    textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                else {
                    textToSpeech.speak("Sorry, I didn't catch the location data.", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            recognizedText.equals("what is the current temperature", ignoreCase = true) -> {
              //  val Value = homeFragment.view?.findViewById<TextView>(R.id.tv_current_degree)?.text
              //  textToSpeech.speak(" the current temperature is ${homeFragment.getTemperature()}", TextToSpeech.QUEUE_FLUSH, null, null)
            }
            recognizedText.equals("what is the date", ignoreCase = true) -> {
                //  val Value = homeFragment.view?.findViewById<TextView>(R.id.tv_current_degree)?.text
               // textToSpeech.speak(" the current temperature is ${homeFragment.date}", TextToSpeech.QUEUE_FLUSH, null, null)
            }
            else -> {
                textToSpeech.speak("Sorry, I didn't understand that.", TextToSpeech.QUEUE_FLUSH, null, null)

            }
        }

    }


    // Function to send SMS with location and address
    private fun sendSMSWithLocation() {

        val messageBody = "Al72oni ya nas ana m5tofaaa yalhwi yanas ana m5toofaaa wl location ahoo : Latitude: $latitude, Longitude: $longitude\n Address: $streetAddress"
        val phoneNumber = "+201273065152"

        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$phoneNumber")
            putExtra("sms_body", messageBody)
        }

        if (smsIntent.resolveActivity(packageManager) != null) {
            startActivity(smsIntent)
        } else {
            Toast.makeText(this, "No SMS app found", Toast.LENGTH_SHORT).show()
        }
    }

}
