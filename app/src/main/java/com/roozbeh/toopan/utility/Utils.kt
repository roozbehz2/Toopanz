package com.roozbeh.toopan.utility

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

object Utils {

    @JvmStatic
    fun getSignedInToken(context: Context): String? {
        var token: String? = null
        try {
            val preferences = context.applicationContext.getSharedPreferences(
                Constants.MY_SAVE_PREF,
                Context.MODE_PRIVATE
            )
            token = preferences.getString(Constants.TOKEN_KEY, "")
            if (token!!.isEmpty()) {
                token = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return token
    }


    fun showSnackBar(view: View, text: String){
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
//                                .setAction("Action") {
//                                    // Responds to click on the action
//                                }
            .show()
    }
}