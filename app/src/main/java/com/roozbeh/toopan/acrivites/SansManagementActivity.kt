package com.roozbeh.toopan.acrivites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ActivitySansManagementBinding

class SansManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySansManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sans_management)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sans_management)

    }
}