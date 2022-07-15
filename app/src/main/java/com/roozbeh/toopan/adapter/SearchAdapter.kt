package com.roozbeh.toopan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemSearchNeshanBinding
import org.neshan.common.model.LatLng
import org.neshan.servicessdk.search.model.Item

class SearchAdapter(
    private var items: ArrayList<Item>,
    private val onSearchItemListener: OnSearchItemListener
) : RecyclerView.Adapter<SearchAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemSearchNeshanBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_search_neshan, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtSearchItemNeshan.text = items[position].address
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateList(items: ArrayList<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun historyAddList(items: List<Item>) {
        this.items.addAll(0,items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(var binding: ItemSearchNeshanBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View) {
            val location = items[adapterPosition].location
            val LatLng = location?.let { LatLng(it.latitude, location.longitude) }
            onSearchItemListener.onSearchItemClick(LatLng, items[adapterPosition])
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnSearchItemListener {
        fun onSearchItemClick(LatLng: LatLng?, item: Item)
    }
}