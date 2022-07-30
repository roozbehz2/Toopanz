package com.roozbeh.toopan.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemsSansWeekBinding
import com.roozbeh.toopan.modelApi.WeekSalonSanseResponse

class SansAdapter(
    val item: WeekSalonSanseResponse,
    private val cnx: Context
) : RecyclerView.Adapter<SansAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemsSansWeekBinding =
            DataBindingUtil.inflate(inflater, R.layout.items_sans_week, parent, false)

        val view = binding.root
        val params: ViewGroup.LayoutParams = view.layoutParams
        params.height =
            ((parent.measuredWidth - cnx.resources.getDimension(com.intuit.sdp.R.dimen._60sdp)
                    - (8 * cnx.resources.getDimension(com.intuit.sdp.R.dimen._7sdp))) / 7).toInt()
        view.layoutParams = params
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        item.times?.get(position)?.let { holder.binding.txtTimeSans.text = it }




    }

    override fun getItemCount(): Int = item.times?.size ?: 0


    class ViewHolder(var binding: ItemsSansWeekBinding) : RecyclerView.ViewHolder(binding.root)

}