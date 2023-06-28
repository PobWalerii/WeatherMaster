package com.example.weathermaster.ui.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.databinding.CityListItemBinding
import com.example.weathermaster.databinding.CityStringItemBinding
import java.util.*


@SuppressLint("NotifyDataSetChanged")
class CityStringAdapter: RecyclerView.Adapter<CityStringAdapter.ViewHolder>() {

    private var listCity: MutableList<CityAndWeatherFormated> = mutableListOf()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = CityStringItemBinding.bind(itemView)
        fun bind(item: CityAndWeatherFormated) {
            binding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.city_string_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind( listCity[position] )
    }

    override fun getItemCount(): Int = listCity.size
    override fun getItemId(position: Int) = listCity[position].id

    fun setList(list: List<CityAndWeatherFormated>) {
        listCity.clear()
        listCity.addAll(list)
        notifyDataSetChanged()
    }

    fun getCityFromPosition(position: Int): CityAndWeatherFormated = listCity[position]

}
