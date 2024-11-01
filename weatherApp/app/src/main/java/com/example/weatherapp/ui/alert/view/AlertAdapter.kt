package com.example.weatherapp.ui.alert.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.iti.data.model.AlarmEntity
import com.example.iti.data.model.DailyForecastElement
import com.example.weatherapp.databinding.ItemAlertBinding
import com.example.weatherapp.ui.home.view.DailyAdapter
import com.example.weatherapp.ui.home.view.dailyDiffUtil
import com.example.weatherapp.utils.Helpers
import kotlinx.coroutines.CoroutineScope


class alarmDiffUtil : DiffUtil.ItemCallback<AlarmEntity>()
{
    override fun areItemsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
        // return oldItem.id == newItem.id
        return oldItem.time == newItem.time
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
        return oldItem == newItem
    }

}

class AlertAdapter :  ListAdapter<AlarmEntity, AlertAdapter.ViewHolder>(
    alarmDiffUtil()
){


    inner class ViewHolder(private val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(currentItem: AlarmEntity) {
            binding.apply {
                tvKind.text = currentItem.kind

                tvFromDate.text =
                   Helpers.formatLongToAnyString(currentItem.time, "dd MMM yyyy")

                tvFromTime.text =
                    Helpers.formatLongToAnyString(currentItem.time, "hh:mm a")

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemAlertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
    }
}