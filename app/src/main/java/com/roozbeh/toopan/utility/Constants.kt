package com.roozbeh.toopan.utility

import android.os.Environment
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
    const val BUNDLE_ADD_OR_EDIT_KEY = "bundleAddOrEditSalon"
    const val MAP_STYLE = "mapStyle"
    const val CITY_ID = "cityId"
    const val STATE_ID = "stateId"


    var lat = 0.0
    var lon = 0.0



    //URL SERVER
    const val BASE_URL = "https://toopan.footfant.com/api"
    const val LOGIN = "/user/login"
    const val GET_USER = "/user/"
    const val GET_STATE = "/location/state"
    const val GET_CITY = "/location/city/"
    const val GET_GENDER = "/user/get-sex"
    const val POST_UPDATE_PROFILE = "/user/update-profile"
    const val POST_UPLOAD_PROFILE = "/user/upload-profile/"
    const val POST_UPLOAD_PROFILE_SALON = "/salon/upload-avatar/"
    const val POST_UPLOAD_IMAGE_SALON = "/salon/upload-images/"
    const val POST_REFRESH_TOKEN = "/user/refresh-token"
    const val GET_SALONS = "/salon/get-all"
    const val GET_SALONS_INFO_ADD = "/salon/create-salon-data"
    const val GET_SALONS_INFO_EDIT = "/salon/get-salon-update/"
    const val POST_DELETE_IMAGE_SALON = "/salon/delete-salon-image"
    const val POST_ADD_EDIT_SALON = "/salon"
    const val POST_SALON_WEEK_SANS = "/salon/get-salon-weak-sanse"
    const val GET_MONTHS_MILLIS = "/sanse/get-month"


    //Folders
    val BASE_FOLDER =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath +
                File.separator + MyApplication.getContext().getString(R.string.app_name) + File.separator

    val MEDIA = BASE_FOLDER + File.separator + MyApplication.getContext().getString(R.string.Image)

}