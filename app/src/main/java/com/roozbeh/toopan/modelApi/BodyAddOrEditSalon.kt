package com.roozbeh.toopan.modelApi

class BodyAddOrEditSalon {
    var name:String? = null
    var ownership:Int? = null
    var sex:Int? = null
    var address:Address? = null
    var salonPossibilities = arrayListOf<Int>()
    var salonTypes = arrayListOf<Int>()
    var startTime:String? = null
    var endTime:String? = null
    var periodMin:Int? = null
    var amount:Int? = null
    var cityId:Int? = null
    var ownerId:Int? = null
    var id:Int? = null
}