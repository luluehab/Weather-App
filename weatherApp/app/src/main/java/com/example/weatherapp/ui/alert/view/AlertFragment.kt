package com.example.weatherapp.ui.alert.view

import android.Manifest
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.iti.data.model.AlarmEntity
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.AlertDialogLayoutBinding
import com.example.weatherapp.databinding.FragmentAlertBinding
import com.example.weatherapp.model.Repo.RepositoryImpl
import com.example.weatherapp.network.APIClient
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.ui.alert.service.AlarmReceiver
import com.example.weatherapp.ui.alert.viewmodel.AlertViewModel
import com.example.weatherapp.ui.alert.viewmodel.AlertViewModelFactory
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.Helpers
import com.example.weatherapp.utils.Permission
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import com.google.android.material.R

class AlertFragment : Fragment() {

     val TAG = "splash"
    private var _binding: FragmentAlertBinding? = null
    private lateinit var bindingAlertLayout: AlertDialogLayoutBinding
    private lateinit var alertRecyclerAdapter: AlertAdapter
    private lateinit var settingNotification : String
    private lateinit var remoteSource: RemoteSource
    private lateinit var localSource: LocalSource
    private lateinit var repository: RepositoryImpl
    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(requireActivity().application)
    }
    private val alertViewModel: AlertViewModel by viewModels {
        AlertViewModelFactory(repository)
    }

    private val alarmManager: AlarmManager by lazy {
        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView: Alarm Fragment ")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val root: View = binding.root

        remoteSource = RemoteSource(APIClient.getApiService())
        localSource = context?.let { LocalSource(it) }!!
        repository = RepositoryImpl.getRepository(remoteSource, localSource, settingViewModel)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Notification Setting
        //settingViewModel.notificationSetting.observe(viewLifecycleOwner) { notificationSetting ->
        //    settingNotification = notificationSetting
        //}
        settingNotification = settingViewModel.getNotificationSetting().toString()
        alertViewModel.getAllAlarms()
        alertRecyclerAdapter = AlertAdapter()
        binding.rvAlerts.adapter = alertRecyclerAdapter
        deleteBySwipe(view)

        lifecycleScope.launch {
            alertViewModel.alarmsStateFlow.collectLatest {
                alertRecyclerAdapter.submitList(it)
            }
        }

        binding.btnAddAlert.setOnClickListener {
            Log.i(TAG, "onViewCreated:setOnClickListener $settingNotification ${Constants.NOTIFICATION}  ")
            if (Constants.NOTIFICATION == settingNotification) {
                    if (Permission.notificationPermission(requireContext())) {
                        showTimeDialog()
                    } else {
                        showSettingDialog()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "you need to enable notification first.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }

        binding.btnEnableNotification.setOnClickListener {
            requestNotificationPermission()
        }

        binding.btnEnableAlert.setOnClickListener {
            requestOverlayPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        tellUserAboutPermissions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteBySwipe(view: View) {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alarmItem = alertRecyclerAdapter.currentList[position]

                cancelAlarm(alarmItem)
                alertViewModel.deleteAlarm(alarmItem)

                val mediaPlayer = MediaPlayer.create(context, com.example.weatherapp.R.raw.delete)
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { mp -> mp.release() }


                Snackbar.make(view, "Alarm deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {

                        alertViewModel.insertAlarm(alarmItem)
                    }
                    show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rvAlerts)
    }

    private fun showTimeDialog() {
        val currentTimeInMillis = System.currentTimeMillis()

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        bindingAlertLayout = AlertDialogLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(bindingAlertLayout.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingAlertLayout.tvFromDateDialog.text =
            Helpers.formatLongToAnyString(currentTimeInMillis, "dd MMM yyyy")
        bindingAlertLayout.tvFromTimeDialog.text =
            Helpers.formatLongToAnyString(currentTimeInMillis + 60 * 1000, "hh:mm a")

        bindingAlertLayout.cvFrom.setOnClickListener {
            showDatePicker()
        }

        bindingAlertLayout.radioGroupAlertDialog.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == bindingAlertLayout.radioAlert.id && !Settings.canDrawOverlays(
                    requireContext()
                )
            ) {
                requestOverlayPermission()
                dialog.dismiss()
            }
        }

        bindingAlertLayout.btnSaveDialog.setOnClickListener {

            val kindId = bindingAlertLayout.radioGroupAlertDialog.checkedRadioButtonId
            var kind: String = Constants.NOTIFICATION
            if (kindId == bindingAlertLayout.radioAlert.id) {
                kind = Constants.ALERT
            }

            val time = Helpers.formatFromStringToLong(
                bindingAlertLayout.tvFromDateDialog.text.toString(),
                bindingAlertLayout.tvFromTimeDialog.text.toString()
            )

            val alarmItem = AlarmEntity(time, kind)
            if (time > currentTimeInMillis) {
                alertViewModel.insertAlarm(alarmItem)


                setUpTheAlarm(alarmItem)

                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "please select a time in the future",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun setUpTheAlarm(alert: AlarmEntity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission()
                return
            }
        }
        try {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alert.time,
                getPendingIntent(alert)
            )
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Failed to set alarm: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            Log.i(TAG, "Failed to set alarm: ${e.message} ")
        }
    }

    private fun getPendingIntent(alert: AlarmEntity): PendingIntent {
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            action = if (alert.kind == Constants.NOTIFICATION) {
                Constants.ALERT_ACTION_NOTIFICATION
            } else {
                Constants.ALERT_ACTION_ALARM
            }
            putExtra(Constants.ALERT_KEY, alert)
        }

        return PendingIntent.getBroadcast(
            requireContext(),
            alert.time.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:com.example.weatherapp")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun requestOverlayPermission() {
        val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:com.example.weatherapp")
        startActivity(intent)
    }

    private fun requestNotificationPermission() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:com.example.weatherapp")
        startActivity(intent)
    }

    private fun cancelAlarm(alarmItem: AlarmEntity) {
        val pendingIntent = getPendingIntent(alarmItem)
        alarmManager.cancel(pendingIntent)
    }


    private fun showDatePicker() {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .setTitleText("Select date")
                .build()

        datePicker.show(parentFragmentManager, "date")

        datePicker.addOnPositiveButtonClickListener { date ->
            bindingAlertLayout.tvFromDateDialog.text =
                Helpers.formatLongToAnyString(date, "dd MMM yyyy")
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(currentHour)
                .setMinute(currentMinute + 1)
                .setTitleText("Select Appointment time")
                .build()

        timePicker.show(parentFragmentManager, "time")
        timePicker.addOnPositiveButtonClickListener {

            bindingAlertLayout.tvFromTimeDialog.text =
                Helpers.formatHourMinuteToString(timePicker.hour, timePicker.minute)

        }

        timePicker.addOnCancelListener {

            bindingAlertLayout.tvFromTimeDialog.text =
                Helpers.formatHourMinuteToString(timePicker.hour, timePicker.minute)

        }
    }

    private fun tellUserAboutPermissions() {
        if (Permission.notificationPermission(requireContext())) {
            binding.tvAlertUserNotification.visibility = View.GONE
            binding.btnEnableNotification.visibility = View.GONE
        } else {
            binding.tvAlertUserNotification.visibility = View.VISIBLE
            binding.btnEnableNotification.visibility = View.VISIBLE
        }

        if (Settings.canDrawOverlays(requireContext())) {
            binding.tvAlertUserAlert.visibility = View.GONE
            binding.btnEnableAlert.visibility = View.GONE
        } else {
            binding.tvAlertUserAlert.visibility = View.VISIBLE
            binding.btnEnableAlert.visibility = View.VISIBLE
        }
    }
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!requireContext().getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${requireContext().packageName}"))
                startActivity(intent)
            }
        }
    }

}
