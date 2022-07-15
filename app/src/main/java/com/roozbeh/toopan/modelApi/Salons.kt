package com.roozbeh.toopan.modelApi

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Salons() : Parcelable {
    var id: Int? = null
    var name: String? = null
    var description: String? = null
    var ownership: Int? = null
    var sex: Int? = null
    var city: City? = null
    var startTime: String? = null
    var endTime: String? = null
    var periodMin: Int? = null
    var amount: Int? = null
    var address: String? = null
    var lat: Float? = null
    var lng: Float? = null
    var salonPossibilities: ArrayList<SalonPossibilities>? = null
    var salonTypes: ArrayList<SalonTypes>? = null
    var avatar: String? = null
    var images = arrayListOf<String>()
    var activate: Boolean? = null
    var deleted: Boolean? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        name = parcel.readString()
        description = parcel.readString()
        ownership = parcel.readValue(Int::class.java.classLoader) as? Int
        sex = parcel.readValue(Int::class.java.classLoader) as? Int
        startTime = parcel.readString()
        endTime = parcel.readString()
        periodMin = parcel.readValue(Int::class.java.classLoader) as? Int
        amount = parcel.readValue(Int::class.java.classLoader) as? Int
        address = parcel.readString()
        lat = parcel.readValue(Float::class.java.classLoader) as? Float
        lng = parcel.readValue(Float::class.java.classLoader) as? Float
        avatar = parcel.readString()
        activate = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        deleted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeValue(ownership)
        parcel.writeValue(sex)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeValue(periodMin)
        parcel.writeValue(amount)
        parcel.writeString(address)
        parcel.writeValue(lat)
        parcel.writeValue(lng)
        parcel.writeString(avatar)
        parcel.writeValue(activate)
        parcel.writeValue(deleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Salons> {
        override fun createFromParcel(parcel: Parcel): Salons {
            return Salons(parcel)
        }

        override fun newArray(size: Int): Array<Salons?> {
            return arrayOfNulls(size)
        }
    }

}