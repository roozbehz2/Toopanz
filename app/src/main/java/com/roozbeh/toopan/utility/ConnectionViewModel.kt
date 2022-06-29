package com.roozbeh.toopan.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.roozbeh.toopan.utility.ConnectionModel
import com.roozbeh.toopan.utility.ConnectionViewModel

class ConnectionViewModel private constructor() : ViewModel() {
    private val mutableLiveData = MutableLiveData<ConnectionModel>()
    val updateUser = MutableLiveData<Boolean>()

    companion object {
        val instance = ConnectionViewModel()
    }
}