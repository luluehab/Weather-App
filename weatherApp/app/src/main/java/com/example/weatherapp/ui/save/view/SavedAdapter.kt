package com.example.weatherapp.ui.save.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.iti.data.model.DailyForecastElement
import com.example.iti.data.model.WeatherEntity
import com.example.weatherapp.databinding.ItemSavedBinding
import com.example.weatherapp.model.LocationData
import kotlinx.coroutines.CoroutineScope



class SavedDiffUtil : DiffUtil.ItemCallback<LocationData>()
{
    override fun areItemsTheSame(oldItem: LocationData, newItem: LocationData): Boolean {
        // return oldItem.id == newItem.id
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: LocationData, newItem: LocationData): Boolean {
        return oldItem == newItem
    }

}
class SavedAdapter( private val lifecycleScope: CoroutineScope,val onItemClick: (place: LocationData) -> Unit
) : ListAdapter<LocationData, SavedAdapter.SavedViewHolder>(SavedDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemSavedBinding.inflate(inflater, parent, false)
        return SavedViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
    }

    inner class SavedViewHolder(private val binding: ItemSavedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(currentItem: LocationData) {

            binding.tvLocationNameFavourites.text = currentItem.city
            itemView.setOnClickListener {
                onItemClick(currentItem)
            }
        }
    }
}



