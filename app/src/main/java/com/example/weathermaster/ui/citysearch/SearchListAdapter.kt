package com.example.weathermaster.ui.citysearch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermaster.R
import com.example.weathermaster.data.database.entity.SearchListItem
import com.example.weathermaster.databinding.SearhListItemBinding

@SuppressLint("NotifyDataSetChanged")
class SearchListAdapter: RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var listCity: List<SearchListItem> = emptyList()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = SearhListItemBinding.bind(itemView)
        fun bind(item: SearchListItem) {
            binding.item = item
        }
        fun getBinding(): SearhListItemBinding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.searh_list_item, parent, false)
        val holder = ViewHolder(view)
        val binding = holder.getBinding()

        binding.actionButton.setOnClickListener {
            listener?.onItemClick( listCity[holder.layoutPosition] )
        }
        return holder
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    interface OnItemClickListener {
        fun onItemClick(currentItem: SearchListItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind( listCity[position] )
    }

    override fun getItemCount(): Int = listCity.size

    fun setList(list: List<SearchListItem>) {
        listCity = list
        notifyDataSetChanged()
    }
}
