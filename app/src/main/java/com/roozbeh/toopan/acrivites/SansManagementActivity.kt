package com.roozbeh.toopan.acrivites

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.JsonObject
import com.roozbeh.toopan.R
import com.roozbeh.toopan.adapter.SansAdapter
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.communication.volleyRequest.VolleyGetMonths
import com.roozbeh.toopan.communication.volleyRequest.VolleyPostGetSalonWeekSans
import com.roozbeh.toopan.databinding.ActivitySansManagementBinding
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.Months
import com.roozbeh.toopan.modelApi.WeekSalonSanseResponse
import com.roozbeh.toopan.utility.Constants
import com.roozbeh.toopan.utility.Utils
import java.util.*
import java.util.concurrent.TimeUnit


class SansManagementActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySansManagementBinding
    private val getSalonWeekSansTag = "getSalonWeekSansTag"
    private val getMonthsTag = "getMonthsTag"
    private var salonsId: Int? = null
    private var salonsName: String? = null
    private var currentTimeMillis: Long? = null
    private var months: Months? = null
    private var first = false
    private lateinit var adapterSans: SansAdapter
    private val monthString: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sans_management)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sans_management)
        binding.imgBackSans.setOnClickListener(this)
        binding.imgPreviousMonthSans.setOnClickListener(this)
        binding.imgNextMonthSans.setOnClickListener(this)
        binding.pageBlackSans.setOnClickListener(this)
        binding.spinnerCurrentMonthSans.onItemSelectedListener = this



        BottomSheetBehavior.from(binding.constSheet).apply {
            peekHeight = resources.getDimension(com.intuit.sdp.R.dimen._70sdp).toInt()
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        intent.extras?.let { salonsId = it.getInt(Constants.BUNDLE_ADD_OR_EDIT_KEY) }
        intent.extras?.let { salonsName = it.getString("nameSalon") }
        if (salonsId != null) {
            checkNetMonth()
            currentTimeMillis = System.currentTimeMillis()
            currentTimeMillis?.let { checkNet(it, false) }
            binding.txtTitleSans.text = "مدیریت سانس($salonsName)"
        }


    }


    private fun checkNet(currentTimeMillis: Long, smooth: Boolean) {
        binding.pageBlackSans.visibility = View.VISIBLE
        binding.refreshSans.visibility = View.VISIBLE
        NetDetector.check {
            if (it) {
                requestServer(currentTimeMillis, smooth)
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

    private fun checkNetMonth() {

        NetDetector.check {
            if (it) {
                getMonths()
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
    private fun requestServer(currentTimeMillis: Long, smooth: Boolean) {
        val body = JsonObject()
        body.addProperty("date", currentTimeMillis)
        salonsId?.let { body.addProperty("salonId", it) }
        VolleyPostGetSalonWeekSans.getSalonWeekSans(object :
            VolleyInterface<WeekSalonSanseResponse> {
            override fun onSuccess(body: WeekSalonSanseResponse?) {
                if (body != null) {
                    binding.pageBlackSans.visibility = View.GONE
                    binding.refreshSans.visibility = View.GONE
                    if (smooth) {
                        adapterSans.setItem(body)
                        body.times?.size?.let { adapterSans.notifyItemRangeChanged(0, it, true) }
                        binding.recySansSalon.startLayoutAnimation()
                        binding.imgPreviousMonthSans.isEnabled = true
                        binding.imgNextMonthSans.isEnabled = true
                    } else {
                        initRecycler(body)
                    }
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

                binding.pageBlackSans.visibility = View.GONE
                binding.refreshSans.visibility = View.GONE
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
        binding.recySansSalon.startLayoutAnimation()
    }


    private fun nextMonth() {
        currentTimeMillis = currentTimeMillis?.plus(TimeUnit.DAYS.toMillis(1) * 7)
        currentTimeMillis?.let { checkNet(it, true) }
    }

    private fun previousMonth() {
        currentTimeMillis = currentTimeMillis?.minus(TimeUnit.DAYS.toMillis(1) * 7)
        currentTimeMillis?.let { checkNet(it, true) }
    }


    private fun setAdapterSpinner(months: Months) {

        for (i in months.indices.step(1)) {
            months[i].name.let { monthString.add(it) }
        }


        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            R.layout.item_spinner_months_transparent,
            monthString as List<Any?>
        )
        // set simple layout resource file
        // for each item of spinner

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            R.layout.item_spinner_months
        )

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        binding.spinnerCurrentMonthSans.adapter = ad

    }

    private fun getMonths() {
        VolleyGetMonths.getMonths(object : VolleyInterface<Months> {
            override fun onSuccess(body: Months?) {
                if (body != null) {
                    months = body
                    setAdapterSpinner(body)
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

        }, this, getMonthsTag)
    }


    override fun onBackPressed() {
        finish()
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun onPause() {
        super.onPause()
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getSalonWeekSansTag)
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.imgBackSans.id -> onBackPressed()
            binding.imgPreviousMonthSans.id -> {
                binding.imgPreviousMonthSans.isEnabled = false
                previousMonth()
            }
            binding.imgNextMonthSans.id -> {
                binding.imgNextMonthSans.isEnabled = false
                nextMonth()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (first) {
            months?.find { month -> month.name == monthString[position] }?.date?.let {
                currentTimeMillis = it
            }
            currentTimeMillis?.let { checkNet(it, false) }
        }else{
            first = true
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


}