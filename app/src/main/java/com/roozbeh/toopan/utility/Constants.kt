package com.roozbeh.toopan.utility

import android.os.Environment
import android.provider.Settings.Global.getString
import com.roozbeh.toopan.R
import com.roozbeh.toopan.app.MyApplication
import java.io.File

object Constants {


    /* Store key for save values in shared preferences
         *****Please do not change anything here*****
         */

    const val MY_SAVE_PREF = "MyMagSavePref"
    const val TOKEN_KEY = "userTokenKey"
    const val ID_KEY = "userIdKey"
    const val REFRESH_TOKEN_KEY = "userRefreshTokenKey"
    const val LOGIN_KEY = "loginUser"





    //URL SERVER
    const val BASE_URL = "https://toopan.footfant.com/api"
    const val LOGIN = "/user/login"
    const val GET_USER = "/user/"
    const val GET_STATE = "/location/state"
    const val GET_CITY = "/location/city/"
    const val GET_GENDER = "/user/get-sex"
    const val POST_UPDATE_PROFILE = "/user/update-profile"
    const val POST_UPLOAD_PROFILE = "/user/upload-profile/"
    const val POST_REFRESH_TOKEN = "/user/refresh-token"


    //Folders
    val BASE_FOLDER =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath +
                File.separator + MyApplication.getContext().getString(R.string.app_name) + File.separator

    val MEDIA = BASE_FOLDER + File.separator + MyApplication.getContext().getString(R.string.Image)

}