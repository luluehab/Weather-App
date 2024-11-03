package com.example.weatherapp.ui.alert.service

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.iti.data.model.AlarmEntity
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.database.WeatherDB
import com.example.weatherapp.database.WeatherDao
import com.example.weatherapp.model.Repo.RepositoryImpl
import com.example.weatherapp.network.APIClient
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.ui.alert.viewmodel.AlertViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import com.example.weatherapp.ui.splash.view.MainActivity
import com.example.weatherapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver: BroadcastReceiver() {

    private val TAG = "track"


    private lateinit var remoteSource: RemoteSource
    private lateinit var localSource: LocalSource
   private lateinit var repository: RepositoryImpl
   private lateinit var weatherDao: WeatherDao
   // private val weatherDao: WeatherDao = WeatherDB.getDatabase(context).GetWeatherDao()

    override fun onReceive(context: Context?, intent: Intent?) {

        val alertAction = intent?.action
        val alert = intent?.getParcelableExtra<AlarmEntity>(Constants.ALERT_KEY)
        weatherDao = context?.let { WeatherDB.getDatabase(it).GetWeatherDao() }!!

        when (alertAction) {
            Constants.ALERT_ACTION_NOTIFICATION -> {
                // Check for Android 13+ notification permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permission = ContextCompat.checkSelfPermission(
                        context!!, android.Manifest.permission.POST_NOTIFICATIONS
                    )
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Notification permission not granted.")
                        return
                    }
                }


                val sharedPrefs = context?.getSharedPreferences(
                    Constants.SETTINGS_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE
                )
                val showNotification = sharedPrefs?.getBoolean(
                    Constants.NOTIFICATION_KEY, true
                ) ?: true

                if (showNotification) {
                    showNotification(context)
                } else {
                    Log.d(TAG, "Notification is disabled in preferences.")
                }
            }

            Constants.ALERT_ACTION_ALARM -> {
                if (context != null) {
                    showAlarmDialog(context)
                }
            }
        }

        alert?.let {

            Log.i(TAG, "onReceive: delete the notification")

            localSource = context?.let { LocalSource(weatherDao) }!!
            CoroutineScope(Dispatchers.IO).launch {
                localSource.deleteAlarm(it)
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun createChannel(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannel = NotificationChannel(
                Constants.CHANNEL_NAME, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = Constants.CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun showNotification(context: Context?) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        createChannel(context)

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationText = context?.getString(R.string.check)

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_NAME)
            .setContentTitle("ask lulu")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.asklulu)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val mediaPlayer = MediaPlayer.create(context, R.raw.pop_up)
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener { mp ->
            mp.release()
        }

        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            mp.release()
            true
        }

        notificationManager.notify(Constants.CHANNEL_ID, notification)
    }

    @SuppressLint("InflateParams")
    private fun showAlarmDialog(context: Context) {
        // Inflate custom dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_alarm, null)
        val dialogOkButton = dialogView.findViewById<Button>(R.id.alert_stop)

        val mediaPlayer = MediaPlayer.create(context, R.raw.alert)


        val builder = AlertDialog.Builder(context, R.style.luluAlertDialogStyle)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.setCancelable(false)

        dialog.setOnShowListener {
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }


        val window = dialog.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            window?.setType(WindowManager.LayoutParams.TYPE_PHONE)
        }
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.TOP)

        dialog.show()

        // Coroutine to auto-dismiss the dialog after 15 seconds
        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(15000)
                withContext(Dispatchers.Main) {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                        showNotification(context)
                    }
                }
            } catch (_: Exception) {
            } finally {
                cancel()
            }
        }


        dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setOnDismissListener {
            mediaPlayer.stop()
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
        }
    }

}