package com.example.weatherapp.ui.home.view

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.iti.data.model.DailyForecastElement
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemDailyBinding
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModel
import com.example.weatherapp.ui.setting.viewmodel.SettingViewModelFactory
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.Constants.TEMPERATURE_FORMAT
import com.example.weatherapp.utils.Helpers.convertTemperature
import com.example.weatherapp.utils.Helpers.getUnitSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class dailyDiffUtil : DiffUtil.ItemCallback<DailyForecastElement>()
{
    override fun areItemsTheSame(oldItem: DailyForecastElement, newItem: DailyForecastElement): Boolean {
       // return oldItem.id == newItem.id
        return oldItem.dt == newItem.dt
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DailyForecastElement, newItem: DailyForecastElement): Boolean {
        return oldItem == newItem
    }

}

 class DailyAdapter (private val lifecycleScope: CoroutineScope, private val unit: String):  ListAdapter<DailyForecastElement, DailyAdapter.ViewHolder>(dailyDiffUtil()){


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
         return ViewHolder(binding)
     }

     @RequiresApi(Build.VERSION_CODES.O)
     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val animation =
             AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_bottom)
         holder.bind(getItem(position))
         holder.itemView.startAnimation(animation)
     }




    inner class ViewHolder(private val binding: ItemDailyBinding): RecyclerView.ViewHolder(binding.root){
         @RequiresApi(Build.VERSION_CODES.O)
         fun bind(dailyWeather: DailyForecastElement) {

             lifecycleScope.launch(Dispatchers.Main) {

                 //val unit = settingViewModel.getTemperatureUnit()
                 // Convert the timestamp to a LocalDate
                 val date = Instant.ofEpochSecond(dailyWeather.dt)
                     .atZone(ZoneId.systemDefault())
                     .toLocalDate()

                 // Check if the date is today
                 val today = LocalDate.now()
                 val dayString = when (date) {
                     today -> getString(itemView.context, R.string.today)
                     today.plusDays(1) -> getString(itemView.context, R.string.tomorrow)
                     else -> date.format(
                         DateTimeFormatter.ofPattern(
                             "EEEE",
                             Locale.getDefault()
                         )
                     ) // Day of the week
                 }
                 val maxTemp = convertTemperature(dailyWeather.main.temp_max, unit)
                 val minTemp = convertTemperature(dailyWeather.main.temp_min, unit)
                 binding.tvDayDays.text = dayString

                 // Set weather details
                 binding.tvWeatherCondition.text = dailyWeather.weather[0].description
                     .split(" ")
                     .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                 binding.tvHighDegree.text = String.format("%.0f", maxTemp, getUnitSymbol(unit))
                 binding.tvLowDegree.text =
                     String.format(TEMPERATURE_FORMAT, minTemp, getUnitSymbol( unit))

                 val iconCode = dailyWeather.weather[0].icon
                 binding.ivIconDays.setImageResource(getCustomIconForWeather(iconCode))
             }
         }

         private fun getCustomIconForWeather(iconCode: String): Int {
             return when (iconCode) {
                 "01d", "01n" -> R.drawable.ic_clear_sky
                 "02d", "02n" -> R.drawable.ic_few_cloud
                 "03d", "03n" -> R.drawable.ic_scattered_clouds
                 "04d", "04n" -> R.drawable.ic_broken_clouds
                 "09d", "09n" -> R.drawable.ic_shower_rain
                 "10d", "10n" -> R.drawable.ic_rain
                 "11d", "11n" -> R.drawable.ic_thunderstorm
                 "13d", "13n" -> R.drawable.ic_snow
                 "50d", "50n" -> R.drawable.ic_mist
                 else -> R.drawable.ic_clear_sky
             }
         }

     }
 }