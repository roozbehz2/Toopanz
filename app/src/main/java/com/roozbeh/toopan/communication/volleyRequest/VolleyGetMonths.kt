package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.Months
import com.roozbeh.toopan.utility.Constants

class VolleyGetMonths {
    companion object {

        fun getMonths(
            check: VolleyInterface<Months>,
            context: Context?,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.GET_MONTHS_MILLIS


            val request: AuthRequest<Months> = AuthRequest<Months>(
                Request.Method.GET,
                url,
                Months::class.java,
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