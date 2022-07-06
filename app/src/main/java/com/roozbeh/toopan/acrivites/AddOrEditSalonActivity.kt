package com.roozbeh.toopan.acrivites

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ActivityAddOrEditSalonBinding
import nl.joery.timerangepicker.TimeRangePicker


class AddOrEditSalonActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddOrEditSalonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_or_edit_salon)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_or_edit_salon)


    }


    private fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.dialog_timing_salons, null)
        alertDialog.setView(customLayout)
        alertDialog.setPositiveButton(getString(R.string.accept),
            DialogInterface.OnClickListener { dialog, which -> // send data from the AlertDialog to the Activity
                val timePickerDialog =
                    customLayout.findViewById<TimeRangePicker>(R.id.timePickerDialog)
                timePickerDialog.setOnTimeChangeListener(object :
                    TimeRangePicker.OnTimeChangeListener {
                    override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
                        Log.d("TimeRangePicker", "Start time: " + startTime)
                    }

                    override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
                        Log.d("TimeRangePicker", "End time: " + endTime.hour)
                    }

                    override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
                        Log.d("TimeRangePicker", "Duration: " + duration.hour)
                    }
                })

            })
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.txtInputLayoutGetTimeStartEndADSalon.id -> {
                showAlertDialog()
            }
        }
    }
}