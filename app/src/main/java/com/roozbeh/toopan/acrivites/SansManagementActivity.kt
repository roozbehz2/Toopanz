package com.roozbeh.toopan.acrivites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.JsonObject
import com.roozbeh.toopan.R
import com.roozbeh.toopan.adapter.PossibilitiesAdapter
import com.roozbeh.toopan.adapter.SansAdapter
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyRequest.VolleyPostGetSalonWeekSans
import com.roozbeh.toopan.databinding.ActivitySansManagementBinding
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.SalonPossibilities
import com.roozbeh.toopan.modelApi.WeekSalonSanseResponse
import com.roozbeh.toopan.utility.Constants
import com.roozbeh.toopan.utility.Utils
import org.json.JSONObject
import java.util.*

class SansManagementActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySansManagementBinding
    private val getSalonWeekSansTag = "getSalonWeekSansTag"
    private var salonsId: Int? = null
    private var salonsName: String? = null
    private lateinit var adapterSans: SansAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sans_management)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sans_management)
        binding.imgBackSans.setOnClickListener(this)

        BottomSheetBehavior.from(binding.constSheet).apply {
            peekHeight = resources.getDimension(com.intuit.sdp.R.dimen._70sdp).toInt()
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        intent.extras?.let { salonsId = it.getInt(Constants.BUNDLE_ADD_OR_EDIT_KEY) }
        intent.extras?.let { salonsName = it.getString("nameSalon") }
        if (salonsId != null) {
            checkNet(System.currentTimeMillis())
            binding.txtTitleSans.text = "مدیریت سانس($salonsName)"
        }


    }

    private fun checkNet(currentTimeMillis: Long) {

        NetDetector.check {
            if (it) {
                requestServer(currentTimeMillis)
            } else {
                Utils.showSnackBar(
                    this,
                    binding.constSheet,
                    getString(R.string.noInternet),
                    getColor(R.color.snackBar)
                )
            }
        }
    }

    //todo
//    android.icu.util.Calendar.ONE_DAY
    private fun requestServer(currentTimeMillis: Long) {
        val body = JsonObject()
        body.addProperty("date", currentTimeMillis)
        salonsId?.let { body.addProperty("salonId", it) }
        VolleyPostGetSalonWeekSans.getSalonWeekSans(object :
            VolleyInterface<WeekSalonSanseResponse> {
            override fun onSuccess(body: WeekSalonSanseResponse?) {
                if (body != null) {
                    initRecycler(body)
                    binding.txtCurrentMonthSans.text = body.monthName
                    binding.txtDateSaturday.text = body.saturdayDate
                    binding.txtDateSunday.text = body.sundayDate
                    binding.txtDateMonday.text = body.mondayDate
                    binding.txtDateTuesday.text = body.tuesdayDate
                    binding.txtDateWednesday.text = body.wednesdayDate
                    binding.txtDateThursday.text = body.thursdayDate
                    binding.txtDateFriday.text = body.fridayDate

                }
            }

            override fun onFailed(error: VolleyError?) {

                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext,
                            binding.constSheet,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, this, body, getSalonWeekSansTag)

    }

    private fun initRecycler(body: WeekSalonSanseResponse) {
        adapterSans = SansAdapter(body, this)
        binding.recySansSalon.layoutManager = LinearLayoutManager(this)
        binding.recySansSalon.adapter = adapterSans
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.imgBackSans.id ->onBackPressed()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finish()
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }


}