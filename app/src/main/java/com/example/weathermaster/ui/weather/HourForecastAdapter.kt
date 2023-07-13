package com.example.weathermaster.ui.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.data.database.entity.ForecastWeatherHour
import com.example.weathermaster.databinding.ForecastHourItemBinding
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class HourForecastAdapter: RecyclerView.Adapter<HourForecastAdapter.ViewHolder>() {

    private var listHour: MutableList<ForecastWeatherHour> = mutableListOf()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ForecastHourItemBinding.bind(itemView)
        fun bind(item: ForecastWeatherHour) {
            binding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.forecast_hour_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind( listHour[position] )
    }

    override fun getItemCount(): Int = listHour.size

    fun setList(list: List<ForecastWeatherHour>) {
        listHour.clear()
        listHour.addAll(list)
        notifyDataSetChanged()
    }

    fun getItems(start: Int, end: Int): List<ForecastWeatherHour> {
        return if(!( start >= 0 && itemCount != 0)) emptyList() else listHour.subList(start, end)
    }

}
