package com.roozbeh.toopan.modelApi

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Entity
class Location : Serializable {
    @SerializedName("y")
    var latitude = 0.0

    @SerializedName("x")
    var longitude = 0.0
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
}