package com.roozbeh.toopan.modelApi

class InfoSalon {
    var salon: Salons = Salons()
    var ownership = arrayListOf<SalonOwnerShip>()
    var sexes: ListGender = ListGender()
    var salonTypes = arrayListOf<SalonTypes>()
    var salonPossibilities = arrayListOf<SalonPossibilities>()
    var states = arrayListOf<State>()
}