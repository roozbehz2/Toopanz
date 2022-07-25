package com.roozbeh.toopan.adapter

import android.content.Context
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemsSalonsBinding
import com.roozbeh.toopan.modelApi.Salons
import com.roozbeh.toopan.utility.Constants


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

        Glide
            .with(cnx!!)
            .load(Constants.BASE_URL + item.avatar)
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

    inner class ViewHolder(var binding: ItemsSalonsBinding) : RecyclerView.ViewHolder(binding.root)

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
        if (items[position].activate == true){
            popupMenu.menu[2].title = cnx?.getString(R.string.deActive)
            popupMenu.menu[2].icon = cnx?.getDrawable(R.drawable.ic_deactive)
        }else{
            popupMenu.menu[2].title = cnx?.getString(R.string.active)
            popupMenu.menu[2].icon = cnx?.getDrawable(R.drawable.ic_active)
        }
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                if (item != null) {
                    when (item.itemId) {
                        R.id.jam -> {
                            Toast.makeText(cnx, position.toString(), Toast.LENGTH_SHORT).show()
                        }
                        R.id.edit -> {
                            listener.onEditSalon(items[position].id)
                        }
                        R.id.enableDisable -> {
                            /*item.title = cnx?.getString(R.string.active)
                            item.icon =  cnx?.getDrawable(R.drawable.ic_active)*/
                            listener.onActiveDeActive(items[position].id)
                        }


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
                    Boolean::class.java
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
        fun onActiveDeActive(salonsId: Int?)
        fun onEditSalon(salonsId: Int?)
        fun onManageClick()
    }
}
