package com.roozbeh.toopan.acrivites

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.roozbeh.toopan.R
import com.roozbeh.toopan.app.MyApplication
import com.roozbeh.toopan.utility.Constants
import com.roozbeh.toopan.utility.UiHandler
import com.roozbeh.toopan.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        initLayout()
    }

    private fun initLayout() {

        binding.constLoginSplash.setOnClickListener(this)

        val text =
            "<font color=#2B3234>به </font> <font color=#FF5821>توپان </font> <font color=#2B3234>خوش امدید</font>"
        binding.txtWelcomeSplash.text = Html.fromHtml(text, 0)
        if (MyApplication.preferences().getBoolean(Constants.LOGIN_KEY, false)) {
            binding.constLoginSplash.isEnabled = false

            object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.start()
        } else {
            binding.constLoginSplash.alpha = 1f
            UiHandler.enableMode(true, binding.constLoginSplash)
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            binding.constLoginSplash.id -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
}