package com.roozbeh.toopan.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemsImagesSalonBinding
import com.roozbeh.toopan.utility.Constants
import com.roozbeh.toopan.utility.Utils

class ImagesSalonAdapter() : RecyclerView.Adapter<ImagesSalonAdapter.ViewHolder>() {

    private var items = arrayListOf<String>()
    private var selectedItems = arrayListOf<String>()
    private var cnx: Context? = null
    private lateinit var listener: OnItemClickListener


    constructor(
        items: ArrayList<String>,
        cnx: Context,
        listener: OnItemClickListener
    ) : this() {
        this.cnx = cnx
        this.items = items
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemsImagesSalonBinding =
            DataBindingUtil.inflate(inflater, R.layout.items_images_salon, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cnx?.let { it1 ->
            Glide
                .with(it1)
                .load(Constants.BASE_URL + items[position])
                .placeholder(R.drawable.ic_profile)
                .into(holder.binding.imgImageADSalon)

            holder.itemView.setOnClickListener {
                Utils.showSnackBar(
                    it1,
                    holder.itemView,
                    it1.getString(R.string.forDeleteHoldItem),
                    it1.getColor(R.color.snackBar)
                )
            }


            holder.itemView.setOnLongClickListener {
                listener.onItemClick(items[position])
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(0, itemCount)
                true
            }
        }

    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(var binding: ItemsImagesSalonBinding) : RecyclerView.ViewHolder(binding.root)


    interface OnItemClickListener {
        fun onItemClick(selectedItems: String)
    }
}