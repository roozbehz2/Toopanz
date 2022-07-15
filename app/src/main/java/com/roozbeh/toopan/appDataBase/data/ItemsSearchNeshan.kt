package com.roozbeh.toopan.appDataBase.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.neshan.servicessdk.search.model.Item

@Entity
class ItemsSearchNeshan {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title : String? = null
    var address: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
}
