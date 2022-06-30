package com.roozbeh.toopan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemsSalonsBinding

class SalonsAdapter(private val items: ArrayList<CoinMarkets>, cnx: Context?, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<SalonsAdapter.ViewHolder>() {

    private var cnx: Context? = null

    init {
        this.cnx = cnx
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemsSalonsBinding =
            DataBindingUtil.inflate(inflater, R.layout.items_salons, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        /*Glide
            .with(cnx!!)
            .load(item.image)
            .placeholder(R.drawable.ic__placeholder)
            .into(holder.binding.imgCoin)

        holder.binding.txtFullNameCoin.text = item.name
        holder.binding.txtNameCoin.text = item.symbol

        holder.binding.txtPriceCoin.text = "$" + item.currentPrice.toString()
        holder.binding.txtMarketCap.text = "$" + item.marketCap.toString()

        if (item.priceChangePercentage24h > 0){
            holder.binding.txtPercentage.setTextColor(cnx!!.getColor(R.color.teal_200))
        }else if (item.priceChangePercentage24h < 0){
            holder.binding.txtPercentage.setTextColor(cnx!!.getColor(R.color.red))
        }else{
            holder.binding.txtPercentage.setTextColor(cnx!!.getColor(R.color.white))
        }
        holder.binding.txtPercentage.text = item.priceChangePercentage24h.toString() + "%"


        holder.binding.txtTotalVolume.text = "$" + item.totalVolume.toString()



        holder.binding.constParent.setOnClickListener {
            listener.onItemClick(item.id, item.marketCap.toString(), item.currentPrice.toString(), item.priceChangePercentage24h)
        }
*/

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(var binding: ItemsSalonsBinding) : RecyclerView.ViewHolder(binding.root) {


    }


    interface OnItemClickListener {
        fun onItemClick(id: String, marketCap:String, currentPrice:String, priceChangePercentage24h:Double, )
    }
}
