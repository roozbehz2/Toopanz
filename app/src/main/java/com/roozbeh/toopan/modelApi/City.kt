package com.roozbeh.toopan.modelApi

import java.io.Serializable

class City: Serializable {
    var id: Int? = null
    var name: String? = null
    var state: State? = null
    override fun toString(): String {
        return "City(id=$id, name=$name, state=$state)"
    }


}