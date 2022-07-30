package com.roozbeh.toopan.modelApi

class WeekSalonSanseResponse {
    var monthName: String? = null
    var times: ArrayList<String>? = null

    var saturday: ArrayList<SansResponse>? = null
    var saturdayDate: String? = null

    var sunday: ArrayList<SansResponse>? = null
    var sundayDate: String? = null

    var monday: ArrayList<SansResponse>? = null
    var mondayDate: String? = null

    var tuesday: ArrayList<SansResponse>? = null
    var tuesdayDate: String? = null

    var wednesday: ArrayList<SansResponse>? = null
    var wednesdayDate: String? = null

    var thursday: ArrayList<SansResponse>? = null
    var thursdayDate: String? = null

    var friday: ArrayList<SansResponse>? = null
    var fridayDate: String? = null
    override fun toString(): String {
        return "WeekSalonSanseResponse(monthName=$monthName, times=$times, saturday=$saturday, saturdayDate=$saturdayDate, sunday=$sunday, sundayDate=$sundayDate, monday=$monday, mondayDate=$mondayDate, tuesday=$tuesday, tuesdayDate=$tuesdayDate, wednesday=$wednesday, wednesdayDate=$wednesdayDate, thursday=$thursday, thursdayDate=$thursdayDate, friday=$friday, fridayDate=$fridayDate)"
    }


}