package com.roozbeh.toopan.dialog

import android.content.Context
import android.graphics.Rect
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.DialogTimingSalonsBinding
import com.roozbeh.toopan.utility.AnimationManager
import nl.joery.timerangepicker.TimeRangePicker

class TimingDialog(context: Context) : Dialog(context) {
    private lateinit var binding: DialogTimingSalonsBinding

    public override fun initDialog() {
        val displayRectangle = Rect()
        val window = window
        window!!.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        getWindow()!!.setLayout(
            (displayRectangle.width() * 0.9f).toInt(),
            (displayRectangle.height() * 0.5f).toInt()
        )

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(
                context
            ), R.layout.dialog_timing_salons, null, false
        )
        setContentView(binding.root)
        binding.txtSubmitDialogTiming.setOnClickListener(this)



        binding.timePickerDialog.setOnTimeChangeListener(object :
            TimeRangePicker.OnTimeChangeListener {
            override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
                binding.txtStartSalons.text = "آغاز از: $startTime"

            }

            override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
                binding.txtEndSalons.text = "پایان از: $endTime"
            }

            override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
            }
        })

        binding.txtStartSalons.text = "آغاز از: ${binding.timePickerDialog.startTime}"
        binding.txtEndSalons.text = "پایان از: ${binding.timePickerDialog.endTime}"


    }


    override fun onClick(v: View) {
        when (v.id) {
            binding.txtSubmitDialogTiming.id -> {
                if (onClickListener != null) {
                    AnimationManager.blueRedButtonClick(binding.txtSubmitDialogTiming, 0.2f, 1f)
                    val countDownTimer: CountDownTimer = object : CountDownTimer(60, 10) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            onClickListener.submitSetTime(
                                binding.timePickerDialog.startTime.toString(),
                                binding.timePickerDialog.endTime.toString()
                            )
                            dismiss()
                        }
                    }
                    countDownTimer.start()
                }
            }
        }
    }

    override fun setTimes(start: String, end: String) {

        binding.txtStartSalons.text = "آغاز از: $start"
        binding.txtEndSalons.text = "پایان از: $end"
//        binding.timePickerDialog.startTime = TimeRangePicker.Time(start.substring(0,2).toInt(),start.substring(3,5).toInt())
//        binding.timePickerDialog.startTime = TimeRangePicker.Time(18,30)
//        binding.timePickerDialog.endTime = TimeRangePicker.Time(21,30)

        val unitsStart = start.split(":").toTypedArray()
        val sH = unitsStart[0].toInt()
        val sM = unitsStart[1].toInt()
        val minuteStart = 60 * sH + sM
        val unitsEnd = end.split(":").toTypedArray()
        val eH = unitsEnd[0].toInt()
        val eM = unitsEnd[1].toInt()
        val minuteEnd = 60 * eH + eM

        binding.timePickerDialog.startTimeMinutes = minuteStart
        binding.timePickerDialog.endTimeMinutes = minuteEnd
    }
}