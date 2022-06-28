package com.roozbeh.toopan.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.utility.Constants


open class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NetDetector.init(applicationContext)

    }

    companion object{
        private var pref: SharedPreferences? = null
        fun preferences(context :Context): SharedPreferences {
            if (pref == null) {
                pref = context.getSharedPreferences(Constants.MY_SAVE_PREF, Context.MODE_PRIVATE)
            }
            return pref!!
        }
    }

}