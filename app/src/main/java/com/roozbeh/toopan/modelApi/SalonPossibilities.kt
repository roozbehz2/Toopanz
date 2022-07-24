package com.roozbeh.toopan.modelApi

import java.io.Serializable

class SalonPossibilities: Serializable {
    var id: Int? = null
    var possibilities: String? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SalonPossibilities

        if (id != other.id) return false
        if (possibilities != other.possibilities) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (possibilities?.hashCode() ?: 0)
        return result
    }

}