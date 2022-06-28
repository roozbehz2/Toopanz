package com.roozbeh.toopan.interfaces

import com.android.volley.VolleyError
import com.roozbeh.toopan.modelApi.ListCity

interface VolleyInterface<T> {

    fun onSuccess(body: T)
    fun onFailed(error: VolleyError?)
}