package com.roozbeh.toopan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemsPossibilitiesBinding
import com.roozbeh.toopan.databinding.ItemsSalonsBinding
import com.roozbeh.toopan.modelApi.SalonPossibilities
import com.roozbeh.toopan.modelApi.Salons

class PossibilitiesAdapter() : RecyclerView.Adapter<PossibilitiesAdapter.ViewHolder>() {

    private var items = arrayListOf<SalonPossibilities>()
    private var selectedItems = arrayListOf<SalonPossibilities>()
    private var cnx: Context? = null
    private lateinit var listener: OnItemClickListener


    constructor(
        items: ArrayList<SalonPossibilities>,
        selectedItems: ArrayList<SalonPossibilities>?,
        cnx: Context,
        listener: OnItemClickListener
    ) : this() {
        this.cnx = cnx
        this.items = items
        if (selectedItems != null) {
            this.selectedItems = selectedItems
        }
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemsPossibilitiesBinding =
            DataBindingUtil.inflate(inflater, R.layout.items_possibilities, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.checkboxPossibilities.text = items[position].possibilities

        for (i in selectedItems.indices step 1){
            if (items[position].possibilities == selectedItems[i].possibilities){
                holder.binding.checkboxPossibilities.isChecked = true
            }
        }


        holder.binding.checkboxPossibilities.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedItems.add(items[position])
            } else {
                selectedItems.remove(items[position])
            }
            listener.onItemClick(selectedItems)
        }
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(var binding: ItemsPossibilitiesBinding) : RecyclerView.ViewHolder(binding.root)


    interface OnItemClickListener {
        fun onItemClick(selectedItems: ArrayList<SalonPossibilities>)
    }
}