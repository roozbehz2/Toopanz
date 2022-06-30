package com.roozbeh.toopan.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.utility.Constants
import kotlin.coroutines.coroutineContext


open class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NetDetector.init(applicationContext)
        context = applicationContext

    }

    companion object{
        private var context: Context? = null
        private var pref: SharedPreferences? = null
        fun preferences(): SharedPreferences {
            if (pref == null) {
                pref = context!!.getSharedPreferences(Constants.MY_SAVE_PREF, Context.MODE_PRIVATE)
            }
            return pref!!
        }

        fun getContext(): Context{return context!!}
    }

}