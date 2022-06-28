package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.ListCity
import com.roozbeh.toopan.utility.Constants

class VolleyGetCity {
    companion object {

        fun getCity(
            check: VolleyInterface<ListCity>,
            context: Context?,
            id: Int,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.GET_CITY + id


            val request: AuthRequest<ListCity> = AuthRequest<ListCity>(
                Request.Method.GET,
                url,
                ListCity::class.java,
                null,
                true,
                context,
                { response -> check.onSuccess(response) },
                { error -> check.onFailed(error) }
            )
            request.retryPolicy = DefaultRetryPolicy(10000, 1, 1.0f)
            VolleyController.INSTANCE.addToRequestQueue(context, request, TAG)


        }

    }
}