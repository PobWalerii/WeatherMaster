package com.example.weathermaster.ui.citylist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.data.apiservice.result.SearchListItem
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.databinding.CityListItemBinding
import com.example.weathermaster.databinding.SearhListItemBinding

@SuppressLint("NotifyDataSetChanged")
class CityListAdapter: RecyclerView.Adapter<CityListAdapter.ViewHolder>() {

    //private var listener: OnItemClickListener? = null
    private var listCity: List<City> = emptyList()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = CityListItemBinding.bind(itemView)
        fun bind(item: City) {
            binding.item = item
        }
        //fun getBinding(): CityListItemBinding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.city_list_item, parent, false)
        val holder = ViewHolder(view)
        //val binding = holder.getBinding()

        return holder
    }

    //fun setOnItemClickListener(listener: OnItemClickListener) {
    //    this.listener = listener
    //}
    //interface OnItemClickListener {
    //    fun onItemClick(currentItem: SearchListItem)
    //}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind( listCity[position] )
    }

    override fun getItemCount(): Int = listCity.size

    fun setList(list: List<City>) {
        listCity = list
        notifyDataSetChanged()
    }
}
