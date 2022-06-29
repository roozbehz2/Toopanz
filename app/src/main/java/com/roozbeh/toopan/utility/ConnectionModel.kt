package com.roozbeh.toopan.utility

import com.roozbeh.toopan.utility.ConnectionModel

class ConnectionModel private constructor() {
    var isUpdateUser = true

    companion object {
        val instance = ConnectionModel()
    }
}