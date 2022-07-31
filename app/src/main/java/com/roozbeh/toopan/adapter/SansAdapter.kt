package com.roozbeh.toopan.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Status
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ItemsSansWeekBinding
import com.roozbeh.toopan.modelApi.WeekSalonSanseResponse

class SansAdapter(
    val item: WeekSalonSanseResponse,
    private val cnx: Context
) : RecyclerView.Adapter<SansAdapter.ViewHolder>() {

    private var statusSunday: Status = Status.NOT_RESERVED
    private var statusSaturday: Status = Status.NOT_RESERVED
    private var statusMonday: Status = Status.NOT_RESERVED
    private var statusTuesday: Status = Status.NOT_RESERVED
    private var statusWednesday: Status = Status.NOT_RESERVED
    private var statusThursDay: Status = Status.NOT_RESERVED
    private var statusFriday: Status = Status.NOT_RESERVED

    private var sexStatusSunday: SexStatus = SexStatus.TWO_TYPE
    private var sexStatusSaturday: SexStatus = SexStatus.TWO_TYPE
    private var sexStatusMonday: SexStatus = SexStatus.TWO_TYPE
    private var sexStatusTuesday: SexStatus = SexStatus.TWO_TYPE
    private var sexStatusWednesday: SexStatus = SexStatus.TWO_TYPE
    private var sexStatusThursDay: SexStatus = SexStatus.TWO_TYPE
    private var sexStatusFriday: SexStatus = SexStatus.TWO_TYPE


    enum class Status {
        NOT_RESERVED,
        RESERVED,
        ADMIN_RESERVED
    }

    enum class SexStatus() {
        MEN,
        WOMEN,
        TWO_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemsSansWeekBinding =
            DataBindingUtil.inflate(inflater, R.layout.items_sans_week, parent, false)

        val view = binding.root
        val params: ViewGroup.LayoutParams = view.layoutParams
        params.height =
            ((parent.measuredWidth - cnx.resources.getDimension(com.intuit.sdp.R.dimen._55sdp)
                    - (8 * cnx.resources.getDimension(com.intuit.sdp.R.dimen._6sdp))) / 7).toInt()
        view.layoutParams = params
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        item.times?.get(position)?.let { holder.binding.txtTimeSans.text = it }
        //enum status
        item.sunday?.get(position)?.sanseStatus?.let { statusSunday = enumValueOf(it) }
        item.saturday?.get(position)?.sanseStatus?.let { statusSaturday = enumValueOf(it) }
        item.monday?.get(position)?.sanseStatus?.let { statusMonday = enumValueOf(it) }
        item.tuesday?.get(position)?.sanseStatus?.let { statusTuesday = enumValueOf(it) }
        item.wednesday?.get(position)?.sanseStatus?.let { statusWednesday = enumValueOf(it) }
        item.thursday?.get(position)?.sanseStatus?.let { statusThursDay = enumValueOf(it) }
        item.friday?.get(position)?.sanseStatus?.let { statusFriday = enumValueOf(it) }
        //enum status

        //SexStatus
        item.sunday?.get(position)?.sanseSex?.let { sexStatusSunday = enumValueOf(it) }
        item.saturday?.get(position)?.sanseSex?.let { sexStatusSaturday = enumValueOf(it) }
        item.monday?.get(position)?.sanseSex?.let { sexStatusMonday = enumValueOf(it) }
        item.tuesday?.get(position)?.sanseSex?.let { sexStatusTuesday = enumValueOf(it) }
        item.wednesday?.get(position)?.sanseSex?.let { sexStatusWednesday = enumValueOf(it) }
        item.thursday?.get(position)?.sanseSex?.let { sexStatusThursDay = enumValueOf(it) }
        item.friday?.get(position)?.sanseSex?.let { sexStatusFriday = enumValueOf(it) }
        //SexStatus

        holder.binding.constSansSunday.background = initialStatus(statusSunday)
        holder.binding.constSansSaturday.background = initialStatus(statusSaturday)
        holder.binding.constSansMonday.background = initialStatus(statusMonday)
        holder.binding.constSansTuesday.background = initialStatus(statusTuesday)
        holder.binding.constSansWednesday.background = initialStatus(statusWednesday)
        holder.binding.constSansThursday.background = initialStatus(statusThursDay)
        holder.binding.constSansFriday.background = initialStatus(statusFriday)

        holder.binding.imgSexSansSunday.setImageDrawable(initialGender(sexStatusSunday))
        holder.binding.imgSexSansSaturday.setImageDrawable(initialGender(sexStatusSaturday))
        holder.binding.imgSexSansMonday.setImageDrawable(initialGender(sexStatusMonday))
        holder.binding.imgSexSansTuesday.setImageDrawable(initialGender(sexStatusTuesday))
        holder.binding.imgSexSansWednesday.setImageDrawable(initialGender(sexStatusWednesday))
        holder.binding.imgSexSansThursday.setImageDrawable(initialGender(sexStatusThursDay))
        holder.binding.imgSexSansFriday.setImageDrawable(initialGender(sexStatusFriday))


        holder.binding.constSansSunday.setOnClickListener { view ->
            item.sunday?.get(position)?.sanseStatus?.let { statusSunday = enumValueOf(it) }
            view.background = selected(holder.binding.imgSelectSansSunday, statusSunday)
        }
        holder.binding.constSansSaturday.setOnClickListener { view ->
            item.saturday?.get(position)?.sanseStatus?.let { statusSaturday = enumValueOf(it) }
            view.background = selected(holder.binding.imgSelectSansSaturday, statusSaturday)
        }
        holder.binding.constSansMonday.setOnClickListener { view ->
            item.monday?.get(position)?.sanseStatus?.let { statusMonday = enumValueOf(it) }
            view.background = selected(holder.binding.imgSelectSansMonday, statusMonday)
        }
        holder.binding.constSansTuesday.setOnClickListener { view ->
            item.tuesday?.get(position)?.sanseStatus?.let { statusTuesday = enumValueOf(it) }
            view.background = selected(holder.binding.imgSelectSansTuesday, statusTuesday)
        }
        holder.binding.constSansWednesday.setOnClickListener { view ->
            item.wednesday?.get(position)?.sanseStatus?.let { statusWednesday = enumValueOf(it) }
            view.background = selected(holder.binding.imgSelectSansWednesday, statusWednesday)
        }
        holder.binding.constSansThursday.setOnClickListener { view ->
            item.thursday?.get(position)?.sanseStatus?.let { statusThursDay = enumValueOf(it) }
            view.background = selected(holder.binding.imgSelectSansThursday, statusThursDay)
        }
        holder.binding.constSansFriday.setOnClickListener { view ->
            item.friday?.get(position)?.sanseStatus?.let { statusFriday = enumValueOf(it) }
            view.background = selected(holder.binding.imgSelectSansFriday, statusFriday)
        }


    }

    override fun getItemCount(): Int = item.times?.size ?: 0


    class ViewHolder(var binding: ItemsSansWeekBinding) : RecyclerView.ViewHolder(binding.root)


    private fun initialStatus(status: Status): Drawable? {
        return when (status) {
            Status.NOT_RESERVED -> {
                cnx.getDrawable(R.drawable.border_grey_free)
            }
            Status.RESERVED -> {
                cnx.getDrawable(R.drawable.border_white)
            }
            Status.ADMIN_RESERVED -> {
                cnx.getDrawable(R.drawable.border_red)
            }
        }
    }

    private fun initialGender(gender: SexStatus): Drawable? {
        return when (gender) {
            SexStatus.MEN -> {
                cnx.getDrawable(R.drawable.ic_men_icon)
            }
            SexStatus.WOMEN -> {
                cnx.getDrawable(R.drawable.ic_women_icon)
            }
            SexStatus.TWO_TYPE -> {
                null
            }
        }
    }


    private fun selected(view: View, status: Status): Drawable? {
        return if (view.isVisible) {
            view.visibility = View.GONE
            initialStatus(status)
        } else {
            view.visibility = View.VISIBLE
            cnx.getDrawable(R.drawable.border_blue_selected)

        }
    }

}