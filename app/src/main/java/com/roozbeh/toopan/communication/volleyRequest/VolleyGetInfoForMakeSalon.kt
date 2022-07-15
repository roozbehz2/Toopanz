package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.google.gson.JsonObject
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.InfoSalon
import com.roozbeh.toopan.modelApi.ListCity
import com.roozbeh.toopan.modelApi.PaginationBody
import com.roozbeh.toopan.modelApi.SalonsResponse
import com.roozbeh.toopan.utility.Constants

class VolleyGetInfoForMakeSalon {
    companion object {

        fun getInfo(
            check: VolleyInterface<InfoSalon>,
            context: Context?,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.GET_SALONS_INFO_ADD

            val request: AuthRequest<InfoSalon> = AuthRequest<InfoSalon>(
                Request.Method.GET,
                url,
                InfoSalon::class.java,
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