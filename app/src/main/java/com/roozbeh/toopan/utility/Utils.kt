package com.roozbeh.toopan.utility

import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.roozbeh.toopan.R
import com.roozbeh.toopan.app.MyApplication
import com.stfalcon.imageviewer.StfalconImageViewer
import java.io.File


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


    fun showSnackBar(context: Context, view: View, text: String, color: Int) {

        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
//            .setAction("Action", null)
        val sbView = snackbar.view
        sbView.setBackgroundColor(color)
        val textView =
            snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.parseColor("#FFFFFF"))
        textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        textView.typeface = context.resources.getFont(R.font.estedad_regular)
        textView.textSize = context.resources.getDimension(com.intuit.ssp.R.dimen._5ssp)
        snackbar.show()
    }


    fun openImageViewer(context: Context, targetImageView: ImageView, imageUrl: String) {
        val images = ArrayList<String>()
        images.add(imageUrl) //your img url here

        StfalconImageViewer.Builder(
            context, images
        ) { imageView, image -> Glide.with(context).load(image).into(imageView) }
            .withTransitionFrom(targetImageView)
            .show()
    }


}