package com.roozbeh.toopan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemsSalonsBinding
import com.roozbeh.toopan.modelApi.Salons
import com.roozbeh.toopan.utility.Constants
import kotlin.Exception
import kotlin.Int


class SalonsAdapter() :
    RecyclerView.Adapter<SalonsAdapter.ViewHolder>() {
    private var items = arrayListOf<Salons>()
    private var cnx: Context? = null
    private lateinit var listener: OnItemClickListener

    constructor(items: ArrayList<Salons>, cnx: Context?, listener: OnItemClickListener) : this() {
        this.cnx = cnx
        this.items = items
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /*parent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

        }*/
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemsSalonsBinding =
            DataBindingUtil.inflate(inflater, R.layout.items_salons, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var image = ""
        for (img in item.images) {
            image = img
        }

        Glide
            .with(cnx!!)
            .load(Constants.BASE_URL + image)
            .placeholder(R.drawable.ic_clubs)
            .into(holder.binding.imgSalon)

        holder.binding.txtNameSalon.text = item.name
        holder.binding.txtAddressSalon.text = item.address
        holder.binding.txtAddressSalon.text = item.address
        holder.binding.txtPriceSalonSince.text = "${item.amount} تومان"
        if (item.activate == true) {
            holder.binding.constAboutSinceSalon.visibility = View.INVISIBLE
            holder.binding.constManageSinceSalon.visibility = View.VISIBLE
        } else {
            holder.binding.constAboutSinceSalon.visibility = View.VISIBLE
            holder.binding.constManageSinceSalon.visibility = View.INVISIBLE
        }


        showMenu(holder.binding, position)
        holder.itemView.setOnClickListener {

        }

        holder.binding.constManageSinceSalon.setOnClickListener {
            listener.onManageClick()
        }

        holder.binding.constCreateJam.setOnClickListener {

        }
        holder.binding.constDetailsAndEdit.setOnClickListener {

        }


    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(var binding: ItemsSalonsBinding) : RecyclerView.ViewHolder(binding.root)

    private fun add(item: Salons) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun addAll(itemsList: ArrayList<Salons>) {
//        items.clear()
//        items.addAll(itemsList)
        for (salons in itemsList) {
            add(salons)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    //In the showMenu function from the previous example:
    private fun showMenu(binding: ItemsSalonsBinding, position: Int) {

        val popupMenu = PopupMenu(cnx, binding.imgDotsItemSalon)
        popupMenu.inflate(R.menu.menu_salon)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): kotlin.Boolean {
                if (item != null) {
                    when (item.itemId) {
                        R.id.jam -> {
                            Toast.makeText(cnx, position.toString(), Toast.LENGTH_SHORT).show()
                        }
                        R.id.edit -> {
                            listener.onEditSalon(items[position])
                        }
                        else -> true

                    }
                }
                return false
            }
        })

        binding.imgDotsFake.setOnClickListener {
            try {

                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu: Any? = popup.get(popupMenu)
                menu?.javaClass?.getDeclaredMethod(
                    "setForceShowIcon",
                    kotlin.Boolean::class.java
                )
                    ?.invoke(menu, true)

                true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(/*id: String, marketCap:String, currentPrice:String, priceChangePercentage24h:Double*/)
        fun onEditSalon(salons: Salons)
        fun onManageClick()
    }
}
