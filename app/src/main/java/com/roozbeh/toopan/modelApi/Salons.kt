package com.roozbeh.toopan.modelApi

import java.io.Serializable

class Salons : Serializable {
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
    var images = emptyArray<String>()
    var activate: Boolean? = null
    var deleted: Boolean? = null


}