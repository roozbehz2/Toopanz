package com.roozbeh.toopan.modelApi

class SansResponse {
    var id: Long? = null
    var startDate: Long? = null
    var endDate: Long? = null
    var price: Long? = null
    var finalPrice: Long? = null
    var sanseSex: String? = null
    var sanseStatus: String? = null
    override fun toString(): String {
        return "SansResponse(id=$id, startDate=$startDate, endDate=$endDate, price=$price, finalPrice=$finalPrice, sanseSex=$sanseSex, sanseStatus=$sanseStatus)"
    }


}